package com.mh.model.database;

import java.sql.*;

/**
 * Crea le tabelle nel database dell'applicazione web. Il database viene creato,
 * sotto forma di file, automaticamente all'atto del caricamento della classe
 * Connector. Il codice SQL è ottimizzato per SQLite.
 *
 * @author Enrico Sbrighi
 */
public final class Manager{

	/* 
	 * Qualsiasi campo dichiarato INTEGER PRIMARY KEY in SQLite
	 * è un alias per la pseudocolonna rowid
	 *
	 * AUTOINCREMENT is to prevent the reuse of ROWIDs from
	 * previously deleted rows.
	 */

	private Connection connection = null;

	public Manager(){
		connection = Connector.getConnection();
	}
	
	/**
	 * Crea le tabelle nel file del database e inserisce le informazioni minime
	 * necessarie per l'accesso al sito come gestore.
	 */
	public void initDatabase() throws SQLException {
		
		Statement statement = connection.createStatement();
		String sql = null;
		
		connection.setAutoCommit(false); // INIZIO DELLA TRANSAZIONE
		
		//TABELLA TRACCIAUTENTE
		sql =
		"CREATE TABLE TRACCIAUTENTE" +
		"(" +
			
			"ID						INTEGER			PRIMARY KEY," +
			"DATAREGISTRAZIONE		DATETIME		NOT NULL," +
			"DATAULTIMOACCESSO		DATETIME				," +
			"CANCELLATO				BOOLEAN					," +
			"DATACANCELLAZIONE		DATETIME				 " +
		");";
		statement.executeUpdate(sql);
		
		// TABELLA UTENTE
		sql =
		"CREATE TABLE UTENTE" +
		"(" +
			"CF						VARCHAR(16)		PRIMARY KEY," +
			"NOME					VARCHAR(32)		NOT NULL," +
			"SECONDONOME			VARCHAR(32)				," +
			"COGNOME				VARCHAR(32)		NOT NULL," +
			"SESSO					VARCHAR(6)		NOT NULL," +
			"MAIL					VARCHAR(32)		NOT NULL," +
			"TELEFONO				CHAR(10)				," +
			"DATANASCITA			DATETIME		NOT NULL," +
			"CITTANASCITA			VARCHAR(64)		NOT NULL," +
			"CARTACREDITO			VARCHAR(16)				," +
			"FOTOGRAFIA				VARCHAR(64)				," +
			"IDUTENTE				INTEGER			NOT NULL," + // ASSOCIAZIONE PERMANENZA
			"ACCOUNTUSERNAME		VARCHAR(32)		NOT NULL," + // ASSOCIAZIONE POSSESSO
			"FOREIGN KEY(IDUTENTE) REFERENCES TRACCIAUTENTE(ID)," +
			"FOREIGN KEY(ACCOUNTUSERNAME) REFERENCES ACCOUNT(USERNAME)" +
		");";
		statement.executeUpdate(sql);
		
		// TABELLA ACCOUNT
		sql =
		"CREATE TABLE ACCOUNT" +
		"(" +
			"USERNAME				VARCHAR(32)		PRIMARY KEY," +
			"PASSWORD				VARCHAR(32)		NOT NULL," +
			"CONFERMATO				BOOLEAN			NOT NULL," +
			"GESTORE				BOOLEAN			NOT NULL " +
		");";
		statement.executeUpdate(sql);
		
		//TABELLA INDIRIZZO
		sql =
		"CREATE TABLE INDIRIZZO" +
		"(" +
			"IDINDIRIZZO		INTEGER				PRIMARY KEY," +
			"PROVINCIA			VARCHAR(16)			NOT NULL," +
			"COMUNE				VARCHAR(64)			NOT NULL," +
			"CAP				VARCHAR(5)			NOT NULL," +
			"VIAPIAZZA			VARCHAR(32)			NOT NULL," +
			"CIVICO				VARCHAR(5)			NOT NULL," +
			"TIPOLOGIA			VARCHAR(10)			NOT NULL," + //ASSOCIAZIONE UBICAZIONE - VALORI: RESIDENZA O SPEDIZIONE
			"IDUTENTE			INTEGER				NOT NULL," + //ASSOCIAZIONE UBICAZIONE
			"FOREIGN KEY(IDUTENTE) REFERENCES TRACCIAUTENTE(ID)" +
		");";
		statement.executeUpdate(sql);
		
		//TABELLA ORDINE
		sql =
		"CREATE TABLE ORDINE" +
			"(" +
			"NUMERO					INTEGER			PRIMARY KEY," +
			"DATA					DATETIME		NOT NULL," +
			"TOTALE					DECIMAL(3,2)	NOT NULL," +
			"IDUTENTE				INTEGER			NOT NULL," + //ASSOCIAZIONE COMMESSA
			"INDIRIZZO				INTEGER			NOT NULL," + //ASSOCIAZIONE SPEDIZIONE
			"TIPOSPEDIZIONE			VARCHAR(64)				," + //ASSOCIAZIONE SPEDIZIONE
			"STATOSPEDIZIONE		VARCHAR(16)				," + //ASSOCIAZIONE SPEDIZIONE
			"FOREIGN KEY(IDUTENTE) REFERENCES TRACCIAUTENTE(ID)," +
			"FOREIGN KEY(INDIRIZZO) REFERENCES INDIRIZZO(IDINDIRIZZO)" +
		");";
		statement.executeUpdate(sql);
		
		//TABELLA PRODOTTO
		sql =
		"CREATE TABLE PRODOTTO" +
			"(" +
			"CODICE					INTEGER			PRIMARY KEY," +
			"NOMEPRODOTTO			VARCHAR(32)				," +
			"LINEA					VARCHAR(8)		NOT NULL," +
			"TIPO					VARCHAR(32)		NOT NULL," +
			"IMMAGINI				INTEGER					," +
			"UNITADISPONIBILI		INTEGER			NOT NULL," +
			"SCORTAMINIMA			INTEGER					," +
			"PREZZOVENDITA			DECIMAL(3,2)	NOT NULL," +
			"PREZZOACQUISTO			DECIMAL(3,2)			," +
			"DATAINSERIMENTO		DATETIME		NOT NULL," +
			"DESCRIZIONE			VARCHAR(256)			," +
			"ELIMINATO				BOOLEAN			NOT NULL," +
			"DATAELIMINAZIONE		DATETIME				 " +
		");";
		statement.executeUpdate(sql);
		
		//TABELLA MATERIALE
		sql =
		"CREATE TABLE MATERIALE" +
			"(" +
			"DENOMINAZIONE			VARCHAR(32)			PRIMARY KEY" +
		");";
		statement.executeUpdate(sql);
		
		//ASSOCIAZIONE PAGAMENTO
		sql =
		"CREATE TABLE PAGAMENTO" +
			"(" +
			"NUMEROORDINE			INTEGER			PRIMARY KEY," +
			"IDUTENTE				INTEGER			NOT NULL," +
			"MODALITAPAGAMENTO		VARCHAR(32)				," +
			"STATOPAGAMENTO			VARCHAR(32)				," +
			"DATAPAGAMENTO			DATETIME				," +
			"FOREIGN KEY(NUMEROORDINE) REFERENCES ORDINE(NUMERO)," +
			"FOREIGN KEY(IDUTENTE) REFERENCES TRACCIAUTENTE(ID) " +
		");";
		statement.executeUpdate(sql);
		
		//ASSOCIAZIONE ACQUISTO
		sql =
		"CREATE TABLE ACQUISTO" +
			"(" +
			"NUMEROORDINE			INTEGER			NOT NULL," +
			"CODICEPRODOTTO			INTEGER			NOT NULL," +
			"QUANTITA				INTEGER			NOT NULL," +
			"PREZZOORIGINALE		DECIMAL(3,2)	NOT NULL," +
			"PRIMARY KEY(NUMEROORDINE, CODICEPRODOTTO)," +
			"FOREIGN KEY(NUMEROORDINE) REFERENCES ORDINE(NUMERO)," +
			"FOREIGN KEY(CODICEPRODOTTO) REFERENCES PRODOTTO(CODICE)" +
		");";
		statement.executeUpdate(sql);
		
		//ASSOCIAZIONE COMPOSIZIONE
		sql =
		"CREATE TABLE COMPOSIZIONE" +
			"(" +
			"CODICEPRODOTTO			INTEGER			NOT NULL," +
			"MATERIALE				VARCHAR(32)		NOT NULL," +
			"PRIMARY KEY(CODICEPRODOTTO, MATERIALE)," +
			"FOREIGN KEY(CODICEPRODOTTO) REFERENCES PRODOTTO(CODICE)," +
			"FOREIGN KEY(MATERIALE) REFERENCES MATERIALE(DENOMINAZIONE)" +
		");";
		statement.executeUpdate(sql);
		connection.commit();
		
		//POPOLAMENTO UTENTI: GESTORE DEL SITO
		sql =
		"INSERT INTO TRACCIAUTENTE(ID, DATAREGISTRAZIONE, DATAULTIMOACCESSO," +
		"CANCELLATO) VALUES " +
		"(1,'2017-07-21T00:00:00','2017-07-21T00:00:00',0);";
		statement.executeUpdate(sql);
		
		sql =
		"INSERT INTO ACCOUNT(USERNAME, PASSWORD, CONFERMATO, GESTORE) VALUES " +
		"('mariahandmade','password',1,1);";
		statement.executeUpdate(sql);
		
		sql =
		"INSERT INTO UTENTE(CF, NOME, COGNOME, SESSO, MAIL, TELEFONO, DATANASCITA," +
		"CITTANASCITA, FOTOGRAFIA, IDUTENTE, ACCOUNTUSERNAME) VALUES " +
		"('SBRMRA84B67A489V','Maria','Sbrighi', 'female','mariasbrighi@libero.it','3249843284','1984-02-27'," +
		"'Atripalda','01.png',1,'mariahandmade');";
		statement.executeUpdate(sql);
		
		sql =
		"INSERT INTO INDIRIZZO (IDINDIRIZZO, PROVINCIA, COMUNE, CAP, VIAPIAZZA," +
		"CIVICO, TIPOLOGIA, IDUTENTE) VALUES " +
		"(1, 'AV', 'Sorbo Serpico', '83050', 'Via Padula', '39', 'Both', 1);";
		statement.executeUpdate(sql);
		
		connection.commit(); // FINE DELLA TRANSAZIONE
		
		/*
		 * La connessione è condivisa quindi al termine della transazione
		 * bisogna reimpostare il commit automatico.
		 */
		connection.setAutoCommit(true);
		
		statement.close();
		Connector.releaseConnection(connection);
	}
	
