package com.mh.web.gestioneMagazzino;

import java.io.*;
import java.sql.SQLException;

import javax.servlet.*;
import javax.servlet.http.*;

import com.mh.model.gestioneMagazzino.DBProduct;
import com.mh.model.javaBeans.RegisteredUser;

/**
 * Ci si aspetta un parametro 'productCode' contenente il codice del prodotto da eliminare.
 * 
 * La richiesta di marcatura di un prodotto come eliminato arriva a questo componenete in maniera
 * asincrona e pertanto questo componente fa uso del servizio di notifica standard per avvisare
 * dell'esito dell'operazione (Request Dispatching verso NotificationSender).
 * 
 * @author Enrico Sbrighi
 */
public class DeleteProduct extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// Parametri per l'invio della notifica
	boolean errorOccurred;
	String message;
	String details;
	
	public void init() {
		errorOccurred = false;
		message = null;
		details = null;
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		doPost(request, response);
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		
		response.setContentType("text/html");
		
		// Controlliamo che chi accede a questa funzionalità sia un gestore del sito
		HttpSession session = request.getSession(false);
		RegisteredUser user = null;
		
		if(session != null) {
			user = (RegisteredUser) session.getAttribute("userData");
		}
		
		if(user == null || !user.getAccount().isManager()) {
			errorOccurred = true;
			message = "Permesso negato";
			details = "Prova a ripetere il login; probabilmente la sessione è scaduta.";
			sendNotification(request, response);
			return;
		}
		
		// Il codice del prodotto da eliminare
		String productCode = request.getParameter("productCode");
		Integer productCodeInt = null;
		
		if(productCode != null) {
			productCodeInt = Integer.parseInt(productCode);
		}
		else {
			errorOccurred = true;
			message = "Nessun prodotto!";
			details = "Specificare un prodotto da marcare come eliminato.";
			sendNotification(request, response);
			return;
		}
		
		// Marchiamo il prodotto come eliminato
		DBProduct productAPI = new DBProduct();
		
		try {
			productAPI.markProductAsDeleted(productCodeInt);
			errorOccurred = false;
			message = "Perfetto!";
			details = "Il prodotto è stato marcato come eliminato.";
			
		}
		catch(SQLException e) {
			errorOccurred = true;
			message = "Errore del database!";
			details = "Non siamo riusciti a marcare un prodotto come eliminato!";
		}
		
		sendNotification(request, response);
	}
	
	/*
	 * Effettua il Request Dispatching verso il servizio di notifica. Questo metodo usa le
	 * variabili di istanza
	 * - errorOccurred
	 * - message
	 * - details
	 * Esse quindi devono essere già inizializzate al momento della chiamata a questo metodo.
	 */
	private void sendNotification(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Invio della notifica
		RequestDispatcher notificationService = request.getRequestDispatcher("/notify.do");
		request.setAttribute("errorOccurred", errorOccurred);
		request.setAttribute("message", message);
		request.setAttribute("details", details);
		notificationService.forward(request, response);
	}
}
