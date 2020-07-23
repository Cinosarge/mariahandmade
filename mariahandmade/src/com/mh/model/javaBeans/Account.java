package com.mh.model.javaBeans;

import java.io.Serializable;

public class Account implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	String username;
	String password;
	boolean manager;
	boolean checked;
	
	public Account(){
		
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public boolean isManager() {
		return manager;
	}

	public void setManager(boolean manager) {
		this.manager = manager;
	}
	
	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}
	
	@Override
	public String toString() {
		return "Account [username=" + username + ", password=" + password + ", checked=" + checked + ", manager="
				+ manager + "]";
	}
}
