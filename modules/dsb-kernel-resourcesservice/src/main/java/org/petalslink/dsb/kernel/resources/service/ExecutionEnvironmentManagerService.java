/**
 * 
 */
package org.petalslink.dsb.kernel.resources.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.objectweb.fractal.fraclet.annotation.annotations.FractalComponent;
import org.objectweb.fractal.fraclet.annotation.annotations.Interface;
import org.objectweb.fractal.fraclet.annotation.annotations.LifeCycle;
import org.objectweb.fractal.fraclet.annotation.annotations.Monolog;
import org.objectweb.fractal.fraclet.annotation.annotations.Provides;
import org.objectweb.fractal.fraclet.annotation.annotations.Requires;
import org.objectweb.fractal.fraclet.annotation.annotations.type.LifeCycleType;
import org.objectweb.util.monolog.api.Logger;
import org.ow2.easywsdl.extensions.wsdl4complexwsdl.WSDL4ComplexWsdlFactory;
import org.ow2.easywsdl.extensions.wsdl4complexwsdl.api.Description;
import org.ow2.easywsdl.extensions.wsdl4complexwsdl.api.WSDL4ComplexWsdlException;
import org.ow2.petals.jbi.messaging.registry.EndpointRegistry;
import org.ow2.petals.jbi.messaging.registry.RegistryException;
import org.ow2.petals.kernel.configuration.ConfigurationService;
import org.ow2.petals.util.LoggingUtil;
import org.ow2.petals.util.XMLUtil;
import org.petalslink.dsb.api.ServiceEndpoint;
import org.petalslink.dsb.jbi.Adapter;
import org.petalslink.dsb.kernel.api.DSBConfigurationService;
import org.w3c.dom.Document;

import com.petalslink.easyresources.execution_environment_connection_api._1.GetAdditionalContent;
import com.petalslink.easyresources.execution_environment_connection_api._1.GetAdditionalContentResponse;
import com.petalslink.easyresources.execution_environment_connection_api._1.GetContent;
import com.petalslink.easyresources.execution_environment_connection_api._1.GetContentResponse;
import com.petalslink.easyresources.execution_environment_connection_api._1.GetResourceIdentifiers;
import com.petalslink.easyresources.execution_environment_connection_api._1.GetResourceIdentifiersResponse;
import com.petalslink.easyresources.execution_environment_connection_api._1_0.ExecutionEnvironmentManager;
import com.petalslink.easyresources.execution_environment_connection_api._1_0.GetAdditionalContentFault;
import com.petalslink.easyresources.execution_environment_connection_api._1_0.GetContentFault;
import com.petalslink.easyresources.execution_environment_connection_api._1_0.GetExecutionEnvironmentInformationFault;
import com.petalslink.easyresources.execution_environment_connection_api._1_0.GetResourceIdentifiersFault;
import com.petalslink.easyresources.execution_environment_connection_model.ExecutionEnvironmentInformationType;
import com.petalslink.easyresources.execution_environment_connection_model.ExecutionEnvironmentInformationTypeType;
import com.petalslink.easyresources.execution_environment_connection_model.InterfaceConnector;
import com.petalslink.easyresources.execution_environment_connection_model.ResourceIdentifier;

/**
 * @author chamerling
 * 
 */
@FractalComponent
@Provides(interfaces = { @Interface(name = "service", signature = ExecutionEnvironmentManager.class) })
public class ExecutionEnvironmentManagerService implements ExecutionEnvironmentManager {

    @Requires(name = "endpoint", signature = EndpointRegistry.class)
    private EndpointRegistry endpointRegistry;

    @Requires(name = "configuration", signature = ConfigurationService.class)
    private ConfigurationService configurationService;

    @Requires(name = "dsbconfiguration", signature = DSBConfigurationService.class)
    private DSBConfigurationService dsbConfigurationService;

    @Monolog(name = "logger")
    private Logger logger;

