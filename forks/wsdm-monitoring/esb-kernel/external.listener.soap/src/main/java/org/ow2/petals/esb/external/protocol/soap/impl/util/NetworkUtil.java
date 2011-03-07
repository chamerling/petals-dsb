/**
 * PETALS - PETALS Services Platform. Copyright (c) 2008 EBM Websourcing,
 * http://www.ebmwebsourcing.com/
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * -------------------------------------------------------------------------
 * $Id$
 * -------------------------------------------------------------------------
 */
package org.ow2.petals.esb.external.protocol.soap.impl.util;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

/**
 * A network class util used to manipulate network related stuff
 * 
 * @author Christophe HAMERLING - eBM WebSourcing
 * 
 */
public class NetworkUtil {

    /**
     * Get all the interfaces which are not loopback ones
     * 
     * @return
     */
    public static Set<NetworkInterface> getAllLocalInterfaces() {
        Set<NetworkInterface> result = new HashSet<NetworkInterface>();
        try {
            Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();
            while (e.hasMoreElements()) {
                NetworkInterface ni = e.nextElement();
                
                Enumeration<InetAddress> set = ni.getInetAddresses();
                // assume that all the inet addresses of the interface are all
                // loopback or not all loopback...
                if (set.hasMoreElements()) {
                    InetAddress address = set.nextElement();
                    if (!address.isLoopbackAddress()) {
                        result.add(ni);
                    }
                }
            }
        } catch (SocketException e) {
        }
        return result;
    }

    /**
     * Get all the host interfaces including the loopback one
     * 
     * @return
     */
    public static Set<NetworkInterface> getAllInterfaces() {
        Set<NetworkInterface> result = new HashSet<NetworkInterface>();
        try {
            Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();
            while (e.hasMoreElements()) {
                result.add(e.nextElement());
            }
        } catch (SocketException e) {
        }
        return result;
    }

    /**
     * Get all the IPv4 {@link InetAddress} of the local host which are not
     * loopback ones
     * 
     * @return
     */
    public static Set<Inet4Address> getAllLocalIPv4InetAddresses() {
        Set<Inet4Address> result = new HashSet<Inet4Address>();
        Set<NetworkInterface> iset = getAllLocalInterfaces();
        for (NetworkInterface networkInterface : iset) {
            Enumeration<InetAddress> e = networkInterface.getInetAddresses();
            while (e.hasMoreElements()) {
                InetAddress elem = e.nextElement();
                if (elem instanceof Inet4Address) {
                    result.add((Inet4Address) elem);
                }
            }
        }
        return result;
    }

    /**
     * Get all the local IPV4 addresses without the loopback one
     * 
     * @return
     */
    public static Set<Inet4Address> getAllIPv4InetAddresses() {
        Set<Inet4Address> result = new HashSet<Inet4Address>();
        Set<NetworkInterface> iset = getAllInterfaces();
        for (NetworkInterface networkInterface : iset) {
            Enumeration<InetAddress> e = networkInterface.getInetAddresses();
            while (e.hasMoreElements()) {
                InetAddress elem = e.nextElement();
                if (elem instanceof Inet4Address) {
                    result.add((Inet4Address) elem);
                }
            }
        }
        return result;
    }

    /**
     * Returns true if the given address is not null and is one of the local
     * host address including the loopback address
     * 
     * @param address
     * @return
     */
    public static boolean isLocalAddress(InetAddress address) {
        boolean result = false;
        if (address != null) {
            try {
                Enumeration<NetworkInterface> itfs = NetworkInterface.getNetworkInterfaces();
                while (itfs.hasMoreElements()) {
                    NetworkInterface itf = itfs.nextElement();
                    Enumeration<InetAddress> iaenum = itf.getInetAddresses();
                    while (iaenum.hasMoreElements() && !result) {
                        InetAddress ia = iaenum.nextElement();
                        result = ia.equals(address);
                    }
                }
            } catch (SocketException e) {
            }
        }
        return result;
    }

    /**
     * Test if the given address is a loopback one ie localhost or 127.0.0.1
     * 
     * @param addr
     * @return
     */
    public static boolean isLoopbackAddress(InetAddress addr) {
        return addr.isLoopbackAddress();
        // JAVA 6
        // boolean result = false;
        // Set<NetworkInterface> itfs = getAllInterfaces();
        // for (NetworkInterface networkInterface : itfs) {
        // try {
        // if (networkInterface.isLoopback()) {
        // Enumeration<InetAddress> e = networkInterface.getInetAddresses();
        // while (e.hasMoreElements()) {
        // InetAddress elem = e.nextElement();
        // if (elem.equals(addr)) {
        // return true;
        // }
        // }
        // }
        // } catch (SocketException e) {
        // }
        // }
        // return result;
    }
}
