package com.mh.model.gestioneMagazzino;

import com.mh.exception.InvalidProductCodeException;
import com.mh.model.database.Connector;
import com.mh.model.javaBeans.Product;

import java.sql.*;
import java.time.LocalDateTime;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 * Fornisce una API di accesso al database per la parte relativa
 * ai prodotti, ai materiali di composizione dei prodotti.
 */
public class DBProduct{
	private Connection connection = null;
	
	public DBProduct(){
		connection = Connector.getConnection();
	}
	
	/**
	 * Restituisce una struttura dati associativa contenente come chiave i codici prodotto
	 * attualmente registrati nel database  e come valore un riferimento ad un oggetto
	 * segnaposto.
	 *
	 * @return una struttura dati di tipo HashMap che ha per chiave un codice prodotto
	 * e per valore un riferimento ad un segnaposto di tipo Boolean
	 */
	private Map<Integer,Boolean> getProductCodeMap(){
		/*
		 * Preleviamo i codici prodotto attualmente registrati nel database
		 */
		Statement codeSt = null;
		ResultSet codeRs = null;
		try{
			codeSt = connection.createStatement();
			String sql = "SELECT CODICE FROM PRODOTTO;";
			codeRs = codeSt.executeQuery(sql);
		}
		catch(SQLException sqle){
			System.out.println("DB issue: failed to fetch product IDs from database.\n");
			System.out.println(sqle.getClass().getName() + ": " + sqle.getMessage());
		}
		
		Map<Integer,Boolean> codeMap = new HashMap<Integer,Boolean>();
		Boolean placeHolder = new Boolean(true);
		try{
			while(codeRs.next()){
				codeMap.put(codeRs.getInt("CODICE"), placeHolder);
			}
			codeSt.close(); // Chiude anche il ResultSet codeRs
		}
		catch(SQLException sqle){
			System.out.println("ResultSet issue: failed to create the product code list.\n");
			System.out.println(sqle.getClass().getName() + ": " + sqle.getMessage());
		}
		return codeMap;
	}
	
