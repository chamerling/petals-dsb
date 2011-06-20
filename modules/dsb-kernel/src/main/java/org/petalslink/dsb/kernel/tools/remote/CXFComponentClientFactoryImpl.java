/**
 * 
 */
package org.petalslink.dsb.kernel.tools.remote;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.ow2.petals.communication.topology.TopologyService;
import org.ow2.petals.tools.ws.WebServiceHelper;
import org.petalslink.dsb.kernel.api.tools.remote.ComponentClientFactory;
import org.petalslink.dsb.kernel.api.tools.remote.RemoteComponentException;

/**
 * @author chamerling - PetalsLink
 * 
 */
public class CXFComponentClientFactoryImpl implements ComponentClientFactory {

    private TopologyService topologyService;

    public <T> T getClient(Class<T> clazz, String containerId) throws RemoteComponentException {
        T result = null;
        boolean isWs = WebServiceHelper.hasWebServiceAnnotation(clazz);
        if (isWs) {
            JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
            factory.setServiceClass(clazz);
            factory.setAddress(getAddress(containerId, clazz));
            Object o = factory.create();
            try {
                result = clazz.cast(o);
            } catch (Exception e) {
                throw new RemoteComponentException(e.getMessage());
            }
        }
        return result;
    }

    private <T> String getAddress(String containerId, Class<T> clazz) {
        return ComponentAddressResolver.getComponentAddress(
                topologyService.getContainerConfiguration(containerId), clazz);
    }

}