	/**
	 * Inserisce dei dati di prova per verificare le funzionlità del sito web
	 */
	public void populateDatabase() throws SQLException {
		Statement statement = connection.createStatement();
		String sql;
		
		connection.setAutoCommit(false); // INIZIO DELLA TRANSAZIONE
	
		//POPOLAMENTO UTENTI: UTENTE DI PROVA
		sql =
		"INSERT INTO TRACCIAUTENTE(ID, DATAREGISTRAZIONE, DATAULTIMOACCESSO," +
		"CANCELLATO) VALUES " +
		"(5,'2017-07-21T00:00:00','2017-07-21T00:00:00',0);";
		statement.executeUpdate(sql);
		
		sql =
		"INSERT INTO ACCOUNT(USERNAME, PASSWORD, CONFERMATO, GESTORE) VALUES " +
		"('mariorossi','password',0,0);";
		statement.executeUpdate(sql);
		
		sql =
		"INSERT INTO UTENTE(CF, NOME, COGNOME, 'SESSO', MAIL, TELEFONO, DATANASCITA," +
		"CITTANASCITA, CARTACREDITO, FOTOGRAFIA, IDUTENTE, ACCOUNTUSERNAME) VALUES " +
		"('RSSMRA85T10A562S','Mario', 'Rossi', 'male', 'mariorossi@fakemail.com','0123456789','1975-03-08'," +
		"'Roma','0101 0101 0101 0101','02.png',5,'mariorossi');";
		statement.executeUpdate(sql);
		
		sql =
		"INSERT INTO INDIRIZZO (IDINDIRIZZO, PROVINCIA, COMUNE, CAP, VIAPIAZZA," +
		"CIVICO, TIPOLOGIA, IDUTENTE) VALUES " +
		"(3, 'MI', 'Milano', '20144', 'Via Tortona', '42', 'Residenza', 5);";
		statement.executeUpdate(sql);
		
		sql =
		"INSERT INTO INDIRIZZO (IDINDIRIZZO, PROVINCIA, COMUNE, CAP, VIAPIAZZA," +
		"CIVICO, TIPOLOGIA, IDUTENTE) VALUES " +
		"(4, 'MI', 'Milano', '20144', 'Via Savona', '25', 'Spedizione', 5);";
		statement.executeUpdate(sql);
		
		//POPOLAMENTO TABELLA MATERIALI
		sql =
		"INSERT INTO MATERIALE(DENOMINAZIONE) " +
		"VALUES " +
		"('altro')," +
		"('fimo')," +
		"('pietre dure')," +
		"('swarovski')," +
		"('wired');";
		statement.executeUpdate(sql);
		
		//POPOLAMENTO TABELLA PRODOTTI E COMPOSIZIONE
		sql =
		"INSERT INTO PRODOTTO (CODICE, NOMEPRODOTTO, LINEA, TIPO, IMMAGINI, UNITADISPONIBILI, SCORTAMINIMA, " +
		"PREZZOVENDITA, PREZZOACQUISTO, DATAINSERIMENTO, DESCRIZIONE, ELIMINATO) " +
		"VALUES " +
		"(1, 'Summer', 'uomo', 'orecchino', '01.png', 3, 3, 3.5, 0, '2017-07-21T00:00:00', 'Orecchini uomo.', 0)," +
		"(2, 'Daemia', 'donna', 'bracciale', '02.png', 12, 10, 4.0, 0, '2017-07-21T00:00:00', 'Bracciale donna.', 0)," +
		"(3, 'Handy', 'unisex', 'collana', '03.png', 11, 0, 4.7, 0, '2017-07-21T00:00:00', 'Collana unisex.', 0)," +
		"(4, 'Mortgage', 'donna', 'bomboniera', '04.png', 1, 0, 299.99, 0, '2017-07-21T00:00:00', 'Bomboniera donna.', 0)," +
		"(5, 'Royal', 'donna', 'partecipazione', '05.png', 17, 5, 2.30, 0, '2017-07-21T00:00:00', 'Partecipazione.', 0)," +
		"(6, 'Criteria', 'unisex', 'biglietto', '06.png', 100, 50, 1.20, 0, '2017-07-21T00:00:00', 'Biglietto.', 0)," +
		"(7, 'Dana', 'donna', 'anello', '07.png', 7, 5, 3.0, 0, '2017-07-21T00:00:00', 'Anello donna.', 0)," +
		"(8, 'Sarge', 'uomo', 'ciondolo', '08.png', 23, 10, 2.0, 0, '2017-07-21T00:00:00', 'Ciondolo uomo.', 0)," +
		"(9, 'Visor', 'uomo', 'anello', '09.png', 8, 5, 5.5, 0, '2017-07-21T00:00:00', 'Anello uomo.', 0)," +
		"(10, 'Lucy', 'donna', 'orecchino', '10.png', 0, 5, 4.45, 0, '2017-07-21T00:00:00', 'Orecchini donna.', 0)," +
		"(11, 'Blue', 'unisex', 'ciondolo', '11.png', 41, 20, 3.10, 0, '2017-07-21T00:00:00', 'Ciondolo unisex.', 0)," +
		"(12, 'Xaero', 'uomo', 'orecchino', '12.png', 11, 5, 9.90, 0, '2017-07-21T00:00:00', 'Orecchino uomo.', 0)," +
		"(13, 'Moon', 'donna', 'collana', '13.png', 3, 0, 2.5, 0, '2017-07-21T00:00:00', 'Collana donna.', 0)," +
		"(14, 'Western', 'unisex', 'partecipazione', '14.png', 58, 30, 3.40, 0, '2017-07-21T00:00:00', 'Partecipazione.', 0)," +
		"(15, 'Fresh', 'donna', 'bracciale', '15.png', 14, 10, 12.30, 0, '2017-07-21T00:00:00', 'Bracciale donna.', 0)," +
		"(16, 'Klesk', 'uomo', 'portachiave', '16.png', 7, 9, 7.20, 0, '2017-07-21T00:00:00', 'Portachiavi uomo.', 0)," +
		"(17, 'Mino', 'uomo', 'collana', '17.png', 4, 3, 3.0, 0, '2017-07-21T00:00:00', 'Collana uomo', 0)," +
		"(18, 'Iuppiter', 'donna', 'anello', '18.png', 12, 5, 2.60, 0, '2017-07-21T00:00:00', 'Anello donna.', 0)," +
		"(19, 'Goodbye', 'unisex', 'biglietto', '19.png', 54, 25, 1.50, 0, '2017-07-21T00:00:00', 'Biglietto.', 0)," +
		"(20, 'Peter', 'uomo', 'portachiavi', '20.png', 0, 0, 3.0, 0, '2017-07-21T00:00:00', 'Portachiavi uomo.', 0);";
		statement.executeUpdate(sql);
		
		sql =
		"INSERT INTO COMPOSIZIONE (CODICEPRODOTTO, MATERIALE) " +
		"VALUES " +
		"(1, 'fimo')," +
		"(1, 'wired')," +
		"(2, 'swarovski')," +
		"(3, 'wired')," +
		"(3, 'swarovski')," +
		"(3, 'altro')," +
		"(5, 'fimo')," +
		"(6, 'wired')," +
		"(6, 'altro')," +
		"(7, 'pietre dure')," +
		"(8, 'swarovski')," +
		"(9, 'pietre dure')," +
		"(9, 'altro')," +
		"(10, 'fimo')," +
		"(10, 'pietre dure')," +
		"(10, 'wired')," +
		"(11, 'wired')," +
		"(14, 'pietre dure')," +
		"(15, 'wired')," +
		"(16, 'wired')," +
		"(16, 'altro')," +
		"(17, 'swarovski')," +
		"(18, 'fimo')," +
		"(19, 'pietre dure')," +
		"(20, 'pietre dure')," +
		"(20, 'altro')," +
		"(20, 'swarovski');";
		statement.executeUpdate(sql);
		
		connection.commit(); // FINE DELLA TRANSAZIONE
		
		/*
		 * La connessione è condivisa quindi al termine della transazione
		 * bisogna reimpostare il commit automatico.
		 */
		connection.setAutoCommit(true);
		
		statement.close();
		Connector.releaseConnection(connection);
	}
	
