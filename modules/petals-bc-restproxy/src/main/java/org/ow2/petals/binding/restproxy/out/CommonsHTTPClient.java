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
package org.ow2.petals.binding.restproxy.out;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

import javax.xml.transform.Source;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.ow2.petals.binding.restproxy.Constants;
import org.ow2.petals.binding.restproxy.HTTPConstants;
import org.ow2.petals.binding.restproxy.HTTPUtils;
import org.ow2.petals.binding.restproxy.RESTException;
import org.ow2.petals.component.framework.api.exception.PEtALSCDKException;
import org.ow2.petals.component.framework.util.SourceUtil;
import org.ow2.petals.component.framework.util.XMLUtil;
import org.ow2.petals.messaging.framework.EngineFactory;
import org.ow2.petals.messaging.framework.message.Callback;
import org.ow2.petals.messaging.framework.message.Client;
import org.ow2.petals.messaging.framework.message.Message;
import org.ow2.petals.messaging.framework.message.MessageImpl;
import org.ow2.petals.messaging.framework.message.MessagingException;
import org.ow2.petals.messaging.framework.message.mime.ReaderRegistry;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * HTTP Client implementation based on Apache HTTP Commons
 * 
 * @author chamerling - eBM WebSourcing
 * 
 */
public class CommonsHTTPClient implements Client {

    private final Logger logger;

    /**
	 * 
	 */
    public CommonsHTTPClient(final Logger logger) {
        this.logger = logger;
    }

    public Message send(final Message message) throws MessagingException {
        final URL url = (URL) message
                .get(org.ow2.petals.messaging.framework.message.Constants.HTTP_URL);
        if (url == null) {
            throw new MessagingException("URL can not be found on message, please set the "
                    + org.ow2.petals.messaging.framework.message.Constants.HTTP_URL + " property");
        }

        String method = (String) message
                .get(org.ow2.petals.messaging.framework.message.Constants.HTTP_METHOD);
        Message result = new MessageImpl();

        if (HTTPConstants.HTTP_METHOD_GET.equals(method)) {
            result = this.get(message, url);
        } else if (HTTPConstants.HTTP_METHOD_POST.equals(method)) {
            result = this.post(message, url);
        } else if (HTTPConstants.HTTP_METHOD_PUT.equals(method)) {
            result = this.put(message, url);
        } else if (HTTPConstants.HTTP_METHOD_DELETE.equals(method)) {
            result = this.delete(message, url);
        } else {
            throw new MessagingException("This is not a valid HTTP method in REST style : "
                    + method);
        }
        return result;
    }

    protected Message delete(Message message, URL url) throws MessagingException {
        Message result = null;
        HttpDelete method = new HttpDelete(url.toString());
        this.setParameters(method, message.getAll());

        String contentEncoding = (String) message
                .get(org.ow2.petals.messaging.framework.message.Constants.CHARSET_ENCODING);
        if (contentEncoding == null) {
            contentEncoding = Constants.DEFAULT_ENCODING;
        }

        String contentType = (String) message
                .get(org.ow2.petals.messaging.framework.message.Constants.CONTENT_TYPE);
        if (contentType == null) {
            contentType = org.ow2.petals.messaging.framework.Constants.MimeTypes.TYPE_TEXT_XML;
        }

        if (this.logger.isLoggable(Level.FINE)) {
            this.logger.fine("Outgoing Content-Type will be " + contentType);
            this.logger.fine("Outgoing Charset-Encoding will be " + contentEncoding);
        }

        HttpClient httpclient = new DefaultHttpClient();
        try {
            HttpResponse response = httpclient.execute(method);
            result = this.handleResponse(response);
        } catch (Exception e) {
            throw new MessagingException("Failed to invoke DELETE", e);
        } finally {
            this.cleanup(httpclient);
        }
        return result;
    }

    protected Message post(Message message, URL url) throws MessagingException {
        Message result = null;
        HttpPost method = new HttpPost(url.toString());
        this.setParameters(method, message.getAll());

        String contentEncoding = (String) message
                .get(org.ow2.petals.messaging.framework.message.Constants.CHARSET_ENCODING);
        if (contentEncoding == null) {
            contentEncoding = Constants.DEFAULT_ENCODING;
        }

        String contentType = (String) message
                .get(org.ow2.petals.messaging.framework.message.Constants.CONTENT_TYPE);
        if (contentType == null) {
            contentType = org.ow2.petals.messaging.framework.Constants.MimeTypes.TYPE_TEXT_XML;
        }

        if (this.logger.isLoggable(Level.FINE)) {
            this.logger.fine("Outgoing Content-Type will be " + contentType);
            this.logger.fine("Outgoing Charset-Encoding will be " + contentEncoding);
        }

        AbstractHttpEntity entity = this.createEntity(message, contentEncoding, contentType);

        if (entity != null) {
            method.setEntity(entity);
        }

        HttpClient httpclient = new DefaultHttpClient();
        try {
            HttpResponse response = httpclient.execute(method);
            result = this.handleResponse(response);
        } catch (Exception e) {
            throw new MessagingException("Failed to invoke POST", e);
        } finally {
            this.cleanup(httpclient);
        }
        return result;
    }

