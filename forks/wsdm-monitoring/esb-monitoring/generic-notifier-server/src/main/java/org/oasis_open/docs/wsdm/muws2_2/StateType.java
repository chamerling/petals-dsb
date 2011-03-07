
package org.oasis_open.docs.wsdm.muws2_2;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.oasis_open.docs.wsdm.mows_2.OperationalStateType;
import org.oasis_open.docs.wsdm.mows_2.RequestProcessingStateType;


/**
 * <p>Java class for StateType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="StateType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://docs.oasis-open.org/wsdm/muws2-2.xsd}CategoryType">
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StateType")
@XmlSeeAlso({
    OperationalStateType.class,
    RequestProcessingStateType.class
})
public class StateType
    extends CategoryType
{


}
