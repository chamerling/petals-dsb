/**
 * 
 */
package org.ow2.petals.binding.soap;

import java.util.List;

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
    
    /**
     * The service description
     */
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
     * @return the serviceParams
     */
    public String getServiceParams() {
        return serviceParams;
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
     * @param serviceParams
     *            the serviceParams to set
     */
    public void setServiceParams(String serviceParams) {
        this.serviceParams = serviceParams;
    }

    public Description getServiceDescription() {
        return serviceDescription;
    }

    public void setServiceDescription(Description serviceDescription) {
        this.serviceDescription = serviceDescription;
    }
    
}
