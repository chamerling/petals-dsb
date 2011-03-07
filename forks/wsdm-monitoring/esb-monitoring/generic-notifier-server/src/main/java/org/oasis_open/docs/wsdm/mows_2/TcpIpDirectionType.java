
package org.oasis_open.docs.wsdm.mows_2;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for TcpIpDirectionType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="TcpIpDirectionType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="to"/>
 *     &lt;enumeration value="from"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "TcpIpDirectionType")
@XmlEnum
public enum TcpIpDirectionType {

    @XmlEnumValue("to")
    TO("to"),
    @XmlEnumValue("from")
    FROM("from");
    private final String value;

    TcpIpDirectionType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static TcpIpDirectionType fromValue(String v) {
        for (TcpIpDirectionType c: TcpIpDirectionType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
