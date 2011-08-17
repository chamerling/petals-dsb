/**
 * 
 */
package org.petalslink.notification;

import junit.framework.TestCase;

import org.oasis_open.docs.wsn.bw_2.InvalidFilterFault;
import org.oasis_open.docs.wsn.bw_2.InvalidMessageContentExpressionFault;
import org.oasis_open.docs.wsn.bw_2.InvalidProducerPropertiesExpressionFault;
import org.oasis_open.docs.wsn.bw_2.InvalidTopicExpressionFault;
import org.oasis_open.docs.wsn.bw_2.MultipleTopicsSpecifiedFault;
import org.oasis_open.docs.wsn.bw_2.NoCurrentMessageOnTopicFault;
import org.oasis_open.docs.wsn.bw_2.NotifyMessageNotSupportedFault;
import org.oasis_open.docs.wsn.bw_2.SubscribeCreationFailedFault;
import org.oasis_open.docs.wsn.bw_2.TopicExpressionDialectUnknownFault;
import org.oasis_open.docs.wsn.bw_2.TopicNotSupportedFault;
import org.oasis_open.docs.wsn.bw_2.UnacceptableInitialTerminationTimeFault;
import org.oasis_open.docs.wsn.bw_2.UnrecognizedPolicyRequestFault;
import org.oasis_open.docs.wsn.bw_2.UnsupportedPolicyRequestFault;
import org.oasis_open.docs.wsrf.rw_2.ResourceUnknownFault;
import org.petalslink.dsb.commons.service.api.Service;
import org.petalslink.dsb.cxf.CXFHelper;
import org.petalslink.dsb.notification.jaxws.NotificationProducerService;
import org.petalslink.dsb.notification.jaxws.api.NotificationConsumer;
import org.petalslink.dsb.notification.jaxws.api.NotificationProducer;

import com.ebmwebsourcing.wsstar.jaxb.notification.base.GetCurrentMessage;
import com.ebmwebsourcing.wsstar.jaxb.notification.base.GetCurrentMessageResponse;
import com.ebmwebsourcing.wsstar.jaxb.notification.base.Notify;
import com.ebmwebsourcing.wsstar.jaxb.notification.base.Subscribe;
import com.ebmwebsourcing.wsstar.jaxb.notification.base.SubscribeResponse;

/**
 * @author chamerling
 * 
 */
public class NotificationProducerServiceTest extends TestCase {

    public void testSubscribe() throws Exception {

        Service server = CXFHelper.getServiceFromFinalURL("http://localhost:8889/foo/bar/Service",
                NotificationConsumer.class, new NotificationConsumer() {

                    public void notify(Notify notify) {
                        // TODO Auto-generated method stub

                    }
                });

        server.start();
        // Thread.sleep(1000000L);
        server.stop();

    }

    public void testProducer() throws Exception {

        Service server = CXFHelper.getServiceFromFinalURL("http://localhost:8889/foo/bar/Service",
                NotificationProducer.class, new NotificationProducer() {

                    public SubscribeResponse subscribe(Subscribe subscribeRequest)
                            throws InvalidTopicExpressionFault, ResourceUnknownFault,
                            InvalidProducerPropertiesExpressionFault,
                            UnrecognizedPolicyRequestFault, TopicExpressionDialectUnknownFault,
                            NotifyMessageNotSupportedFault, InvalidFilterFault,
                            UnsupportedPolicyRequestFault, InvalidMessageContentExpressionFault,
                            SubscribeCreationFailedFault, TopicNotSupportedFault,
                            UnacceptableInitialTerminationTimeFault {
                        // TODO Auto-generated method stub
                        return null;
                    }

                    public GetCurrentMessageResponse getCurrentMessage(
                            GetCurrentMessage getCurrentMessageRequest)
                            throws InvalidTopicExpressionFault, ResourceUnknownFault,
                            TopicExpressionDialectUnknownFault, MultipleTopicsSpecifiedFault,
                            NoCurrentMessageOnTopicFault, TopicNotSupportedFault {
                        // TODO Auto-generated method stub
                        return null;
                    }

                });

        server.start();
        //Thread.sleep(1000000L);
        server.stop();

    }

}
