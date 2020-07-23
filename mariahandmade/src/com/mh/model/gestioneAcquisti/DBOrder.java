package com.mh.model.gestioneAcquisti;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mh.model.database.Connector;
import com.mh.model.javaBeans.Order;
import com.mh.model.javaBeans.Product;
import com.mh.model.javaBeans.Purchase;

/**
 * Fornisce una API di accesso al database per la parte relativa
 * agli acquisti, cioè gli ordini effettuati e aggiorna i dati
 * relativi alle scorte dei prodotti.
 */
public class DBOrder {
	
	Connection connection = null;
	
	public DBOrder() {
		connection = Connector.getConnection();
	}
	
	/**
	 * Riceve in input un ordine e lo memorizza nel database. La memorizzazione di un ordine
	 * deve essere eseguita come una operazione atomica (transazione). Nella transazione sono
	 * inclusi anche gli aggiornamenti delle unità disponibili dei prodotti acquistati.
	 * 
	 * @param order L'ordine da memorizzare nel database.
	 * @throws SQLException
	 */
	public void insertOrder(Order order) throws SQLException {
		// STATEMENT PER L'AGGIORNAMENTO DELLA TABELLA ORDINE
		String orderSql = "INSERT INTO ORDINE (DATA, TOTALE, IDUTENTE, INDIRIZZO, TIPOSPEDIZIONE, STATOSPEDIZIONE)" + 
				" VALUES (?, ?, ?, ?, ?, ?);";
		
		PreparedStatement orderStm = connection.prepareStatement(orderSql);
		orderStm.setString(1, LocalDateTime.now().toString());
		orderStm.setBigDecimal(2, order.getTotal());
		orderStm.setInt(3, order.getUserID());
		orderStm.setInt(4, order.getAddressID());
		orderStm.setString(5, order.getDeliveryType());
		orderStm.setString(6, order.getDeliveryState());
		
		// STATEMENT PER L'AGGIORNAMENTO DELLA TABELLA PAGAMENTO
		String paymentSql = "INSERT INTO PAGAMENTO (NUMEROORDINE, IDUTENTE, MODALITAPAGAMENTO, STATOPAGAMENTO, DATAPAGAMENTO)" + 
				" VALUES(?, ?, ?, ?, ?);";
		
		PreparedStatement paymentStm = connection.prepareStatement(paymentSql);
		paymentStm.setInt(2, order.getPayment().getUserID());
		paymentStm.setString(3, order.getPayment().getPaymentType());
		paymentStm.setString(4, order.getPayment().getPaymentState());
		paymentStm.setString(5, LocalDateTime.now().toString()); // La data dell'ordine è quella calcolata in questo istante.
		
		// STATEMENT PER L'AGGIORNAMENTO DEGLI ACQUISTI
		String purchaseSql = "INSERT INTO ACQUISTO (NUMEROORDINE, CODICEPRODOTTO, QUANTITA, PREZZOORIGINALE)" + 
				" VALUES(?, ?, ?, ?);";
		
		PreparedStatement purchaseStm = connection.prepareStatement(purchaseSql);
		
		// STATEMENT PER L'AGGIORNAMENTO DELLA TABELLA PRODOTTO
		String updateUnitsSql = "UPDATE PRODOTTO" +
				" SET UNITADISPONIBILI = CASE CODICE";
		
		for(int i = 0; i < order.getPurchaseList().size() ; i++) {
			updateUnitsSql += " WHEN ? THEN UNITADISPONIBILI - ?";
		}
		
		updateUnitsSql += " ELSE UNITADISPONIBILI" +
		" END;";
		
		PreparedStatement updateUnitsStm = connection.prepareStatement(updateUnitsSql);
		
		// FINE DELLA PREPARAZIONE DEGLI STATEMENT
		
		connection.setAutoCommit(false); // INIZIO TRANSAZIONE
		
		orderStm.executeUpdate();
		
		/*
		 * Otteniamo il numero dell'ordine appena inserito per poterlo riutilizzare
		 * quando inseriamo gli acquisti.
		 */
		Integer orderNumber = null;
		
		Statement generatedKeySt = connection.createStatement();
		ResultSet generatedKeys = generatedKeySt.executeQuery("SELECT last_insert_rowid()"); // NON SOGGEETTA ALLA TRANSAZIONE
		if (generatedKeys.next()) {
			orderNumber = generatedKeys.getInt(1);
		}
		generatedKeySt.close();
		
		/*
		 * Ora possiamo usare il numero dell'ordine per inserire le informazioni sul
		 * pagamento
		 */
		paymentStm.setInt(1, orderNumber);
		paymentStm.executeUpdate();
		
		/*
		 * E possiamo usare il numero d'ordine anche per inserire tutti gli acquisti
		 * che rientrano in questo particolare ordine.
		 */
		for(Purchase p : order.getPurchaseList()) {
			purchaseStm.setInt(1, orderNumber);
			purchaseStm.setInt(2, p.getProductCode());
			purchaseStm.setInt(3, p.getUnits());
			purchaseStm.setDouble(4, p.getOriginalPrice().doubleValue());
			
			purchaseStm.executeUpdate();
		}
		
		/*
		 * Decrementiamo il numero di prodotti acquistati dalle scorte disponibili
		 */
		int param = 1;
		for(Purchase p : order.getPurchaseList()) {
			updateUnitsStm.setInt(param, p.getProductCode());
			updateUnitsStm.setInt(param + 1, p.getUnits());
			param += 2;
		}
		
		updateUnitsStm.executeUpdate();
		
		connection.commit(); // FINE TRANSAZIONE
		
		connection.setAutoCommit(true);
		
		if(orderStm != null) {
			orderStm.close();
		}
		
		if(paymentStm != null) {
			paymentStm.close();
		}
		
		if(purchaseStm != null) {
			purchaseStm.close();
		}
		
		Connector.releaseConnection(connection);
	}
	
