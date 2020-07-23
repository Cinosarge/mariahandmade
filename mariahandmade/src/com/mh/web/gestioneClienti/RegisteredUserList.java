package com.mh.web.gestioneClienti;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

import java.util.List;

import com.mh.model.gestioneClienti.DBUser;
import com.mh.model.javaBeans.RegisteredUser;

public class RegisteredUserList extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		
		response.setContentType("text/html");
		
		DBUser userAPI = new DBUser();
		List<RegisteredUser> userList = userAPI.getRegisteredUserList();
		
		/* Dovresti controllare che è un amministratore */
		HttpSession session = request.getSession();
		session.setAttribute("userList", userList);
		
		RequestDispatcher view = request.getRequestDispatcher("DisplayClients.jsp");
		view.forward(request, response);
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		doGet(request, response);
	}
}
