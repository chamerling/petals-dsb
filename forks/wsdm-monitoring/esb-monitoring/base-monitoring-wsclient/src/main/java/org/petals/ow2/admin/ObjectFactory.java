
package org.petals.ow2.admin;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.petals.ow2.admin package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _WrapSoapEndpointProviderName_QNAME = new QName("", "providerName");
    private final static QName _WrapSoapEndpointClientName_QNAME = new QName("", "clientName");
    private final static QName _CreateServiceEndpointOnSoapClientFractalServiceItfName_QNAME = new QName("", "fractalServiceItfName");
    private final static QName _CreateServiceEndpointOnSoapClientClassServiceName_QNAME = new QName("", "classServiceName");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.petals.ow2.admin
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link CreateProviderResponse }
     * 
     */
    public CreateProviderResponse createCreateProviderResponse() {
        return new CreateProviderResponse();
    }

    /**
     * Create an instance of {@link ImportSoapEndpointResponse }
     * 
     */
    public ImportSoapEndpointResponse createImportSoapEndpointResponse() {
        return new ImportSoapEndpointResponse();
    }

    /**
     * Create an instance of {@link CreateProviderEndpoint }
     * 
     */
    public CreateProviderEndpoint createCreateProviderEndpoint() {
        return new CreateProviderEndpoint();
    }

    /**
     * Create an instance of {@link WrapSoapEndpoint }
     * 
     */
    public WrapSoapEndpoint createWrapSoapEndpoint() {
        return new WrapSoapEndpoint();
    }

    /**
     * Create an instance of {@link ImportSoapEndpoint }
     * 
     */
    public ImportSoapEndpoint createImportSoapEndpoint() {
        return new ImportSoapEndpoint();
    }

    /**
     * Create an instance of {@link CreateClientAndProviderResponse }
     * 
     */
    public CreateClientAndProviderResponse createCreateClientAndProviderResponse() {
        return new CreateClientAndProviderResponse();
    }

    /**
     * Create an instance of {@link AddSoapListenerResponse }
     * 
     */
    public AddSoapListenerResponse createAddSoapListenerResponse() {
        return new AddSoapListenerResponse();
    }

    /**
     * Create an instance of {@link CreateMonitoringEndpointResponse }
     * 
     */
    public CreateMonitoringEndpointResponse createCreateMonitoringEndpointResponse() {
        return new CreateMonitoringEndpointResponse();
    }

    /**
     * Create an instance of {@link CreateServiceEndpoint }
     * 
     */
    public CreateServiceEndpoint createCreateServiceEndpoint() {
        return new CreateServiceEndpoint();
    }

    /**
     * Create an instance of {@link CreateComponent }
     * 
     */
    public CreateComponent createCreateComponent() {
        return new CreateComponent();
    }

    /**
     * Create an instance of {@link CreateComponentResponse }
     * 
     */
    public CreateComponentResponse createCreateComponentResponse() {
        return new CreateComponentResponse();
    }

    /**
     * Create an instance of {@link CreateServiceEndpointOnSoapClientResponse }
     * 
     */
    public CreateServiceEndpointOnSoapClientResponse createCreateServiceEndpointOnSoapClientResponse() {
        return new CreateServiceEndpointOnSoapClientResponse();
    }

    /**
     * Create an instance of {@link ExposeInSoapOnClient }
     * 
     */
    public ExposeInSoapOnClient createExposeInSoapOnClient() {
        return new ExposeInSoapOnClient();
    }

    /**
     * Create an instance of {@link CreateProvider }
     * 
     */
    public CreateProvider createCreateProvider() {
        return new CreateProvider();
    }

    /**
     * Create an instance of {@link CreateServiceEndpointOnSoapClient }
     * 
     */
    public CreateServiceEndpointOnSoapClient createCreateServiceEndpointOnSoapClient() {
        return new CreateServiceEndpointOnSoapClient();
    }

    /**
     * Create an instance of {@link CreateMonitoringEndpoint }
     * 
     */
    public CreateMonitoringEndpoint createCreateMonitoringEndpoint() {
        return new CreateMonitoringEndpoint();
    }

    /**
     * Create an instance of {@link CreateServiceResponse }
     * 
     */
    public CreateServiceResponse createCreateServiceResponse() {
        return new CreateServiceResponse();
    }

    /**
     * Create an instance of {@link WrapSoapEndpointResponse }
     * 
     */
    public WrapSoapEndpointResponse createWrapSoapEndpointResponse() {
        return new WrapSoapEndpointResponse();
    }

    /**
     * Create an instance of {@link ExposeInSoapOnClientResponse }
     * 
     */
    public ExposeInSoapOnClientResponse createExposeInSoapOnClientResponse() {
        return new ExposeInSoapOnClientResponse();
    }

    /**
     * Create an instance of {@link CreateClientAndProvider }
     * 
     */
    public CreateClientAndProvider createCreateClientAndProvider() {
        return new CreateClientAndProvider();
    }

    /**
     * Create an instance of {@link CreateProviderEndpointResponse }
     * 
     */
    public CreateProviderEndpointResponse createCreateProviderEndpointResponse() {
        return new CreateProviderEndpointResponse();
    }

    /**
     * Create an instance of {@link CreateClientEndpointResponse }
     * 
     */
    public CreateClientEndpointResponse createCreateClientEndpointResponse() {
        return new CreateClientEndpointResponse();
    }

    /**
     * Create an instance of {@link CreateClientResponse }
     * 
     */
    public CreateClientResponse createCreateClientResponse() {
        return new CreateClientResponse();
    }

    /**
     * Create an instance of {@link CreateService }
     * 
     */
    public CreateService createCreateService() {
        return new CreateService();
    }

    /**
     * Create an instance of {@link CreateClient }
     * 
     */
    public CreateClient createCreateClient() {
        return new CreateClient();
    }

    /**
     * Create an instance of {@link AddSoapListener }
     * 
     */
    public AddSoapListener createAddSoapListener() {
        return new AddSoapListener();
    }

    /**
     * Create an instance of {@link CreateServiceEndpointResponse }
     * 
     */
    public CreateServiceEndpointResponse createCreateServiceEndpointResponse() {
        return new CreateServiceEndpointResponse();
    }

    /**
     * Create an instance of {@link CreateClientEndpoint }
     * 
     */
    public CreateClientEndpoint createCreateClientEndpoint() {
        return new CreateClientEndpoint();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link QName }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "providerName", scope = WrapSoapEndpoint.class)
    public JAXBElement<QName> createWrapSoapEndpointProviderName(QName value) {
        return new JAXBElement<QName>(_WrapSoapEndpointProviderName_QNAME, QName.class, WrapSoapEndpoint.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link QName }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "clientName", scope = WrapSoapEndpoint.class)
    public JAXBElement<QName> createWrapSoapEndpointClientName(QName value) {
        return new JAXBElement<QName>(_WrapSoapEndpointClientName_QNAME, QName.class, WrapSoapEndpoint.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link QName }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "providerName", scope = ImportSoapEndpoint.class)
    public JAXBElement<QName> createImportSoapEndpointProviderName(QName value) {
        return new JAXBElement<QName>(_WrapSoapEndpointProviderName_QNAME, QName.class, ImportSoapEndpoint.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "fractalServiceItfName", scope = CreateServiceEndpointOnSoapClient.class)
    public JAXBElement<String> createCreateServiceEndpointOnSoapClientFractalServiceItfName(String value) {
        return new JAXBElement<String>(_CreateServiceEndpointOnSoapClientFractalServiceItfName_QNAME, String.class, CreateServiceEndpointOnSoapClient.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link QName }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "providerName", scope = CreateServiceEndpointOnSoapClient.class)
    public JAXBElement<QName> createCreateServiceEndpointOnSoapClientProviderName(QName value) {
        return new JAXBElement<QName>(_WrapSoapEndpointProviderName_QNAME, QName.class, CreateServiceEndpointOnSoapClient.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link QName }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "clientName", scope = CreateServiceEndpointOnSoapClient.class)
    public JAXBElement<QName> createCreateServiceEndpointOnSoapClientClientName(QName value) {
        return new JAXBElement<QName>(_WrapSoapEndpointClientName_QNAME, QName.class, CreateServiceEndpointOnSoapClient.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "classServiceName", scope = CreateServiceEndpointOnSoapClient.class)
    public JAXBElement<String> createCreateServiceEndpointOnSoapClientClassServiceName(String value) {
        return new JAXBElement<String>(_CreateServiceEndpointOnSoapClientClassServiceName_QNAME, String.class, CreateServiceEndpointOnSoapClient.class, value);
    }

}