	/**
	 * Restituisce la lista degli ordini effettuati da un cliente, ognuno dei quali accompagnato
	 * dalla data, dalla lista degli oggetti acquistati, dal prezzo originale di ciasun oggetto
	 * ed il totale della spesa.
	 */
	public Map<Integer, Order> getOrderList(String userid) {
		// La chiave è il numero dell'ordine, il valore è l'ordine
		Map<Integer, Order> orderMap = new HashMap<Integer, Order>();
		
		String sql = "SELECT TOTALE, ORDINE.NUMERO, ORDINE.'DATA', QUANTITA, CODICE, PREZZOORIGINALE, NOMEPRODOTTO" +
		" FROM TRACCIAUTENTE INNER JOIN ORDINE INNER JOIN ACQUISTO INNER JOIN PRODOTTO" +
		" ON TRACCIAUTENTE.ID = ORDINE.IDUTENTE" +
		" AND ORDINE.NUMERO = ACQUISTO.NUMEROORDINE" +
		" AND ACQUISTO.CODICEPRODOTTO = PRODOTTO.CODICE" +
		" WHERE TRACCIAUTENTE.ID = ?;";
		
		PreparedStatement ordersStm = null;
		ResultSet rs = null;
		
		try {
			ordersStm = connection.prepareStatement(sql);
			ordersStm.setString(1, userid);
			rs = ordersStm.executeQuery();
			
			while(rs.next()) {
				Order order = null;
				
				// Un nuovo ordine viene aggiunto alla struttura associativa
				if(!orderMap.containsKey(rs.getInt(2))) {
					order = new Order();
					order.setOrderNumber(rs.getInt(2));
					order.setData(LocalDateTime.parse(rs.getString(3)));
					order.setTotal(BigDecimal.valueOf(rs.getDouble(1)));
					order.setUserID(Integer.parseInt(userid));
					orderMap.put(rs.getInt(2), order);
					
					List<Purchase> purchaseList = new ArrayList<Purchase>();
					order.setPurchaseList(purchaseList);
				}
				else {
					// Otteniamo un riferimento all'ordine esistente
					order = orderMap.get(rs.getInt(2)); //AUTOBOXING
				}
				
				Purchase purchase = new Purchase();
				purchase.setOrderNumber(rs.getInt(2));
				purchase.setProductCode(rs.getInt(5));
				purchase.setUnits(rs.getInt(4));
				purchase.setOriginalPrice(BigDecimal.valueOf(rs.getDouble(6)));
				
				Product product = new Product();
				product.setName(rs.getString(7));
				
				purchase.setProduct(product);
				
				order.getPurchaseList().add(purchase);
			}
		}
		catch(SQLException sqle) {
			System.out.println("Issue: failed to retrive Orders data.");
			System.out.println(sqle.getClass().getName() + ": " + sqle.getMessage());
		}
		finally {
			if(ordersStm != null) {
				try {
					ordersStm.close();
				}
				catch(SQLException sqle) {
					System.out.println("Issue: failed to close statement.");
					System.out.println(sqle.getClass().getName() + ": " + sqle.getMessage());
				}
				finally {
					Connector.releaseConnection(connection);
				}
			}
		}
		return orderMap;
	}
	
