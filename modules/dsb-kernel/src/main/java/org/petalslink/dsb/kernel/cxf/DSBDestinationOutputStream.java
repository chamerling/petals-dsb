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
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;

import org.apache.cxf.common.logging.LogUtils;
import org.apache.cxf.io.CachedOutputStream;
import org.apache.cxf.message.Message;
import org.ow2.petals.registry.api.util.XMLUtil;
import org.petalslink.dsb.service.client.MessageListener;
import org.petalslink.dsb.xmlutils.XMLHelper;
import org.w3c.dom.Document;

public class DSBDestinationOutputStream extends CachedOutputStream {

    private static final Logger LOG = LogUtils.getL7dLogger(DSBDestinationOutputStream.class);

    private Message inMessage;

    private Message outMessage;
    
    private MessageListener responseListener;

    public DSBDestinationOutputStream(Message m, Message outM, MessageListener responseListener) {
        super();
        inMessage = m;
        outMessage = outM;
        this.responseListener = responseListener;
    }

    @Override
    protected void doFlush() throws IOException {
        // so far do nothing
    }

    @Override
    protected void doClose() throws IOException {
        commitOutputMessage();
    }

    @Override
    protected void onWrite() throws IOException {
        // so far do nothing
    }

    private void commitOutputMessage() throws IOException {
        try {
            if (inMessage.getExchange().isOneWay()) {
                return;
            } else {

                InputStream bais = getInputStream();
                final Document doc = XMLHelper.createDocument(new StreamSource(bais), true);
                
                if (LOG.isLoggable(Level.INFO)) {
                    LOG.info("On destination to be sent back to the client" + XMLUtil.createStringFromDOMDocument(doc));
                }
                bais.close();

                org.petalslink.dsb.service.client.Message out = new org.petalslink.dsb.service.client.Message() {

                    public QName getService() {
                        return null;
                    }

                    public Map<String, String> getProperties() {
                        Map<String, String> props = new java.util.HashMap<String, String>();
                        if (inMessage.get(DSBDestination.CORRELATION) != null) {
                            props.put(DSBDestination.CORRELATION, inMessage.get(DSBDestination.CORRELATION).toString());
                        }
                        return props;
                    }

                    public Document getPayload() {
                        return doc;
                    }

                    public QName getOperation() {
                        return null;
                    }

                    public QName getInterface() {
                        return null;
                    }

                    public Map<String, Document> getHeaders() {
                        return null;
                    }

                    public String getEndpoint() {
                        return null;
                    }

                    public String getProperty(String name) {
                        // TODO Auto-generated method stub
                        return null;
                    }

                    public void setProperty(String name, String value) {
                        // TODO Auto-generated method stub
                        
                    }
                };
                
                // notify incoming listener that the response is available...
                responseListener.onMessage(out);
                
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
