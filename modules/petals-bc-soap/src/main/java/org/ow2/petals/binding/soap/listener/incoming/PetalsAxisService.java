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

package org.ow2.petals.binding.soap.listener.incoming;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.jbi.component.ComponentContext;
import javax.jbi.servicedesc.ServiceEndpoint;
import javax.xml.namespace.QName;

import org.apache.axis2.AxisFault;
import org.apache.axis2.description.AxisOperation;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.description.InOutAxisOperation;
import org.apache.axis2.engine.AxisConfiguration;
import org.ow2.easywsdl.extensions.wsdl4complexwsdl.WSDL4ComplexWsdlFactory;
import org.ow2.easywsdl.extensions.wsdl4complexwsdl.api.Description;
import org.ow2.easywsdl.schema.util.XMLPrettyPrinter;
import org.ow2.petals.binding.soap.util.WsdlHelper;
import org.ow2.petals.component.framework.api.configuration.ConfigurationExtensions;
import org.ow2.petals.component.framework.jbidescriptor.generated.Consumes;
import org.w3c.dom.Document;

/**
 * The {@link PetalsAxisService} extends an {@link AxisService}. It is
 * registered during SU deployment into the {@link AxisConfiguration}.
 * 
 * This class override the printWSDL method to dynamically generate the WSDL
 * from the JBI container.
 * 
 * @author Adrien LOUIS - EBMWebSourcing
 * @author Christophe HAMERLING - eBMWebSourcing
 * 
 */
public class PetalsAxisService extends AxisService {

    /**
     * The JBI component context
     */
    private final ComponentContext componentContext;

    /**
     * The consumes JBI extensions used in this AxisService
     */
    private final Consumes consumes;

    /**
     * The consumes cdk extensions
     */
    private final ConfigurationExtensions consumesExtensions;

    /**
     * The local URL where this service is exposed (http://HOST:PORT/PREFIX)
     * without the service name
     */
    private final String localAddress;

    /**
     * Map of operation MEPs
     */
    private final Map<QName, URI> operationMep;

    /**
     * The logger
     */
    private final Logger logger;

    /**
     * The client service name prefix
     */
    public static final String OUTGOING_SERVICE_CLIENT_PREFIX = "OutgoingWSClient";

    public static final QName NOTIFY_OPERATION_NAME = QName.valueOf("/NotifyRequest");

    public static final QName GENERIC_OPERATION_NAME = QName.valueOf("PetalsGenericOperation");

    private Document descWithoutImport;
    
    private Map<URI, Document> importsMap = null;
    
    private Description desc;
    
    /**
     * Creates a new instance of {@link PetalsAxisService}
     * 
     * @param name
     * @param componentContext
     * @param jbiEndpoint
     * @param extensions
     */
    public PetalsAxisService(final String name, final ComponentContext componentContext,
            final Consumes consumes, final ConfigurationExtensions consumesExtensions,
            final String localAddress, final PetalsReceiver petalsReceiver, final Logger logger) {
        super(name);
        this.componentContext = componentContext;
        this.consumes = consumes;
        this.localAddress = localAddress;
        this.logger = logger;
        this.consumesExtensions = consumesExtensions;
        this.operationMep = new HashMap<QName, URI>();
        
        final AxisOperation genericOperation = new InOutAxisOperation(GENERIC_OPERATION_NAME);
        genericOperation.setMessageReceiver(petalsReceiver);
        this.addOperation(genericOperation);
        final AxisOperation notifyOperation = new InOutAxisOperation(NOTIFY_OPERATION_NAME);
        notifyOperation.setMessageReceiver(petalsReceiver);
        this.addOperation(notifyOperation);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.apache.axis2.description.AxisService#printWSDL(java.io.OutputStream,
     * java.lang.String, java.lang.String)
     */
    @Override
    public void printWSDL(final OutputStream out, final String requestIP) throws AxisFault {
        this.logger.fine("Print WSDL");
        try {
            if (this.descWithoutImport == null){
                Document doc = this.getServiceDefinition();
                if (doc == null) {
                    this.printWSDLError(out,
                    "WSDL description can not been retrieved from JBI endpoint");
                }else {
                    this.generateComplexWSDL(requestIP);        
                }
            }

            XMLPrettyPrinter.prettify(this.descWithoutImport, out, XMLPrettyPrinter.getEncoding(this.descWithoutImport));
        } catch (final Exception e) {
            throw new AxisFault("Server", e);
        }
    }