	/**
	 * Permette l'inserimento di una lista di prodotti.
	 *
	 * - I prodotti il cui bean reca un 'codice' null oppure un valore non gi� presente
	 *   nel database vengono inseriti come nuovi prodotti (i primi con un codice
	 *   automatico).
	 * - Per prodotti il cui bean reca un 'codice' gi� presente nel database viene
	 *   aggiornato soltanto il numero di unit�.
	 *
	 * @param products lista di prodotti da inserire nel database
	 */
	public void insertProductList(List<Product> products) throws SQLException {
		/*
		 * La seguente lista di esistenza � una struttura dati associativa in cui la chiave
		 * � costituita da un codice prodotto gi� registrato e il valore associato ad ogni
		 * chiave � un riferimento ad un oggetto segnaposto.
		 */
		Map<Integer,Boolean> existenceList = this.getProductCodeMap();
		
		/*
		 * I bean di tipo Prodotto con 'codice' pari a null devono essere inseriti per ultimi.
		 * Questo evita il fallito inserimento di quei prodotti nuovi il cui codice (esplicito)
		 * sia stato gi� implicitamente assegnato ad un prodotto con codice null per via del
		 * calcolo autoincrementale del codice.
		 */
		List<Product> nullCodeProducts = new ArrayList<Product>();
		List<Product> codeProducts = new ArrayList<Product>();
		for(Product p : products){
			if(p.getCode() != null)
				codeProducts.add(p);
		}
		for(Product p : products){
			if(p.getCode() == null)
				nullCodeProducts.add(p);
		}
		products.clear();
		products.addAll(codeProducts);
		products.addAll(nullCodeProducts);
		
		/*
		 * In base alla lista di esistenza si determina se un dato prodotto p nella lista
		 * passata come parametro � un prodotto gi� esistente nel database o un prodotto
		 * nuovo.
		 */
		PreparedStatement productSt = null;
		PreparedStatement compositionSt = null;
		PreparedStatement unitSt = null;
		
		String sql =
			"INSERT INTO PRODOTTO" +
			" (CODICE, NOMEPRODOTTO, LINEA, TIPO, IMMAGINI, UNITADISPONIBILI, SCORTAMINIMA, PREZZOVENDITA, PREZZOACQUISTO, DATAINSERIMENTO, DESCRIZIONE, ELIMINATO, DATAELIMINAZIONE)" +
			" VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?);";
		String materialsSQL =
			"INSERT INTO COMPOSIZIONE" +
			" (CODICEPRODOTTO, MATERIALE)" +
			" VALUES(?,?);";
		String unitsSQL =
			"UPDATE PRODOTTO SET UNITADISPONIBILI = ? WHERE CODICE = ?;";
			
		try{
			connection.setAutoCommit(false); // INIZIO DELLA TRANSAZIONE
			productSt = connection.prepareStatement(sql);
			compositionSt = connection.prepareStatement(materialsSQL);
			unitSt = connection.prepareStatement(unitsSQL);
			for(Product p : products){
				if(p.getCode() == null || !existenceList.containsKey(p.getCode())){ //PRODOTTO NUOVO
					/*
					 * L'oggetto PreparedStatement potrebbe avere associato al primo parametro
					 * il codice di un prodotto precedentemente inserito.
					 */
					if(p.getCode() != null)
						productSt.setInt(1, p.getCode());
					else
						productSt.clearParameters(); // IN ASSENZA CIO' POTREBBE PORTARE A UN ERRORE DI CHIAVE RIPETUTA.
					
					productSt.setString(2, p.getName());
					productSt.setString(3, p.getLine());
					productSt.setString(4, p.getType());
					productSt.setString(5, p.getImages());
					productSt.setInt(6, p.getAvailableUnits());
					productSt.setInt(7, p.getMinInventory()); // 0 di default
					productSt.setBigDecimal(8, p.getPrice());
					if(p.getCost() == null)
						productSt.setNull(9, Types.DECIMAL);
					else
						productSt.setBigDecimal(9, p.getCost());
					productSt.setString(10, p.getInsertDate().toString());
					productSt.setString(11, p.getDescription());
					productSt.setBoolean(12, p.isDeleted());
					productSt.setString(13, (p.getDeletionDate() != null) ? p.getDeletionDate().toString() : null);
					
					productSt.executeUpdate();
					/*
					 * Determiniamo il codice del prodotto
					 */
					Integer productKey = null;
					if(p.getCode() == null){ //PRODOTTO NUOVO SENZA CODICE
						//Preleviamo il codice dell'ultimo prodotto inserito.
						Statement generatedKeySt = connection.createStatement();
						ResultSet generatedKeys = generatedKeySt.executeQuery("SELECT last_insert_rowid()"); // NON SOGGEETTA ALLA TRANSAZIONE
						if (generatedKeys.next()) {
							productKey = generatedKeys.getInt(1);
						}
						generatedKeySt.close(); // Chiude anche il ResultSet generatedKeys
					}
					else{ //PRODOTTO NUOVO CON CODICE
						productKey = p.getCode();
					}
					
					/*
					 * Aggiorniamo la tabella COMPOSIZIONE
					 */
					if(p.getMaterials() != null){
						for(String material : p.getMaterials()){
							compositionSt.setInt(1, productKey);
							compositionSt.setString(2, material.toLowerCase());
							compositionSt.executeUpdate();
						}
					}
					connection.commit(); // FINE DELLA TRANSAZIONE
				}
				else{
					// PRODOTTO GIA' PRESENTE (existenceList.containsKey(p.getCode()))
					unitSt.setInt(1, p.getAvailableUnits());
					unitSt.setInt(2, p.getCode());
					unitSt.executeUpdate();
				}
			}
		}
		catch(SQLException sqle){
			System.out.println("DB issue: failed to update a product.\n");
			System.out.println(sqle.getClass().getName() + ": " + sqle.getMessage());
			try{
				System.out.println("Transaction is being rolled back.");
				connection.rollback();
			}
			catch(SQLException sqle2){
				System.out.println("Failed to roll back transaction!");
				System.out.println(sqle2.getClass().getName() + ": " + sqle2.getMessage());
			}
		}
		finally{
			// Chiusura dei tre prepared statements
			if(productSt != null)
				productSt.close();
			if(compositionSt != null)
				compositionSt.close();
			if(unitSt != null)
				unitSt.close();
			/*
			 * La connessione è condivisa quindi al termine della transazione
			 * bisogna reimpostare il commit automatico.
			 */
			connection.setAutoCommit(true);
			Connector.releaseConnection(connection);
		}
	}
	
