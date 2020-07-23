package com.mh.model.javaBeans;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class Product implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Integer code;
	private String name;
	private String line;
	private String type;
	private String images;
	private int availableUnits;
	private int minInventory;
	private BigDecimal price;
	private BigDecimal cost;
	private LocalDateTime insertDate;
	private String description;
	private boolean deleted;
	private LocalDateTime deletionDate;
	private List<String> materials;
	
	public Product(){
		this.code = null;
		this.name = null;
		this.line = null;
		this.type = null;
		this.images = null;
		this.availableUnits = 0;
		this.minInventory = 0;
		this.price = null;
		this.cost = null;
		this.insertDate = null;
		this.description = null;
		this.deleted = false;
		this.deletionDate = null;
		this.materials = null;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLine() {
		return line;
	}

	public void setLine(String line) {
		this.line = line;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getImages() {
		return images;
	}

	public void setImages(String images) {
		this.images = images;
	}

	public int getAvailableUnits() {
		return availableUnits;
	}

	public void setAvailableUnits(int units) {
		this.availableUnits = units;
	}

	public int getMinInventory() {
		return minInventory;
	}

	public void setMinInventory(int minInventory) {
		this.minInventory = minInventory;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public BigDecimal getCost() {
		return cost;
	}

	public void setCost(BigDecimal cost) {
		this.cost = cost;
	}

	public LocalDateTime getInsertDate() {
		return insertDate;
	}

	public void setInsertDate(LocalDateTime insertDate) {
		this.insertDate = insertDate;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public LocalDateTime getDeletionDate() {
		return deletionDate;
	}

	public void setDeletionDate(LocalDateTime deletionDate) {
		this.deletionDate = deletionDate;
	}

	public List<String> getMaterials() {
		return materials;
	}

	public void setMaterials(List<String> materials) {
		this.materials = materials;
	}
	
	@Override
	public String toString() {
		return "Product [code=" + code + ", name=" + name + ", line=" + line + ", type=" + type + ", images="
				+ images + ", units=" + availableUnits + ", minInventory=" + minInventory + ", insertDate=" + insertDate
				+ ", description=" + description + ", deleted=" + deleted + ", deletionDate=" + deletionDate
				+ ", materials=" + materials + "]";
	}
	
	
	public boolean equals(Product p){
		return this.code.equals(p.getCode());
	}
}
