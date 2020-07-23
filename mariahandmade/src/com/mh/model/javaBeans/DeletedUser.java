package com.mh.model.javaBeans;

import java.io.Serializable;
import java.time.LocalDateTime;

public class DeletedUser extends User implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	LocalDateTime deletionDate;

	public LocalDateTime getDeletionDate() {
		return deletionDate;
	}

	public void setDeletionDate(LocalDateTime deletionDate) {
		this.deletionDate = deletionDate;
	}

	@Override
	public String toString() {
		return "DeletedUser [deletionDate=" + deletionDate + ", userID=" + userID + ", registrationDate="
				+ registrationDate + ", lastAccessDate=" + lastAccessDate + "]";
	}
}
