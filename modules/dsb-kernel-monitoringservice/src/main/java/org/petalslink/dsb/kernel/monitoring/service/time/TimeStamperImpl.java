package org.petalslink.dsb.kernel.monitoring.service.time;

import org.ow2.petals.jbi.messaging.exchange.MessageExchangeWrapper;

public class TimeStamperImpl implements TimeStamper {

    private MessageExchangeWrapper me;

    public TimeStamperImpl() {
    }

    public long getDateClientIn() {
        long res = 0L;
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
                res = t;
            }
        }
        return res;
    }

    public long getDateClientOut() {
        long res = 0L;
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
                res = t;
            }
        }
        return res;
    }

    public long getDateProviderIn() {
        long res = 0L;
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
                res = t;
            }
        }
        return res;
    }

    public long getDateProviderOut() {
        long res = 0L;
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
                res = t;
            }
        }
        return res;
    }

    public void setDateClientIn(long date) {
        if (this.me != null) {
            if (date != 0L) {
                this.me.setProperty(TimeStamper.DATE_CLIENT_IN_PROPERTY.toString(), new Long(date));
            } else {
                this.me.setProperty(TimeStamper.DATE_CLIENT_IN_PROPERTY.toString(), null);
            }
        }
    }

    public void setDateClientOut(long date) {
        if (this.me != null) {
            if (date != 0L) {
                this.me.setProperty(TimeStamper.DATE_CLIENT_OUT_PROPERTY.toString(), new Long(date));
            } else {
                this.me.setProperty(TimeStamper.DATE_CLIENT_OUT_PROPERTY.toString(), null);
            }
        }
    }

    public void setDateProviderIn(long date) {
        if (this.me != null) {
            if (date != 0L) {
                this.me.setProperty(TimeStamper.DATE_PROVIDER_IN_PROPERTY.toString(), new Long(date));
            } else {
                this.me.setProperty(TimeStamper.DATE_PROVIDER_IN_PROPERTY.toString(), null);
            }
        }
    }

    public void setDateProviderOut(long date) {
        if (this.me != null) {
            if (date != 0L) {
                this.me.setProperty(TimeStamper.DATE_PROVIDER_OUT_PROPERTY.toString(), new Long(date));
            } else {
                this.me.setProperty(TimeStamper.DATE_PROVIDER_OUT_PROPERTY.toString(), null);
            }
        }
    }

    public MessageExchangeWrapper getMessageExchange() {
        return this.me;
    }

    public void setMessageExchange(MessageExchangeWrapper me) {
        this.me = me;
    }

}