    private LoggingUtil log;

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
     * com.petalslink.easyresources.execution_environment_connection_api._1_0
     * .ExecutionEnvironmentManager
     * #getAdditionalContent(com.petalslink.easyresources
     * .execution_environment_connection_api._1.GetAdditionalContent)
     */
    public GetAdditionalContentResponse getAdditionalContent(
            GetAdditionalContent getAdditionalContent) throws GetAdditionalContentFault {
        if (log.isInfoEnabled()) {
            log.info("Got a getAdditionalContent call");
        }
        GetAdditionalContentResponse response = new GetAdditionalContentResponse();

        ServiceEndpoint se = getEndpoint(getAdditionalContent.getResourceIdentifier().getId());
        if (se == null) {
            throw new GetAdditionalContentFault(String.format(
                    "Impossible to find endpoint corresponding to this qname: %s",
                    getAdditionalContent.getResourceIdentifier().getId()));
        }

        String relativePath = getAdditionalContent.getId();
        Document doc = getDescription(se.getDescription());

        Document importDesc = getImport(doc, relativePath);
        if (importDesc == null) {
            throw new GetAdditionalContentFault(String.format(
                    "Impossible to find import corresponding to %s on endpoint %s", relativePath,
                    getAdditionalContent.getResourceIdentifier().getId()));
        }
        response.setAny(importDesc.getDocumentElement());
        return response;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.petalslink.easyresources.execution_environment_connection_api._1_0
     * .ExecutionEnvironmentManager
     * #getResourceIdentifiers(com.petalslink.easyresources
     * .execution_environment_connection_api._1.GetResourceIdentifiers)
     */
    public GetResourceIdentifiersResponse getResourceIdentifiers(
            GetResourceIdentifiers getResourceIdentifiers) throws GetResourceIdentifiersFault {
        if (log.isInfoEnabled()) {
            log.info("Got a getResourceIdentifiers call");
        }

        GetResourceIdentifiersResponse response = new GetResourceIdentifiersResponse();

        // get all the endpoints
        List<ServiceEndpoint> endpoints = null;
        try {
            endpoints = getEndpoints();
        } catch (RegistryException e) {
            e.printStackTrace();
            throw new GetResourceIdentifiersFault(
                    "Error while trying to get endpoints from infrastructure");
        }
        for (ServiceEndpoint serviceEndpoint : endpoints) {
            ResourceIdentifier epInfo = new ResourceIdentifier();
            epInfo.setId(ResourceIdBuilder.getId(serviceEndpoint));
            epInfo.setResourceType("endpoint");
            response.getResourceIdentifier().add(epInfo);
        }
        return response;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.petalslink.easyresources.execution_environment_connection_api._1_0
     * .ExecutionEnvironmentManager
     * #getContent(com.petalslink.easyresources.execution_environment_connection_api
     * ._1.GetContent)
     */
    public GetContentResponse getContent(GetContent getContent) throws GetContentFault {
        if (log.isInfoEnabled()) {
            log.info("Got a getContent call");
        }

        GetContentResponse response = new GetContentResponse();

        // only get the WSDLs for now...

        // get the endpoint from its ID
        ServiceEndpoint se = getEndpoint(getContent.getResourceIdentifier().getId());
        if (se == null) {
            throw new GetContentFault(String.format(
                    "Impossible to find endpoint corresponding to this qname: %s", getContent
                            .getResourceIdentifier().getId()));
        }

        String description = se.getDescription();
        if (description == null) {
            throw new GetContentFault(String.format(
                    "No service description found for endpoint: %s", getContent
                            .getResourceIdentifier().getId()));
        }

        Document doc = getDescription(se.getDescription());
        if (doc == null) {
            throw new GetContentFault("Can not generate WSDL document from endpoint description");
        }
        response.setAny(doc.getDocumentElement());
        return response;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.petalslink.easyresources.execution_environment_connection_api._1_0
     * .ExecutionEnvironmentManager#getExecutionEnvironmentInformation()
     */
    public ExecutionEnvironmentInformationType getExecutionEnvironmentInformation()
            throws GetExecutionEnvironmentInformationFault {
        if (log.isInfoEnabled()) {
            log.info("Got a getExecutionEnvironmentInformation call");
        }

        ExecutionEnvironmentInformationType result = new ExecutionEnvironmentInformationType();

        // Get local node information
        String nodeName = configurationService.getContainerConfiguration().getName();
        String nodeVersion = "1.0-SNAPSHOT";
        result.setName(nodeName);
        result.setType(ExecutionEnvironmentInformationTypeType.ESB);
        result.setVersion(nodeVersion);

        // interface connectors
        com.petalslink.easyresources.execution_environment_connection_model.ObjectFactory objFactory = new com.petalslink.easyresources.execution_environment_connection_model.ObjectFactory();

        InterfaceConnector itfSubscribeProducer = new InterfaceConnector();
        itfSubscribeProducer.setId(objFactory
                .createInterfaceConnectorId("resourcesSubscriptionEndpoint"));
        itfSubscribeProducer.setInterfaceName(new QName(
                "http://www.petalslink.com/wsn/service/WsnProducer", "NotificationProducer"));
        itfSubscribeProducer.setEndpointAddress(getNotificationEndpoint());
        result.getInterfaceConnector().add(itfSubscribeProducer);

        InterfaceConnector itfUnSubscribeProducer = new InterfaceConnector();
        itfUnSubscribeProducer.setId(objFactory
                .createInterfaceConnectorId("resourcesUnSubscriptionEndpoint"));
        itfUnSubscribeProducer
                .setInterfaceName(new QName("http://www.petalslink.com/wsn/service/WsnProducer",
                        "PausableSubscriptionManager"));
        itfUnSubscribeProducer.setEndpointAddress(getProducerEndpoint());
        result.getInterfaceConnector().add(itfUnSubscribeProducer);

        return result;
    }

    /**
     * @param description
     * @return
     */
    private Document getDescription(String description) {
        Description desc;
        try {
            desc = WSDL4ComplexWsdlFactory.newInstance().newWSDLReader()
                    .read(XMLUtil.createDocumentFromString(description));
            return WSDL4ComplexWsdlFactory.newInstance().newWSDLWriter().getDocument(desc);

        } catch (WSDL4ComplexWsdlException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param doc
     * @param relativePath
     * @return
     */
    private Document getImport(Document doc, String relativePath) {
        if (log.isDebugEnabled()) {
            log.debug(String.format("Try to get import from %s", relativePath));
        }

        try {
            Description desc = WSDL4ComplexWsdlFactory.newInstance().newWSDLReader().read(doc);
            Map<URI, org.w3c.dom.Document> imports = desc.deleteImportedDocumentsInWsdl();
            if (imports != null) {
                for (URI uri : imports.keySet()) {
                    System.out.printf("Import URI %s", uri.toString());
                    if (uri.toString().equals(relativePath)) {
                        return imports.get(uri);
                    }
                }
            }
        } catch (WSDL4ComplexWsdlException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param id
     * @return
     */
    private ServiceEndpoint getEndpoint(String id) {
        ServiceEndpoint result = null;
        List<org.ow2.petals.jbi.messaging.endpoint.ServiceEndpoint> endpoints;
        try {
            endpoints = endpointRegistry.getEndpoints();
        } catch (RegistryException e) {
            e.printStackTrace();
            return null;
        }

        for (org.ow2.petals.jbi.messaging.endpoint.ServiceEndpoint serviceEndpoint : endpoints) {
            if (serviceEndpoint.getEndpointName().equals(ResourceIdBuilder.getEndpointName(id))
                    && serviceEndpoint.getLocation().getComponentName()
                            .equals(ResourceIdBuilder.getComponent(id))
                    && serviceEndpoint.getLocation().getContainerName()
                            .equals(ResourceIdBuilder.getContainer(id))
                    && serviceEndpoint.getLocation().getSubdomainName()
                            .equals(ResourceIdBuilder.getDomain(id))) {
                return Adapter.createServiceEndpoint(serviceEndpoint);
            }
        }
        return result;
    }

    /**
     * @return
     */
    private String getProducerEndpoint() {
        return dsbConfigurationService.getWSKernelBaseURL() + "NotificationProducer";
    }

    /**
     * @return
     */
    private String getNotificationEndpoint() {
        return dsbConfigurationService.getWSKernelBaseURL() + "NotificationConsumer";
    }

    /**
     * @return
     * @throws RegistryException
     */
    private List<ServiceEndpoint> getEndpoints() throws RegistryException {
        final List<ServiceEndpoint> result = new ArrayList<ServiceEndpoint>();
        List<org.ow2.petals.jbi.messaging.endpoint.ServiceEndpoint> endpoints = this.endpointRegistry
                .getEndpoints();

        for (org.ow2.petals.jbi.messaging.endpoint.ServiceEndpoint serviceEndpoint : endpoints) {
            ServiceEndpoint se = Adapter.createServiceEndpoint(serviceEndpoint);
            result.add(se);
        }

        return result;
    }
}
