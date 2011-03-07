
package org.oasis_open.docs.wsdm.mows_2;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for TcpIpProtocolType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="TcpIpProtocolType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="TCP"/>
 *     &lt;enumeration value="UDP"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "TcpIpProtocolType")
@XmlEnum
public enum TcpIpProtocolType {

    TCP,
    UDP;

    public String value() {
        return name();
    }

    public static TcpIpProtocolType fromValue(String v) {
        return valueOf(v);
    }

}
