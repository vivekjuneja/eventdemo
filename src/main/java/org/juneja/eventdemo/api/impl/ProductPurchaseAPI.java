package org.juneja.eventdemo.api.impl;

import org.juneja.eventdemo.entity.Response;
import org.juneja.eventdemo.utils.AWSUtil;
import org.juneja.eventdemo.utils.RandomString;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductPurchaseAPI {

	AWSUtil aws = AWSUtil.newInstance();

	@RequestMapping("/products/{id}/purchase")
	public Response purchaseProduct(@PathVariable(value = "id") String id,
			@RequestParam(value = "quantity") String quantity) {

		/**
		 * aws.publishMessageToTopic(
		 * "arn:aws:sns:us-west-2:579199831891:TestTopic_EventDriven_2",
		 * "Product Purchase message");
		 **/

		String uniqueGeneratedId = this.generateUUID();
		String messageToSend =  uniqueGeneratedId + ":" + id + ":" + quantity ;
		System.out.println("Sending message : " + messageToSend + " to Queue");
		aws.sendMessageToQueue("TestQueue_EventDriven_2", messageToSend);

		String messageToPublish = "Purchase Work " + uniqueGeneratedId + " added to Queue";
		System.out.println("Publish message : " + messageToPublish
				+ " to Notification System");

		aws.publishMessageToTopic(
				"arn:aws:sns:us-west-2:579199831891:TestTopic_EventDriven_2",
				messageToPublish);

		return new Response("1", messageToPublish, "200 OK");
	}

	private String generateUUID() {
		RandomString randomStringGen = new RandomString(10);
		return randomStringGen.nextString();

	}
}
