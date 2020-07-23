package com.mh.model.javaBeans;

import java.io.Serializable;

/**
 * Un prodotto che è stato aggiunto al carrello e che pertanto è presente in una
 * determinata quantità.
 * 
 * Nasce come classe decorator della classe Product al solo scopo di aggiungere
 * il numero di unità presenti nel carrello.
 * 
 * @author Enrico
 *
 */
public class CartProduct implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// Indica quante unità di un determinato prodotto sono state aggiunte al carrello.
	private int deal;
	
	private Product product;
	
	public CartProduct() {
		super();
		this.deal = 0;
	}

	public int getDeal() {
		return this.deal;
	}

	public void setDeal(int deal) {
		this.deal = deal;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}
	
}
