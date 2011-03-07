package org.ow2.petals.timestamp.module;

import java.util.Date;

import org.ow2.petals.jbi.messaging.exchange.MessageExchange;

public class TimeStamperImpl implements TimeStamper {

	private MessageExchange me = null;


	public TimeStamperImpl() {
	}


	public Date getDateClientIn() {
		Date res = null;
		if(this.me != null) {
			Long dateClientIn = (Long) this.me.getProperty(TimeStamper.DATE_CLIENT_IN_PROPERTY.toString());
			if(dateClientIn != null) {
				res = new Date(dateClientIn);
			}
		}
		return res;
	}

	public Date getDateClientOut() {
		Date res = null;
		if(this.me != null) {
			Long dateClientOut = (Long) this.me.getProperty(TimeStamper.DATE_CLIENT_OUT_PROPERTY.toString());
			if(dateClientOut != null) {
				res = new Date(dateClientOut);
			}
		}
		return res;
	}

	public Date getDateProviderIn() {
		Date res = null;
		if(this.me != null) {
			Long dateProviderIn = (Long) this.me.getProperty(TimeStamper.DATE_PROVIDER_IN_PROPERTY.toString());
			if(dateProviderIn != null) {
				res = new Date(dateProviderIn);
			}
		}
		return res;
	}

	public Date getDateProviderOut() {
		Date res = null;
		if(this.me != null) {
			Long dateProviderOut = (Long) this.me.getProperty(TimeStamper.DATE_PROVIDER_OUT_PROPERTY.toString());
			if(dateProviderOut != null) {
				res = new Date(dateProviderOut);
			}
		}
		return res;
	}

	public void setDateClientIn(Date date) {
		if(this.me != null) {
			if(date != null) {
				this.me.setProperty(TimeStamper.DATE_CLIENT_IN_PROPERTY.toString(), new Long(date.getTime()));
			} else {
				this.me.setProperty(TimeStamper.DATE_CLIENT_IN_PROPERTY.toString(), null);
			}
		}
	}

	public void setDateClientOut(Date date) {
		if(this.me != null) {
			if(date != null) {
				this.me.setProperty(TimeStamper.DATE_CLIENT_OUT_PROPERTY.toString(), new Long(date.getTime()));
			} else {
				this.me.setProperty(TimeStamper.DATE_CLIENT_OUT_PROPERTY.toString(), null);
			}
		}
	}

	public void setDateProviderIn(Date date) {
		if(this.me != null) {
			if(date != null) {
				this.me.setProperty(TimeStamper.DATE_PROVIDER_IN_PROPERTY.toString(), new Long(date.getTime()));
			} else {
				this.me.setProperty(TimeStamper.DATE_PROVIDER_IN_PROPERTY.toString(), null);
			}
		}
	}

	public void setDateProviderOut(Date date) {
		if(this.me != null) {
			if(date != null) {
				this.me.setProperty(TimeStamper.DATE_PROVIDER_OUT_PROPERTY.toString(), new Long(date.getTime()));
			} else {
				this.me.setProperty(TimeStamper.DATE_PROVIDER_OUT_PROPERTY.toString(), null);
			}
		}
	}

	public MessageExchange getMessageExchange() {
		return me;
	}


	public void setMessageExchange(MessageExchange me) {
		this.me = me;
	}

}