    protected Message get(Message message, URL url) throws MessagingException {
        Message result = null;
        HttpGet method = new HttpGet(url.toString());
        this.setParameters(method, message.getAll());

        HttpClient httpclient = new DefaultHttpClient();
        try {
            HttpResponse response = httpclient.execute(method);
            result = this.handleResponse(response);
        } catch (Exception e) {
            throw new MessagingException("Failed to invoke GET", e);
        } finally {
            this.cleanup(httpclient);
        }
        return result;
    }

    /**
	 * 
	 */
    private void cleanup(HttpClient method) {

    }

    private Message handleResponse(HttpResponse response) throws RESTException {
        // FIXME : depends on the response mime type...

        if (response == null) {
            throw new RESTException("HTTP response is null!");
        }

        Header header = response.getFirstHeader(HTTPConstants.HEADER_CONTENT_TYPE);
        String contentType = org.ow2.petals.messaging.framework.Constants.MimeTypes.TYPE_TEXT_PLAIN;
        if (header != null) {
            contentType = header.getValue();
        }
        contentType = HTTPUtils.getContentType(contentType);

        if (this.logger.isLoggable(Level.FINE)) {
            this.logger.fine("Response content type is set to : " + contentType);
        }

        ReaderRegistry readers = EngineFactory.getEngine().getComponent(ReaderRegistry.class);
        if ((readers == null) || (readers.get(contentType) == null)) {
            throw new RESTException("Can not find a valid handler for content type " + contentType);
        }

        String encoding = null;
        if (response.getEntity() != null) {
            header = response.getEntity().getContentEncoding();
        }

        if (header == null) {
            encoding = Constants.DEFAULT_ENCODING;
        } else {
            encoding = header.getValue();
        }
        if (encoding == null) {
            encoding = Constants.DEFAULT_ENCODING;
        }

        if (this.logger.isLoggable(Level.FINE)) {
            this.logger.fine("Response content encoding is set to : " + encoding);
        }

        Message result = null;
        try {
            if (response.getEntity() != null) {
                result = readers.get(contentType).read(response.getEntity().getContent(), encoding);
            } else {
                result = new MessageImpl();
            }
        } catch (Exception e) {
            // TODO : do not throw and exception but set the error somewhere to
            // be returned to the client...
            if (this.logger.isLoggable(Level.WARNING)) {
                this.logger.log(Level.WARNING, "Handling problem", e);

            }
            throw new RESTException(e.getMessage());
        }

        this.addProperties(response, result);

        return result;
    }

    /**
     * @param method
     */
    private void addProperties(HttpResponse response, Message message) throws RESTException {

        // fill properties!
        Header[] headers = response.getAllHeaders();
        for (Header header : headers) {
            message.put(org.ow2.petals.messaging.framework.message.Constants.HEADER + "."
                    + header.getName(), header.getValue());
        }

        // set result value and more
        // method.getStatusCode();
        message.put(HTTPConstants.STATUS_CODE, "" + response.getStatusLine().getStatusCode());

        if (this.logger.isLoggable(Level.FINE)) {
            for (String key : message.getAll().keySet()) {
                this.logger.fine("From HTTP Client response : Property '" + key + "' = '"
                        + message.get(key) + "'");
            }
        }

    }

    /**
     * @param method
     * @param result
     * @throws IOException
     * @throws RESTException
     * @throws SAXException
     * @throws PEtALSCDKException
     */
    private Source getResultAsXML(HttpResponse response) {
        Source result = null;

        try {
            if (response != null) {
                InputStream in = response.getEntity().getContent();
                if (in != null) {

                    Header contentEncoding = response
                            .getFirstHeader(HTTPConstants.HEADER_CONTENT_ENCODING);
                    if (contentEncoding != null) {
                        if (contentEncoding.getValue().equalsIgnoreCase(
                                HTTPConstants.COMPRESSION_GZIP)) {
                            in = new GZIPInputStream(in);
                        } else {

                        }
                    }
                    // let's create the source if we can!
                    Document doc = XMLUtil.loadDocument(in);
                    result = SourceUtil.createStreamSource(doc);
                }
            }
        } catch (Exception e) {
            this.logger.warning(e.getMessage());
        }
        return result;
    }

    protected void setParameters(HttpRequestBase method, Map<String, Object> properties) {
        HttpParams params = new BasicHttpParams();
        for (String key : properties.keySet()) {
            if (this.isParamaterKey(key)) {
                String paramName = this.getParamName(key);
                Object paramValue = properties.get(key);
                if (this.logger.isLoggable(Level.FINE)) {
                    this.logger.fine("Adding HTTP parameter '" + paramName + "' with value '"
                            + paramValue + "'");
                }
                params.setParameter(paramName, properties.get(key));
            }
        }
        method.setParams(params);
    }

