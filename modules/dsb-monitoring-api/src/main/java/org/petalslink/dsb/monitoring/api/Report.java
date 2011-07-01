/**
 * 
 */
package org.petalslink.dsb.monitoring.api;

import java.util.Date;

/**
 * TODO : XML Bean
 * @author chamerling
 *
 */
public interface Report {

    /**
     * @param exchangeId
     */
    void setExchangeId(String exchangeId);

    /**
     * @param string
     */
    void setServiceName(String string);

    /**
     * @param endpointName
     */
    void setEndPoint(String endpointName);

    /**
     * @param string
     */
    void setOperationName(String string);

    /**
     * @param string
     */
    void setInterfaceName(String string);

    /**
     * @param string
     */
    void setConsumerName(String string);

    /**
     * @param string
     */
    void setServiceProviderName(String string);

    /**
     * @param b
     */
    void setDoesThisRequestInIsAnException(boolean b);

    /**
     * @return
     */
    long getContentLength();

    /**
     * @param contentLength
     */
    void setContentLength(long contentLength);

    /**
     * @param dateProviderOut
     */
    void setDateInGMT(Date dateProviderOut);

}
