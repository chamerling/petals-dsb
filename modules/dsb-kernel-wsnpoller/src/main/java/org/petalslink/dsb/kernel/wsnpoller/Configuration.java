/**
 * 
 */
package org.petalslink.dsb.kernel.wsnpoller;

import javax.xml.namespace.QName;

import org.petalslink.dsb.servicepoller.api.ServicePollerInformation;
import org.w3c.dom.Document;

/**
 * @author chamerling
 * 
 */
public class Configuration {
    ServicePollerInformation toPoll;

    Document inputMessage;

    String cronExpression;

    ServicePollerInformation replyTo;

    QName topic;

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Configuration [toPoll=");
        builder.append(toPoll);
        builder.append(", inputMessage=");
        builder.append(inputMessage);
        builder.append(", cronExpression=");
        builder.append(cronExpression);
        builder.append(", replyTo=");
        builder.append(replyTo);
        builder.append(", topic=");
        builder.append(topic);
        builder.append("]");
        return builder.toString();
    }
}
