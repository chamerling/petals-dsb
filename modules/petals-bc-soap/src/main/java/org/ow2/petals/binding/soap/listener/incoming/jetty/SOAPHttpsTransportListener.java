package org.ow2.petals.binding.soap.listener.incoming.jetty;

import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.context.SessionContext;
import org.apache.axis2.description.TransportInDescription;
import org.apache.axis2.transport.TransportListener;
import org.ow2.petals.binding.soap.listener.incoming.SoapServerConfig;

public class SOAPHttpsTransportListener implements TransportListener {

    private SoapServerConfig config;

    public SOAPHttpsTransportListener(SoapServerConfig config) {
        this.config = config;
    }
    
    @Override
    public void destroy() {
        // TODO Auto-generated method stub
    }

    @Override
    public EndpointReference getEPRForService(String serviceName, String ip) throws AxisFault {
        return this.getEPRsForService(serviceName, ip)[0];
    }

    @Override
    public EndpointReference[] getEPRsForService(String serviceName, String ip) throws AxisFault {
        if (this.config != null) {
            return this.config.getEPRsForAxisService(serviceName, Constants.TRANSPORT_HTTPS);
        } else {
            throw new AxisFault("Unable to generate EPR for the transport: " + Constants.TRANSPORT_HTTPS);
        }
    }

    @Override
    public SessionContext getSessionContext(MessageContext messageContext) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void init(ConfigurationContext axisConf, TransportInDescription transprtInDesc)
            throws AxisFault {
        this.config.initTransportListenerForAxis(transprtInDesc, Constants.TRANSPORT_HTTPS);
    }

    @Override
    public void start() throws AxisFault {
        // TODO Auto-generated method stub
    }

    @Override
    public void stop() throws AxisFault {
        // TODO Auto-generated method stub
    }

}
