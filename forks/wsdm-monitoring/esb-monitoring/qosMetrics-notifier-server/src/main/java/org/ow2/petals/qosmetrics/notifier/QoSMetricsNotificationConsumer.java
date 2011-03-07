package org.ow2.petals.qosmetrics.notifier;

import javax.xml.parsers.DocumentBuilderFactory;

import org.oasis_open.docs.wsn.b_2.Notify;
import org.ow2.petals.notifier.NotificationConsumerDecorator;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;

import com.ebmwebsourcing.wsstar.dm.WSDMFactory;
import com.ebmwebsourcing.wsstar.dm.api.QoSMetrics;
import com.ebmwebsourcing.wsstar.dm.api.WSDMException;
import com.ebmwebsourcing.wsstar.dm.api.WSDMReader;

public class QoSMetricsNotificationConsumer implements
		NotificationConsumerDecorator {

	private WSDMReader reader = null;

	public WSDMReader getReader() throws WSDMException {
		if (this.reader == null) {
			this.reader = WSDMFactory.newInstance().newWSDMReader();
		}
		return this.reader;
	}

	public void notify(Notify notification) {
		System.out
				.println("\n\n***************************** NEW NOTIFICATION ***********************");

		if (notification != null) {
			this.printQosMetrics(notification);
		}
	}

	public void printQosMetrics(Notify notification) {
		try {
			Element msg = (Element) notification.getNotificationMessage()
					.get(0).getMessage().getAny();

			DocumentBuilderFactory builderFactory = DocumentBuilderFactory
					.newInstance();
			builderFactory.setNamespaceAware(true);

			QoSMetrics metric = this.getReader().readOperationMetric(
					msg.getOwnerDocument());

			System.out.println("metric received:\n " + metric);
		} catch (WSDMException e) {
			e.printStackTrace();
		} catch (DOMException e) {
			e.printStackTrace();
		}
	}

}