	/**
	 * Il prodotto deve essere già presente nel database quando questo metodo viene invocato.
	 * Il bean di tipo Product passato come parametro deve obbligatoriamente esporre un
	 * codice prodotto per non incorrere in una InvalidProductCodeException.
	 * 
	 * @param product Il prodotto per il quale applicare le modifiche.
	 * @throws SQLException
	 * @throws InvalidProductCodeException
	 */
	public void updateProduct(Product product) throws SQLException, InvalidProductCodeException {
		if(product.getCode() == null)
			throw new InvalidProductCodeException("Could not update a null-code product.");
		
		PreparedStatement updateProductSt = null;
		String updateProductSql = "UPDATE PRODOTTO SET " +
		"CODICE = ?, NOMEPRODOTTO = ?, LINEA = ?, TIPO = ?, IMMAGINI = ?, UNITADISPONIBILI = ?, SCORTAMINIMA = ?, " +
		"PREZZOVENDITA= ? , PREZZOACQUISTO = ?, DATAINSERIMENTO = ?, DESCRIZIONE = ? " +
		"WHERE CODICE = ?;";
		
		PreparedStatement resetCompositionSt = null;
		String resetCompositionSql = "DELETE FROM COMPOSIZIONE WHERE CODICEPRODOTTO = ?";
		
		PreparedStatement updateCompositionSt = null;
		String updateCompositionSql = "INSERT INTO COMPOSIZIONE" +
				" (CODICEPRODOTTO, MATERIALE)" +
				" VALUES(?,?);";
		
		connection.setAutoCommit(false); // Inizio della transazione
		
		try{
			updateProductSt = connection.prepareStatement(updateProductSql);
			updateProductSt.setInt(1, product.getCode());
			updateProductSt.setString(2, product.getName());
			updateProductSt.setString(3, product.getLine());
			updateProductSt.setString(4, product.getType());
			updateProductSt.setString(5, product.getImages());
			updateProductSt.setInt(6, product.getAvailableUnits());
			updateProductSt.setInt(7, product.getMinInventory());
			updateProductSt.setBigDecimal(8, product.getPrice());
			updateProductSt.setBigDecimal(9, product.getCost());
			updateProductSt.setString(10, product.getInsertDate().toString());
			updateProductSt.setString(11, product.getDescription());
			updateProductSt.setInt(12, product.getCode());
			updateProductSt.executeUpdate();
			
			/*
			 * Aggiorniamo la tabella COMPOSIZIONE
			 */
			resetCompositionSt = connection.prepareStatement(resetCompositionSql);
			resetCompositionSt.setInt(1, product.getCode());
			resetCompositionSt.executeUpdate();
			
			updateCompositionSt = connection.prepareStatement(updateCompositionSql);
			if(product.getMaterials() != null) {
				for(String material : product.getMaterials()) {
					updateCompositionSt.setInt(1, product.getCode());
					updateCompositionSt.setString(2, material.toLowerCase());
					updateCompositionSt.executeUpdate();
				}
			}
			
			connection.commit(); // Fine della transazione
		}
		catch(SQLException sqle){
			System.out.println("Error while updating a product.");
			System.out.println(sqle.getClass().getName() + ": " + sqle.getMessage());
		}
		finally{
			if(updateProductSt != null)
				updateProductSt.close();
			
			// Rispristino dell'autocommit
			connection.setAutoCommit(true);
			
			Connector.releaseConnection(connection);
		}
	}
	
	/**
	 * Restituisce la lista completa dei prodotti. Combina i dati dei prodotti
	 * con i dati relativi ai materiali componenti i prodotti.
	 *
	 * @return List<Product> la lista completa dei prodotti
	 */
	public List<Product> getFullProductList(){
		/*
		 * La lista dei prodotti da restituire.
		 */
		List<Product> productList = new ArrayList<Product>();
		
		/*
		 * Preleviamo il contenuto della tabella PRODOTTO.
		 */
		Statement productStm = null;
		ResultSet productRs = null;
		
		try{
			productStm = connection.createStatement();
			String sql = "SELECT * FROM PRODOTTO;";
			productRs = productStm.executeQuery(sql);
		}
		catch(SQLException sqle){
			System.out.println("DB issue: failed to retrive product data from database.");
			System.out.println(sqle.getClass().getName() + ": " + sqle.getMessage());
		}
		
		try{
			productList = this.getProductListFromRs(productRs);
			productStm.close(); // Chiude anche il ResultSet productRs
			Connector.releaseConnection(connection);
		}
		catch(SQLException sqle){
			System.out.println("ResultSet issue 2: failed to retrive data from ResultSet.\n");
			System.out.println(sqle.getClass().getName() + ": " + sqle.getMessage());
		}
		
		return productList;
	}
	
