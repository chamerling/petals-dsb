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

import javax.xml.transform.Source;

import org.ow2.petals.messaging.framework.EngineException;
import org.ow2.petals.messaging.framework.message.Message;
import org.ow2.petals.messaging.framework.message.mime.Writer;
import org.ow2.petals.messaging.framework.utils.XMLUtil;


/**
 * @author chamerling - eBM WebSourcing
 * 
 */
public class TextXMLWriter implements Writer {

    /**
     * {@inheritDoc}
     */
    public byte[] getBytes(Message message, String encoding) throws WriterException {
        byte[] result = null;
        if (message.getContent(Source.class) != null) {
            try {
				String s = XMLUtil.createString(
                        message.getContent(Source.class), true);
                result = s.getBytes();
			} catch (EngineException e) {
                throw new WriterException(e);
            }
        }
        return result;
    }
}
