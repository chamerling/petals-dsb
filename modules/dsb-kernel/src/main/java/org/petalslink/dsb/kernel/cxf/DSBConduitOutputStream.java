/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.petalslink.dsb.kernel.cxf;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jws.WebService;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;

import org.apache.cxf.Bus;
import org.apache.cxf.common.logging.LogUtils;
import org.apache.cxf.io.CachedOutputStream;
import org.apache.cxf.message.Exchange;
import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageImpl;
import org.apache.cxf.service.model.BindingOperationInfo;
import org.apache.cxf.ws.addressing.EndpointReferenceType;
import org.apache.cxf.wsdl.EndpointReferenceUtils;
import org.ow2.petals.registry.api.util.XMLUtil;
import org.petalslink.dsb.api.ServiceEndpoint;
import org.petalslink.dsb.kernel.io.Constants;
import org.petalslink.dsb.kernel.io.client.ClientFactoryRegistry;
import org.petalslink.dsb.kernel.service.EndpointHelper;
import org.petalslink.dsb.service.client.Client;
import org.petalslink.dsb.service.client.ClientFactory;
import org.petalslink.dsb.xmlutils.XMLHelper;
import org.w3c.dom.Document;

/**
 * This is a fake output stream implementation since the default CXF behaviour
 * works with outputstreams to send messages to remotes. Here we just send
 * Petals message on {@link #close()} call, get the response and send it back to
 * CXF used the observer.
 * 
 * @author chamerling
 * 
 */
public class DSBConduitOutputStream extends CachedOutputStream {
    
    public static final String CLEAN_ENDPOINT = "jbi.client.CLEAN_ENDPOINT";

    private static final Logger LOG = LogUtils.getL7dLogger(DSBConduitOutputStream.class);

    private Message message;

    private boolean isOneWay;

    private DSBConduit conduit;

    private EndpointReferenceType target;

    public DSBConduitOutputStream(Message m, EndpointReferenceType target,
            DSBConduit conduit) {
        message = m;
        this.conduit = conduit;
        this.target = target;
    }

    @Override
    protected void doFlush() throws IOException {

    }

    @Override
    protected void doClose() throws IOException {
        isOneWay = message.getExchange().isOneWay();
        sendOutputMessage();
        if (target != null) {
            target.getClass();
        }
    }

    private void sendOutputMessage() throws IOException {
        try {
            Member member = (Member) message.get(Method.class.getName());
            Class<?> clz = member.getDeclaringClass();
            Exchange exchange = message.getExchange();
            final BindingOperationInfo bop = exchange.get(BindingOperationInfo.class);

            WebService ws = clz.getAnnotation(WebService.class);
            assert ws != null;
            // TODO : Fix is annotation is not parametrized ie ws.name and ws*
            // are not set
            final QName interfaceName = new QName(ws.targetNamespace(), ws.name());
            QName serviceName = null;
            if (target != null) {
                serviceName = EndpointReferenceUtils.getServiceName(target, message.getExchange()
                        .get(Bus.class));
            } else {
                serviceName = message.getExchange().get(org.apache.cxf.service.Service.class)
                        .getName();
            }

            final QName service = serviceName;
            final String endpointName = EndpointHelper.getInstance()
                    .getEndpoint(
                            DSBConduitOutputStream.this.message.get(Message.ENDPOINT_ADDRESS)
                                    .toString());
            final Document doc = XMLHelper.createDocument(new StreamSource(this.getInputStream()),
                    true);
            org.petalslink.dsb.service.client.Message message = new org.petalslink.dsb.service.client.Message() {

                private Map<String, String> properties = new HashMap<String, String>();

                public QName getService() {
                    return service;
                }

                public Map<String, String> getProperties() {
                    return properties;
                }

                public Document getPayload() {
                    return doc;
                }

                public QName getOperation() {
                    return bop.getName();
                }

                public QName getInterface() {
                    return interfaceName;
                }

                public Map<String, Document> getHeaders() {
                    return new HashMap<String, Document>();
                }

                public String getEndpoint() {
                    return endpointName;
                }
            };

            message.getProperties().put(Constants.MESSAGE_TYPE, Constants.DSB_INVOKE);
            message.getProperties().put(
                    Message.ENDPOINT_ADDRESS,
                    this.message.get(Message.ENDPOINT_ADDRESS) != null ? this.message.get(
                            Message.ENDPOINT_ADDRESS).toString() : null);

            // put service, einterface and endpoint in properties so that the
            // petals router does not search for a given endpoint...
            message.getProperties().put(Constants.SERVICE_NAME, serviceName.toString());
            message.getProperties().put(Constants.ENDPOINT_NAME, endpointName.toString());
            message.getProperties().put(Constants.ITF_NAME, interfaceName.toString());
            message.getProperties().put(CLEAN_ENDPOINT, Boolean.TRUE.toString());

            if (LOG.isLoggable(Level.INFO))
                LOG.info("Sending the message : " + XMLUtil.createStringFromDOMDocument(doc));

            // get a client
            ServiceEndpoint serviceEndpoint = new ServiceEndpoint();
            serviceEndpoint.setEndpointName(endpointName);
            serviceEndpoint.setServiceName(serviceName);
            serviceEndpoint.setInterfaces(new QName[] { interfaceName });
            
            // try to get a clienty from the factory
            ClientFactory clientFactory = ClientFactoryRegistry.getFactory();
            if (clientFactory == null) {
                throw new IOException("Can not find any client factory in the kernel");
            }

            Client client = clientFactory.getClient(serviceEndpoint);

            if (!isOneWay) {
                // send and wait for a response
                org.petalslink.dsb.service.client.Message out = client.sendReceive(message);

                // TODO : implement fault handling on client

                Message inMessage = new MessageImpl();
                inMessage.setExchange(exchange);

                if (LOG.isLoggable(Level.INFO))
                    LOG.info("RESPONSE from service : "
                            + XMLUtil.createStringFromDOMDocument(out.getPayload()));

                InputStream ins = XMLHelper.getInputStream(out.getPayload());
                if (ins == null) {
                    throw new IOException(new org.apache.cxf.common.i18n.Message(
                            "UNABLE.RETRIEVE.MESSAGE", LOG).toString());
                }
                inMessage.setContent(InputStream.class, ins);
                // store the response in the CXF message for future use (by the
                // observer)
                inMessage.put(org.petalslink.dsb.service.client.Message.class, out);

                // send back the response to CXF client ie the petals service
                conduit.getMessageObserver().onMessage(inMessage);

                // FIXME : send back an ack?
            } else {
                // one way communication, just send message...
                client.fireAndForget(message);
            }
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e.toString());
        }
    }

    @Override
    protected void onWrite() throws IOException {

    }

}
