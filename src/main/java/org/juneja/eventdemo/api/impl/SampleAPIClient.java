package org.juneja.eventdemo.api.impl;

import org.juneja.eventdemo.entity.Response;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SampleAPIClient {

	@RequestMapping("/client/callback")
	public Response receiveOrder(@RequestParam(value = "orderId") String orderId) {
		System.out.println("Received order Id " + orderId);
		return new Response("1", orderId, "");
	}

}
