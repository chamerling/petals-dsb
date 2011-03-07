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

import java.io.InputStream;

import javax.xml.transform.Source;

import org.ow2.petals.messaging.framework.message.Message;
import org.ow2.petals.messaging.framework.message.MessageImpl;
import org.ow2.petals.messaging.framework.message.mime.Reader;
import org.ow2.petals.messaging.framework.utils.XMLUtil;
import org.w3c.dom.Document;


/**
 * @author chamerling - eBM WebSourcing
 * 
 */
public class TextXMLReader implements Reader {

    /**
     * {@inheritDoc}
     */
    public Message read(final InputStream inputStream, final String encoding)
            throws ReaderException {
        Message message = new MessageImpl();

        // get content as XML
        Source result = null;
        try {
            if ((inputStream != null)) {
                Document doc = XMLUtil.loadDocument(inputStream);
                if (doc != null) {
					result = XMLUtil.createStreamSource(doc);
                }
            }
        } catch (Exception e) {
        }

        if (result != null) {
            message.setContent(Source.class, result);
        }
        return message;
    }

}
