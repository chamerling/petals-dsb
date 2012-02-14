/**
 * 
 */
package org.petalslink.dsb.kernel.esmanagement;

import javax.jws.WebService;
import javax.xml.namespace.QName;

import org.oasis_open.docs.wsn.bw_2.InvalidFilterFault;
import org.oasis_open.docs.wsn.bw_2.InvalidMessageContentExpressionFault;
import org.oasis_open.docs.wsn.bw_2.InvalidProducerPropertiesExpressionFault;
import org.oasis_open.docs.wsn.bw_2.InvalidTopicExpressionFault;
import org.oasis_open.docs.wsn.bw_2.NotifyMessageNotSupportedFault;
import org.oasis_open.docs.wsn.bw_2.SubscribeCreationFailedFault;
import org.oasis_open.docs.wsn.bw_2.TopicExpressionDialectUnknownFault;
import org.oasis_open.docs.wsn.bw_2.TopicNotSupportedFault;
import org.oasis_open.docs.wsn.bw_2.UnableToDestroySubscriptionFault;
import org.oasis_open.docs.wsn.bw_2.UnacceptableInitialTerminationTimeFault;
import org.oasis_open.docs.wsn.bw_2.UnrecognizedPolicyRequestFault;
import org.oasis_open.docs.wsn.bw_2.UnsupportedPolicyRequestFault;
import org.oasis_open.docs.wsrf.rpw_2.InvalidResourcePropertyQNameFault;
import org.oasis_open.docs.wsrf.rw_2.ResourceUnavailableFault;
import org.oasis_open.docs.wsrf.rw_2.ResourceUnknownFault;
import org.objectweb.fractal.fraclet.annotation.annotations.FractalComponent;
import org.objectweb.fractal.fraclet.annotation.annotations.Interface;
import org.objectweb.fractal.fraclet.annotation.annotations.LifeCycle;
import org.objectweb.fractal.fraclet.annotation.annotations.Monolog;
import org.objectweb.fractal.fraclet.annotation.annotations.Provides;
import org.objectweb.fractal.fraclet.annotation.annotations.type.LifeCycleType;
import org.objectweb.util.monolog.api.Logger;
import org.ow2.petals.util.LoggingUtil;

import com.ebmwebsourcing.wsstar.jaxb.notification.base.Notify;
import com.ebmwebsourcing.wsstar.jaxb.notification.base.Subscribe;
import com.ebmwebsourcing.wsstar.jaxb.notification.base.SubscribeResponse;
import com.ebmwebsourcing.wsstar.jaxb.notification.base.Unsubscribe;
import com.ebmwebsourcing.wsstar.jaxb.notification.base.UnsubscribeResponse;
import com.ebmwebsourcing.wsstar.jaxb.resource.resourceproperties.GetResourcePropertyResponse;

import esstar.petalslink.com.data.management.admin._1.Deploy;
import esstar.petalslink.com.data.management.admin._1.DeployResponse;
import esstar.petalslink.com.data.management.admin._1.GetAdditionalContent;
import esstar.petalslink.com.data.management.admin._1.GetAdditionalContentResponse;
import esstar.petalslink.com.data.management.admin._1.GetContent;
import esstar.petalslink.com.data.management.admin._1.GetContentResponse;
import esstar.petalslink.com.data.management.admin._1.GetExecutionEnvironmentInformation;
import esstar.petalslink.com.data.management.admin._1.GetExecutionEnvironmentInformationResponse;
import esstar.petalslink.com.data.management.admin._1.GetResourceIdentifiers;
import esstar.petalslink.com.data.management.admin._1.GetResourceIdentifiersResponse;
import esstar.petalslink.com.service.management.admin._1_0.AdminManagementException;

/**
 * @author chamerling
 * 
 */
