
package org.oasis_open.docs.wsdm.mows_2;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.oasis_open.docs.wsdm.muws2_2.StateType;


/**
 * <p>Java class for OperationalStateType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="OperationalStateType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://docs.oasis-open.org/wsdm/muws2-2.xsd}StateType">
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OperationalStateType")
@XmlSeeAlso({
    BusyState.class,
    StoppedState.class,
    DownState.class,
    UpState.class,
    CrashedState.class,
    SaturatedState.class,
    IdleState.class
})
public class OperationalStateType
    extends StateType
{


}
