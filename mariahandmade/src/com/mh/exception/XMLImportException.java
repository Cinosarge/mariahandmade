package com.mh.exception;

public class XMLImportException extends RuntimeException{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public XMLImportException(String error){
		super(error);
	}
}