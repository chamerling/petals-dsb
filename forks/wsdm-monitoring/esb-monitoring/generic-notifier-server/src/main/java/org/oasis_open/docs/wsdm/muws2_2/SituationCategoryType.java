
package org.oasis_open.docs.wsdm.muws2_2;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SituationCategoryType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SituationCategoryType">
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
@XmlType(name = "SituationCategoryType")
@XmlSeeAlso({
    TraceReport.class,
    RequestSituation.class,
    CreateSituation.class,
    PauseInitiated.class,
    StopCompleted.class,
    StopInitiated.class,
    CreateInitiated.class,
    HeartbeatReport.class,
    DestroySituation.class,
    ReconnectInitiated.class,
    StartInitiated.class,
    ConnectSituation.class,
    DebugReport.class,
    LogReport.class,
    StartSituation.class,
    ConnectInitiated.class,
    ConfigureSituation.class,
    DestroyCompleted.class,
    CapabilitySituation.class,
    AbortInitiated.class,
    StopSituation.class,
    RequestInitiated.class,
    PerformanceReport.class,
    StatusReport.class,
    DestroyInitiated.class,
    SecurityReport.class,
    ReportSituation.class,
    RestartInitiated.class,
    CreateCompleted.class,
    StartCompleted.class,
    ConnectCompleted.class,
    RequestCompleted.class,
    AvailabilitySituation.class,
    OtherSituation.class
})
public class SituationCategoryType
    extends CategoryType
{


}
