/**
 * PETALS - PETALS Services Platform. Copyright (c) 2007 EBM Websourcing,
 * http://www.ebmwebsourcing.com/
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * -------------------------------------------------------------------------
 * $Id$
 * -------------------------------------------------------------------------
 */

package org.ow2.petals.binding.soap.util;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jbi.JBIException;
import javax.jbi.component.ComponentContext;
import javax.jbi.servicedesc.ServiceEndpoint;
import javax.xml.namespace.QName;

import org.apache.axis2.AxisFault;
import org.ow2.easywsdl.extensions.wsdl4complexwsdl.WSDL4ComplexWsdlFactory;
import org.ow2.easywsdl.extensions.wsdl4complexwsdl.api.Description;
import org.ow2.easywsdl.extensions.wsdl4complexwsdl.api.WSDL4ComplexWsdlException;
import org.ow2.easywsdl.extensions.wsdl4complexwsdl.api.WSDL4ComplexWsdlReader;
import org.ow2.easywsdl.extensions.wsdl4complexwsdl.api.WSDL4ComplexWsdlWriter;
import org.ow2.easywsdl.wsdl.api.Binding;
import org.ow2.easywsdl.wsdl.api.BindingOperation;
import org.ow2.easywsdl.wsdl.api.Endpoint;
import org.ow2.easywsdl.wsdl.api.Service;
import org.ow2.easywsdl.wsdl.api.abstractItf.AbsItfBinding.StyleConstant;
import org.ow2.petals.component.framework.jbidescriptor.generated.Consumes;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import static org.ow2.petals.binding.soap.SoapConstants.SOAP.FAULT_SERVER;

import com.ebmwebsourcing.easycommons.xml.XMLPrettyPrinter;

public final class WsdlHelper {

    private WsdlHelper() {
    }

    /**
     * Replace the endpoint address in the WSDL
     * 
     * @return
     * @throws WSDL4ComplexWsdlException
     * @throws URISyntaxException
     */
    public static final Document replaceServiceAddressInWSDL(final Document doc,
            final String address) throws WSDL4ComplexWsdlException, URISyntaxException {
        Document result = null;
        final WSDL4ComplexWsdlFactory wsdlFactory = WSDL4ComplexWsdlFactory.newInstance();
        final WSDL4ComplexWsdlWriter wsdlWriter = wsdlFactory.newWSDLWriter();
        final WSDL4ComplexWsdlReader reader = wsdlFactory.newWSDLReader();
        final Description desc = reader.read(doc);
        final List<Service> services = desc.getServices();
        for (final Service service : services) {
            final List<Endpoint> endpoints = service.getEndpoints();
            for (final Endpoint endpoint : endpoints) {
                endpoint.setAddress(address);
            }
        }
        result = wsdlWriter.getDocument(desc);
        return result;
    }

    /***
     * Read soap action in the wsdl and store for the given service and endpoint
     * name
     * 
     * @param doc
     *            associated wsdl
     * @param serviceName
     * @param endpointName
     * @return a map where the keys is a bindingOperation name and the value is
     *         the associated soap action
     * @throws URISyntaxException
     * @throws WSDL4ComplexWsdlException
     */
    public static final Map<String, String> readAndStoreSoapAction(final Document doc,
            final QName serviceName, final String endpointName) throws WSDL4ComplexWsdlException,
            URISyntaxException {
        // Map of BindingOperation/soap action
        final Map<String, String> map = new HashMap<String, String>();
        final Description desc = WSDL4ComplexWsdlFactory.newInstance().newWSDLReader().read(doc);
        final Service srv = desc.getService(serviceName);
        final Endpoint ep = srv.getEndpoint(endpointName);
        final Binding bd = ep.getBinding();
        if (bd == null) {
            return map;
        }
        final List<BindingOperation> bOperations = bd.getBindingOperations();
        for (final BindingOperation op : bOperations) {
            map.put(op.getQName().getLocalPart(), op.getSoapAction());
        }
        return map;
    }

