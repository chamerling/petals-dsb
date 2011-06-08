/**
 * 
 */
package org.petalslink.dsb.kernel.ws.jbi;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.objectweb.fractal.fraclet.annotation.annotations.FractalComponent;
import org.objectweb.fractal.fraclet.annotation.annotations.Interface;
import org.objectweb.fractal.fraclet.annotation.annotations.LifeCycle;
import org.objectweb.fractal.fraclet.annotation.annotations.Monolog;
import org.objectweb.fractal.fraclet.annotation.annotations.Provides;
import org.objectweb.fractal.fraclet.annotation.annotations.Requires;
import org.objectweb.fractal.fraclet.annotation.annotations.type.LifeCycleType;
import org.objectweb.util.monolog.api.Logger;
import org.ow2.petals.container.lifecycle.ServiceAssemblyLifeCycle;
import org.ow2.petals.container.lifecycle.ServiceUnitLifeCycle;
import org.ow2.petals.jbi.descriptor.JBIDescriptorException;
import org.ow2.petals.jbi.descriptor.original.JBIDescriptorBuilder;
import org.ow2.petals.jbi.descriptor.original.generated.Jbi;
import org.ow2.petals.jbi.management.admin.AdminService;
import org.ow2.petals.util.LoggingUtil;
import org.petalslink.dsb.ws.api.DSBWebServiceException;
import org.petalslink.dsb.ws.api.jbi.ServiceArtefactsInformationService;

/**
 * @author chamerling
 * 
 */
@FractalComponent
@Provides(interfaces = { @Interface(name = "service", signature = ServiceArtefactsInformationService.class) })
public class ServiceArtefactsInformationServiceImpl implements ServiceArtefactsInformationService {

    @Monolog(name = "logger")
    private Logger logger;

    private LoggingUtil log;

    @Requires(name = "admin", signature = AdminService.class)
    private AdminService adminService;

    @LifeCycle(on = LifeCycleType.START)
    protected void start() {
        this.log = new LoggingUtil(this.logger);
    }

    @LifeCycle(on = LifeCycleType.STOP)
    protected void stop() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.ws.api.jbi.ServiceUnitInformationService#getSUForSA
     * (java.lang.String)
     */
    public Set<String> getSUForSA(String saName) throws DSBWebServiceException {
        log.start();
        ServiceAssemblyLifeCycle sa = this.adminService.getServiceAssemblyByName(saName);
        if (sa == null) {
            throw new DSBWebServiceException("SA %s not found", saName);
        }
        Set<String> result = new HashSet<String>();
        List<ServiceUnitLifeCycle> sus = sa.getServiceUnitLifeCycles();
        for (ServiceUnitLifeCycle serviceUnitLifeCycle : sus) {
            result.add(serviceUnitLifeCycle.getSuName());
        }
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.ws.api.jbi.ServiceUnitInformationService#getSUForComponent
     * (java.lang.String)
     */
    public Set<String> getSUForComponent(String componentName) throws DSBWebServiceException {
        log.start();
        throw new DSBWebServiceException("Not implemented");
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.ws.api.jbi.ServiceUnitInformationService#getSUDescription
     * (java.lang.String, java.lang.String)
     */
    public String getSUDescription(String saName, String suName) throws DSBWebServiceException {
        log.start();
        if (saName == null) {
            throw new DSBWebServiceException("SA name should not be null...");
        }
        if (suName == null) {
            throw new DSBWebServiceException("SU name should not be null...");
        }

        ServiceAssemblyLifeCycle sa = this.adminService.getServiceAssemblyByName(saName);
        if (sa == null) {
            throw new DSBWebServiceException("SA %s not found", saName);
        }

        String result = null;
        List<ServiceUnitLifeCycle> sus = sa.getServiceUnitLifeCycles();
        for (ServiceUnitLifeCycle serviceUnitLifeCycle : sus) {
            if (suName.equals(serviceUnitLifeCycle.getSuName())) {
                Jbi descriptor = serviceUnitLifeCycle.getServiceUnitDescriptor();
                try {
                    result = JBIDescriptorBuilder.buildXmlStringJBIdescriptor(descriptor);
                } catch (JBIDescriptorException e) {
                    throw new DSBWebServiceException("Error while creating descriptor", e);
                }
            }
        }
        if (result == null) {
            throw new DSBWebServiceException("No descriptor found for SU %s in SA %s", suName,
                    saName);
        }
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.petalslink.dsb.ws.api.jbi.ServiceUnitInformationService#getSAs()
     */
    public Set<String> getSAs() throws DSBWebServiceException {
        this.log.start();
        Set<String> result = new HashSet<String>();
        Set<String> names = this.adminService.getServiceAssemblies().keySet();
        if (names != null) {
            result.addAll(names);
        }
        return result;
    }

    /* (non-Javadoc)
     * @see org.petalslink.dsb.ws.api.jbi.ServiceArtefactsInformationService#getSADescription(java.lang.String)
     */
    public String getSADescription(String saName) throws DSBWebServiceException {
        log.start();
        if (saName == null) {
            throw new DSBWebServiceException("SA name should not be null...");
        }
        ServiceAssemblyLifeCycle sa = this.adminService.getServiceAssemblyByName(saName);
        if (sa == null) {
            throw new DSBWebServiceException("SA %s not found", saName);
        }
        // TODO
        throw new DSBWebServiceException("Not implemented");
    }
}
