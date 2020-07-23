package com.mh.model.gestioneClienti;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

import com.mh.model.database.*;
import com.mh.model.javaBeans.Account;
import com.mh.model.javaBeans.Address;
import com.mh.model.javaBeans.RegisteredUser;

public class DBUser {
	Connection connection = null;
	
	public DBUser() {
		connection = Connector.getConnection();
	}
	
	/**
	 * Verifica l'esistenza, nel database, di un utente con username e password corrispondenti.
	 * Questo metodo viene utilizzato per effettuare il login.
	 * 
	 * @param username lo username in input
	 * @param password la password in input
	 * @return tutti i dati dell'utente se esiste, altrimenti null
	 * @throws SQLException
	 */
	public RegisteredUser getRegisteredUser(String username, String password)
			throws SQLException {
		RegisteredUser registeredUser = null;
		Account account = null;
		Address address = null;
		
		PreparedStatement userSt = null;
		ResultSet userRs = null;
		
		String userSQL = "SELECT * FROM" +
				" ACCOUNT JOIN UTENTE JOIN" +
				" TRACCIAUTENTE JOIN INDIRIZZO" +
				" ON" +
				" ACCOUNT.USERNAME = UTENTE.ACCOUNTUSERNAME AND" +
				" UTENTE.IDUTENTE = TRACCIAUTENTE.ID AND" +
				" TRACCIAUTENTE.ID = INDIRIZZO.IDUTENTE" +
				" WHERE ACCOUNT.USERNAME = ? AND ACCOUNT.PASSWORD = ?";
		
		try {
			userSt = connection.prepareStatement(userSQL);
			userSt.setString(1, username);
			userSt.setString(2, password);
			userRs = userSt.executeQuery();
			
			//login fallito
			if(!userRs.next())
				return null;
			
			registeredUser = new RegisteredUser();
			
			//campi del bean RegisteredUser ereditati dal bean User
			registeredUser.setUserID(userRs.getInt("IDUTENTE"));
			registeredUser.setRegistrationDate(LocalDateTime.parse(userRs.getString("DATAREGISTRAZIONE")));
			if(userRs.getString("DATAULTIMOACCESSO") != null)
				registeredUser.setLastAccessDate(LocalDateTime.parse(userRs.getString("DATAULTIMOACCESSO")));
			
			//campi del bean RegisteredUser
			registeredUser.setFiscalCode(userRs.getString("CF"));
			registeredUser.setName(userRs.getString("NOME"));
			registeredUser.setSecondName(userRs.getString("SECONDONOME"));
			registeredUser.setSurname(userRs.getString("COGNOME"));
			registeredUser.setGender(userRs.getString("SESSO"));
			registeredUser.setMail(userRs.getString("MAIL"));
			registeredUser.setPhone(userRs.getString("TELEFONO"));
			registeredUser.setBirthDate(LocalDate.parse(userRs.getString("DATANASCITA")));
			registeredUser.setBirthCity(userRs.getString("CITTANASCITA"));
			registeredUser.setCreditCard(userRs.getString("CARTACREDITO"));
			registeredUser.setPhoto(userRs.getString("FOTOGRAFIA"));
			
			//campi del bean Account
			account = new Account();
			account.setUsername(userRs.getString("USERNAME"));
			account.setPassword(userRs.getString("PASSWORD"));
			account.setChecked(userRs.getBoolean("CONFERMATO"));
			account.setManager(userRs.getBoolean("GESTORE"));
			
			//campi del (dei) bean indirizzo
			address = new Address();
			address.setAddressID(userRs.getInt("IDINDIRIZZO"));
			address.setProvince(userRs.getString("PROVINCIA"));
			address.setCity(userRs.getString("COMUNE"));
			address.setCap(userRs.getString("CAP"));
			address.setStreet(userRs.getString("VIAPIAZZA"));
			address.setHouseNumber(userRs.getString("CIVICO"));
			address.setAddressType(userRs.getString("TIPOLOGIA"));
			
			ArrayList<Address> addressList = new ArrayList<Address>();
			addressList.add(address);
			registeredUser.setAccount(account);
			registeredUser.setAddressList(addressList);
			
			//l'utente ha più indirizzi
			while(userRs.next()){
				address = new Address();
				address.setAddressID(userRs.getInt("IDINDIRIZZO"));
				address.setProvince(userRs.getString("PROVINCIA"));
				address.setCity(userRs.getString("COMUNE"));
				address.setCap(userRs.getString("CAP"));
				address.setStreet(userRs.getString("VIAPIAZZA"));
				address.setHouseNumber(userRs.getString("CIVICO"));
				address.setAddressType(userRs.getString("TIPOLOGIA"));
				addressList.add(address);
			}
		}
		catch(SQLException sqle) {
			System.out.println("Error while querying database to find a user.");
			System.out.println(sqle.getClass().getName()+ ": " + sqle.getMessage());
		}
		finally{
			if(userSt != null)
				userSt.close();
			Connector.releaseConnection(connection);
		}
		
		return registeredUser;
	}
	