    public static BindingOperation findOperationUsingElement(final Element firstElement,
            final Description desc, final String endpointName, final QName serviceName) {
        BindingOperation result = null, guess = null;

        // Trying to retrieve the Binding
        final Binding b = desc.getService(serviceName).getEndpoint(endpointName).getBinding();

        if (firstElement != null) {
            for (final BindingOperation op : b.getBindingOperations()) {
                if (op.getStyle().equals(StyleConstant.RPC)) {
                    if (op.getOperation().getQName().getNamespaceURI()
                            .equals(firstElement.getNamespaceURI())
                            && op.getOperation().getQName().getLocalPart()
                                    .equals(firstElement.getLocalName())) {
                        result = op;
                        break;
                    }
                } else {
                    final QName elmtName = new QName(firstElement.getNamespaceURI(),
                            org.ow2.easywsdl.wsdl.util.Util.getLocalPartWithoutPrefix(firstElement
                                    .getNodeName()));

                    final org.ow2.easywsdl.schema.api.Element e = op.getOperation().getInput()
                            .getElement();
                    if (e != null && e.getQName() != null
                            && e.getQName().getLocalPart().equals(elmtName.getLocalPart())) {

                        guess = op;
                        if (e.getQName().getNamespaceURI().equals(elmtName.getNamespaceURI())) {
                            result = op;
                            break;
                        }
                    }
                }
            }
        }
        if (result == null) {
            result = guess; // Best choice possible... URI not matched, but
                            // localpart OK
        }
        return result;
    }

    public static ArrayList<BindingOperation> findOperationUsingSoapAction(final String soapAction,
            final Description desc) {
        ArrayList<BindingOperation> bindingOperations = new ArrayList<BindingOperation>();

        for (final Binding b : desc.getBindings()) {
            for (final BindingOperation op : b.getBindingOperations()) {
                if (op.getSoapAction() != null && op.getSoapAction().equals(soapAction)) {
                    bindingOperations.add(op);
                }
            }
        }

        return bindingOperations;
    }

    /***
     * Retrieve soapAction in the WSDL depending on the first element of the
     * request
     * 
     * @param firstElement
     *            the first element of the request
     * @param desc
     *            the WSDL of the service
     * @return the soapAction of the operation
     */
    public static String findSoapAction(final Element firstElement, final Description desc,
            final String endpointName, final QName serviceName) {
        final BindingOperation op = findOperationUsingElement(firstElement, desc, endpointName,
                serviceName);
        if (op != null) {
            return op.getSoapAction();
        } else {
            return "";
        }
    }

    /**
     * Get the service definition document (WSDL)
     */
    public static final Description getDescription(Consumes consumes,
            ComponentContext componentContext, Logger logger) throws AxisFault {
        Description desc = null;

        try {
            Document doc = getServiceDefinition(consumes, componentContext, logger);

            if (doc != null) {
                desc = WSDL4ComplexWsdlFactory.newInstance().newWSDLReader().read(doc);
            }
        } catch (WSDL4ComplexWsdlException e) {
            throw new AxisFault(FAULT_SERVER, e);
        } catch (JBIException e) {
            throw new AxisFault(FAULT_SERVER, e);
        } catch (URISyntaxException e) {
            throw new AxisFault(FAULT_SERVER, e);
        }

        return desc;
    }

    /**
     * Get the service definition document (WSDL) with the updated service
     * address
     */
    public static final Document getServiceDefinition(Consumes consumes,
            ComponentContext componentContext, Logger logger, String serviceAddress)
            throws JBIException, WSDL4ComplexWsdlException, URISyntaxException {
        Document result = getServiceDefinition(consumes, componentContext, logger);

        if (result != null) {
            result = WsdlHelper.replaceServiceAddressInWSDL(result, serviceAddress);
        }

        return result;
    }

