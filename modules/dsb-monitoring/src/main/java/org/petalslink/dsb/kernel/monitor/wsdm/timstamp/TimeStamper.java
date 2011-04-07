package org.petalslink.dsb.kernel.monitor.wsdm.timstamp;

import java.net.URI;
import java.util.Date;

import org.ow2.petals.jbi.messaging.exchange.MessageExchange;

public interface TimeStamper {

	public static final URI DATE_CLIENT_IN_PROPERTY = URI
			.create("http://petals.ow2.org/date/client/in");

	public static final URI DATE_PROVIDER_IN_PROPERTY = URI
			.create("http://petals.ow2.org/date/provider/in");

	public static final URI DATE_PROVIDER_OUT_PROPERTY = URI
			.create("http://petals.ow2.org/date/provider/out");

	public static final URI DATE_CLIENT_OUT_PROPERTY = URI
			.create("http://petals.ow2.org/date/client/out");

	void setDateClientIn(Date date);

	void setDateClientOut(Date date);

	void setDateProviderIn(Date date);

	void setDateProviderOut(Date date);

	Date getDateClientIn();

	Date getDateClientOut();

	Date getDateProviderIn();

	Date getDateProviderOut();

	MessageExchange getMessageExchange();

	void setMessageExchange(MessageExchange me);
}
