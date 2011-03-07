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
package org.ow2.petals.messaging.framework.message.mime.writer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.ow2.petals.messaging.framework.message.Constants;
import org.ow2.petals.messaging.framework.message.Message;
import org.ow2.petals.messaging.framework.message.mime.Writer;


/**
 * @author chamerling - eBM WebSourcing
 * 
 */
public class TextHTMLWriter implements Writer {

    /**
     * {@inheritDoc}
     */
    public byte[] getBytes(Message message, String encoding) throws WriterException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        // content is is a property...
        Object o = message.get(Constants.RAW);
        if ((o != null) && (o instanceof String)) {
            try {
                out.write(((String) o).getBytes());
            } catch (IOException e) {
                throw new WriterException(e);
            }
        }
        return out.toByteArray();
    }

}
