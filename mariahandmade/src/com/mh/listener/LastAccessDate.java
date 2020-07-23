package com.mh.listener;

import javax.servlet.*;
import javax.servlet.http.*;

import com.mh.model.database.Connector;
import com.mh.model.javaBeans.RegisteredUser;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;

/**
 * Aggiorna la data di ultimo accesso di un utente registrato. Data e ora dell'ultimo
 * accesso corrispondono a data e ora dell'ultima richiesta inviata dall'utente.
 *
 * Quando una richiesta è associata a una sessione autenticata questo listener aggiorna
 * l'attributo di sessione lastAccessDate e persiste nel database il contenuto dell'
 * attributo di sessione quando la sessione viene distrutta.
 */
public class LastAccessDate implements ServletRequestListener, HttpSessionListener{
	
	public void requestInitialized(ServletRequestEvent event) {
		HttpSession session = ( (HttpServletRequest) event.getServletRequest() ).getSession(false);
		if(session != null && session.getAttribute("userData") != null)
			session.setAttribute("lastAccessDate", LocalDateTime.now());
	}
	
	public void requestDestroyed(ServletRequestEvent event) {
		// nothing to do here
	}
	
	public void sessionCreated(HttpSessionEvent event) {
		// nothing to do here
	}
	
	public void sessionDestroyed(HttpSessionEvent event) {
		HttpSession session = event.getSession();
		LocalDateTime lastAccess = (LocalDateTime) session.getAttribute("lastAccessDate");
		
		Connection connection = Connector.getConnection();
		PreparedStatement lastAccessSt = null;
		
		if(lastAccess != null){
			try{
				String lastAccessSQL = "UPDATE TRACCIAUTENTE" +
						" SET DATAULTIMOACCESSO = ?" +
						" WHERE ID = ?;";
				
				lastAccessSt = connection.prepareStatement(lastAccessSQL);
				lastAccessSt.setString(1, lastAccess.toString());
				lastAccessSt.setInt(2, ((RegisteredUser) session.getAttribute("userData")).getUserID());

				lastAccessSt.executeUpdate();
			}
			catch(SQLException e){
				System.out.println("HttpSessionListener: Error while persisting lastAccessDate session attribute.");
				System.out.println(e.getClass().getName() + ": " + e.getMessage());
			}
			finally{
				try{
					if(lastAccessSt != null)
						lastAccessSt.close();
				}
				catch(SQLException e){
					System.out.println("HttpSessionListener: Error while persisting lastAccessDate session attribute.");
					System.out.println(e.getClass().getName() + ": " + e.getMessage());
				}
				finally{
					Connector.releaseConnection(connection);
				}
			}
		}
	}
}
