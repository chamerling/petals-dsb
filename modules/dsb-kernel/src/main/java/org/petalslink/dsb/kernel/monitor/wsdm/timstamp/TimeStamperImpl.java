package org.petalslink.dsb.kernel.monitor.wsdm.timstamp;

import java.util.Date;

import org.ow2.petals.jbi.messaging.exchange.MessageExchange;

public class TimeStamperImpl implements TimeStamper {

    private MessageExchange me = null;

    public TimeStamperImpl() {
    }

    public Date getDateClientIn() {
        Date res = null;
        if (this.me != null) {
            long t = 0L;
            Object tmp = this.me.getProperty(TimeStamper.DATE_CLIENT_IN_PROPERTY.toString());
            if (tmp != null) {
                if (tmp instanceof Long) {
                    t = (Long) tmp;
                } else {
                    t = Long.parseLong(tmp.toString());
                }
            }
            if (tmp != null) {
                res = new Date(t);
            }
        }
        return res;
    }

    public Date getDateClientOut() {
        Date res = null;
        if (this.me != null) {
            long t = 0L;
            Object tmp = this.me.getProperty(TimeStamper.DATE_CLIENT_OUT_PROPERTY.toString());
            if (tmp != null) {
                if (tmp instanceof Long) {
                    t = (Long) tmp;
                } else {
                    t = Long.parseLong(tmp.toString());
                }
            }
            if (tmp != null) {
                res = new Date(t);
            }
        }
        return res;
    }

    public Date getDateProviderIn() {
        Date res = null;
        if (this.me != null) {
            long t = 0L;
            Object tmp = this.me.getProperty(TimeStamper.DATE_PROVIDER_IN_PROPERTY.toString());
            if (tmp != null) {
                if (tmp instanceof Long) {
                    t = (Long) tmp;
                } else {
                    t = Long.parseLong(tmp.toString());
                }
            }
            if (tmp != null) {
                res = new Date(t);
            }
        }
        return res;
    }

    public Date getDateProviderOut() {
        Date res = null;
        if (this.me != null) {
            long t = 0L;
            Object tmp = this.me.getProperty(TimeStamper.DATE_PROVIDER_OUT_PROPERTY.toString());
            if (tmp != null) {
                if (tmp instanceof Long) {
                    t = (Long) tmp;
                } else {
                    t = Long.parseLong(tmp.toString());
                }
            }
            if (tmp != null) {
                res = new Date(t);
            }
        }
        return res;
    }

    public void setDateClientIn(Date date) {
        if (this.me != null) {
            if (date != null) {
                this.me.setProperty(TimeStamper.DATE_CLIENT_IN_PROPERTY.toString(), new Long(date
                        .getTime()));
            } else {
                this.me.setProperty(TimeStamper.DATE_CLIENT_IN_PROPERTY.toString(), null);
            }
        }
    }

    public void setDateClientOut(Date date) {
        if (this.me != null) {
            if (date != null) {
                this.me.setProperty(TimeStamper.DATE_CLIENT_OUT_PROPERTY.toString(), new Long(date
                        .getTime()));
            } else {
                this.me.setProperty(TimeStamper.DATE_CLIENT_OUT_PROPERTY.toString(), null);
            }
        }
    }

    public void setDateProviderIn(Date date) {
        if (this.me != null) {
            if (date != null) {
                this.me.setProperty(TimeStamper.DATE_PROVIDER_IN_PROPERTY.toString(), new Long(date
                        .getTime()));
            } else {
                this.me.setProperty(TimeStamper.DATE_PROVIDER_IN_PROPERTY.toString(), null);
            }
        }
    }

    public void setDateProviderOut(Date date) {
        if (this.me != null) {
            if (date != null) {
                this.me.setProperty(TimeStamper.DATE_PROVIDER_OUT_PROPERTY.toString(), new Long(
                        date.getTime()));
            } else {
                this.me.setProperty(TimeStamper.DATE_PROVIDER_OUT_PROPERTY.toString(), null);
            }
        }
    }

    public MessageExchange getMessageExchange() {
        return this.me;
    }

    public void setMessageExchange(MessageExchange me) {
        this.me = me;
    }

}
