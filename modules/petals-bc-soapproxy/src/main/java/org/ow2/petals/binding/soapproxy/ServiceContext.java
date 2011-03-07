/**
 * 
 */
package org.ow2.petals.binding.soapproxy;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.ow2.easywsdl.extensions.wsdl4complexwsdl.api.Description;

/**
 * @author chamerling - eBM WebSourcing
 * 
 */
public class ServiceContext<E> {

    /**
     * List of Axis2 modules
     */
    private List<String> modules;

    /**
     * Service parameters
     */
    private String serviceParams;

    /**
     * The service class loader
     */
    private ClassLoader classloader;

    private File policyPath;

    private String wsaTo;

    private String wsaFrom;

    private String wsaReplyTo;

    private Map<String, String> soapActionMap;
    
    private Description serviceDescription;

    /**
     * Will be provides or consumes block
     */
    private E e;

    public ServiceContext(E e) {
        this.e = e;
    }

    /**
     * @return the classloader
     */
    public ClassLoader getClassloader() {
        return classloader;
    }

    public E getConfig() {
        return this.e;
    }

    /**
     * @return the modules
     */
    public List<String> getModules() {
        return modules;
    }

    /**
     * @return the policyPath
     */
    public File getPolicyPath() {
        return policyPath;
    }

    /**
     * @return the serviceParams
     */
    public String getServiceParams() {
        return serviceParams;
    }

    public Map<String, String> getSoapActionMap() {
        return soapActionMap;
    }

    /**
     * @return the wsaFrom
     */
    public String getWsaFrom() {
        return wsaFrom;
    }

    /**
     * @return the wsaReplyTo
     */
    public String getWsaReplyTo() {
        return wsaReplyTo;
    }

    /**
     * @return the wsaTo
     */
    public String getWsaTo() {
        return wsaTo;
    }

    /**
     * @param classloader
     *            the classloader to set
     */
    public void setClassloader(ClassLoader classloader) {
        this.classloader = classloader;
    }

    /**
     * @param modules
     *            the modules to set
     */
    public void setModules(List<String> modules) {
        this.modules = modules;
    }

    /**
     * @param policyPath
     *            the policyPath to set
     */
    public void setPolicyPath(File policyPath) {
        this.policyPath = policyPath;
    }

    /**
     * @param serviceParams
     *            the serviceParams to set
     */
    public void setServiceParams(String serviceParams) {
        this.serviceParams = serviceParams;
    }

    public void setSoapActionMap(Map<String, String> soapActionMap) {
        this.soapActionMap = soapActionMap;
    }

    /**
     * @param wsaFrom
     *            the wsaFrom to set
     */
    public void setWsaFrom(String wsaFrom) {
        this.wsaFrom = wsaFrom;
    }

    /**
     * @param wsaReplyTo
     *            the wsaReplyTo to set
     */
    public void setWsaReplyTo(String wsaReplyTo) {
        this.wsaReplyTo = wsaReplyTo;
    }

    /**
     * @param wsaTo
     *            the wsaTo to set
     */
    public void setWsaTo(String wsaTo) {
        this.wsaTo = wsaTo;
    }

    public Description getServiceDescription() {
        return serviceDescription;
    }

    public void setServiceDescription(Description serviceDescription) {
        this.serviceDescription = serviceDescription;
    }
    
}
