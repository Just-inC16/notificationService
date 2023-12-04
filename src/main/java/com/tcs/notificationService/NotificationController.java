package com.tcs.notificationService;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/notifications")
public class NotificationController {
	private NotificationRepository notificationRepository;
	private KafkaTemplate<String, NotificationEvent> kafkaTemplate;
	private KafkaTemplate<String, AccountEvent> kafkaAccountTemplate;
	private final String SENDER_MESSAGE = "Your deposit was successful!";
	private final String RECEVIER_MESSAGE = "You received a deposit!";

	public NotificationController(NotificationRepository notificationRepository,
			KafkaTemplate<String, NotificationEvent> kafkaTemplate,
			KafkaTemplate<String, AccountEvent> kafkaAccountTemplate) {
		this.notificationRepository = notificationRepository;
		this.kafkaTemplate = kafkaTemplate;
		this.kafkaAccountTemplate = kafkaAccountTemplate;
	}

	@KafkaListener(topics = "new-transaction")
	public void getNotifications(String event) throws JsonMappingException, JsonProcessingException {
		System.out.println("Recieved account event" + event);
		AccountEvent accountEvent = new ObjectMapper().readValue(event, AccountEvent.class);
		Account senderAccount = accountEvent.getSenderAccount();
		Account receiverAccount = accountEvent.getReceiverAccount();
		try {
			Notification sender = new Notification(senderAccount.getId(), SENDER_MESSAGE);
			this.notificationRepository.save(sender);

			Notification receiver = new Notification(receiverAccount.getId(), RECEVIER_MESSAGE);
			this.notificationRepository.save(receiver);

			NotificationEvent notificationEvent = new NotificationEvent();
			notificationEvent.setType("Success!");
			notificationEvent.setMessage("Transaction Complete");
			this.kafkaTemplate.send("success-transaction", notificationEvent);
		} catch (Exception e) {
			AccountEvent newAccountEvent = accountEvent;
			newAccountEvent.setType("Notification Failed!");
			this.kafkaAccountTemplate.send("reverse-account", newAccountEvent);

		}

	}
}