	/**
	 * Restituisce un prodotto. Combina i dati del prodotto con i dati relativi ai materiali
	 * costituienti il prodotto
	 *
	 * @return Product il prodotto
	 */
	public Product getProduct(int productCode){
		/*
		 * Il prodotto da restituire
		 */
		Product product = new Product();
		
		/*
		 * Preleviamo il contenuto della tabella PRODOTTO.
		 */
		PreparedStatement productStm = null;
		ResultSet productRs = null;
		
		String sql = "SELECT * FROM PRODOTTO WHERE CODICE = ?;";
		
		try{
			productStm = connection.prepareStatement(sql);
			productStm.setInt(1, productCode);
			productRs = productStm.executeQuery();
		}
		catch(SQLException sqle){
			System.out.println("DB issue: failed to retrive product data from database.");
			System.out.println(sqle.getClass().getName() + ": " + sqle.getMessage());
		}
		
		try{
			List<Product> productList = this.getProductListFromRs(productRs);
			product = productList.get(0);
			
			productStm.close(); // Chiude anche il ResultSet productRs
			Connector.releaseConnection(connection);
		}
		catch(SQLException sqle){
			System.out.println("ResultSet issue 2: failed to retrive data from ResultSet.\n");
			System.out.println(sqle.getClass().getName() + ": " + sqle.getMessage());
		}
		
		return product;
	}
	
	/**
	 * Restituisce la lista dei prodotti disponibili. Combina i dati dei prodotti
	 * con i dati relativi ai materiali componenti i prodotti.
	 *
	 * @return List<Product> la lista dei prodotti disponibili
	 */
	public List<Product> getAvailableProductList(int offset, int limit) {
		
		List<Product> productList = new ArrayList<Product>();
		PreparedStatement productStm = null;
		
		try {
			String sql = "SELECT * FROM PRODOTTO WHERE ELIMINATO = 0 AND UNITADISPONIBILI > 0 LIMIT ? OFFSET ?;";
			productStm = connection.prepareStatement(sql);
			productStm.setInt(1, limit);
			productStm.setInt(2,  offset);
			ResultSet productRs = productStm.executeQuery();
			
			productList = getProductListFromRs(productRs);
		}
		catch(SQLException e) {
			System.out.println("Errore: impossibile ottenere la lista di prodotti disponibili.");
			System.out.println(e.getClass().getName() + ": " + e.getMessage());
		}
		finally {
			if(productStm != null) {
				try {
					productStm.close();
				}
				catch(SQLException e) {
					System.out.println("Errore: impossibile chiudere lo statement.");
					System.out.println(e.getClass().getName() + ": " + e.getMessage());
				}
				finally {
					Connector.releaseConnection(connection);
				}
			}
		}

		return productList;
	}
	
	/**
	 * Restituisce la lista dei prodotti i cui codici sono presenti nel carrello.
	 * Combina i dati dei prodotti con i dati relativi ai materiali componenti i prodotti.
	 *
	 * @return List<Product> la lista dei prodotti se nella lista di input c'è almeno un codice valido
	 * oppure una lista vuota.
	 */
	public List<Product> getProductListByCode(List<Integer> productCodes) {
		List<Product> productList = new ArrayList<Product>();
		
		if(productCodes == null || productCodes.isEmpty()) {
			return productList;
		}
		
		PreparedStatement productStm = null;
		
		try {
			String sql = "SELECT * FROM PRODOTTO WHERE CODICE = ?";
			
			for(int i = 1; i < productCodes.size(); i++) {
				sql += " OR CODICE = ?" ;
			}
			
			sql += ";";
			
			productStm = connection.prepareStatement(sql);
			
			for(int i = 0; i < productCodes.size(); i++) {
				productStm.setInt(i + 1, productCodes.get(i));
			}
			
			ResultSet productRs = productStm.executeQuery();
			
			productList = getProductListFromRs(productRs);
		}
		catch(SQLException e) {
			System.out.println("Errore: impossibile ottenere la lista di prodotti del carrello.");
			System.out.println(e.getClass().getName() + ": " + e.getMessage());
		}
		finally {
			if(productStm != null) {
				try {
					productStm.close();
				}
				catch(SQLException e) {
					System.out.println("Errore: impossibile chiudere lo statement.");
					System.out.println(e.getClass().getName() + ": " + e.getMessage());
				}
				finally {
					Connector.releaseConnection(connection);
				}
			}
		}

		return productList;
	}
	