    /**
     * Get the service definition document (WSDL)
     */
    public static final Document getServiceDefinition(Consumes consumes,
            ComponentContext componentContext, Logger logger) throws JBIException,
            WSDL4ComplexWsdlException, URISyntaxException {
        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "Get the service definition");
        }

        Document result = null;
        final ServiceEndpoint ep = getServiceEndpoint(consumes, componentContext);
        logger.fine("JBI Service endpoint " + ep);
        if (ep != null) {
            result = componentContext.getEndpointDescriptor(ep);
        }

        return result;
    }

    /**
     * Get the service endpoint. Try to retrieve if the endpoint name and
     * service name has been specified, else try to get it if only the interface
     * has been specified.
     */
    private static final ServiceEndpoint getServiceEndpoint(Consumes consumes,
            ComponentContext componentContext) {
        ServiceEndpoint jbiEndpoint = null;

        final String endpointName = consumes.getEndpointName();
        final QName serviceName = consumes.getServiceName();
        final QName itf = consumes.getInterfaceName();
        if (endpointName != null && serviceName != null) {
            // get from EP and service values
            jbiEndpoint = componentContext.getEndpoint(serviceName, endpointName);
        } else if (serviceName != null) {
            // get from service name
            final ServiceEndpoint[] endpoints = componentContext
                    .getEndpointsForService(serviceName);
            if ((endpoints != null) && (endpoints.length > 0)) {
                jbiEndpoint = endpoints[0];
            }
        } else {
            ServiceEndpoint[] endpoints = componentContext.getEndpoints(itf);
            if ((endpoints != null) && (endpoints.length > 0)) {
                jbiEndpoint = endpoints[0];
            }
        }

        return jbiEndpoint;
    }

    public static final void printWSDL(final Logger logger, final Consumes consumes,
            final ComponentContext componentContext, final OutputStream out, final String serviceAddress) throws AxisFault {

        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "Print WSDL");
        }

        try {

            Document doc = WsdlHelper.getServiceDefinition(consumes, componentContext, logger, serviceAddress);

            if (doc == null) {
                printWSDLError(out, "WSDL description can not been retrieved from JBI endpoint");
            } else {
                // create a easyWSDL description
                Description desc = WSDL4ComplexWsdlFactory.newInstance().newWSDLReader().read(doc);

                // delete imported documents
                desc.deleteImportedDocumentsInWsdl(new URI(serviceAddress + "?wsdl="));

                // recreate a Document without imports
                Document descWithoutImport = WSDL4ComplexWsdlFactory.newInstance().newWSDLWriter()
                        .getDocument(desc);
                try {
                    XMLPrettyPrinter.prettify(descWithoutImport, out,
                            XMLPrettyPrinter.getEncoding(descWithoutImport));
                } catch (Exception e) {
                    throw new AxisFault(FAULT_SERVER, e);
                }
            }
        } catch (WSDL4ComplexWsdlException e) {
            throw new AxisFault(FAULT_SERVER, e);
        } catch (JBIException e) {
            throw new AxisFault(FAULT_SERVER, e);
        } catch (URISyntaxException e) {
            throw new AxisFault(FAULT_SERVER, e);
        }
    }

    /**
     * Return the imported documents in the WSDL description of the services. As
     * these documents are rebuilt, the exposed web service URL is required to
     * build them correctly
     * 
     * @param logger
     *            the logger
     * @param consumes
     *            the consumes
     * @param componentContext
     *            the component context
     * @param requestURL
     *            the URL of the web service, typically
     *            http://..:8084/petals/services/MyWebService
     * @return
     * @throws AxisFault
     */
    public static final Map<URI, Document> getImportedDocuments(final Logger logger,
            final Consumes consumes, final ComponentContext componentContext,
            final String requestURL) throws AxisFault {
        Map<URI, Document> importsMap = null;

        // In the case an import is requested before the complexWSDL has been
        // generated...
        try {
            Document doc = WsdlHelper.getServiceDefinition(consumes, componentContext, logger);

            if (doc == null) {
                throw new AxisFault("Server",
                        "WSDL description can not been retrieved from JBI endpoint");
            } else {
                // create a easyWSDL description
                Description desc = WSDL4ComplexWsdlFactory.newInstance().newWSDLReader().read(doc);

                // remove imported docs
                importsMap = desc.deleteImportedDocumentsInWsdl(new URI(requestURL));
            }
        } catch (WSDL4ComplexWsdlException wsdl4cwe) {
            throw new AxisFault(FAULT_SERVER, wsdl4cwe);
        } catch (JBIException jbie) {
            throw new AxisFault(FAULT_SERVER, jbie);
        } catch (URISyntaxException urise) {
            throw new AxisFault(FAULT_SERVER, urise);
        }

        return importsMap;
    }

    /**
     * Print the WSDL error on the output stream
     * 
     * @param out
     *            the output stream
     * @param message
     *            a message describing the reason of the error
     * @throws AxisFault
     */
    private static final void printWSDLError(final OutputStream out, final String message) {
        final String errorMessage = "<error><description>Unable to get WSDL for this service</description><reason>"
                + message + "</reason></error>";

        try {
            out.write(errorMessage.getBytes());
            out.flush();
        } catch (final IOException e) {
        } finally {
            try {
                out.close();
            } catch (final IOException e) {
                // Do nothing
            }
        }
    }
}
