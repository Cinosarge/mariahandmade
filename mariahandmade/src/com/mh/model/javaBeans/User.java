package com.mh.model.javaBeans;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Rappresenta un utente generico, sia esso un utente ancora registrato al servizio oppure
 * un utente che ha richiesto l'eliminazione dei propri dati personali.
 * 
 * @author Enrico
 *
 */
public abstract class User implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	Integer userID;
	LocalDateTime registrationDate;
	LocalDateTime lastAccessDate;
	
	public User(){
		
	}

	public Integer getUserID() {
		return userID;
	}

	public void setUserID(Integer userID) {
		this.userID = userID;
	}

	public LocalDateTime getRegistrationDate() {
		return registrationDate;
	}

	public void setRegistrationDate(LocalDateTime registrationDate) {
		this.registrationDate = registrationDate;
	}

	public LocalDateTime getLastAccessDate() {
		return lastAccessDate;
	}

	public void setLastAccessDate(LocalDateTime lastAccessDate) {
		this.lastAccessDate = lastAccessDate;
	}
}
