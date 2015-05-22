package org.juneja.eventdemo.utils;

import java.util.List;

import org.apache.log4j.Logger;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.GetQueueUrlResult;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;

public class AWSUtil {

	private static Logger logger = Logger.getLogger(AWSUtil.class);

	private static String keyId;

	private static String secretKey;

	private static AWSUtil aws;

	private AWSCredentials credentials = null;

	private AWSUtil() {

		logger.info("Initializing the Logger for "
				+ AWSUtil.class.getCanonicalName());

		try {
			credentials = new ProfileCredentialsProvider().getCredentials();
		} catch (Exception e) {
			throw new AmazonClientException(
					"Cannot load the credentials from the credential profiles file. "
							+ "Please make sure that your credentials file is at the correct "
							+ "location (~/.aws/credentials), and is in valid format.",
					e);
		}

	}

	public static AWSUtil newInstance() {
		if (aws == null) {
			aws = new AWSUtil();
		}
		logger.info("Getting new Instance of AWSUtil");
		return aws;
	}

	private AmazonSQS getSQSConnection(Region region) {
		AmazonSQS sqs = new AmazonSQSClient(credentials);

		sqs.setRegion(region);
		return sqs;
	}

	private AmazonSNS getSNSConnection(Region region) {
		AmazonSNS sns = new AmazonSNSClient(credentials);
		sns.setRegion(region);
		return sns;
	}

	public void sendMessageToQueue(String queueName, String msg) {
		sendMessageToQueue(Regions.DEFAULT_REGION, queueName, msg);
	}

	public void sendMessageToQueue(Regions region, String queueName, String msg) {

		Region sqsRegion = Region.getRegion(region);
		AmazonSQS sqs = aws.getSQSConnection(sqsRegion);

		GetQueueUrlResult queueResult = sqs.getQueueUrl(queueName);
		String myQueueUrl = queueResult.getQueueUrl();

		sqs.sendMessage(new SendMessageRequest(myQueueUrl, msg));

	}
	
	public String receiveAndDeleteMessageFromQueue(String queueName) {
		return receiveAndDeleteMessageFromQueue(Regions.DEFAULT_REGION, queueName);
	}

	public String receiveAndDeleteMessageFromQueue(Regions region, String queueName) {
		Region sqsRegion = Region.getRegion(region);
		AmazonSQS sqs = aws.getSQSConnection(sqsRegion);

		GetQueueUrlResult queueResult = sqs.getQueueUrl(queueName);
		String myQueueUrl = queueResult.getQueueUrl();

		ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(
				myQueueUrl);

		receiveMessageRequest.setMaxNumberOfMessages(1);
		List<Message> messages = sqs.receiveMessage(receiveMessageRequest)
				.getMessages();

		
		String messageToReturn = null;
		
		for (Message message : messages) {
			
			System.out.println("Message to be deleted : "+message.getMessageId());
			messageToReturn = message.getBody();
			
			System.out.println("Message Handle: "+message.getReceiptHandle());
			/**
			 * Delete the read message from the Queue
			 */
			DeleteMessageRequest deleteMessageRequest = new DeleteMessageRequest(
					myQueueUrl, message.getReceiptHandle());
			
		
		
		}
		
		return messageToReturn;

	}

	public String publishMessageToTopic(String topic, String msg) {
		return publishMessageToTopic(Regions.DEFAULT_REGION, topic, msg);

	}

	public String publishMessageToTopic(Regions region, String topic, String msg) {

		Region snsRegion = Region.getRegion(region);
		AmazonSNS sns = aws.getSNSConnection(snsRegion);

		PublishRequest publishRequest = new PublishRequest(topic, msg);

		PublishResult publishResult = sns.publish(publishRequest);

		String messageId = publishResult.getMessageId();

		System.out.println("MessageId - " + messageId);

		return messageId;

	}

	public static void main(String[] args) {

		/**
		 * Testing the AWS Util code TODO: Move to a JUNIT Test
		 */
		logger.info("Testing code");
		AWSUtil aws = AWSUtil.newInstance();
		// aws.sendMessageToQueue("TestQueue_EventDriven_2", "Hello World");

		aws.publishMessageToTopic(
				"arn:aws:sns:us-west-2:579199831891:TestTopic_EventDriven_2",
				"First Message to Topic");

	}
}