	/**
	 * Restituisce il numero di prodotti presenti nel database che non sono stati marcati per l'eliminazione
	 * e con almeno una unità disponibile.
	 * 
	 * NOTA D'USO: se è necessario anche ottenere anche la lista -completa- dei
	 * prodotti disponibili è meglio chiamare il metodo getAvailableProductList e poi invocare il metodo
	 * size() sulla lista così ottenuta.
	 */
	public int getAvailableProductListSize() {
		int productListSize = 0;
		Statement countStm = null;
		
		try {
			countStm = connection.createStatement();
			String sql = "SELECT COUNT(*) FROM PRODOTTO WHERE ELIMINATO = 0 AND UNITADISPONIBILI > 0;";
			ResultSet countRs = countStm.executeQuery(sql);
			
			if(countRs.next()) {
				productListSize = countRs.getInt(1);
			}
		}
		catch(SQLException e) {
			System.out.println("Errore: impossibile contare il numero di prodotti disponibili.");
			System.out.println(e.getClass().getName() + ": " + e.getMessage());
		}
		finally {
			if(countStm != null) {
				try {
					countStm.close();
				}
				catch(SQLException e) {
					System.out.println("Errore: impossibile chiudere lo statement.");
					System.out.println(e.getClass().getName() + ": " + e.getMessage());
				}
				finally {
					Connector.releaseConnection(connection);
				}
			}
		}

		return productListSize;
	}
	
	/**
	 * Marca un prodotto come eliminato. I dati del prodotto non vengono eliminati in modo
	 * permanenete per consentire la visualizzione dei dattagli degli ordini che contengono
	 * un tale prodotto. Viene aggiornata la data di eliminazione.
	 * 
	 * @param productCode codice del prodotto da marcare come eliminato
	 */
	public void markProductAsDeleted(int productCode) throws SQLException {
		PreparedStatement productSt = null;
		String productSql = "UPDATE PRODOTTO SET ELIMINATO = ?, DATAELIMINAZIONE = ? WHERE CODICE = ?;";
		
		try{
			productSt = connection.prepareStatement(productSql);
			productSt.setBoolean(1, true);
			productSt.setString(2, LocalDateTime.now().toString());
			productSt.setInt(3, productCode);
			productSt.executeUpdate();
		}
		catch(SQLException sqle){
			System.out.println("Product deletion failed.\n");
			System.out.println(sqle.getClass().getName() + ": " + sqle.getMessage());
		}
		finally{
			if(productSt != null){
				productSt.close();
			}
			Connector.releaseConnection(connection);
		}
	}
	
	/**
	 * Elimina definitivamente un prodotto dal database. Questa operazione rende non pi�
	 * reperibili i dati di un prodotto per la consultazione di un elenco di acquisti.
	 * 
	 * @param productCode codice del prodotto da eliminare
	 */
	public void deleteProduct(int productCode) throws SQLException {
		PreparedStatement productSt = null;
		PreparedStatement compositionSt = null;
		String productSql = "DELETE FROM PRODOTTO WHERE CODICE = ?;";
		String compositionSql = "DELETE FROM COMPOSIZIONE WHERE CODICEPRODOTTO = ?;";
		/*
		 * E' necessario eliminare prima i dati dalla tabella di composizione e poi il prodotto
		 * per rispettare il vincolo di chiave esterna. Il tutto deve essere gestito come transazione.
		 */
		try{
			connection.setAutoCommit(false); // INIZIO DELLA TRANSAZIONE
			
			compositionSt = connection.prepareStatement(compositionSql);
			compositionSt.setInt(1, productCode);
			compositionSt.executeUpdate();
			
			productSt = connection.prepareStatement(productSql);
			productSt.setInt(1, productCode);
			productSt.executeUpdate();
			
			connection.commit(); // FINE DELLA TRANSAZIONE
		}
		catch(SQLException sqle){
			System.out.println("Product deletion failed.\n");
			System.out.println(sqle.getClass().getName() + ": " + sqle.getMessage());
			try{
				connection.rollback();
			}
			catch(SQLException sqle2){
				System.out.println("Failed to rollback after unsuccessful product deletion.\n");
				System.out.println(sqle2.getClass().getName() + ": " + sqle2.getMessage());
			}
		}
		finally{
			if(compositionSt != null){
				compositionSt.close();
			}
			if(productSt != null){
				productSt.close();
			}
			connection.setAutoCommit(true);
			Connector.releaseConnection(connection);
		}
	}
	