@WebService
@FractalComponent
@Provides(interfaces = { @Interface(name = "service", signature = esstar.petalslink.com.service.management.admin._1_0.AdminManagement.class) })
public class AdminManagement implements
        esstar.petalslink.com.service.management.admin._1_0.AdminManagement {

    @Monolog(name = "logger")
    private Logger logger;

    private LoggingUtil log;

    @LifeCycle(on = LifeCycleType.START)
    protected void start() {
        this.log = new LoggingUtil(this.logger);
    }

    @LifeCycle(on = LifeCycleType.STOP)
    protected void stopFc() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see esstar.petalslink.com.service.management.admin._1_0.AdminManagement#
     * connectToGovernance(java.lang.String)
     */
    public void connectToGovernance(String arg0) throws AdminManagementException {

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * esstar.petalslink.com.service.management.admin._1_0.AdminManagement#deploy
     * (esstar.petalslink.com.data.management.admin._1.Deploy)
     */
    public DeployResponse deploy(Deploy arg0) throws AdminManagementException {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see esstar.petalslink.com.service.management.admin._1_0.AdminManagement#
     * getAdditionalContent
     * (esstar.petalslink.com.data.management.admin._1.GetAdditionalContent)
     */
    public GetAdditionalContentResponse getAdditionalContent(GetAdditionalContent arg0)
            throws AdminManagementException {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see esstar.petalslink.com.service.management.admin._1_0.AdminManagement#
     * getContent(esstar.petalslink.com.data.management.admin._1.GetContent)
     */
    public GetContentResponse getContent(GetContent arg0) throws AdminManagementException {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see esstar.petalslink.com.service.management.admin._1_0.AdminManagement#
     * getExecutionEnvironmentInformation
     * (esstar.petalslink.com.data.management.admin
     * ._1.GetExecutionEnvironmentInformation)
     */
    public GetExecutionEnvironmentInformationResponse getExecutionEnvironmentInformation(
            GetExecutionEnvironmentInformation arg0) throws AdminManagementException {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see esstar.petalslink.com.service.management.admin._1_0.AdminManagement#
     * getResourceIdentifiers
     * (esstar.petalslink.com.data.management.admin._1.GetResourceIdentifiers)
     */
    public GetResourceIdentifiersResponse getResourceIdentifiers(GetResourceIdentifiers arg0)
            throws AdminManagementException {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see esstar.petalslink.com.service.management.admin._1_0.AdminManagement#
     * getResourceProperty(javax.xml.namespace.QName)
     */
    public GetResourcePropertyResponse getResourceProperty(QName arg0)
            throws InvalidResourcePropertyQNameFault, ResourceUnavailableFault,
            ResourceUnknownFault {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * esstar.petalslink.com.service.management.admin._1_0.AdminManagement#notify
     * (com.ebmwebsourcing.wsstar.jaxb.notification.base.Notify)
     */
    public void notify(Notify arg0) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * esstar.petalslink.com.service.management.admin._1_0.AdminManagement#stop
     * ()
     */
    public void stop() {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * esstar.petalslink.com.service.management.admin._1_0.AdminManagement#subscribe
     * (com.ebmwebsourcing.wsstar.jaxb.notification.base.Subscribe)
     */
    public SubscribeResponse subscribe(Subscribe subscribe)
            throws UnacceptableInitialTerminationTimeFault, TopicExpressionDialectUnknownFault,
            InvalidTopicExpressionFault, NotifyMessageNotSupportedFault, TopicNotSupportedFault,
            UnsupportedPolicyRequestFault, ResourceUnknownFault, InvalidFilterFault,
            InvalidProducerPropertiesExpressionFault, UnrecognizedPolicyRequestFault,
            InvalidMessageContentExpressionFault, SubscribeCreationFailedFault {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see esstar.petalslink.com.service.management.admin._1_0.AdminManagement#
     * unconnectToGovernance(java.lang.String)
     */
    public void unconnectToGovernance(String url) throws AdminManagementException {

    }

    /*
     * (non-Javadoc)
     * 
     * @see esstar.petalslink.com.service.management.admin._1_0.AdminManagement#
     * unsubscribe(com.ebmwebsourcing.wsstar.jaxb.notification.base.Unsubscribe)
     */
    public UnsubscribeResponse unsubscribe(Unsubscribe unsubscribe)
            throws UnableToDestroySubscriptionFault, ResourceUnknownFault {
        return null;
    }

}
