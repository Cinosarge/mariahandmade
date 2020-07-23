package com.mh.web.gestioneMagazzino;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.*;
import javax.servlet.http.*;

import com.mh.model.gestioneMagazzino.DBProduct;
import com.mh.model.javaBeans.Cart;
import com.mh.model.javaBeans.Product;

/**
 * I dati dei prodotti vengono caricati solo quando si visualizza per la prima volta
 * il carrello e vengono conservati nello scope della sessione per permettere il
 * prelievo del prezzo in fase di checkout.
 * 
 * TODO - Il prezzo di un oggetto è definito al momento in cui viene visualizzato il
 * carrello. Potremmo prevedere un componente che tiene allineati i dati tra il database
 * e la sessione avvisando eventualmente l'utente di una modifica avvenuta ad un prodotto
 * che sta acquistando.
 * 
 * @author Enrico
 *
 */
public class FetchCartProducts extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		
		HttpSession session = request.getSession(false);
		Cart cart = null;
		
		if(session != null) {
			cart = (Cart) session.getAttribute("cart");
		}
		
		/*
		 * Carichiamo nel carrello tutti i dati dei prodotti che figurano in esso solo con codice e
		 * quantità richiesta.
		 */
		if(cart != null && cart.getCartProductMap() != null && !cart.getCartProductMap().isEmpty()) {
			// Produco una lista di chiavi
			Set<Integer> keySet = cart.getCartProductMap().keySet();
			List<Integer> productCodes = new ArrayList<Integer>();
			for(Integer i : keySet) {
				productCodes.add(i);
			}
			
			// Ottengo la mappa dei prodotti nel carrello
			DBProduct productAPI = new DBProduct();
			List<Product> productList = productAPI.getProductListByCode(productCodes);
			
			/*
			 * Aggiorno la mappa dei prodotti nel carrello. Nota che a questo punto nel carrello
			 * sono solo presenti i codici dei prodotti e la quantità richiesta ma non i dati
			 * dei prodotti.
			 */
			for(Product product : productList) {
				/*
				 * Il campo "deal" (quantità richiesa di un determinato prodotto) è già impostato
				 * negli elementi CartProduct nel carrello ma va controllato e per questo è
				 * necessario invocare il metodo
				 * 
				 * Cart.putProductData(Product p)
				 * 
				 * che si occupa anche di controllare che la quantità richesta di un determinato
				 * prodotto sia compatibilecon le scorte disponibili.
				 */
				cart.putProductData(product);
			}
		}
		
		RequestDispatcher cartPage = request.getRequestDispatcher("Cart.jsp");
		cartPage.forward(request, response);
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		doGet(request, response);
	}
}