	/**
	 * In caso di errore nell'inserimento è preferibile riportare l'eccezione al componente che utilizzerà
	 * questo metodo; tuttavia è opportuna la gestione del rollback in caso di transazione fallita in
	 * questa parte del codice. Il rollback viene quindi gestito in questa posizione e dopo la gestione
	 * l'eccezione viene lanciata nuovamente per una ulteriore gestione.
	 * 
	 * @param user
	 * @throws SQLException
	 */
	public void insertUser(RegisteredUser user) throws SQLException {
		PreparedStatement userTraceSt = null;
		PreparedStatement userSt = null;
		PreparedStatement accountSt = null;
		PreparedStatement addressSt = null;
		
		String userTraceSql = "INSERT INTO TRACCIAUTENTE (DATAREGISTRAZIONE, DATAULTIMOACCESSO, CANCELLATO, DATACANCELLAZIONE) " +
				"VALUES(?,?,?,?);";
		String userSql = "INSERT INTO UTENTE (CF, NOME, SECONDONOME, COGNOME, SESSO, MAIL, TELEFONO, DATANASCITA, CITTANASCITA, " +
				"CARTACREDITO, FOTOGRAFIA, IDUTENTE, ACCOUNTUSERNAME) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?);";
		String accountSql = "INSERT INTO ACCOUNT (USERNAME, PASSWORD, CONFERMATO, GESTORE) VALUES (?,?,?,?);";
		String addressSql = "INSERT INTO INDIRIZZO (PROVINCIA, COMUNE, CAP, VIAPIAZZA, CIVICO, TIPOLOGIA, IDUTENTE) " +
				"VALUES (?,?,?,?,?,?,?)";
		
		try{
			connection.setAutoCommit(false); // INIZIO TRANSAZIONE
			
			userTraceSt = connection.prepareStatement(userTraceSql);
			userSt = connection.prepareStatement(userSql);
			accountSt = connection.prepareStatement(accountSql);
			addressSt = connection.prepareStatement(addressSql);
			
			//Table TRACCIAUTENTE
			userTraceSt.setString(1, (user.getRegistrationDate() != null) ? user.getRegistrationDate().toString() : null );
		    userTraceSt.setString(2, (user.getLastAccessDate() != null) ? user.getLastAccessDate().toString() : null);
			userTraceSt.setBoolean(3, false);
			userTraceSt.setString(4, null);
			userTraceSt.executeUpdate();
			
			//Ricaviamo l'identificatore della "traccia utente"
			Integer userTraceID = null;
			Statement lastRowIdSt = connection.createStatement();
			ResultSet lastRowIdRs = lastRowIdSt.executeQuery("SELECT last_insert_rowid()");
			if(lastRowIdRs.next())
				userTraceID = lastRowIdRs.getInt(1);
			lastRowIdSt.close();
			
			//Table ACCOUNT
			accountSt.setString(1, user.getAccount().getUsername());
			accountSt.setString(2, user.getAccount().getPassword());
			accountSt.setBoolean(3, user.getAccount().isChecked());
			accountSt.setBoolean(4, user.getAccount().isManager());
			accountSt.executeUpdate();
			
			//Table UTENTE
			userSt.setString(1, user.getFiscalCode());
			userSt.setString(2, user.getName());
			userSt.setString(3, user.getSecondName()); //not null check not to do
			userSt.setString(4, user.getSurname());
			userSt.setString(5, user.getGender());
			userSt.setString(6, user.getMail());
			userSt.setString(7, user.getPhone()); // not null check not to do
			userSt.setString(8, (user.getBirthDate() != null) ? user.getBirthDate().toString() : null);
			userSt.setString(9, user.getBirthCity());
			userSt.setString(10, user.getCreditCard()); // not null check not to do
			userSt.setString(11, (user.getPhoto() != "") ? user.getPhoto() : null); // not null check not to do
			userSt.setInt(12, userTraceID);
			userSt.setString(13, user.getAccount().getUsername());
			userSt.executeUpdate();
			
			//Table INDIRIZZO
			for(Address address : user.getAddressList()) {
				addressSt.setString(1, address.getProvince());
				addressSt.setString(2, address.getCity());
				addressSt.setString(3, address.getCap());
				addressSt.setString(4, address.getStreet());
				addressSt.setString(5, address.getHouseNumber());
				addressSt.setString(6, address.getAddressType());
				addressSt.setInt(7, userTraceID);
				addressSt.executeUpdate();
			}
			
			connection.commit(); // FINE TRANSAZIONE
		}
		catch(SQLException sqle) {
			System.out.println("DB issue: failed to register a new user.");
			System.out.println(sqle.getClass().getName() + ": " + sqle.getMessage());
			
			connection.rollback();
			
			throw sqle; // Facciamo comunque risalire l'errore
		}
		finally{
			if(userTraceSt != null)
				userTraceSt.close();
			if(userSt != null)
				userSt.close();
			if(accountSt != null)
				accountSt.close();
			if(addressSt != null)
				addressSt.close();
			
			connection.setAutoCommit(true); // RIPRISTINO AUTOCOMMIT SULLA CONNESSIONE
			Connector.releaseConnection(connection);
		}
	}
	
