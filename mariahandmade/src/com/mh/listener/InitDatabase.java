package com.mh.listener;

import java.sql.SQLException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.mh.model.database.Connector;
import com.mh.model.database.Manager;

/**
 * Al primo deploy dell'applicazione (e anche per tutti i successivi) si controlla che le
 * opportune risorse (cartelle e file) siano al giusto posto nel filesystem.
 * 
 * Questo listener controlla che il database esista e sia inizializzato con i dati minimi
 * necessari a permettere al gestore del sito di effettuare l'accesso.
 * 
 * Le circostanze in cui questi dati nel database posssono mancare sono due
 * 
 * - il deploy dell'applicazione viene fatto per la prima volta
 * - il gestore del sito ha eliminato le tabelle dal suo pannello amministratore e la sua
 *   sessione è scaduta prima che potesse ricrearle.
 *   
 *   TODO - dovremmo effettivamente controllare anche che ci sia la struttura delle cartelle richiesta per far
 *   funzionare il sito. (C:\enricosbrighi\.. eccetera) oltre che il database
 */
public class InitDatabase implements ServletContextListener {

	/**
     * Controlliamo che il database contenga una tabella TRACCIAUTENTE e che in questa
     * vi sia almeno una entry. Se questo non avviene o il file del database non è
     * stato mai creato oppure non ha tabelle.
     */
    public void contextInitialized(ServletContextEvent servletContextEvent) {
    	/*
    	 * TODO - Controlla che C:\sbrighienrico esista - qui si suppone
    	 * semplicemente che già ci sia
    	 */
    	
		try {
			if(Connector.isEmptyDatabase()) {
				Manager manager = new Manager();
				manager.initDatabase();
			}
	    	
		}
		catch(SQLException e) {
			System.out.println("Errore nell'inizializzazione del database.");
			System.out.println(e.getClass().getName() + ": " + e.getMessage());
		}
    }
	
	/**
     * 
     */
    public void contextDestroyed(ServletContextEvent arg0) { 
         // Not much to do here, you know
    }
}
