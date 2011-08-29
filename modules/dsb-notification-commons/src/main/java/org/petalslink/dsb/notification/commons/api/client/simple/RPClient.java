/**
 * 
 */
package org.petalslink.dsb.notification.commons.api.client.simple;

import java.util.List;

import javax.xml.namespace.QName;

import org.petalslink.dsb.notification.commons.NotificationException;

/**
 * @author chamerling
 *
 */
public interface RPClient {
    
    /**
     * 
     * @return
     */
    List<QName> getTopics() throws NotificationException;

}