	/**
	 * Elimina tutte le tabelle presenti nel database ma non elimina il file del
	 * database.
	 */
	public void dropDatabaseTables() throws SQLException {
		/*
		 * Disabilitiamo momentaneament i vincoli di chiave esterna per eliminare
		 * le tabelle più agevolmente.
		 */
		Connector.setEnforceForeignKeys(false);
		
		Statement statement = connection.createStatement();
		String sql = null;

		connection.setAutoCommit(false); //INIZIO DELLA TRANSAZIONE
		
		sql = "DROP TABLE COMPOSIZIONE;";
		statement.executeUpdate(sql);
		
		sql = "DROP TABLE MATERIALE;";
		statement.executeUpdate(sql);
		
		sql = "DROP TABLE ACQUISTO;";
		statement.executeUpdate(sql);
		
		sql = "DROP TABLE PRODOTTO;";
		statement.executeUpdate(sql);
		
		sql = "DROP TABLE PAGAMENTO;";
		statement.executeUpdate(sql);
		
		sql = "DROP TABLE ORDINE;";
		statement.executeUpdate(sql);
		
		sql = "DROP TABLE UTENTE;";
		statement.executeUpdate(sql);
		
		sql = "DROP TABLE INDIRIZZO;";
		statement.executeUpdate(sql);
		
		sql = "DROP TABLE TRACCIAUTENTE;";
		statement.executeUpdate(sql);
		
		sql = "DROP TABLE ACCOUNT;";
		statement.executeUpdate(sql);
		
		connection.commit(); // FINE DELLA TRANSAZIONE
		
		/*
		 * La connessione è condivisa quindi al termine della transazione
		 * bisogna reimpostare il commit automatico.
		 */
		connection.setAutoCommit(true);
		
		/*
		 * Ora le chiavi primarie sono di nuovo operative.
		 */
		Connector.setEnforceForeignKeys(true);
		
		statement.close();
		Connector.releaseConnection(connection);
	}
}