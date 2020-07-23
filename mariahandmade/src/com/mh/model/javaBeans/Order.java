package com.mh.model.javaBeans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.math.BigDecimal;

public class Order implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Integer orderNumber;
	private LocalDateTime data;
	private BigDecimal total;
	private int userID;
	private int addressID;
	private String deliveryType;
	private String deliveryState;
	
	private Payment payment;
	
	private List<Purchase> purchaseList;
	
	public Order(){
		purchaseList = new ArrayList<Purchase>();
	}
	

	public Integer getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(Integer orderNumber) {
		this.orderNumber = orderNumber;
	}

	public LocalDateTime getData() {
		return data;
	}

	public void setData(LocalDateTime data) {
		this.data = data;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}

	public int getUserID() {
		return userID;
	}

	public void setUserID(int userID) {
		this.userID = userID;
	}

	public int getAddressID() {
		return addressID;
	}

	public void setAddressID(int addressID) {
		this.addressID = addressID;
	}

	public String getDeliveryType() {
		return deliveryType;
	}

	public void setDeliveryType(String deliveryType) {
		this.deliveryType = deliveryType;
	}

	public String getDeliveryState() {
		return deliveryState;
	}

	public void setDeliveryState(String deliveryState) {
		this.deliveryState = deliveryState;
	}

	public Payment getPayment() {
		return payment;
	}

	public void setPayment(Payment payment) {
		this.payment = payment;
	}

	public List<Purchase> getPurchaseList() {
		return purchaseList;
	}

	public void setPurchaseList(List<Purchase> purchaseList) {
		this.purchaseList = purchaseList;
	}
	
	/**
	 * Un numero d'ordine è univoco per uno stesso utente e anche per ordini
	 * riferiti ad utenti diversi.
	 * 
	 * Questo metodo è utilizzato dal metodo List.contains()
	 * @param o
	 * @return
	 */
	public boolean equals(Order o) {
		return this.orderNumber == o.orderNumber;
	}
}
