package com.mh.web.report;

import java.io.*;
import java.util.List;
import java.util.Map;

import javax.servlet.*;
import javax.servlet.http.*;

import com.mh.model.gestioneAcquisti.DBOrder;
import com.mh.model.gestioneMagazzino.DBProduct;
import com.mh.model.javaBeans.Order;
import com.mh.model.javaBeans.Product;
import com.mh.model.javaBeans.RegisteredUser;

/**
 * Questo servlet riceve il parametro 'reportNumber'. I valori possibili sono
 * 
 * Valore '1': Genera una lista di prodotti le cui unità disponibili sono inferiori alla scorta
 * minima prevista. La lista include i prodotti con un numero di unità nullo.
 * 
 * Valore '2': Genera una lista di prodotti in base a una fascia di prezzo indicata dai parametri
 * della richiesta 'min' e 'max'
 * 
 * Valore '3': Genera una lista di ordini che non sono stati ancora evasi.
 * 
 * @author Enrico
 *
 */
public class ReportEngine extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		
		response.setContentType("text/html");
		
		HttpSession session = request.getSession(false);
		if(!userIsManager(session)) {
			response.sendError(403);
			return;
		}
		
		String reportNumber = request.getParameter("reportNumber");
		
		DBProduct productAPI = new DBProduct();
		DBOrder orderAPI = new DBOrder();
		List<Product> productList = null;
		Map<Integer,Order> orderMap = null;
		
		switch (reportNumber) {
		case "1" :
			productList = productAPI.report1();
			request.setAttribute("productList", productList);
			request.getRequestDispatcher("Report1.jsp").forward(request, response);
			break;
		case "2" :
			double min = Double.parseDouble(request.getParameter("min"));
			double max = Double.parseDouble(request.getParameter("max"));
			productList = productAPI.report2(min, max);
			request.setAttribute("productList", productList);
			request.getRequestDispatcher("Report2.jsp").forward(request, response);
			break;
		case "3" :
			orderMap = orderAPI.report3();
			request.setAttribute("orders", orderMap);
			request.getRequestDispatcher("Report3.jsp").forward(request, response);
			break;
		default:
			response.sendError(404); // Report non trovato
		}
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		//
	}
	
	/*
	 * Restituisce true se l'utente con una tale session è un managere false altrimenti.
	 * Il parametro può essere null.
	 */
	private boolean userIsManager(HttpSession session) {
		
		if(session == null)
			return false;
		
		RegisteredUser userData = (RegisteredUser) session.getAttribute("userData");
		
		if(userData != null && userData.getAccount().isManager())
			return true;
		
		return false;
	}
}
