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
package org.ow2.petals.messaging.framework.utils;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * @author chamerling - eBM WebSourcing
 * 
 */
public class DocumentBuilders {
	/**
	 * <p>
	 * DocumentBuilder as thread local.
	 * </p>
	 * <p>
	 * The document builder is the one provided by the JVM, even if an other DOM
	 * implementation is in the classpath (ex: Xerces). The JVM implementation
	 * is needed because the implementation of the DocumentFragment must be
	 * deserializable on other Petals node, that have not a specific
	 * implementation in its classpath.
	 * </p>
	 */
	private final static ThreadLocal<DocumentBuilder> jvmDocumentBuilderThreadLocal = new ThreadLocal<DocumentBuilder>() {

		@Override
		protected DocumentBuilder initialValue() {
			final ClassLoader currentClassLoader = Thread.currentThread()
					.getContextClassLoader();
			try {
				final ClassLoader systemClassLoader = ClassLoader
						.getSystemClassLoader();
				Thread.currentThread().setContextClassLoader(systemClassLoader);

				final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
						.newInstance();
				documentBuilderFactory.setNamespaceAware(true);

				return documentBuilderFactory.newDocumentBuilder();

			} catch (ParserConfigurationException e) {
				throw new RuntimeException("Failed to create DocumentBuilder",
						e);
			} finally {
				Thread.currentThread()
						.setContextClassLoader(currentClassLoader);
			}
		}
	};

	/**
	 * <p>
	 * DocumentBuilder as thread local, supporting XML namespaces.
	 * </p>
	 * <p>
	 * The document builder is the one provided by the classpath supporting
	 * namespaces.
	 * </p>
	 */
	private final static ThreadLocal<DocumentBuilder> namespaceDocumentBuilderThreadLocal = new ThreadLocal<DocumentBuilder>() {

		@Override
		protected DocumentBuilder initialValue() {
			try {
				final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
						.newInstance();
				documentBuilderFactory.setNamespaceAware(true);

				return documentBuilderFactory.newDocumentBuilder();

			} catch (ParserConfigurationException e) {
				throw new RuntimeException("Failed to create DocumentBuilder",
						e);
			}
		}
	};

	/**
	 * <p>
	 * DocumentBuilder as thread local, supporting XML namespaces.
	 * </p>
	 * <p>
	 * The document builder is the default (no property set) one provided by the
	 * classpath.
	 * </p>
	 */
	private final static ThreadLocal<DocumentBuilder> defaultDocumentBuilderThreadLocal = new ThreadLocal<DocumentBuilder>() {

		@Override
		protected DocumentBuilder initialValue() {
			try {
				final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
						.newInstance();

				return documentBuilderFactory.newDocumentBuilder();

			} catch (ParserConfigurationException e) {
				throw new RuntimeException("Failed to create DocumentBuilder",
						e);
			}
		}
	};

	/**
	 * <p>
	 * DocumentBuilder as thread local.
	 * </p>
	 * <p>
	 * The document builder is the one provided by
	 * {@link #jvmDocumentBuilderThreadLocal}.
	 * </p>
	 */
	public final static DocumentBuilder getJvmDocumentBuilder() {
		return DocumentBuilders.jvmDocumentBuilderThreadLocal.get();
	}

	/**
	 * <p>
	 * DocumentBuilder as thread local, supporting XML namespaces.
	 * </p>
	 * <p>
	 * The document builder is the one provided by
	 * {@link #namespaceDocumentBuilderThreadLocal}.
	 * </p>
	 */
	public final static DocumentBuilder getNamespaceDocumentBuilder() {
		return DocumentBuilders.namespaceDocumentBuilderThreadLocal.get();
	}

	/**
	 * <p>
	 * DocumentBuilder as thread local, supporting XML namespaces.
	 * </p>
	 * <p>
	 * The document builder is the one provided by
	 * {@link #defaultDocumentBuilderThreadLocal}.
	 * </p>
	 */
	public final static DocumentBuilder getDefaultDocumentBuilder() {
		return DocumentBuilders.defaultDocumentBuilderThreadLocal.get();
	}

}