	/*
	 * I seguenti metodi sono per lo più metodi di utilità che riutilizzano quelli già creati
	 */
	
	/**
	 * Restituisce una lista completa di prodotti disponibili, ovvero quelli
	 * per cui il numero di unità disponibili è diverso da 0.
	 *
	 * @return lista dei prodotti disponibili
	 */
	public List<Product> getAvailableProductList() {
		List<Product> productList = this.getFullProductList();
		for(Product p : productList){
			if(p.getAvailableUnits() == 0)
				productList.remove(p);
		}
		return productList;
	}
	
	
	/**
	 * Riceve un oggetto di tipo ResultSet con dati provenienti dalla tabella PRODOTTO e li
	 * combina con dati sui materiali costituenti per generare una lista dei prodotti.
	 * 
	 * @param productRs il ResultSet risultante dall'interrogazione della tabella PRODOTTO
	 * @return lista dei prodotti comprensivi dei propri materiali costituienti
	 */
	private List<Product> getProductListFromRs(ResultSet productRs) throws SQLException  {
		/*
		 * Preleviamo il contenuto della tabella COMPOSIZIONE e lo inseriamo in una
		 * struttura associativa in cui la chiave è un codice prodotto (univoco) e
		 * e il valore è la lista dei materiali di cui il prodotto è composto.
		 */
		Statement compositionStm = null;
		ResultSet compositionRs = null;
		Map<Integer,List<String>> materialsMap = new HashMap<Integer,List<String>>();
		try{
			compositionStm = connection.createStatement();
			String sql = "SELECT * FROM COMPOSIZIONE;";
			compositionRs = compositionStm.executeQuery(sql);
			
			while(compositionRs.next()){
				Integer codiceProdotto = compositionRs.getInt("CODICEPRODOTTO");
				if(materialsMap.containsKey(codiceProdotto)){
					materialsMap.get(codiceProdotto).add(compositionRs.getString("MATERIALE"));
				}
				else{
					ArrayList<String> newMaterialList = new ArrayList<String>();
					newMaterialList.add(compositionRs.getString("MATERIALE"));
					materialsMap.put(compositionRs.getInt("CODICEPRODOTTO"), newMaterialList);
				}
			}
			compositionStm.close(); // Chiude anche il ResultSet compositionRs
		}
		catch(SQLException sqle){
			System.out.println("DB issue: failed to retrive product data from database.\n");
			System.out.println(sqle.getClass().getName() + ": " + sqle.getMessage());
		}
		
		/*
		 * Produciamo la lista dei prodotti.
		 */
		List<Product> productList = new ArrayList<Product>();
		while(productRs.next()){
			Product p = new Product();
			p.setCode(productRs.getInt("CODICE")); //AUTOBOXING
			p.setName(productRs.getString("NOMEPRODOTTO"));
			p.setLine(productRs.getString("LINEA"));
			p.setType(productRs.getString("TIPO"));
			p.setImages(productRs.getString("IMMAGINI"));
			p.setAvailableUnits(productRs.getInt("UNITADISPONIBILI"));
			/*
			 * getInt() returns:
			 * the column value; if the value is SQL NULL, the value returned is 0
			 */
			p.setMinInventory(productRs.getInt("SCORTAMINIMA")); // 0 di default
			p.setPrice(productRs.getBigDecimal("PREZZOVENDITA"));
			p.setCost(productRs.getBigDecimal("PREZZOACQUISTO"));
			/*
			 * SQLite non ha un tipo esplicito per le date. Il metodo getTimeStamp()
			 * non riesce ad interprestare la data correttamente. Si utilizza
			 * il metodo getString() anche se ci� non rappresenta la migliore pratica.
			 */
			p.setInsertDate(LocalDateTime.parse(productRs.getString("DATAINSERIMENTO")));
			p.setDescription(productRs.getString("DESCRIZIONE"));
			p.setDeleted(productRs.getBoolean("ELIMINATO"));
			
			// LocalDateTime.parse(null) or LocalDateTime.parse("") would throw an exception
			if(productRs.getString("DATAELIMINAZIONE") != null && !productRs.getString("DATAELIMINAZIONE").equals("")) {
				p.setDeletionDate(LocalDateTime.parse(productRs.getString("DATAELIMINAZIONE")));
			}
			
			/*
			 * Preleviamo dalla struttura dati associativa la lista dei materiali
			 * di fabbricazione per il corrente prodotto e aggiorniamo il campo
			 * relativo del bean "Product".
			 */
			try{
				p.setMaterials(materialsMap.get(productRs.getInt("CODICE")));
			}
			catch(ClassCastException cce){
				System.out.println("Map issue: key not comparable.\n");
				System.out.println(cce.getClass().getName() + ": " + cce.getMessage());
			}
			catch(NullPointerException npe){
				System.out.println("Map issue: invalid Key.\n");
				System.out.println(npe.getClass().getName() + ": " + npe.getMessage());
			}
			
			/*
			 * Aggiungiamo il prodotto alla lista.
			 */
			productList.add(p);
		}
		
		return productList;
	}

