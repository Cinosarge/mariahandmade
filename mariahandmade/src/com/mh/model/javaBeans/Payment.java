package com.mh.model.javaBeans;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Payment implements Serializable {
	private static final long serialVersionUID = 1L;
	
	int OrderNumber;
	int userID;
	String paymentType;
	String paymentState;
	LocalDateTime data;
	
	public Payment(){
		
	}

	public int getOrderNumber() {
		return OrderNumber;
	}

	public void setOrderNumber(int orderNumber) {
		OrderNumber = orderNumber;
	}

	public int getUserID() {
		return userID;
	}

	public void setUserID(int userID) {
		this.userID = userID;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public String getPaymentState() {
		return paymentState;
	}

	public void setPaymentState(String paymentState) {
		this.paymentState = paymentState;
	}

	public LocalDateTime getData() {
		return data;
	}

	public void setData(LocalDateTime data) {
		this.data = data;
	}
	
}
