package org.juneja.eventdemo.api.impl;

import java.io.BufferedReader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.juneja.eventdemo.entity.Response;
import org.juneja.eventdemo.utils.AWSUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.amazonaws.util.json.JSONException;
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

	@Value("${isSubscriptionConfirmed}")
	private boolean isSubscriptionConfirmed;

	@Value("${uriForDataAPI}")
	private String uriForDataAPI;

	/**
	 * Health Check URL
	 * 
	 * @return
	 */
	@RequestMapping("/purchase/health")
	public Response healthCheck() {

		System.out.println("isSubscriptionConfirmed : "
				+ this.isSubscriptionConfirmed);
		System.out.println("uriForDataAPI : " + this.uriForDataAPI);

		return new Response("1", "Hello", "World");
	}

	@RequestMapping("/purchase")
	public Response purchaseProduct(HttpServletRequest request,
			HttpServletResponse response) {

		System.out.println("Received Notification to start Purchase Task....");

		System.out.println("isSubscriptionConfirmed : "
				+ isSubscriptionConfirmed);
		System.out.println("uriForDataAPI : " + uriForDataAPI);

		/**
		 * Received the Notification to allocate a Task to receive from the
		 * queue
		 */

		// If the Subscription has not been confirmed
		if (!isSubscriptionConfirmed) {
			return confirmSubscription(request, response);
		}

		/**
		 * Now pull out the items from the Queue
		 */
		System.out.println("Reading and Deleting Message now");
		Response messageReturned = aws
				.receiveMessageFromQueue("TestQueue_EventDriven_2");
		System.out.println("Message Returned : " + messageReturned);

		// Process the messageReturned, extract the Product id and product
		// quantity to be purchased
		String[] messageReturnedArray = messageReturned.getResponseMessage()
				.split("[|]");

		System.out.println("messageReturnedArray : " + messageReturnedArray);
		String productId = messageReturnedArray[1];
		String productQuantityToBuy = messageReturnedArray[2];
		String uuid = messageReturnedArray[0];
		String callBackUri = messageReturnedArray[3];

		System.out.println("productId : " + productId);
		System.out.println("productQuantityToBuy : " + productQuantityToBuy);
		System.out.println("uuid : " + uuid);
		System.out.println("uri : " + callBackUri);

		/**
		 * Now, Check for Stock, and then if Stock exists, push a message to the
		 * SQS and then SNS to trigger DB Update
		 */

		Integer quantityRemaining = this.checkStock(productId);

		String respString = "No Stock available";

		String returnStatus = "";

		if (quantityRemaining > 0) {
			// Push a message to the Queue - TestQueue_EventDriven_2
			System.out.println("Stock available !");

			String messageToSend = uuid + "|" + productId + "|"
					+ quantityRemaining + "|" + productQuantityToBuy + "|"
					+ callBackUri;

			aws.sendMessageToQueue("TestQueue_DBUpdate_2", messageToSend);

			// Publish a message to the SNS

			String messageToPublish = "DB Update  Work " + uuid
					+ " added to Queue";

			System.out.println("Publish message : " + messageToPublish
					+ " to Notification System");

			returnStatus = aws
					.publishMessageToTopic(
							"arn:aws:sns:us-west-2:579199831891:TestTopic_EventDriven_DBUpdate",
							messageToPublish);

			// Return a positive Response
		}

		if (!returnStatus.isEmpty()) {
			System.out
					.println("Success ! Now deleting the message from the Purchase Task Queue");
			aws.deleteMessageFromQueue("TestQueue_EventDriven_2",
					messageReturned);
		}

		/**
		 * The following code will be put into another service
		 * 
		 * // Call the MongoServer to update the quantity of the Product String
		 * json = "{  \"id\" : " + productId + ", " + "\"quantity\" : " +
		 * (Integer.parseInt(quantity) - Integer
		 * .parseInt(productQuantityToBuy)) + "}";
		 * 
		 * HttpEntity<String> requestEntity = new HttpEntity<String>(json,
		 * headers); ResponseEntity<String> responseEntity = rest.exchange(
		 * "http://localhost:9080" + "products/" + productId, HttpMethod.PUT,
		 * requestEntity, String.class); System.out.println("requestEntity : " +
		 * responseEntity.getStatusCode());
		 * System.out.println("requestEntity : " + responseEntity.getBody());
		 * 
		 * // Generate a random Order number
		 * 
		 * // Return the Random Order number
		 * 
		 * // Delete the message
		 * 
		 * if (responseEntity.getStatusCode().is2xxSuccessful()) { System.out
		 * .println("Success ! Now deleting the message from the Queue");
		 * aws.deleteMessageFromQueue("TestQueue_EventDriven_2",
		 * messageReturned); }
		 * 
		 * return new Response("1", ("Received Message " + messageReturned),
		 * responseEntity.getStatusCode().toString());
		 **/

		return new Response("1", ("Received Message " + messageReturned),
				respString);

	}

	private Integer checkStock(String productId) {

		// Call the MongoClient to update the Product's quantity
		System.out
				.println("**Call the MongoClient to check the Product's quantity**");
		RestTemplate rest = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();

		headers.add("Content-Type", "application/json");
		headers.add("Accept", "*/*");

		// Find the current Quantity available for the relevant Product
		HttpEntity<String> requestEntity_Search = new HttpEntity<String>(
				headers);

		// TODO: Externalize the IP Address
		String uriForDataAPI = this.uriForDataAPI + "?id=" + productId;
		ResponseEntity<String> responseEntity_Search = rest.exchange(
				uriForDataAPI, HttpMethod.GET, requestEntity_Search,
				String.class);
		System.out.println("responseEntity_Search : "
				+ responseEntity_Search.getStatusCode());
		System.out.println("responseEntity_Search : "
				+ responseEntity_Search.getBody());

		// Process the JSON message received from the Mongo Server
		String quantity = null;
		String responseEntitySearchStr = responseEntity_Search.getBody();
		try {
			JSONObject jsonObj = new JSONObject(responseEntitySearchStr);
			quantity = jsonObj.getJSONObject("_embedded")
					.getJSONArray("products").getJSONObject(0)
					.getString("quantity");

			System.out.println("Quantity : " + quantity);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return Integer.parseInt(quantity);

	}

	/**
	 * Used for confirming subscription to Pub-Sub system
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("purchase/subscription/confirm")
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

		System.out.println("Caller : "
				+ request.getHeader("x-amz-sns-message-type"));

		/**
		 * TODO: Make call to confirm the subscription
		 */
		this.isSubscriptionConfirmed = true;

		return new Response("1", "Ok Works !", "200 OK");

	}
}