    private void generateComplexWSDL(final String rootURI) throws AxisFault{

        try{
            Document doc = this.getServiceDefinition();

            // create a easyWSDL description
            this.desc = WSDL4ComplexWsdlFactory.newInstance().newWSDLReader().read(doc);
            // remove imported docs
            this.importsMap = this.desc.deleteImportedDocumentsInWsdl(new URI(rootURI));

            // recreate a Document without imports
            this.descWithoutImport = WSDL4ComplexWsdlFactory.newInstance().newWSDLWriter().getDocument(desc);
        }catch (final Exception e) {
            throw new AxisFault("Server", e);
        }

    }

    /**
     * 
     * @return
     */
    public String getLocalAddress() {
        return this.localAddress;
    }

    /**
     * Return the imported documents in the WSDL description of the services. As
     * these documents are rebuilt, the exposed web service URL is required to
     * build them correctly
     * @param requestURI the URL of the web service, typically http://..:8084/petals/services/MyWebService
     * @return
     * @throws AxisFault
     */
    public Map<URI, Document> getImportedDocuments(final String rootURI) throws AxisFault {

        // In the case an import is requested before the complexWSDL has been generated...
        if (this.descWithoutImport == null){
            this.generateComplexWSDL(rootURI);
        }

        return this.importsMap;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.apache.axis2.description.AxisService#printWSDL2(java.io.OutputStream,
     * java.lang.String)
     */
    @Override
    public void printWSDL2(final OutputStream out, final String requestIP) throws AxisFault {
        this.printWSDL(out, requestIP);
    }

    /**
     * Print the WSDL error on the output stream
     * 
     * @param out
     * @throws AxisFault
     */
    private void printWSDLError(final OutputStream out, final String message) {
        final String wsdlntfound = "<error><description>Unable to get WSDL for this service</description><reason>"
                + message + "</reason></error>";
        this.printMessage(out, wsdlntfound);
    }

    /**
     * Print a message to the output stream
     * 
     * @param out
     * @param str
     * @throws AxisFault
     */
    private void printMessage(final OutputStream out, final String str) {
        try {
            out.write(str.getBytes());
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

    /**
     * Get the service definition document (WSDL)
     * 
     * @return
     * @throws Exception
     */
    private Document getServiceDefinition() throws Exception {
        this.logger.fine("Get and update the service definition");

        Document result = null;
        final ServiceEndpoint ep = this.getServiceEndpoint();
        if (ep != null) {
            result = this.componentContext.getEndpointDescriptor(ep);
            if (result != null) {
                result = WsdlHelper.replaceServiceAddressInWSDL(result, this.localAddress + "/"
                        + this.getName());
            }
        }
        return result;
    }

    /**
     * Get the service endpoint. Try to retrieve if the endpoint name and
     * service name has been specified, else try to get it if only the interface
     * has been specified.
     * 
     * @return
     */
    public ServiceEndpoint getServiceEndpoint() {
        ServiceEndpoint jbiEndpoint = null;

        if ((this.consumes.getEndpointName() != null) && (this.consumes.getServiceName() != null)) {
            // get from EP and service values
            jbiEndpoint = this.componentContext.getEndpoint(this.consumes.getServiceName(),
                    this.consumes.getEndpointName());
        } else if (this.consumes.getInterfaceName() != null) {
            // get from ITF value
            final ServiceEndpoint[] endpoints = this.componentContext.getEndpoints(this.consumes
                    .getInterfaceName());
            if ((endpoints != null) && (endpoints.length > 0)) {
                jbiEndpoint = endpoints[0];
            }
        }
        this.logger.fine("JBI Service endpoint " + jbiEndpoint);
        return jbiEndpoint;
    }

    /**
     * @return the actionMep
     */
    public Map<QName, URI> getOperationMep() {
        return this.operationMep;
    }

    /**
     * 
     * @return the consumes related cdk extensions
     */
    public ConfigurationExtensions getConsumesCDKExtensions() {
        return this.consumesExtensions;
    }

    /**
     * 
     * @return
     */
    public Consumes getConsumes() {
        return this.consumes;
    }
    
    
    public Description getDescription() throws AxisFault{
        if (this.desc == null){
            Document doc;
            try {
                doc = this.getServiceDefinition();
                this.desc = WSDL4ComplexWsdlFactory.newInstance().newWSDLReader().read(doc);
            } catch (Exception e) {
                throw new AxisFault("Server", e);
            }
        }
        return this.desc;
    }
   
}
