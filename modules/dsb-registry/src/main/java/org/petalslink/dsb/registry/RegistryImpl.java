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
package org.petalslink.dsb.registry;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.xml.namespace.QName;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * @author chamerling
 * 
 */
public class RegistryImpl implements Registry<String> {

	/**
	 * Key is ITF QName as String
	 */
	LoadingCache<String, List<String>> itfs;

	LoadingCache<String, List<String>> srvItf;

	LoadingCache<String, List<String>> srvEpItf;

	private final RegistryEntryLoader loader;

	/**
	 * Entry Time To Live. Will be deleted from the cache after ttl seconds.
	 */
	private long ttl;

	/**
	 * 
	 */
	public RegistryImpl(final RegistryEntryLoader loader, long ttl) {
		this.loader = loader;
		this.ttl = ttl;

		itfs = CacheBuilder.newBuilder()
				.expireAfterAccess(this.ttl, TimeUnit.SECONDS)
				.build(new CacheLoader<String, List<String>>() {

					@Override
					public List<String> load(String key) throws Exception {
						return RegistryImpl.this.loader.getByInterface(key);
					}
				});

		srvItf = CacheBuilder.newBuilder()
				.expireAfterAccess(this.ttl, TimeUnit.SECONDS)
				.build(new CacheLoader<String, List<String>>() {

					@Override
					public List<String> load(String key) throws Exception {
						return RegistryImpl.this.loader
								.getByServiceInterface(key);
					}
				});

		srvEpItf = CacheBuilder.newBuilder()
				.expireAfterAccess(this.ttl, TimeUnit.SECONDS)
				.build(new CacheLoader<String, List<String>>() {

					@Override
					public List<String> load(String key) throws Exception {
						return RegistryImpl.this.loader
								.getByServiceInterfaceEndpoint(key);
					}
				});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.petalslink.dsb.registry.Registry#get(java.lang.String,
	 * javax.xml.namespace.QName, javax.xml.namespace.QName)
	 */
	public List<String> get(String endpoint, QName service, QName itf) {
		String key = toKey(endpoint, service, itf);

		if (endpoint != null && service != null && itf != null) {
			try {
				return srvEpItf.get(key);
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		} else if (service != null && itf != null) {
			try {
				return srvItf.get(key);
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		} else if (itf != null) {
			try {
				return itfs.get(key);
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
		return new ArrayList<String>();
	}

	private String toKey(String endpoint, QName service, QName itf) {
		StringBuilder sb = new StringBuilder();
		if (endpoint != null) {
			sb.append(endpoint);
		}
		if (service != null) {
			sb.append(service.toString());

		}
		if (itf != null) {
			sb.append(itf.toString());
		}
		return sb.toString();
	}

}
