/**
 * 
 */
package org.petalslink.dsb.monitoring.api;

import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;

import org.petalslink.dsb.api.MessageExchange;
import org.petalslink.dsb.api.MessageExchangeException;

/**
 * @author chamerling
 * 
 */
public class JAXBHelper {

    private static Marshaller marshaller;

    private static Unmarshaller unmarshaller;

    static {
        try {
            final JAXBContext jaxbContext = JAXBContext.newInstance(new Class[] {
                    ReportListBean.class, ReportListBean.class });

            unmarshaller = jaxbContext.createUnmarshaller();
            marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_ENCODING, "UTF-8");
            marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT,
                    java.lang.Boolean.TRUE);
        } catch (JAXBException e) {
            e.printStackTrace();
        }

    }

    /**
     * Serialize the message exchange in the {@link OutputStream}
     * 
     * @param messageExchange
     * @param os
     */
    public static final void marshall(ReportListBean reportList, OutputStream os)
            throws MessageExchangeException {
        if ((reportList != null) && (os != null)) {
            try {
                JAXBElement<ReportListBean> element = new JAXBElement<ReportListBean>(new QName(
                        "org.petalslink.dsb.monitoring.api", "ReportListBean"),
                        ReportListBean.class, null, reportList);
                
                if (element != null) {
                    synchronized (marshaller) {
                        marshaller.marshal(element, os);
                    }
                }
            } catch (JAXBException ex) {
                throw new MessageExchangeException(
                        "Can not marshall the message to the output stream", ex);
            }
        } else {
            throw new MessageExchangeException("Message exchnage of output stream is/are null");
        }
    }

    /**
     * Read the {@link InputStream} to build a {@link MessageExchange}
     * 
     * @param is
     * @return
     * @throws MessageExchangeException
     */
    public static final ReportListBean unmarshall(InputStream is) throws MessageExchangeException {
        ReportListBean m = null;
        if (is != null) {
            try {
                final JAXBElement<ReportListBean> root;
                // The default Xerces unmarshaller is not thread safe
                synchronized (unmarshaller) {
                    root = unmarshaller.unmarshal(new StreamSource(is), ReportListBean.class);
                }
                m = root.getValue();
            } catch (Exception e) {
                throw new MessageExchangeException(
                        "Can not unmarshall the message from the input stream", e);
            }
        } else {
            throw new MessageExchangeException("Input stream is null!");
        }
        return m;
    }

}
