package com.mh.model.javaBeans;

import java.io.Serializable;
import java.math.BigDecimal;

public class Purchase implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	int orderNumber;
	int productCode;
	
	// Unità di prodotto acquistate
	int units;
	
	BigDecimal originalPrice;
	
	Product product;
	
	public Purchase(){
		
	}

	public int getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(int orderNumber) {
		this.orderNumber = orderNumber;
	}

	public int getProductCode() {
		return productCode;
	}

	public void setProductCode(int productCode) {
		this.productCode = productCode;
	}

	public int getUnits() {
		return units;
	}

	public void setUnits(int units) {
		this.units = units;
	}

	public BigDecimal getOriginalPrice() {
		return originalPrice;
	}

	public void setOriginalPrice(BigDecimal originalPrice) {
		this.originalPrice = originalPrice;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}
	
	
}
