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

	public NotificationController(NotificationRepository notificationRepository,
			KafkaTemplate<String, NotificationEvent> kafkaTemplate,
			KafkaTemplate<String, AccountEvent> kafkaAccountTemplate) {
		this.notificationRepository = notificationRepository;
		this.kafkaTemplate = kafkaTemplate;
		this.kafkaAccountTemplate = kafkaAccountTemplate;
	}

	@KafkaListener(topics = "new-transaction")
	public String getNotifications(String event) throws JsonMappingException, JsonProcessingException {
		System.out.println("Recieved account event" + event);
		AccountEvent accountEvent = new ObjectMapper().readValue(event, AccountEvent.class);
		Account senderAccount = accountEvent.getSenderAccount();
		Account receiverAccount = accountEvent.getReceiverAccount();
		System.out.println("Successful transfer from " + senderAccount.getId() + " to " + receiverAccount.getId());
		return "Successful transfer from " + senderAccount.getId() + " to " + receiverAccount.getId();
	}
}
