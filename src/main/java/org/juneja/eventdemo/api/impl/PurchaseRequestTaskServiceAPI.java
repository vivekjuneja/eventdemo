package org.juneja.eventdemo.api.impl;

import java.io.BufferedReader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.juneja.eventdemo.entity.Response;
import org.juneja.eventdemo.utils.AWSUtil;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.amazonaws.util.json.JSONObject;

/**
 * 
 * Task allocator - gets a Task from the Task pool to service requests from
 * Queue
 * 
 * 
 * 
 * @author vivekjuneja
 *
 */
@RestController
public class PurchaseRequestTaskServiceAPI {

	AWSUtil aws = AWSUtil.newInstance();

	private static boolean isSubscriptionConfirmed = true;

	@RequestMapping("/purchase/{uuid}")
	public Response purchaseProduct(@PathVariable(value = "uuid") String uuid,
			HttpServletRequest request, HttpServletResponse response) {

		/**
		 * aws.publishMessageToTopic(
		 * "arn:aws:sns:us-west-2:579199831891:TestTopic_EventDriven_2",
		 * "Product Purchase message");
		 * 
		 **/

		/**
		 * Received the Notification to allocate a Task to receive from the
		 * queue
		 */

		// If the Subscription has not been confirmed
		if (!isSubscriptionConfirmed) {
			return confirmSubscription(request, response);
		}

		System.out.println("Receieved Purchase Product Notification");

		/**
		 * Now pull out the items from the Queue
		 */
		System.out.println("Reading and Deleting Message now");
		String messageReturned = aws
				.receiveAndDeleteMessageFromQueue("TestQueue_EventDriven_2");
		System.out.println("Message Returned : " + messageReturned);

		// Process the messageReturned, extract the Product id and product
		// quantity to be purchased

		// Call the MongoClient to update the Product's quantity
		System.out.println("**Call the MongoClient to update the Product's quantity**");
		RestTemplate rest = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		
		String json = "{  \"id\" : 2, " +  "\"quantity\" : 6 }";
		headers.add("Content-Type", "application/json");
		headers.add("Accept", "*/*");
		HttpEntity<String> requestEntity = new HttpEntity<String>(json, headers);

		ResponseEntity<String> responseEntity = rest.exchange("http://localhost:9080"
				+ "products/1", HttpMethod.PUT, requestEntity, String.class);
		System.out.println(responseEntity.getStatusCode());
		System.out.println(responseEntity.getBody());

		// Generate a random Order number

		// Return the Random Order number

		return new Response("1", ("Received Message " + messageReturned),
				responseEntity.getStatusCode().toString());
	}

	@RequestMapping("/subscription/confirm")
	public Response confirmSubscription(HttpServletRequest request,
			HttpServletResponse response) {
		StringBuffer jb = new StringBuffer();
		String line = null;
		try {
			BufferedReader reader = request.getReader();
			while ((line = reader.readLine()) != null)
				jb.append(line);
		} catch (Exception e) { /* report an error */
		}

		System.out.println("Json Body : " + jb);

		JSONObject jsonObject = (JSONObject) JSONObject.stringToValue(jb
				.toString());

		System.out.println("JsonObject : " + jsonObject);

		System.out.println("Caller : "
				+ request.getHeader("x-amz-sns-message-type"));

		/**
		 * TODO: Make call to confirm the subscription
		 */

		return new Response("1", "Ok Works !", "200 OK");

	}
}
