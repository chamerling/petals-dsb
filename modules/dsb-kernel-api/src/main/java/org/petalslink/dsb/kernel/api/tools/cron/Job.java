/**
 * 
 */
package org.petalslink.dsb.kernel.api.tools.cron;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * @author chamerling
 * 
 */
public class Job {

    /**
     * The method that the jib must call...
     */
    private Method method;

    /**
     * The object to invoke method on
     */
    private Object target;

    /**
     * 
     */
    private long delay;

    private long period;
    
    private TimeUnit unit;

    /**
     * 
     */
    private String id;

    /**
     * 
     */
    public Job() {
    }

    /**
     * @return the method
     */
    public Method getMethod() {
        return method;
    }

    /**
     * @param method
     *            the method to set
     */
    public void setMethod(Method method) {
        this.method = method;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the target
     */
    public Object getTarget() {
        return target;
    }

    /**
     * @param target
     *            the target to set
     */
    public void setTarget(Object target) {
        this.target = target;
    }

    /**
     * @return the delay
     */
    public long getDelay() {
        return delay;
    }

    /**
     * @param delay
     *            the delay to set
     */
    public void setDelay(long delay) {
        this.delay = delay;
    }

    /**
     * @return the period
     */
    public long getPeriod() {
        return period;
    }

    /**
     * @param period
     *            the period to set
     */
    public void setPeriod(long period) {
        this.period = period;
    }

    /**
     * @return the unit
     */
    public TimeUnit getUnit() {
        return unit;
    }

    /**
     * @param unit the unit to set
     */
    public void setUnit(TimeUnit unit) {
        this.unit = unit;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Job [method=");
        builder.append(method);
        builder.append(", target=");
        builder.append(target);
        builder.append(", delay=");
        builder.append(delay);
        builder.append(", period=");
        builder.append(period);
        builder.append(", unit=");
        builder.append(unit);
        builder.append(", id=");
        builder.append(id);
        builder.append("]");
        return builder.toString();
    }
}
