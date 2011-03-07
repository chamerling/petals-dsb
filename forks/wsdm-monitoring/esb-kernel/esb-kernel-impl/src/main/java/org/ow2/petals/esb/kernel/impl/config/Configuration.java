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
package org.ow2.petals.esb.kernel.impl.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author chamerling - eBM WebSourcing
 * 
 */
public class Configuration {

	private static Logger log = Logger.getLogger(Configuration.class.getName());

	private static Map<String, String> data = new HashMap<String, String>();

	private static final String CONFIG = "config.properties";

	public static final String PORT = "port";

	public static final String REMOTE_REGISTRY_URL = "remote.registry.url";

	public static final String LOCAL_RAW_PORT = "local.raw.port";

	public static final String LOCAL_WSDM_PORT = "local.wsdm.port";

	public static final String POLL_DELAY = "poll.delay";

	static {
		Properties props = new Properties();
		File f = new File(CONFIG);
		if (log.isLoggable(Level.FINE)) {
			log.fine("Loading config from " + f.getAbsoluteFile());
		}

		InputStream is = null;
		if (!f.exists()) {
			// get from classpath
			is = Configuration.class.getClassLoader().getResourceAsStream(
					"/" + CONFIG);
		} else {
			try {
				is = new FileInputStream(f);
			} catch (FileNotFoundException e) {
				log.warning(e.getMessage());
			}
		}

		if (is != null) {
			try {
				props.load(is);
				for (Object k : props.keySet()) {
					data.put(k.toString().trim(), props.get(k).toString()
							.trim());
				}
			} catch (IOException e) {
				log.warning(e.getMessage());
			}
		} else {
            System.out.println("!!!");
			log.warning("No configuration file found...");
		}
	}

	private Configuration() {
	}

	/**
	 * @return the data
	 */
	public static Map<String, String> getData() {
		return data;
	}

}
