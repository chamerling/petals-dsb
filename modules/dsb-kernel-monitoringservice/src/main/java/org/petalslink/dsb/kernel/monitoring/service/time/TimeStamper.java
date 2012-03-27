package org.petalslink.dsb.kernel.monitoring.service.time;

import java.net.URI;

import org.ow2.petals.jbi.messaging.exchange.MessageExchangeWrapper;

public interface TimeStamper {

	public static final URI DATE_CLIENT_IN_PROPERTY = URI
			.create("http://petals.ow2.org/date/client/in");

	public static final URI DATE_PROVIDER_IN_PROPERTY = URI
			.create("http://petals.ow2.org/date/provider/in");

	public static final URI DATE_PROVIDER_OUT_PROPERTY = URI
			.create("http://petals.ow2.org/date/provider/out");

	public static final URI DATE_CLIENT_OUT_PROPERTY = URI
			.create("http://petals.ow2.org/date/client/out");

	void setDateClientIn(long date);

	void setDateClientOut(long date);

	void setDateProviderIn(long date);

	void setDateProviderOut(long date);

	long getDateClientIn();

	long getDateClientOut();

	long getDateProviderIn();

	long getDateProviderOut();

	MessageExchangeWrapper getMessageExchange();

	void setMessageExchange(MessageExchangeWrapper me);
}
