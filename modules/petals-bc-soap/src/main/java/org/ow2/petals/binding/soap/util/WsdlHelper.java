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

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

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
import org.w3c.dom.Document;
import org.ow2.easywsdl.wsdl.decorator.DecoratorDescriptionImpl;
import org.ow2.easywsdl.wsdl.impl.wsdl11.DescriptionImpl;
import org.w3c.dom.Element;


/**
 * 
 * @author Christophe HAMERLING (chamerling) - eBM WebSourcing
 * @author "Mathieu CARROLLE - eBM WebSourcing"
 * 
 */
public class WsdlHelper {

    /**
     * Creates a new instance of {@link WsdlHelper}
     * 
     */
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
     * Read soap action in the wsdl and store for the given service and endpoint name
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
    public static final Map<String, String> readAndStoreSoapAction(Document doc, QName serviceName,
            String endpointName) throws WSDL4ComplexWsdlException, URISyntaxException {
        // Map of BindingOperation/soap action
        Map<String, String> map = new HashMap<String, String>();
        Description desc = WSDL4ComplexWsdlFactory.newInstance().newWSDLReader().read(doc);
        Service srv = desc.getService(serviceName);
        Endpoint ep = srv.getEndpoint(endpointName);
        Binding bd = ep.getBinding();
        if(bd == null)
        {
            return map;
        }
        List<BindingOperation> bOperations = bd.getBindingOperations();
        for (BindingOperation op : bOperations) {
            map.put(op.getQName().getLocalPart(), op.getSoapAction());
        }
        return map;
    }

    public static BindingOperation findOperationUsingElement(Element firstElement, Description desc, String endpointName, QName serviceName) {
        BindingOperation result = null;

        //Trying to retrieve the Binding
        Binding b =desc.getService(serviceName).getEndpoint(endpointName).getBinding();

        if(firstElement != null) {
            for(BindingOperation op: b.getBindingOperations()) {
                if (op.getStyle().equals(StyleConstant.RPC)){
                    if(op.getOperation().getQName().getNamespaceURI().equals(firstElement.getNamespaceURI()) && op.getOperation().getQName().getLocalPart().equals(firstElement.getLocalName())) {
                        result = op;
                        break;
                    }
                }else{
                    QName elmtName = new QName(firstElement.getNamespaceURI(), org.ow2.easywsdl.wsdl.util.Util.getLocalPartWithoutPrefix(firstElement.getNodeName()));
                    ((DescriptionImpl) ((DecoratorDescriptionImpl)desc).getInternalObject()).getMessages();
                    
                    if(op.getOperation().getInput().getElement() != null 
                            && op.getOperation().getInput().getElement().getQName() != null
                            && op.getOperation().getInput().getElement().getQName().getLocalPart().equals(elmtName.getLocalPart())
                            && op.getOperation().getInput().getElement().getQName().getNamespaceURI().equals(elmtName.getNamespaceURI())) {
                        result = op;
                        break;
                    }
                }
            }
        }
        return result;
    }


    public static BindingOperation findOperationUsingSoapAction(String soapAction, Description desc) {
        BindingOperation res = null;
        boolean find = false;
        for(Binding b: desc.getBindings()) {
            for(BindingOperation op: b.getBindingOperations()) {
                if(op.getSoapAction() != null && op.getSoapAction().equals(soapAction)) {
                    res = op;
                    find = true;
                    break;
                }
            }
            if(find) {
                break;
            }
        }
        return res;
    }
    
    /***
     * Retrieve soapAction in the WSDL depending on the first element of the request
     * 
     * @param firstElement the first element of the request
     * @param desc the WSDL of the service
     * @return the soapAction of the operation
     */
    public static String findSoapAction(Element firstElement, Description desc, String endpointName, QName serviceName) {
        BindingOperation op =findOperationUsingElement(firstElement, desc, endpointName, serviceName);
        if (op != null){
            return op.getSoapAction();
        }else
        {
            return "";
        }
    }
    
    
}
