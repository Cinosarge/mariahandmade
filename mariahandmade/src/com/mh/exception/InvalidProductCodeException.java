package com.mh.exception;

public class InvalidProductCodeException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidProductCodeException(String error){
		super(error);
	}
}
