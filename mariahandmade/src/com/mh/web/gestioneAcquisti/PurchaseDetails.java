package com.mh.web.gestioneAcquisti;

import java.io.IOException;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.mh.model.gestioneAcquisti.DBOrder;
import com.mh.model.javaBeans.Order;
import com.mh.model.javaBeans.RegisteredUser;

/**
 * Servlet implementation class PurchaseDetails
 */
public class PurchaseDetails extends HttpServlet {
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	private static final long serialVersionUID = 1L;

	
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String userID = request.getParameter("userid");
		
		DBOrder userAPI = new DBOrder();
		Map<Integer, Order> ordersMap = userAPI.getOrderList(userID);
		
		HttpSession session = request.getSession();
		RegisteredUser registeredUser = null;
		
		if(session != null) {
			registeredUser = (RegisteredUser) session.getAttribute("userData");
		}
		
		/*
		 * Verifichiamo che l'utente sia un amministratore. Se non è un amministratore
		 * controlliamo che il suo id sia lo stesso per il quale ha richiesto l'insieme delle spese.
		 */
		if(registeredUser!= null &&
				(registeredUser.getAccount().isManager() || Integer.parseInt(userID) == registeredUser.getUserID())) {
			session.setAttribute("orders", ordersMap);
			
			RequestDispatcher view = request.getRequestDispatcher("DisplayOrders.jsp");
			view.forward(request, response);
		}
		else {
			response.sendError(403); //FORBIDDEN
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}
