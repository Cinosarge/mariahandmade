package com.mh.web.autenticazione;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

import com.mh.model.javaBeans.Cart;

/**
 * La richiesta GET riceve un parametro 'option'.
 * 
 * Alla pressione del pulsante di Logout il parametro 'option' ha valore 'checkCart'.
 * Questo comporta l'invio di un messaggio per la visualizzazione di un box di scelta.
 * 
 * Alla pressione del pulsante Esci nel box di scelta il parametro 'option' ha valore
 * 'discardCart'.
 * 
 * La sessione viene automaticamente invalidata se
 * - il carrello non esiste
 * - il carrello è vuoto
 * - il parametro 'option' è uguale a 'discardCart'
 * 
 * @author Enrico
 */
public class Logout extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		HttpSession session = request.getSession(false);
		String option = request.getParameter("option");
		
		StringBuffer responseXML = new StringBuffer();
		responseXML.append("<?xml version=\"1.0\" encoding=\"utf-8\" ?>");
		
		response.setContentType("text/xml");
		
		/*
		 * Il pulsante di Logout non appare agli utenti non registrati, pertanto la sessione
		 * autenticata è scaduta prima del logout. I prodotti vengono persi.
		 */
		if(session == null) {
			responseXML.append("<operation name=\"redirect\">/mariahandmade/</operation>");
			PrintWriter out = response.getWriter();
			out.print(responseXML);
			out.flush();
			return;
		}
		
		/*
		 * Il pulsante di Logout non appare agli utenti non registrati, pertanto se la sessione
		 * c'è ma non è autenticata è scaduta prima di effettua il Logout e successivamente
		 * l'utente ha aggiunto un prodotti al carrello.
		 * 
		 * Per non perdere i prodotti non invalidiamo la sessione e ordiniamo a JS un redirect
		 * alla pagina di login.
		 */
		if(session.getAttribute("userData") == null) {
			responseXML.append("<operation name=\"redirect\">Login.jsp</operation>");
			PrintWriter out = response.getWriter();
			out.print(responseXML);
			out.flush();
			return;
		}
		
		// La sessione è presente ed autenticata
		Cart cart = (Cart) session.getAttribute("cart");
		
		if( cart != null && !cart.isEmpty() && option.equals("checkCart") ) {
			responseXML.append("<operation name=\"showDialog\"></operation>");
			PrintWriter out = response.getWriter();
			out.print(responseXML);
			out.flush();
		}
		else {
			session.invalidate();
			
			responseXML.append("<operation name=\"redirect\">/mariahandmade/</operation>");
			PrintWriter out = response.getWriter();
			out.print(responseXML);
			out.flush();
		}
		
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		this.doGet(request, response);
	}
}