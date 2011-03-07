/**
 * PETALS: PETALS Services Platform Copyright (C) 2009 EBM WebSourcing
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 * 
 * Initial developer(s): EBM WebSourcing
 */
package org.ow2.petals.binding.restproxy.in;

import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.namespace.QName;

import org.ow2.petals.binding.restproxy.Constants;
import org.ow2.petals.binding.restproxy.HTTPConstants;
import org.ow2.petals.binding.restproxy.HTTPUtils;
import org.ow2.petals.binding.restproxy.IOUtils;
import org.ow2.petals.binding.restproxy.RESTException;
import org.ow2.petals.binding.restproxy.in.RESTEngineContext.Consume;
import org.ow2.petals.messaging.framework.Engine;
import org.ow2.petals.messaging.framework.EngineFactory;
import org.ow2.petals.messaging.framework.message.Message;
import org.ow2.petals.messaging.framework.message.MessageImpl;
import org.ow2.petals.messaging.framework.message.MessagingEngine;
import org.ow2.petals.messaging.framework.message.MessagingException;
import org.ow2.petals.messaging.framework.message.mime.ReaderRegistry;
import org.ow2.petals.messaging.framework.message.mime.WriterRegistry;
import org.ow2.petals.messaging.framework.message.mime.reader.ReaderException;

/**
 * @author chamerling - eBM WebSourcing
 * 
 */
public class RESTHandler implements HTTPServletProxy {

    final Logger logger;

    /**
     * 
     */
    public RESTHandler(final Logger logger) {
        this.logger = logger;
    }

    /**
     * {@inheritDoc}
     */
    public void proxify(String destination, HttpServletRequest request, HttpServletResponse response) {
        this.logger.fine("Proxy call to " + destination);

        String newTarget = this.fixTarget(destination);

        if (this.logger.isLoggable(Level.FINE)) {
            this.logger.fine("Initial destination was '" + destination + "'");
            this.logger.fine("Final destination is '" + newTarget + "'");
        }

        try {
            this.validatePath(newTarget);
            this.process(newTarget, Constants.ENDPOINT, Constants.SERVICE_NAME,
                    Constants.INTERFACE_NAME, request, response);
        } catch (RESTException e) {
            try {
                HTTPUtils.writeXMLError(e, response.getOutputStream());
            } catch (IOException e1) {
            }
        } finally {
            try {
                IOUtils.flushAndCose(response.getOutputStream());
            } catch (IOException e) {
            }
        }
    }

    public void invoke(String serviceName, String path, HttpServletRequest request,
            HttpServletResponse response) {
        this.logger.fine("Invoke service for " + serviceName + " and path '" + path + "'");

        // get the service from the context...
        Engine engine = EngineFactory.getEngine();
        RESTEngineContext context = engine.getComponent(RESTEngineContext.class);
        if (context == null) {
            // TODO : write error
            return;
        }
        Consume consume = context.getRestConsumers().get(serviceName);
        if (consume == null) {
            try {
                try {
                    HTTPUtils.writeXMLError(new RESTException("No service '" + serviceName
                                    + "' has been found in the REST component"), response
                                    .getOutputStream());
                } catch (IOException e1) {
                }
            } finally {
                try {
                    IOUtils.flushAndCose(response.getOutputStream());
                } catch (IOException e) {
                }
            }
        } else {
            try {
                this.process(path, consume.getEndpointName(), consume.getServiceName(), consume
                        .getInterfaceName(), request, response);
            } catch (RESTException e) {
                try {
                    HTTPUtils.writeXMLError(e, response.getOutputStream());
                } catch (IOException e1) {
                }
            } finally {
                try {
                    IOUtils.flushAndCose(response.getOutputStream());
                } catch (IOException e) {
                }
            }
        }
    }

    /**
     * @param newTarget
     * @return
     */
    private String fixTarget(String target) {
        String result = target;
        if ((result != null) && result.contains("http:/") && !result.contains("http://")) {
            result = result.replaceAll("http:/", "http://");
        }
        return result;
    }

