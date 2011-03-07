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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.ow2.petals.messaging.framework.message.Constants;
import org.ow2.petals.messaging.framework.message.Message;
import org.ow2.petals.messaging.framework.message.MessageImpl;
import org.ow2.petals.messaging.framework.message.mime.Reader;


/**
 * @author chamerling - eBM WebSourcing
 * 
 */
public class RawReader implements Reader {

    public Message read(InputStream is, String encoding) {
        Message message = new MessageImpl();
        try {
            message.put(Constants.RAW, new String(this.getContent(is, encoding)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return message;
    }

    byte[] getContent(final InputStream is, final String encoding) throws IOException {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int read = is.read(buf);

        while (read != -1) {
            out.write(buf, 0, read);
            read = is.read(buf);
        }
        return out.toByteArray();
    }

}
