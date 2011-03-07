package org.ow2.petals.qosmetrics.notifier;

import org.oasis_open.docs.wsn.bw_2.NotificationConsumer_NotifierEndpoint_Server;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		QoSMetricsNotificationConsumer notifier = new QoSMetricsNotificationConsumer();
		try {
			NotificationConsumer_NotifierEndpoint_Server notifServer = new NotificationConsumer_NotifierEndpoint_Server(
					notifier, "http://localhost:9002/NotificationEndpoint");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
