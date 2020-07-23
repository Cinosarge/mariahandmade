package com.mh.web.gestioneMagazzino;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

import java.util.List;

import com.mh.model.gestioneMagazzino.DBProduct;
import com.mh.model.javaBeans.Product;

/**
 * Si occupa di caricare nello scope della richiesta i dati sui prodotti. Dopo il caricamento
 * effettua il Request Dispatching verso una risorsa che può essere indicata alternativmente
 * 
 * 1. come parametro della richiesta (se questa Servlet viene raggiunta con una richiesta GET oppure POST)
 * 2. come attributo della richiesta (se questa Servlet viene raggiunta a sua volta con un Request Dispatching)
 * 
 * Deve essere specificato obbligatoriamente uno dei due. Se entrambi sono specificati viene data precedenza al
 * parametro della richiesta e l'attributo viene ignorato.
 */
public class FetchProducts extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		
		response.setContentType("text/html");
		DBProduct productAPI = new DBProduct();
		
		int offset = 0;
		int limit = productAPI.getAvailableProductListSize();
		
		List<Product> productListBuffer = productAPI.getAvailableProductList(offset, limit);
		request.setAttribute("productListBuffer", productListBuffer);
		
		/*
		 * Request dispatch verso la risorsa indicata come parametro della richiesta.
		 */
		String destinationParameter = request.getParameter("forwardDestination");
		
		/*
		 * Request dispatch verso la risorsa indicata come attributo della richiesta.
		 */
		String destinationAttribute = (String) request.getAttribute("forwardDestination");
		
		RequestDispatcher wrapper = request.getRequestDispatcher(destinationParameter != null ? destinationParameter : destinationAttribute);
		wrapper.forward(request, response);
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		doGet(request, response);
	}
}