	/**
	 * Restituisce la lista degli utenti registrati con le sole informazioni su
	 * USERNAME, NOME, COGNOME, MAIL, TELEFONO
	 *
	 * @return List<RegisteredUser> la lista degli utenti registrati.
	 */
	public List<RegisteredUser> getRegisteredUserList() {
		/*
		 * La lista degli utenti registrati da restituire.
		 */
		List<RegisteredUser> registeredUserList = new ArrayList<RegisteredUser>();
		
		/*
		 * Preleviamo il contenuto dello INNER JOIN delle tabelle
		 * - TRACCIA UTENTE
		 * - UTENTE
		 * - ACCOUNT
		 * con la condizione per cui ACCOUNT.CANCELLATO sia pari a false (0)
		 */
		Statement userStm = null;
		ResultSet userRs = null;
		try{
			userStm = connection.createStatement();
			String sql = "SELECT ID, USERNAME, NOME, COGNOME, MAIL, TELEFONO FROM" +
					" TRACCIAUTENTE INNER JOIN ACCOUNT INNER JOIN UTENTE" +
					" ON TRACCIAUTENTE.ID = UTENTE.IDUTENTE" +
					" AND UTENTE.ACCOUNTUSERNAME = ACCOUNT.USERNAME" +
					" WHERE TRACCIAUTENTE.CANCELLATO = 0;";
			userRs = userStm.executeQuery(sql);
		}
		catch(SQLException sqle){
			System.out.println("DB issue: failed to retrive user data from database.");
			System.out.println(sqle.getClass().getName() + ": " + sqle.getMessage());
		}
		
		/*
		 * Produciamo la lista degli utenti registrati.
		 */
		try{
			while(userRs.next()){
				RegisteredUser regUser = new RegisteredUser();
				regUser.setUserID(userRs.getInt("ID"));
				regUser.setName(userRs.getString("NOME"));
				regUser.setSurname(userRs.getString("COGNOME"));
				regUser.setMail(userRs.getString("MAIL"));
				regUser.setPhone(userRs.getString("TELEFONO"));
				
				Account account = new Account();
				account.setUsername(userRs.getString("USERNAME"));
				regUser.setAccount(account);
				
				//Aggiungiamo l'utente alla lista.
				registeredUserList.add(regUser);
			}
			/*
			 * Fine della fiera.
			 */
			userStm.close(); // Chiude anche il ResultSet userRs
			Connector.releaseConnection(connection);
		}
		catch(SQLException sqle){
			System.out.println("ResultSet issue: failed to retrive data from ResultSet.");
			System.out.println(sqle.getClass().getName() + ": " + sqle.getMessage());
		}
		return registeredUserList;
	}
	
	/**
	 * Lo username viene utilizzato come chiave primaria della tabella ACCOUNT e per questa ragione
	 * non può essere duplicato. Si noti che due username U1 e U2 vengono considerati uguali se
	 * è valida la seguente condizione
	 * 
	 * U1.toLowerCase().equals(U2.toLowerCase())
	 * 
	 * @param username lo username per il quale si intende effettuare il controllo di esistenza
	 * @return true se lo username esiste già nel database, false altrimenti
	 */
	public boolean usernameExists(String username) throws SQLException {
		String searchSql = "SELECT * FROM ACCOUNT WHERE USERNAME = ?";
		PreparedStatement searchStm = null;
		try {
			searchStm = connection.prepareStatement(searchSql);
			searchStm.setString(1, username);
			ResultSet searchRs = searchStm.executeQuery();
			
			if(searchRs.next()) {
				return true;
			}
		}
		catch(SQLException e) {
			System.out.println("Impossibile ottenere informazioni dal server.");
			System.out.println(e.getClass().getName() + ": " + e.getMessage());
			
			throw new SQLException();
		}
		finally{
			if(searchStm != null)
				searchStm.close();
			Connector.releaseConnection(connection);
		}
		
		return false;
	}
}
