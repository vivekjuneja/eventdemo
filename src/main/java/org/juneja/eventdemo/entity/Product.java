package org.juneja.eventdemo.entity;

import org.springframework.data.annotation.Id;

public class Product {

	@Id private Integer id;

	private Integer quantity;
	
	private String name;
	
	
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
		return "Product [id=" + id + ", quantity=" + quantity + ", name="
				+ name + "]";
	}

	
}
