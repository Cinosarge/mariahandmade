package com.mh.web.autenticazione;

import java.io.*;
import java.sql.SQLException;
import javax.servlet.*;
import javax.servlet.http.*;

import com.mh.model.gestioneClienti.DBUser;
import com.mh.model.javaBeans.RegisteredUser;

/**
 * Oltre al semplice servizio di login si occupa anche di gestire il redirect alla pagina opportuna,
 * anche nel caso in cui ci sia una registrazione pre-acquisto.
 */
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest request, HttpServletResponse response) 
				throws ServletException, IOException {
		request.setAttribute("errorMessage", "Metodologia di accesso non valida.");
		request.getRequestDispatcher("Login.jsp").forward(request,response);
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) 
				throws ServletException, IOException {
		
		HttpSession session = request.getSession(false);
		response.setContentType("text/html");
		
		if(session == null || session.getAttribute("userData") == null) {
			/*
			 * utente registrato o non registrato senza sessione corrente
			 * oppure con sessione corrente non autenticata (solo carrello)
			 */
			String username = request.getParameter("username");
			String password = request.getParameter("password");
			
			/*
			 * Request dispatch ricevuto da signin.do. Lo username e la password sono
			 * presenti come attributi nello scope della richeista.
			 */
			if(username == null && password == null) {
				username = (String) request.getAttribute("username");
				password = (String) request.getAttribute("password");
			}
			
			RegisteredUser user = null;
			HttpSession authSession = null;
			
			DBUser userAPI = new DBUser();
			try {
				user = userAPI.getRegisteredUser(username, password);
			}
			catch(SQLException sqle){
				System.out.println("Errore nella verifica utente durante il login.");
				System.out.println(sqle.getClass().getName() + ": " + sqle.getMessage());
				response.sendError(500);//500 - Internal Server Error
			}
			
			if(user == null) {
				/*
				 * Il meccanismo per i messaggi di errore va rivisto e uniformato con quello delle notifiche.
				 * 
				 * Si arriva in questo ramo se il login fallisce oppure se il fallisce in seguito al fallimento
				 * della appenna avvenuta registrazione. Nel caso della registrazione il flusso delle pagine
				 * non è particolamente chiaro. Potresti prevedere qui un attributo 'loginAfterSignin' che renderebbe
				 * le cose più chiare con un redirect alla pagina di registrazione (magari con una notifica dell'errore).
				 */
				request.setAttribute("errorMessage", "Le credenzili non sono corrette, riprova.");
				request.getRequestDispatcher("Login.jsp").forward(request,response);
			}
			else {
				authSession = request.getSession();
				authSession.setAttribute("userData", user);
				
				if(user.getAccount().isManager()) {
					response.sendRedirect(response.encodeRedirectURL("AdminHome.jsp"));
				}
				else if(session.getAttribute("checkoutAfterSignin")!= null && session.getAttribute("checkoutAfterSignin").equals("true")) {
					RequestDispatcher checktoutView = request.getRequestDispatcher("checkout.do");
					session.removeAttribute("checkoutAfterSignin");
					checktoutView.forward(request, response);
				}
				else {
					// La servlet Welcome.java
					response.sendRedirect(response.encodeRedirectURL("/mariahandmade/"));
				}
			}
		}
		else{
			/*
			 * if(session != null && session.getAttribute("userData") != null)
			 * ovvero utente registrato con sessione autenticata (login ripetuto)
			 */
			response.sendRedirect(response.encodeRedirectURL("/mariahandmade/"));
		}
	}
}