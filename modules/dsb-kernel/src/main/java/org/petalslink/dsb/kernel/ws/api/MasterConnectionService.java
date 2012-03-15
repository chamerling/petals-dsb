/**
 * 
 */
package org.petalslink.dsb.kernel.ws.api;

import java.util.List;

import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.ow2.dragon.connection.api.to.Endpoint;
import org.ow2.dragon.connection.api.to.ExecutionEnvironment;

/**
 * @author chamerling
 * 
 */
@WebService(targetNamespace = "http://service.api.connection.dragon.ow2.org/dsb")
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL)
public interface MasterConnectionService {

    /**
     * Returns the list of all execution environments managed by this execution
     * environment manager.
     * 
     * @return a list of managed {@link ExecutionEnvironment}, must be non null,
     *         can be empty.
     */
    @WebResult(targetNamespace = "http://service.api.connection.dragon.ow2.org/dsb", name = "ExecutionEnvironment")
    List<ExecutionEnvironment> getManagedExecutionEnvironments();

    /**
     * Return the list of service endpoints hosted by the given Execution
     * Environment.
     * 
     * @param managedExecEnvName
     *            the name that identifies a unique Execution Environment
     * @return a list of hosted {@link Endpoint}, must be non null, can be
     *         empty.
     */
    @WebResult(targetNamespace = "http://service.api.connection.dragon.ow2.org/dsb", name = "Endpoint")
    List<Endpoint> getHostedEndpointsOnExecEnv(String managedExecEnvName);

    /**
     * Return the list of service endpoints hosted by the given Processor.
     * 
     * @param managedProcessorName
     *            the name that identifies a unique Processor
     * @return a list of hosted {@link Endpoint}, must be non null, can be
     *         empty.
     */
    @WebResult(targetNamespace = "http://service.api.connection.dragon.ow2.org/dsb", name = "Endpoint")
    List<Endpoint> getHostedEndpointsOnProcessor(String managedProcessorName);

    /**
     * Returns the list of all execution environments members of the given
     * federation.
     * 
     * @param managedFederationName
     *            the name that identifies a unique Federation
     * @return a list of members {@link ExecutionEnvironment}, must be non null,
     *         can be empty
     */
    @WebResult(targetNamespace = "http://service.api.connection.dragon.ow2.org/dsb", name = "ExecutionEnvironment")
    List<ExecutionEnvironment> getFederationMembers(String managedFederationName);

    /**
     * Returns Execution Environment Manager properties: name, etc.
     * 
     * @return a {@link ExecutionEnvironmentManager} instance representation,
     *         filled with its properties
     */
    @WebResult(targetNamespace = "http://service.api.connection.dragon.ow2.org/dsb", name = "ExecutionEnvironmentManager")
    org.ow2.dragon.connection.api.to.ExecutionEnvironmentManager getProperties();
}