	/**
	 * Permette l'inserimento di un prodotto.
	 *
	 * - I prodotti il cui bean reca un 'codice' null oppure un valore non già presente
	 *   nel database vengono inseriti come nuovi prodotti (i primi con un codice
	 *   automatico).
	 * - Per prodotti il cui bean reca un 'codice' già presente nel database viene
	 *   aggiornato soltanto il numero di unità.
	 *
	 * @param product prodotto da inserire nel database
	 */
	public void insertProduct(Product product) throws SQLException {
		List<Product> oneProductList = new ArrayList<Product>();
		oneProductList.add(product);
		this.insertProductList(oneProductList);
	}
	
	/**
	 * Elimina definitivamente un prodotto dal database. Questa operazione rende non pi�
	 * reperibili i dati di un prodotto per la consultazione di un ordine.
	 * 
	 * Se ne consiglia l'utilizzo per i soli prodotti che non saranno pi� disponibili e
	 * che non appaiono in nessun ordine.
	 * 
	 * @param p prodotto da eliminare
	 */
	public void deleteProduct(Product p) throws SQLException {
		if(p.getCode() == null)
			throw new InvalidProductCodeException("Product code is null.");
		this.deleteProduct(p.getCode());
	}
	
	/**
	 * Ricerca i prodotti in base a una lista di stringhe di ricerca.
	 */
	public List<Product> searchProduct(List<String> tokens) throws SQLException {
		if(tokens.size() == 0) {
			return new ArrayList<Product>();
		}
		
		/*
		 * Costruiamo una query di ricerca per un insieme di prodotti che esibiscono uno dei
		 * token o parte di esso in uno dei campi nome, tipo, linea, materiali o descrizione.
		 */
		String searchSql = "SELECT CODICE" +
			" FROM PRODOTTO LEFT JOIN COMPOSIZIONE" +
			" ON PRODOTTO.CODICE = COMPOSIZIONE.CODICEPRODOTTO" +
			" WHERE (NOMEPRODOTTO LIKE '%'||?||'%' OR LINEA LIKE '%'||?||'%' OR TIPO LIKE '%'||?||'%' OR DESCRIZIONE LIKE '%'||?||'%' OR MATERIALE LIKE '%'||?||'%')"; // Per il primo token

		for(int i = 1; i < tokens.size(); i++) {
			searchSql += " AND (NOMEPRODOTTO LIKE '%'||?||'%' OR LINEA LIKE '%'||?||'%' OR TIPO LIKE '%'||?||'%' OR DESCRIZIONE LIKE '%'||?||'%' OR MATERIALE LIKE '%'||?||'%')"; // Per uno dei successivi
		}
		searchSql += " GROUP BY CODICE;";
		
		/*
		 * SQLite non supporta lo OUTER JOIN ma solo lo INNER JOIN. Pertanto non abbiamo potuto includere nella
		 * ricerca i materiali costituenti i prodotti poiché i prodotti per i quali non viene specificato un
		 * materiale costituente vengono tagliati fuori.
		 * 
		 * TODO - Preleva i codici prodotto dalla tabella COMPOSIZIONE dove trovi i token di ricerca
		 * fai una intersezione dei codici prodotto con quelli ricavati dalla tabella PRODOTTO precedentemente restituiti.
		 */
		
		/*
		 * Per ogni token si verifica che esso appaia nel nome, nella lina, nel tipo
		 * nel materiale o nella descrizione
		 */
		PreparedStatement searchStm = connection.prepareStatement(searchSql);
		
		for(int i = 0; i < tokens.size(); i++) {
			String token = tokens.get(i);
			
			searchStm.setString( (i * 5) + 1, token);
			searchStm.setString( (i * 5) + 2, token);
			searchStm.setString( (i * 5) + 3, token);
			searchStm.setString( (i * 5) + 4, token);
			searchStm.setString( (i * 5) + 5, token);
		}
		
		ResultSet productCodesRs = searchStm.executeQuery();
		List<Integer> productCodesList = new ArrayList<Integer>();
		while(productCodesRs.next()) {
			productCodesList.add(productCodesRs.getInt(1));
		}
		
		return getProductListByCode(productCodesList);
	}
	
