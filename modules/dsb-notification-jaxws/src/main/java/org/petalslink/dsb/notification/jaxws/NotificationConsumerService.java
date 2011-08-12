/**
 * 
 */
package org.petalslink.dsb.notification.jaxws;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.petalslink.dsb.notification.jaxws.api.NotificationConsumer;

import com.ebmwebsourcing.wsstar.basefaults.datatypes.impl.impl.WsrfbfModelFactoryImpl;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.impl.impl.WsnbModelFactoryImpl;
import com.ebmwebsourcing.wsstar.jaxb.notification.base.Notify;
import com.ebmwebsourcing.wsstar.resource.datatypes.impl.impl.WsrfrModelFactoryImpl;
import com.ebmwebsourcing.wsstar.resourcelifetime.datatypes.impl.impl.WsrfrlModelFactoryImpl;
import com.ebmwebsourcing.wsstar.resourceproperties.datatypes.impl.impl.WsrfrpModelFactoryImpl;
import com.ebmwebsourcing.wsstar.topics.datatypes.impl.impl.WstopModelFactoryImpl;
import com.ebmwebsourcing.wsstar.wsnb.services.impl.util.Wsnb4ServUtils;

/**
 * USed to receive notifications
 * 
 * @author chamerling
 * 
 */
public class NotificationConsumerService implements NotificationConsumer {

	static {
		Wsnb4ServUtils.initModelFactories(new WsrfbfModelFactoryImpl(),
				new WsrfrModelFactoryImpl(), new WsrfrlModelFactoryImpl(),
				new WsrfrpModelFactoryImpl(), new WstopModelFactoryImpl(),
				new WsnbModelFactoryImpl());
	}

	private static Logger logger = Logger.getLogger(NotificationConsumer.class
			.getName());

	/**
	 * 
	 */
	public NotificationConsumerService() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.oasis_open.docs.wsn.bw_2.NotificationConsumer#notify(com.ebmwebsourcing
	 * .wsstar.jaxb.notification.base.Notify)
	 */
	public void notify(Notify notify) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Got a notify...");
		}

		// let's send it to the notification engine, it will do the rest...
		System.out
				.println("Got a notify, for nbow there is nothing to do......");

	}

}
