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

import java.util.Properties;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;

/**
 * @author chamerling - eBM WebSourcing
 * 
 */
public class Transformers {
	private final static TransformerFactory TRANSFORMER_FACTORY = TransformerFactory
			.newInstance();

	/**
	 * <p>
	 * {@link Transformer} as thread local.
	 * </p>
	 * <p>
	 * The Transformer is the default one provided by the classpath (no property
	 * set).
	 * </p>
	 */
	private final static ThreadLocal<Transformer> defaultTransformerThreadLocal = new ThreadLocal<Transformer>() {

		@Override
		protected Transformer initialValue() {
			try {

				return Transformers.TRANSFORMER_FACTORY.newTransformer();

			} catch (TransformerConfigurationException e) {
				throw new RuntimeException("Failed to create Transformer", e);
			}
		}
	};

	/**
	 * <p>
	 * {@link Transformer} as thread local.
	 * </p>
	 * <p>
	 * The Transformer is the one provided by the classpath, configured using
	 * following properties:
	 * <ul>
	 * <li>{@link OutputKeys#OMIT_XML_DECLARATION} set to <code>"no"</code>,</li>
	 * <li>{@link OutputKeys#METHOD} set to <code>"xmlo"</code>.</li>
	 * </ul>
	 * </p>
	 */
	private final static ThreadLocal<Transformer> xmlWithDeclarationTransformerThreadLocal = new ThreadLocal<Transformer>() {

		@Override
		protected Transformer initialValue() {
			try {

				final Transformer transformer = Transformers.TRANSFORMER_FACTORY
						.newTransformer();
				final Properties props = new Properties();
				props.put(OutputKeys.OMIT_XML_DECLARATION, "no");
				props.put(OutputKeys.METHOD, "xml");
				transformer.setOutputProperties(props);
				return transformer;

			} catch (TransformerConfigurationException e) {
				throw new RuntimeException("Failed to create Transformer", e);
			}
		}
	};

	/**
	 * <p>
	 * {@link Transformer} as thread local.
	 * </p>
	 * <p>
	 * The Transformer is the one provided by the classpath, configured using
	 * following properties:
	 * <ul>
	 * <li>{@link OutputKeys#OMIT_XML_DECLARATION} set to <code>"yes"</code>,</li>
	 * <li>{@link OutputKeys#METHOD} set to <code>"xmlo"</code>.</li>
	 * </ul>
	 * </p>
	 */
	private final static ThreadLocal<Transformer> xmlWithoutDeclarationTransformerThreadLocal = new ThreadLocal<Transformer>() {

		@Override
		protected Transformer initialValue() {
			try {

				final Transformer transformer = Transformers.TRANSFORMER_FACTORY
						.newTransformer();
				final Properties props = new Properties();
				props.put(OutputKeys.OMIT_XML_DECLARATION, "yes");
				props.put(OutputKeys.METHOD, "xml");
				transformer.setOutputProperties(props);
				return transformer;

			} catch (TransformerConfigurationException e) {
				throw new RuntimeException("Failed to create Transformer", e);
			}
		}
	};

	/**
	 * <p>
	 * {@link Transformer} as thread local.
	 * </p>
	 * <p>
	 * The Transformer is the one provided by the classpath, configured using
	 * following properties:
	 * <ul>
	 * <li>{@link OutputKeys#OMIT_XML_DECLARATION} set to <code>"yes"</code>.</li>
	 * </ul>
	 * </p>
	 */
	private final static ThreadLocal<Transformer> withoutDeclarationTransformerThreadLocal = new ThreadLocal<Transformer>() {

		@Override
		protected Transformer initialValue() {
			try {

				final Transformer transformer = Transformers.TRANSFORMER_FACTORY
						.newTransformer();
				final Properties props = new Properties();
				props.put(OutputKeys.OMIT_XML_DECLARATION, "yes");
				transformer.setOutputProperties(props);
				return transformer;

			} catch (TransformerConfigurationException e) {
				throw new RuntimeException("Failed to create Transformer", e);
			}
		}
	};

	/**
	 * <p>
	 * {@link Transformer} as thread local.
	 * </p>
	 * <p>
	 * The Transformer is the default one (provided by
	 * {@link #defaultTransformerThreadLocal}).
	 * </p>
	 * <p>
	 * When the {@link Transformer} is no longuer necessary, it must be reset
	 * using {@link Transformer#reset()} to be reuseable.
	 * </p>
	 */
	public final static Transformer getDefaultTransformer() {
		return Transformers.defaultTransformerThreadLocal.get();
	}

	/**
	 * <p>
	 * {@link Transformer} as thread local.
	 * </p>
	 * <p>
	 * The Transformer is the one provided by
	 * {@link #xmlWithDeclarationTransformerThreadLocal}.
	 * </p>
	 * <p>
	 * When the {@link Transformer} is no longuer necessary, it must be reset
	 * using {@link Transformer#reset()} to be reuseable.
	 * </p>
	 */
	public final static Transformer getXmlWithDeclarationTransformer() {
		return Transformers.xmlWithDeclarationTransformerThreadLocal.get();
	}

	/**
	 * <p>
	 * {@link Transformer} as thread local.
	 * </p>
	 * <p>
	 * The Transformer is the one provided by
	 * {@link #xmlWithoutDeclarationTransformerThreadLocal}.
	 * </p>
	 * <p>
	 * When the {@link Transformer} is no longuer necessary, it must be reset
	 * using {@link Transformer#reset()} to be reuseable.
	 * </p>
	 */
	public final static Transformer getXmlWithoutDeclarationTransformer() {
		return Transformers.xmlWithoutDeclarationTransformerThreadLocal.get();
	}

	/**
	 * <p>
	 * {@link Transformer} as thread local.
	 * </p>
	 * <p>
	 * The Transformer is the one provided by
	 * {@link #withoutDeclarationTransformerThreadLocal}.
	 * </p>
	 * <p>
	 * When the {@link Transformer} is no longuer necessary, it must be reset
	 * using {@link Transformer#reset()} to be reuseable.
	 * </p>
	 */
	public final static Transformer getWithoutDeclarationTransformer() {
		return Transformers.withoutDeclarationTransformerThreadLocal.get();
	}

}
