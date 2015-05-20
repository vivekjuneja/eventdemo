# eventdemo
A reference implementation of an Event Driven systems to demonstrate concepts of CQRS, Event Sourcing using AWS

**Problem Statement**


**Solving this using Traditional Design techniques**


**Solving this using Event Driven architecture**


** Overall Flow **
Design Iteration #1

1. Product Purchase API is called by the Order Interface (UI / CLI / Service)

2. This Product Purchase API is then processed by the "Product Buy Request Service". 

3. This service then generates a unique UUID (request ID), then Pushes a message on the Queue (Task Queue). This message includes the "Product ID", "Amount of Product to be bought", and "UUID". After pushing the message to the Queue, it returns the caller with the generated UUID.

4. The Task Queue is bound to a Notification system. A message is automatically published by the Task Queue to a Message Topic - TOPIC-1. This topic signifies that the Purchase Request has been received. A "Process Product Buy Request Task Service" subscribes to this topic TOPIC-1. 

5. This Task Service on receiving the notification from the topic TOPIC-1 then publishes a Notification on the TOPIC-2. This notification is for Stock Check needed request. A Stock Check Service subscribes to the TOPIC-2.

6. The Stock Check Service on receiving the notification from the topic TOPIC-3 queries the Event Pool-1/ DB-1 to pull out all the events for that particular Product that is to be purchases. It replays all the Events to get the final state of the Product. The state includes the last available quantity of the particular Product. 

7. If the Product stock is remaining, the Stock Check Service will then publish a message on the TOPIC-3, which means that that Order procesing can begin. If the Product Stock is not available, the Stock Check Service will then publish a message on the TOPIC-4, which means that the Order Processing will not continue.

8. A Process Order service subscribes to the TOPIC-3. On receiving the notification from this topic, this service will then attempt to change the Product Domain State. A Product Domain state then will trigger a new Event in the Event Pool-1/DB-1. After this, it will then publish a message to TOPIC-5. The TOPIC-5 signifies generation of an Order ID.

9. An Order Failure service subscribes to the TOPIC-4. On receiving the notification from this TOPIC, this service will then respond back to the user with an Error code. 
	- Will it do Server Push or Reverse AJAX ?
		- It can do that as well. The Order Service can either write the failure code to a distributed session cache with the original UUID, which can be queried by the Front end code OR another presentation layer service. 
		
		- Implementation debatable - will be an asynchronous API implementation. 

10. Once a message is published on TOPIC-5, The Order Creation service that is subscribing to the service, is triggered. It creates a new Order, and puts the Order id into a Distributed session cache with the UUID. This message can be pulled by a Front end code or another presentation layer service to end the transaction.


**Why is the design better than traditional design**
