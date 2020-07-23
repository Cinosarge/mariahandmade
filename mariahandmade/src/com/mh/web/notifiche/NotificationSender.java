package com.mh.web.notifiche;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

/**
 * Si presuppone che la richiesta HTTP sia stata originariamente generata attraverso AJAX
 * e che sia stata ricevuta da una Servlet di servizio che poi ha delegato (dispatch della
 * richista) alla presente servlet il compito di inviare una notifica come risposta asincrona.
 * 
 * Nella richiesta devono essere presenti tre attributi
 * 
 * 1. errorOccurred - servirà a javascript per determinare il tipo di notifica
 * 2. message - la notifica vera e propria
 * 3. details - eventuali dettagli, opzionali
 * 
 * Il messggio di risposta verrà quindi intercettato dallo script da cui originariamente è
 * partita la richiesta asincrona.
 */
public class NotificationSender extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		doPost(request, response);
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		/*
		 * Il formato della risposta xml deve essere il seguente
		 * 
		 * <notification>
		 *   <errorOccurred>true/false</errorOccurred>
		 *   <message>Some text</message>
		 *   <details>Some details</details>
		 * </notification>
		 * 
		 * TODO - Forse è più elegante usare JDOM anche se non è necessario
		 */
		
		boolean errorOccurred = (boolean) request.getAttribute("errorOccurred"); // AUTO UNBOXING
		String message = (String) request.getAttribute("message");
		String details = (String) request.getAttribute("details");
		
		StringBuffer responseXML = new StringBuffer();
		responseXML.append("<?xml version=\"1.0\" encoding=\"utf-8\" ?>");
		responseXML.append("<notification>");
		responseXML.append("<errorOccurred>" + errorOccurred + "</errorOccurred>");
		responseXML.append("<message>" + message + "</message>");
		responseXML.append("<details>" + details + "</details>");
		responseXML.append("</notification>");
		
		response.setContentType("text/xml");
		
		PrintWriter out = response.getWriter();
		out.print(responseXML);
	}
}
