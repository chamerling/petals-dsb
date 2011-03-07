
package org.ow2.petals.kernel.ws.api;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.ow2.petals.kernel.ws.api package. 
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

    private final static QName _GetEndpoints_QNAME = new QName("http://api.ws.kernel.petals.ow2.org/", "getEndpoints");
    private final static QName _Query_QNAME = new QName("http://api.ws.kernel.petals.ow2.org/", "query");
    private final static QName _PEtALSWebServiceException_QNAME = new QName("http://api.ws.kernel.petals.ow2.org/", "PEtALSWebServiceException");
    private final static QName _QueryResponse_QNAME = new QName("http://api.ws.kernel.petals.ow2.org/", "queryResponse");
    private final static QName _GetEndpointsResponse_QNAME = new QName("http://api.ws.kernel.petals.ow2.org/", "getEndpointsResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.ow2.petals.kernel.ws.api
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Query }
     * 
     */
    public Query createQuery() {
        return new Query();
    }

    /**
     * Create an instance of {@link PEtALSWebServiceException }
     * 
     */
    public PEtALSWebServiceException createPEtALSWebServiceException() {
        return new PEtALSWebServiceException();
    }

    /**
     * Create an instance of {@link GetEndpoints }
     * 
     */
    public GetEndpoints createGetEndpoints() {
        return new GetEndpoints();
    }

    /**
     * Create an instance of {@link QueryResponse }
     * 
     */
    public QueryResponse createQueryResponse() {
        return new QueryResponse();
    }

    /**
     * Create an instance of {@link GetEndpointsResponse }
     * 
     */
    public GetEndpointsResponse createGetEndpointsResponse() {
        return new GetEndpointsResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetEndpoints }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.ws.kernel.petals.ow2.org/", name = "getEndpoints")
    public JAXBElement<GetEndpoints> createGetEndpoints(GetEndpoints value) {
        return new JAXBElement<GetEndpoints>(_GetEndpoints_QNAME, GetEndpoints.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Query }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.ws.kernel.petals.ow2.org/", name = "query")
    public JAXBElement<Query> createQuery(Query value) {
        return new JAXBElement<Query>(_Query_QNAME, Query.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PEtALSWebServiceException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.ws.kernel.petals.ow2.org/", name = "PEtALSWebServiceException")
    public JAXBElement<PEtALSWebServiceException> createPEtALSWebServiceException(PEtALSWebServiceException value) {
        return new JAXBElement<PEtALSWebServiceException>(_PEtALSWebServiceException_QNAME, PEtALSWebServiceException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link QueryResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.ws.kernel.petals.ow2.org/", name = "queryResponse")
    public JAXBElement<QueryResponse> createQueryResponse(QueryResponse value) {
        return new JAXBElement<QueryResponse>(_QueryResponse_QNAME, QueryResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetEndpointsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.ws.kernel.petals.ow2.org/", name = "getEndpointsResponse")
    public JAXBElement<GetEndpointsResponse> createGetEndpointsResponse(GetEndpointsResponse value) {
        return new JAXBElement<GetEndpointsResponse>(_GetEndpointsResponse_QNAME, GetEndpointsResponse.class, null, value);
    }

}
