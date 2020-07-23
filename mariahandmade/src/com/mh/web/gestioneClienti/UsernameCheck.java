package com.mh.web.gestioneClienti;

import java.io.*;
import java.sql.SQLException;

import javax.servlet.*;
import javax.servlet.http.*;

import com.mh.model.gestioneClienti.DBUser;

/**
 * Riceve un parametro 'username' e controlla che non sia già esistente. Delega alla Servlet di notifica
 * il compito di comunicare l'esito del controllo.
 * 
 * @author Enrico Sbrighi
 *
 */
public class UsernameCheck extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		
		boolean errorOccurred = false;
		String message = null;
		String details = null;
		
		String username = request.getParameter("username");
		DBUser userAPI = new DBUser();
		try {
			if(userAPI.usernameExists(username)) {
				errorOccurred = true;
				message = "Lo username esiste già!";
			}
			else {
				errorOccurred = false;
				message = "Username valido";
			}
		}
		catch(SQLException e) {
			errorOccurred = true;
			message = "Errore del server";
			details = "Problema d'accesso al database per la verifica dello username";
			
			System.out.println(message);
			System.out.println(e.getClass().getName() + ": " + e.getMessage());
		}
		
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
