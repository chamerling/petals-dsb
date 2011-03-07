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
package org.ow2.petals.messaging.framework.message.mime.reader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.HashMap;
import java.util.Map;

import org.ow2.petals.messaging.framework.message.Constants;
import org.ow2.petals.messaging.framework.message.Message;
import org.ow2.petals.messaging.framework.message.MessageImpl;
import org.ow2.petals.messaging.framework.message.mime.Reader;


/**
 * @author chamerling - eBM WebSourcing
 * 
 */
public class XwwwFormURLEncodedReader implements Reader {

    public Message read(InputStream is, String encoding) {
        Message message = new MessageImpl();
        try {
            message.putAll(this.getXFormContent(is, encoding));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return message;
    }

    @SuppressWarnings("unchecked")
    Map<String, Object> getXFormContent(final InputStream is, final String encoding)
            throws UnsupportedEncodingException {

        Map<String, Object> result = new HashMap<String, Object>();
        InputStreamReader inputStreamReader = null;
        try {
            inputStreamReader = (InputStreamReader) AccessController
                    .doPrivileged(new PrivilegedExceptionAction() {
                        public Object run() throws UnsupportedEncodingException {
                            return new InputStreamReader(is, encoding);
                        }
                    });
        } catch (PrivilegedActionException e) {
            throw (UnsupportedEncodingException) e.getException();
        }

        try {
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            while (true) {
                String line = bufferedReader.readLine();
                if (line != null) {
                    String parts[] = line.split("&");
                    for (int i = 0; i < parts.length; i++) {
                        int separator = parts[i].indexOf("=");
                        String key = parts[i].substring(0, separator);
                        String value = parts[i].substring(separator + 1);
                        result.put(Constants.PARAMETERS + "." + i + "." + key, value);
                    }
                } else {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