	/**
	 * Genera una lista di prodotti le cui unità disponibili sono inferiori alla scorta
	 * minima prevista. La lista include i prodotti con un numero di unità nullo.
	 * 
	 * @return lista di prodotti
	 */
	public List<Product> report1() {
		/*
		 * La lista dei prodotti da restituire.
		 */
		List<Product> productList = new ArrayList<Product>();
		
		/*
		 * Preleviamo il contenuto della tabella PRODOTTO.
		 */
		Statement productStm = null;
		ResultSet productRs = null;
		
		try{
			productStm = connection.createStatement();
			String sql = "SELECT * FROM PRODOTTO WHERE UNITADISPONIBILI < SCORTAMINIMA;";
			productRs = productStm.executeQuery(sql);
		}
		catch(SQLException sqle){
			System.out.println("DB issue: failed to retrive product data from database.");
			System.out.println(sqle.getClass().getName() + ": " + sqle.getMessage());
		}
		
		try{
			productList = this.getProductListFromRs(productRs);
			productStm.close(); // Chiude anche il ResultSet productRs
			Connector.releaseConnection(connection);
		}
		catch(SQLException sqle){
			System.out.println("ResultSet issue 2: failed to retrive data from ResultSet.\n");
			System.out.println(sqle.getClass().getName() + ": " + sqle.getMessage());
		}
		
		return productList;
	}
	
	/**
	 * Genera una lista di prodotti in base a una fascia di prezzo indicata dai parametri
	 * della richiesta 'min' e 'max'
	 * 
	 * @return lista di prodotti
	 */
	public List<Product> report2(double min, double max) {

		List<Product> productList = new ArrayList<Product>();
		PreparedStatement productStm = null;
		
		try {
			String sql = "SELECT * FROM PRODOTTO WHERE PREZZOVENDITA >= ? AND PREZZOVENDITA <= ?;";
			productStm = connection.prepareStatement(sql);
			productStm.setDouble(1, min);
			productStm.setDouble(2,  max);
			ResultSet productRs = productStm.executeQuery();
			
			productList = getProductListFromRs(productRs);
		}
		catch(SQLException e) {
			System.out.println("Errore: impossibile ottenere la lista di prodotti nella fascia di prezzo indicata.");
			System.out.println(e.getClass().getName() + ": " + e.getMessage());
		}
		finally {
			if(productStm != null) {
				try {
					productStm.close();
				}
				catch(SQLException e) {
					System.out.println("Errore: impossibile chiudere lo statement.");
					System.out.println(e.getClass().getName() + ": " + e.getMessage());
				}
				finally {
					Connector.releaseConnection(connection);
				}
			}
		}

		return productList;
	}
}
