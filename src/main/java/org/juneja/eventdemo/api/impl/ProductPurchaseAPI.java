package org.juneja.eventdemo.api.impl;

import org.juneja.eventdemo.entity.Response;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductPurchaseAPI {
	
	@RequestMapping("/products/{id}/purchase")
	public Response purchaseProduct(@PathVariable(value="id") String id) {
		return new Response("1", "Ok Works !", "200 OK");
	}

	public static void main(String[] args) {
		
		
	}
	
}


