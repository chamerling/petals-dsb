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
package org.ow2.petals.binding.soap.util;

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
 */
public class NetworkUtil {

    /**
     * Get all the IPv4 {@link InetAddress} of the local host
     * 
     * @return all the local IPv4
     * 
     * @throws SocketException if an I/O error occurs 
     */
    public static Set<Inet4Address> getAllLocalIPv4InetAddresses() throws SocketException {
        Set<Inet4Address> result = new HashSet<Inet4Address>();
        Enumeration<NetworkInterface> niEnum = NetworkInterface.getNetworkInterfaces();
        while (niEnum.hasMoreElements()) {
            NetworkInterface ni = niEnum.nextElement();
            Set<Inet4Address> ipForNi =  getIPv4InetAdressesFromNetworkInterface(ni);
            result.addAll(ipForNi);          
        }
        return result;
    }

    /**
     * Get the IPv4  {@link InetAddress} of the specified network interface
     * 
     * @param ni a network interface
     * 
     * @return all the IPv4 for the specified network interface
     */
    private static final Set<Inet4Address> getIPv4InetAdressesFromNetworkInterface(NetworkInterface ni) {
        Set<Inet4Address> result = new HashSet<Inet4Address>();
        
        Enumeration<InetAddress> e = ni.getInetAddresses();
        while (e.hasMoreElements()) {
            InetAddress ia = e.nextElement();
            if (ia instanceof Inet4Address) {
                result.add((Inet4Address) ia);
            }
        }
        
        return result;
    }
}
