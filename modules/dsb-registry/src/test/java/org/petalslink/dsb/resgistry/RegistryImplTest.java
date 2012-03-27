/**
 * PETALS - PETALS Cloud Platform.
 * Copyright (c) 2012 EBM Websourcing, http://www.ebmwebsourcing.com/
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package org.petalslink.dsb.resgistry;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.xml.namespace.QName;

import org.petalslink.dsb.registry.RegistryEntryLoader;
import org.petalslink.dsb.registry.RegistryImpl;

import junit.framework.TestCase;

/**
 * @author chamerling
 * 
 */
public class RegistryImplTest extends TestCase {


    public void testTTL() throws Exception {

        final AtomicInteger accessCount = new AtomicInteger(0);


		RegistryEntryLoader loader = new RegistryEntryLoader() {

			public List<String> getByInterface(String key) {
				System.out.println("Getting entries for key " + key);
				accessCount.incrementAndGet();
				return new ArrayList<String>();
			}

			public List<String> getByServiceInterface(String key) {
				System.out.println("Getting entries for key " + key);
				accessCount.incrementAndGet();
				return new ArrayList<String>();
			}

			public List<String> getByServiceInterfaceEndpoint(String key) {
				System.out.println("Getting entries for key " + key);
				accessCount.incrementAndGet();
				return new ArrayList<String>();
			}

		};

		RegistryImpl registry = new RegistryImpl(loader, 5);
		List<String> result = registry.get("foobar", QName.valueOf("foo"),
				QName.valueOf("bar"));
		assertTrue(accessCount.get() == 1);
		result = registry.get("foobar", QName.valueOf("foo"),
				QName.valueOf("bar"));
		assertTrue(accessCount.get() == 1);

		// wait mmore than 5 seconds to delete the cache
		System.out.println("Wait that the cache is deleted...");
		TimeUnit.SECONDS.sleep(8);
		result = registry.get("foobar", QName.valueOf("foo"),
				QName.valueOf("bar"));
		assertTrue(accessCount.get() == 2);
	}

}
