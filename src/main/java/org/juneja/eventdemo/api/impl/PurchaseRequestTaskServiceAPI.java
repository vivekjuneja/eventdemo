package org.juneja.eventdemo.api.impl;

import java.io.BufferedReader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.juneja.eventdemo.entity.Response;
import org.juneja.eventdemo.utils.AWSUtil;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.amazonaws.util.json.JSONObject;


/**
 * 
 * Task allocator - gets a Task from the Task pool to service requests from Queue
 * 
 * 
 * 
 * @author vivekjuneja
 *
 */
@RestController
public class PurchaseRequestTaskServiceAPI {

	AWSUtil aws = AWSUtil.newInstance();

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
		 * Received the Notification to allocate a Task to receive from the queue
		 */

		System.out.println("Receieved Purchase Product Notification");
		
		/**
		 * Now pull out the items from the Queue
		 */
		System.out.println("Reading and Deleting Message now");
		String messageReturned = aws.receiveAndDeleteMessageFromQueue("TestQueue_EventDriven_2");
		System.out.println("Message Returned : "+messageReturned);

		return new Response("1", ("Received Message "+messageReturned), "200 OK");
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

		return new Response("1", "Ok Works !", "200 OK");

	}
}
