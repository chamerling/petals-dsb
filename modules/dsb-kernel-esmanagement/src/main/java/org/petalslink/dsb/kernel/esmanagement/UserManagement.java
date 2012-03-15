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
import org.ow2.petals.util.oldies.LoggingUtil;

import com.ebmwebsourcing.wsstar.jaxb.notification.base.Notify;
import com.ebmwebsourcing.wsstar.jaxb.notification.base.Subscribe;
import com.ebmwebsourcing.wsstar.jaxb.notification.base.SubscribeResponse;
import com.ebmwebsourcing.wsstar.jaxb.notification.base.Unsubscribe;
import com.ebmwebsourcing.wsstar.jaxb.notification.base.UnsubscribeResponse;
import com.ebmwebsourcing.wsstar.jaxb.resource.resourceproperties.GetResourcePropertyResponse;

import esstar.petalslink.com.data.management.user._1.Bind;
import esstar.petalslink.com.data.management.user._1.BindResponse;
import esstar.petalslink.com.data.management.user._1.Deploy;
import esstar.petalslink.com.data.management.user._1.DeployResponse;
import esstar.petalslink.com.data.management.user._1.Proxify;
import esstar.petalslink.com.data.management.user._1.ProxifyResponse;
import esstar.petalslink.com.service.management.user._1_0.UserManagementException;

/**
 * @author chamerling
 * 
 */
@WebService
@FractalComponent
@Provides(interfaces = { @Interface(name = "service", signature = esstar.petalslink.com.service.management.user._1_0.UserManagement.class) })
public class UserManagement implements
        esstar.petalslink.com.service.management.user._1_0.UserManagement {
    
    @Monolog(name = "logger")
    private Logger logger;

    private LoggingUtil log;

    @LifeCycle(on = LifeCycleType.START)
    protected void start() {
        this.log = new LoggingUtil(this.logger);
    }

    @LifeCycle(on = LifeCycleType.STOP)
    protected void stop() {
    }



    /*
     * (non-Javadoc)
     * 
     * @see
     * esstar.petalslink.com.service.management.user._1_0.UserManagement#bind
     * (esstar.petalslink.com.data.management.user._1.Bind)
     */
    public BindResponse bind(Bind bind) throws UserManagementException {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * esstar.petalslink.com.service.management.user._1_0.UserManagement#deploy
     * (esstar.petalslink.com.data.management.user._1.Deploy)
     */
    public DeployResponse deploy(Deploy deploy) throws UserManagementException {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * esstar.petalslink.com.service.management.user._1_0.UserManagement#expose
     * (javax.xml.namespace.QName, java.lang.String)
     */
    public String expose(QName service, String endpoint) throws UserManagementException {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see esstar.petalslink.com.service.management.user._1_0.UserManagement#
     * getResourceProperty(javax.xml.namespace.QName)
     */
    public GetResourcePropertyResponse getResourceProperty(QName arg0)
            throws ResourceUnavailableFault, InvalidResourcePropertyQNameFault,
            ResourceUnknownFault {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * esstar.petalslink.com.service.management.user._1_0.UserManagement#notify
     * (com.ebmwebsourcing.wsstar.jaxb.notification.base.Notify)
     */
    public void notify(Notify notify) {

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * esstar.petalslink.com.service.management.user._1_0.UserManagement#proxify
     * (esstar.petalslink.com.data.management.user._1.Proxify)
     */
    public ProxifyResponse proxify(Proxify proxify) throws UserManagementException {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * esstar.petalslink.com.service.management.user._1_0.UserManagement#subscribe
     * (com.ebmwebsourcing.wsstar.jaxb.notification.base.Subscribe)
     */
    public SubscribeResponse subscribe(Subscribe subscribe) throws NotifyMessageNotSupportedFault,
            InvalidFilterFault, InvalidTopicExpressionFault, TopicExpressionDialectUnknownFault,
            UnsupportedPolicyRequestFault, SubscribeCreationFailedFault,
            UnacceptableInitialTerminationTimeFault, UnrecognizedPolicyRequestFault,
            TopicNotSupportedFault, InvalidProducerPropertiesExpressionFault, ResourceUnknownFault,
            InvalidMessageContentExpressionFault {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * esstar.petalslink.com.service.management.user._1_0.UserManagement#unsubscribe
     * (com.ebmwebsourcing.wsstar.jaxb.notification.base.Unsubscribe)
     */
    public UnsubscribeResponse unsubscribe(Unsubscribe unsubscribe)
            throws UnableToDestroySubscriptionFault, ResourceUnknownFault {
        return null;
    }

}
