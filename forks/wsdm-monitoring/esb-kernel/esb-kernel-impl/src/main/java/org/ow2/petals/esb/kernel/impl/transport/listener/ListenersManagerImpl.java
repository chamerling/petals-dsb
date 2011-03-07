package org.ow2.petals.esb.kernel.impl.transport.listener;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.xml.namespace.QName;

import org.apache.commons.lang.NotImplementedException;
import org.ow2.petals.esb.kernel.api.endpoint.Endpoint;
import org.ow2.petals.esb.kernel.api.transport.listener.Listener;
import org.ow2.petals.esb.kernel.api.transport.listener.ListenersManager;

public class ListenersManagerImpl implements ListenersManager {

	public static final int DEFAULT_THREAD_COUNTER = 5;
	
	private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;

	
	public ListenersManagerImpl(int numberOfThreads, Map<QName, Endpoint> endpoints) {
		if(numberOfThreads <= 0) {
			numberOfThreads = 1;
		}
		
		// create the listener collection
        Collection<Listener> listeners = new HashSet<Listener>();
        for(int i = 0; i < numberOfThreads; i++) {
        	listeners.add(new ListenerImpl(endpoints));
        }
        
        // Start the listeners in the thread worker
        scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(
        		numberOfThreads);
        
        for (Listener listener : listeners) {
            scheduledThreadPoolExecutor.scheduleWithFixedDelay(listener, 0, 1,
                TimeUnit.MILLISECONDS);
        }
	}
	
	public void shutdownAllListeners() {
        scheduledThreadPoolExecutor.shutdown();
        try {
            scheduledThreadPoolExecutor.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e1) {
            // do nothing
        }
	}
	
	public void addListener(Listener listener) {
		throw new NotImplementedException();
	}
	

	public Listener removeListener(Listener listener) {
		throw new NotImplementedException();
	}

}