    /**
     * TODO : Reader HTTP headers!
     * 
     * @param message
     * @param response
     * @throws RESTException
     */
    private void writeResponse(Message message, HttpServletResponse response) throws RESTException {

        if (this.logger.isLoggable(Level.FINE)) {
            this.logger.fine("Available properties = " + message.getAll());
        }

        Object o = message.get(org.ow2.petals.messaging.framework.message.Constants.HEADER + "."
                + HTTPConstants.HEADER_CONTENT_TYPE);
        String contentType = null;
        if (o != null) {
            contentType = (String) o;
        }
        if (contentType == null) {
            contentType = org.ow2.petals.messaging.framework.Constants.MimeTypes.TYPE_TEXT_PLAIN;
        }
        contentType = HTTPUtils.getContentType(contentType);

        o = message.get(org.ow2.petals.messaging.framework.message.Constants.CHARSET_ENCODING);
        String encoding = null;
        if (o != null) {
            encoding = (String) o;
        }
        if (encoding == null) {
            encoding = Constants.DEFAULT_ENCODING;
        }

        // get all the headers
        this.setResponseHeaders(response, message);

        int status = 200;
        if (message.get(HTTPConstants.STATUS_CODE) != null) {
            try {
                status = Integer.parseInt((String) message.get(HTTPConstants.STATUS_CODE));
            } catch (NumberFormatException e) {
            }
        }

        if (this.logger.isLoggable(Level.FINE)) {
            this.logger.fine("Response Status is set to : " + status);
        }

        response.setStatus(status);

        // TODO : set the headers from the response
        WriterRegistry writers = EngineFactory.getEngine().getComponent(WriterRegistry.class);
        if ((writers != null) && (writers.get(contentType) != null)) {
            if (this.logger.isLoggable(Level.FINE)) {
                this.logger.fine("Building response for content type = " + contentType);
            }
            try {
                byte[] toWrite = writers.get(contentType).getBytes(message, encoding);
                if (toWrite == null) {
                    this.logger.fine("No content to write to output stream");
                } else {
                    OutputStream os = response.getOutputStream();
                    os.write(toWrite);
                }
            } catch (Exception e) {
                try {
                    HTTPUtils.writeXMLError(new RESTException(e), response.getOutputStream());
                } catch (IOException e1) {
                    this.logger.warning(e1.getMessage());
                }
            }
        } else {
            if (this.logger.isLoggable(Level.FINE)) {
                this.logger.fine("No message writer found for content type = " + contentType);
            }
            try {
                HTTPUtils.writeXMLError(new RESTException(
                        "Can not find any message writer for type " + contentType), response
                        .getOutputStream());
            } catch (IOException e) {
                this.logger.warning(e.getMessage());
            }
        }
    }

    /**
     * @param response
     * @param message
     */
    private void setResponseHeaders(HttpServletResponse response, Message message) {
        for (String key : message.getAll().keySet()) {
            if (this.isValidResponseHeaderProperty(key) && (message.getAll().get(key) != null)) {
                if (this.logger.isLoggable(Level.FINE)) {
                    this.logger.fine("Key " + key
                            + " is valid and will be set in the HTTP response");
                }
                response.setHeader(key, message.get(key).toString());
            }
        }

        // set additional response headers...
        response.setHeader(HTTPConstants.HEADER_SERVER, "PEtALS ESB REST Proxy");

    }

    /**
     * @param key
     * @return
     */
    private boolean isValidResponseHeaderProperty(String key) {
        boolean result = true;
        if ((key != null)
                && key.startsWith(org.ow2.petals.messaging.framework.message.Constants.HEADER)) {
            // not all the headers are valid ones...
            // TODO : Add more
            if (key.endsWith(HTTPConstants.HEADER_SERVER)) {
                result = false;
            }
        } else {
            result = false;
        }
        return result;
    }

    protected void validatePath(String path) throws RESTException {
        try {
            new URL(path);
        } catch (MalformedURLException e) {
            throw new RESTException("Invalid URL " + path);
        }
    }

