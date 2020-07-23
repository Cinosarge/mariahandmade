package com.mh.web.gestioneAcquisti;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

import com.mh.model.javaBeans.Cart;

/**
 * 
 */
public class AddToCart extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		/*
		 * La sessione per un utente non registrato viene creata solo quando l'utente
		 * intende aggiungere prodotti al carrello e di conseguenza la sessione
		 * ha sempre un attributo "cart".
		 *
		 * La sessione per un utente registrato e autenticato viene creata prima dell'
		 * aggiunta di prodotti al carrello e di conseguenza la sessione potrebbe non
		 * avere un attributo "cart".
		 * 
		 * Chiaramente il carrello potrebbe già esistere quando viene aggiungo un
		 * nuovo prodotto.
		 *
		 * Se una sessione scade i dati del carrello vengono persi definitivamente.
		 */
		
		HttpSession session = request.getSession();
		Cart cart = (Cart) session.getAttribute("cart");
		
		if(cart == null) {
			cart = new Cart();
			session.setAttribute("cart", cart);
		}
		
		Integer productCode = Integer.parseInt(request.getParameter("productCode"));
		cart.putProduct(productCode);
		
		/*
		 * Facciamo il Request Dispatching della richiesta verso la servlet che gestisce le
		 * notifiche AJAX
		 */
		RequestDispatcher notificationSender = request.getRequestDispatcher("notify.do");
		
		request.setAttribute("errorOccurred", false);
		request.setAttribute("message", "Aggiunto al carrello");
		request.setAttribute("details", "Ci sono " + cart.size() + " prodotti nel carrello");
		
		notificationSender.forward(request, response);
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		doGet(request, response);
	}
}