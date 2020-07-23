package com.mh.web.gestioneMagazzino;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

/**
 * I report sono generati sulla base di una lista di prodotti generata dalla Servlet di
 * ricerca dei prodotti.
 */
public class ProductReport extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		
	}
	
	/**
	 * I Gestori possono ottenere i seguenti report
	 * - lista di prodotti le cui unità disponibili in magazzino sono inferiori alla scorta minima prevista
	 * - lista di prodotti in una fascia di prezzo definita dal gestore
	 * - lista degli ordini che non hanno ricevuto pagamento (^)
	 * - lista degli ordini non ancora evasi (^)
	 * 
	 * (^ da mettere in un altro motore report in gestioneAcquisti oppure centralizare la funzionalità e inserire la servlet in un package dedicato)
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		
	}
}