    /**
     * @throws RESTException
     * 
     */
    public void process(String path, String endpointName, QName serviceName, QName interfaceName,
            HttpServletRequest request, HttpServletResponse response) throws RESTException {
        Message out = null;
        Message message = new MessageImpl();

        String contentType = HTTPUtils.getContentType(request.getContentType());

        if (contentType == null) {
            contentType = org.ow2.petals.messaging.framework.Constants.MimeTypes.TYPE_RAW;
            if (this.logger.isLoggable(Level.FINE)) {
                this.logger.fine("Content type is null, setting default : " + contentType);
            }
        } else {
            if (this.logger.isLoggable(Level.FINE)) {
                this.logger.fine("Initial content type is : " + contentType);
            }
        }

        String encoding = this.getEncoding(request);
        if (this.logger.isLoggable(Level.FINE)) {
            this.logger.fine("Char encoding is : " + encoding);
        }

        ReaderRegistry readers = EngineFactory.getEngine().getComponent(ReaderRegistry.class);
        if ((readers != null) && (readers.get(contentType) != null)) {
            try {
                message = readers.get(contentType).read(request.getInputStream(), encoding);
            } catch (ReaderException e) {
                throw new RESTException(e);
            } catch (IOException e) {
                throw new RESTException(e);
            }

            // add some additional properties which are common to all
            message.putAll(this.createProperties(path, request));

            try {
                message.put(org.ow2.petals.messaging.framework.message.Constants.OPERATION, QName
                        .valueOf(request.getMethod().toUpperCase()));
                message.put(org.ow2.petals.messaging.framework.message.Constants.SERVICE,
                        serviceName);
                message.put(org.ow2.petals.messaging.framework.message.Constants.INTERFACE,
                        interfaceName);
                // FIXME = This should be in the context and not iin the code
                message.put(org.ow2.petals.messaging.framework.message.Constants.PROTOCOL, "jbi");

                MessagingEngine engine = EngineFactory.getEngine()
                        .getComponent(MessagingEngine.class);
                if (engine != null) {
                    out = engine.send(message);
                }
            } catch (MessagingException e) {
                throw new RESTException(e);
            }

            if (out != null) {
                this.writeResponse(out, response);
            } else {
                throw new RESTException("No message response...");
            }
        } else {
            throw new RESTException("Can not find a valid reader for type '" + contentType + "'");
        }
    }

    /**
     * @param request
     * @return
     */
    private String getEncoding(HttpServletRequest request) {
        String charsetEncoding = request.getCharacterEncoding();
        if (charsetEncoding == null) {
            charsetEncoding = Constants.DEFAULT_ENCODING;
        }
        return charsetEncoding;
    }

    /**
     * @param path
     * @param request
     * @return
     */
    private Map<String, Object> createProperties(final String path, final HttpServletRequest request) {
        Map<String, Object> result = new HashMap<String, Object>();

        result.put(org.ow2.petals.messaging.framework.message.Constants.HTTP_URL, path);

        Enumeration<?> headers = request.getHeaderNames();
        if (headers != null) {
            while (headers.hasMoreElements()) {
                Object object = headers.nextElement();
                String key = object.toString();
                result.put(org.ow2.petals.messaging.framework.message.Constants.HEADER + "." + key,
                        request.getHeader(key));
            }
        }

        result.put(org.ow2.petals.messaging.framework.message.Constants.CONTENT_LENGTH, request
                .getContentLength());
        String contentType = HTTPUtils.getContentType(request.getContentType());
        result.put(org.ow2.petals.messaging.framework.message.Constants.CONTENT_TYPE, contentType);
        final String charsetEncoding = request.getCharacterEncoding();
        result.put(org.ow2.petals.messaging.framework.message.Constants.CHARSET_ENCODING,
                charsetEncoding);
        result.put(org.ow2.petals.messaging.framework.message.Constants.HTTP_METHOD, request
                .getMethod());

        // get form data which is not XML!
        Enumeration<?> e = request.getParameterNames();
        while (e.hasMoreElements()) {
            String paramName = (String) e.nextElement();
            String[] values = request.getParameterValues(paramName.toString());
            int i = 0;
            for (String string : values) {
                result.put(org.ow2.petals.messaging.framework.message.Constants.PARAMETERS + "."
                        + (i++) + "." + paramName, string);
            }
        }

        if (this.logger.isLoggable(Level.FINE)) {
            for (String key : result.keySet()) {
                this.logger.fine("From HTTPRequest : Property '" + key + "' = '" + result.get(key)
                        + "'");
            }
        }
        return result;
    }

}
