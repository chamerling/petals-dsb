/**
 * easySchema - easyWSDL toolbox Platform.
 * Copyright (c) 2008,  eBM Websourcing
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the University of California, Berkeley nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE REGENTS AND CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.ow2.petals.exchange;

import java.net.URI;
import java.net.URISyntaxException;

import org.ow2.easywsdl.schema.api.SchemaException;
import org.ow2.easywsdl.schema.api.extensions.NamespaceMapperImpl;
import org.ow2.easywsdl.schema.api.extensions.SchemaLocatorImpl;
import org.ow2.petals.exchange.api.Exchange;
import org.ow2.petals.exchange.api.ExchangeException;
import org.ow2.petals.exchange.api.ExchangeReader;
import org.ow2.petals.exchange.api.ExchangeWriter;
import org.ow2.petals.exchange.impl.ExchangeImpl;
import org.ow2.petals.exchange.impl.ExchangeReaderImpl;
import org.ow2.petals.exchange.impl.ExchangeWriterImpl;

import petals.ow2.org.exchange.ExchangeType;


/**
 * This class is a concrete implementation of the abstract class SchemaFactory.
 * Some ideas used here have been shamelessly copied from the wonderful JAXP and
 * Xerces work.
 *
 * @author Nicolas Salatge - eBM WebSourcing
 */
public class ExchangeFactoryImpl extends ExchangeFactory {



    /**
     * Create a new instance of a SchemaReaderImpl.
     * @throws SchemaException 
     */
    @Override
    public ExchangeReader newExchangeReader() throws ExchangeException {
        ExchangeReader reader;
		try {
			reader = new ExchangeReaderImpl();
		} catch (SchemaException e) {
			throw new ExchangeException(e);
		}
        return reader;
    }


    /**
     * Create a new instance of a SchemaWriterImpl.
     * @throws SchemaException 
     */
    @Override
    public ExchangeWriter newExchangeWriter() throws ExchangeException {
        ExchangeWriter writer = null;
		try {
			writer = new ExchangeWriterImpl();
		} catch (SchemaException e) {
			throw new ExchangeException(e);
		}
        return writer;
    }


	@Override
	public Exchange newExchange() throws ExchangeException {
		Exchange res = null;
		try {
			res = new ExchangeImpl(new URI("."), new ExchangeType(), new NamespaceMapperImpl(), new SchemaLocatorImpl());
		} catch (URISyntaxException e) {
			throw new ExchangeException(e);
		}
		return res;
	}

}
