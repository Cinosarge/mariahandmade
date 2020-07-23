package com.mh.web.database;

import java.io.*;
import java.sql.SQLException;

import javax.servlet.*;
import javax.servlet.http.*;

import com.mh.model.database.Manager;
import com.mh.model.javaBeans.RegisteredUser;

/**
 * Viene intercettata una richiesta GET con parametro "operation".
 * Il parametro "operation" può avere un valore tra i senguenti:
 * - create
 * - populate
 * - destroy
 * e viene effettuata la corrispondente operazione se l'utente che
 * la ha richiesta è un gestore del sito.
 * @author Enrico
 *
 */
public class ManagerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		
		boolean errorOccurred = false;
		String message = "";
		String details = "";
		
		/*
		 * Controlliamo che l'utente sia abilitato all'intervento diretto sul database.
		 */
		HttpSession session = request.getSession(false);
		RegisteredUser userData = null;
		
		if(session != null) {
			userData = (RegisteredUser) session.getAttribute("userData");
		}
		
		if(userData != null && userData.getAccount().isManager()) {
			String operation = request.getParameter("operation");
			Manager databaseManager = new Manager();
			
			try {
				switch (operation) {
				case "create" :
					databaseManager.initDatabase();
					break;
				case "populate" :
					databaseManager.populateDatabase();
					break;
				case "destroy" :
					databaseManager.dropDatabaseTables();
					break;
				default :
					response.sendError(422); // UNPROCESSABLE ENTITY - Manca il parametro GET
				}
				
				message = "Tutto ok.";
				details = "Operazione '" + operation + "' effettuata con successo";
			}
			catch(SQLException e) {
				errorOccurred = true;
				message = "Si &egrave; verificato un errore.";
				details = e.getClass().getName() + ": " + e.getMessage();
				System.out.println(details);
			}
		}
		else {
			/*
			 * Non possiamo inviare una pagina di errore 403 (forbidden) perché
			 * stiamo comunicando con AJAX. Invieremo comunque una notifica per
			 * indiare all'utente che o non ha l'autorizzazione per eseguire
			 * questa operazione oppure la sua sessione da gestore è scaduta.
			 */
			errorOccurred = true;
			message = "Permesso negato.";
			details = "La tua sessione è scaduta oppure non sei un amministratore.";
		}
		
		/*
		 * Forward della richeista verso il servizio di notifica.
		 */
		RequestDispatcher notificationService = request.getRequestDispatcher("notify.do");
		
		request.setAttribute("errorOccurred", errorOccurred);
		request.setAttribute("message", message);
		request.setAttribute("details", details);
		
		notificationService.forward(request, response);
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		//
	}
}
