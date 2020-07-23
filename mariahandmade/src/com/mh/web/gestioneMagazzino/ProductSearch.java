package com.mh.web.gestioneMagazzino;

import java.io.*;
import java.sql.SQLException;

import javax.servlet.*;
import javax.servlet.http.*;

import com.mh.model.gestioneMagazzino.DBProduct;
import com.mh.model.javaBeans.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * I Clienti e i Gestori possono cercare i prodotti in modalità automatica (query di ricerca)
 * oppure possono effettuare una ricerca manuale.
 * 
 * Nel caso di ricerca automatica possono cercare i prodotti per mezzo di informazioni
 * anche parziali, su nome, tipo, linea, materiali, descrizione
 * 
 * Nel caso di ricerca manuale possono cercare i prodotti per mezzo di informazioni
 * dettagliate su
 * - tipo
 * - linea
 * - materiali
 * - fascia di prezzo
 * 
 * I Gestori possono cercare i prodotti anche
 * - per data di inserimento
 * - prodotti marcati come eliminati
 * 
 * Dopo la ricerca la lista di prodotti così ottenuta viene impostata come attributo
 * nello scope della richiesta.
 */
public class ProductSearch extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		response.setContentType("text/html");
		response.sendError(403); // FORBIDDEN
	}
	
	/**
	 * Nel caso di ricerca automatica i parametri della richiesta sono
	 * - il parametro 'type' che assume il valore 'auto'
	 * - il parametro 'query' che contiene la stringa con le informazioni da cercare
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		
		String searchType = request.getParameter("type");
		String searchQuery = request.getParameter("query");
		
		if(searchType.equals("auto")) {
			StringTokenizer tokenizer = new StringTokenizer(searchQuery);
			List<String> tokenList = new ArrayList<String>();
			while(tokenizer.hasMoreTokens()) {
				String token = tokenizer.nextToken();
				tokenList.add(token);
			}
			DBProduct productAPI = new DBProduct();
			List<Product> productsFound = null;
			
			try {
				productsFound = productAPI.searchProduct(tokenList);
			}
			catch(SQLException e) {
				System.out.println("Impossibile ottenere un risultato di ricerca");
				System.out.println(e.getClass().getName() + ": " + e.getMessage());
			}
			
			RequestDispatcher showSearch = request.getRequestDispatcher("SearchResult.jsp");
			request.setAttribute("searchResult", productsFound);
			showSearch.forward(request, response);
			
			return;
		}
		
		
	}
}
