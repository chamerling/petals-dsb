/**
 * 
 */
package org.petalslink.dsb.wsn.cxf;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.Validator;

import com.ebmwebsourcing.wsstar.basenotification.datatypes.impl.WsnbJAXBContext;

/**
 * @author chamerling
 *
 */
public class WSNContext extends JAXBContext {
    
    private WsnbJAXBContext context;
    
    public WSNContext(WsnbJAXBContext context) {
        this.context = context;
    }

    /* (non-Javadoc)
     * @see javax.xml.bind.JAXBContext#createUnmarshaller()
     */
    @Override
    public Unmarshaller createUnmarshaller() throws JAXBException {
        System.out.println("CREATE UNMARSH");
        return context.createWSNotificationUnmarshaller();
    }

    /* (non-Javadoc)
     * @see javax.xml.bind.JAXBContext#createMarshaller()
     */
    @Override
    public Marshaller createMarshaller() throws JAXBException {
        System.out.println("CREATE MARSH");
        return context.createWSNotificationMarshaller();
    }

    /* (non-Javadoc)
     * @see javax.xml.bind.JAXBContext#createValidator()
     */
    @Override
    public Validator createValidator() throws JAXBException {
        return context.getJaxbContext().createValidator();
    }

}