    /**
     * TODO : Check lengths
     * 
     * @param key
     * @return
     */
    private String getParamName(String key) {
        String result = null;
        if ((key != null)
                && key.startsWith(org.ow2.petals.messaging.framework.message.Constants.PARAMETERS)) {
            result = key.substring(org.ow2.petals.messaging.framework.message.Constants.PARAMETERS
                    .length() + 1, key.length());
            result = result.substring(result.indexOf(".") + 1, result.length());
        }
        return result;
    }

    /**
     * @param key
     * @return
     */
    private boolean isParamaterKey(String key) {
        return (key != null)
                && key.startsWith(org.ow2.petals.messaging.framework.message.Constants.PARAMETERS);
    }

    protected Message put(Message message, URL url) throws MessagingException {
        Message result = null;
        HttpPut method = new HttpPut(url.toString());
        this.setParameters(method, message.getAll());

        String contentEncoding = (String) message
                .get(org.ow2.petals.messaging.framework.message.Constants.CHARSET_ENCODING);
        if (contentEncoding == null) {
            contentEncoding = Constants.DEFAULT_ENCODING;
        }

        String contentType = (String) message
                .get(org.ow2.petals.messaging.framework.message.Constants.CONTENT_TYPE);
        if (contentType == null) {
            contentType = org.ow2.petals.messaging.framework.Constants.MimeTypes.TYPE_TEXT_XML;
        }

        if (this.logger.isLoggable(Level.FINE)) {
            this.logger.fine("Outgoing Content-Type will be " + contentType);
            this.logger.fine("Outgoing Charset-Encoding will be " + contentEncoding);
        }

        AbstractHttpEntity entity = this.createEntity(message, contentEncoding, contentType);

        if (this.logger.isLoggable(Level.FINE)) {
            this.logger.fine("Content type is set to " + contentType);
            this.logger.fine("Content encoding is set to " + contentEncoding);
        }

        if (entity != null) {
            method.setEntity(entity);
        }

        HttpClient httpclient = new DefaultHttpClient();
        try {
            HttpResponse response = httpclient.execute(method);
            result = this.handleResponse(response);
        } catch (Exception e) {
            throw new MessagingException("Failed to invoke PUT", e);
        } finally {
            this.cleanup(httpclient);
        }
        return result;
    }

    /**
     * @param message
     * @param contentEncoding
     * @param contentType
     * @return
     * @throws RESTException
     */
    private AbstractHttpEntity createEntity(Message message, String contentEncoding,
            String contentType) throws MessagingException {
        AbstractHttpEntity entity = null;

        if (this.logger.isLoggable(Level.FINE)) {
            this.logger.fine("Creating entity for content type : " + contentType);
        }

        if (org.ow2.petals.messaging.framework.Constants.MimeTypes.TYPE_X_WWW_FORM_URLENCODED
                .equals(contentType)) {
            List<NameValuePair> formparams = new ArrayList<NameValuePair>();
            for (String key : message.getAll().keySet()) {
                if (this.isParamaterKey(key)) {
                    String paramName = this.getParamName(key);
                    Object o = message.getAll().get(key);
                    String paramValue = null;
                    if (o == null) {
                        paramValue = "";
                    } else {
                        paramValue = o.toString();
                    }

                    if (this.logger.isLoggable(Level.FINE)) {
                        this.logger.fine("Adding HTTP Form parameter '" + paramName
                                + "' with value '" + paramValue + "'");
                    }
                    formparams.add(new BasicNameValuePair(paramName, paramValue));
                }
            }
            try {
                entity = new UrlEncodedFormEntity(formparams, contentEncoding);
            } catch (UnsupportedEncodingException e) {
                throw new MessagingException(e.getMessage());
            }

        } else if (org.ow2.petals.messaging.framework.Constants.MimeTypes.TYPE_TEXT_XML
                .equals(contentType)
                || org.ow2.petals.messaging.framework.Constants.MimeTypes.TYPE_SOAP
                        .equals(contentType)) {
            if ((message != null) && (message.getContent(Source.class) != null)) {
                String payload = null;
                try {
                    payload = SourceUtil.createString(
                            message.getContent(Source.class));
                } catch (PEtALSCDKException e) {
                    this.logger.warning(e.getMessage());
                }
                try {
                    entity = new StringEntity(payload, contentEncoding);
                    entity.setContentType(contentType + HTTP.CHARSET_PARAM + contentEncoding);
                } catch (UnsupportedEncodingException e) {
                    this.logger.warning(e.getMessage());
                }
            }
        } else {
            // TODO
            // throw new RESTException(contentType +
            // " is not supported in this method!");
        }
        return entity;
    }

    /**
     * {@inheritDoc}
     */
    public void send(Message in, Callback callback) throws MessagingException {
        throw new MessagingException("Not implemented");
    }
}
