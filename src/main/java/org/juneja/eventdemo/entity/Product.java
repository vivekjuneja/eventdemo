package org.juneja.eventdemo.entity;

import org.springframework.data.annotation.Id;

public class Product {
	
	@Id
	private Integer id;
	
	private Integer quantity;

	
	
	public Product(Integer id, Integer quantity) {
		super();
		this.id = id;
		this.quantity = quantity;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	@Override
	public String toString() {
		return "Product [id=" + id + ", quantity=" + quantity + "]";
	}
	
	

	
	
}
