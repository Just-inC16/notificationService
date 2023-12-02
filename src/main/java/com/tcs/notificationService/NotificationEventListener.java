package com.tcs.notificationService;

import org.springframework.context.event.EventListener;

public class NotificationEventListener {
	@EventListener
	public void handleTransactionSuccessEvent(TransactionSuccessEvent event) {
		Notification notification = createSuccessNotification(event.getTransactionId());
		sendNotification(notification);
	}

	@EventListener
	public void handleTransactionFailureEvent(TransactionFailureEvent event) {
		Notification notification = createFailureNotification(event.getTransactionId(), event.getFailureReason());
		sendNotification(notification);
	}
}
