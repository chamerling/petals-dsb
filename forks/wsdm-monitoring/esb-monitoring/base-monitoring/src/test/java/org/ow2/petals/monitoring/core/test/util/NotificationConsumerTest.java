package org.ow2.petals.monitoring.core.test.util;

import java.util.ArrayList;
import java.util.List;

import org.oasis_open.docs.wsn.b_2.Notify;
import org.ow2.petals.notifier.NotificationConsumerDecorator;

public class NotificationConsumerTest implements NotificationConsumerDecorator {

	private final List<Notify> notifications = new ArrayList<Notify>();

	public List<Notify> getNotifications() {
		return this.notifications;
	}

	public void notify(final Notify arg0) {
		System.out
				.println("***************************** NEW NOTIFICATION ***********************");
		this.notifications.add(arg0);
	}

}