	/**
	 * Genera una lista di ordini che non sono stati ancora evasi.
	 * 
	 * @return lista di ordini non evasi
	 */
	public Map<Integer,Order> report3() {
		// La chiave è il numero dell'ordine, il valore è l'ordine
		Map<Integer, Order> orderMap = new HashMap<Integer, Order>();
		
		String sql = "SELECT TOTALE, ORDINE.NUMERO, ORDINE.'DATA', QUANTITA, CODICE, PREZZOORIGINALE, NOMEPRODOTTO, TRACCIAUTENTE.ID" + 
		" FROM TRACCIAUTENTE INNER JOIN ORDINE INNER JOIN ACQUISTO INNER JOIN PRODOTTO INNER JOIN PAGAMENTO" + 
		" ON TRACCIAUTENTE.ID = ORDINE.IDUTENTE" + 
		" AND ORDINE.NUMERO = PAGAMENTO.NUMEROORDINE" + 
		" AND ORDINE.NUMERO = ACQUISTO.NUMEROORDINE" + 
		" AND ACQUISTO.CODICEPRODOTTO = PRODOTTO.CODICE" + 
		" WHERE STATOPAGAMENTO = 'payed' and STATOSPEDIZIONE != 'shipped';";
		
		Statement ordersStm = null;
		ResultSet rs = null;
		
		try {
			ordersStm = connection.createStatement();
			rs = ordersStm.executeQuery(sql);
			
			while(rs.next()) {
				Order order = null;
				
				// Un nuovo ordine viene aggiunto alla struttura associativa
				if(!orderMap.containsKey(rs.getInt(2))) {
					order = new Order();
					order.setOrderNumber(rs.getInt(2));
					order.setData(LocalDateTime.parse(rs.getString(3)));
					order.setTotal(BigDecimal.valueOf(rs.getDouble(1)));
					order.setUserID(rs.getInt(8));
					orderMap.put(rs.getInt(2), order);
					
					List<Purchase> purchaseList = new ArrayList<Purchase>();
					order.setPurchaseList(purchaseList);
				}
				else {
					// Otteniamo un riferimento all'ordine esistente
					order = orderMap.get(rs.getInt(2)); //AUTOBOXING
				}
				
				Purchase purchase = new Purchase();
				purchase.setOrderNumber(rs.getInt(2));
				purchase.setProductCode(rs.getInt(5));
				purchase.setUnits(rs.getInt(4));
				purchase.setOriginalPrice(BigDecimal.valueOf(rs.getDouble(6)));
				
				Product product = new Product();
				product.setName(rs.getString(7));
				
				purchase.setProduct(product);
				
				order.getPurchaseList().add(purchase);
			}
		}
		catch(SQLException sqle) {
			System.out.println("Issue: failed to retrive Orders data.");
			System.out.println(sqle.getClass().getName() + ": " + sqle.getMessage());
		}
		finally {
			if(ordersStm != null) {
				try {
					ordersStm.close();
				}
				catch(SQLException sqle) {
					System.out.println("Issue: failed to close statement.");
					System.out.println(sqle.getClass().getName() + ": " + sqle.getMessage());
				}
				finally {
					Connector.releaseConnection(connection);
				}
			}
		}
		return orderMap;
	}
}
