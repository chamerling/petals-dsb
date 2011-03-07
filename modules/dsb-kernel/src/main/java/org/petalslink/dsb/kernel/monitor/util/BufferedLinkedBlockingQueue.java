/**
 * PETALS - PETALS Services Platform. Copyright (c) 2007 EBM Websourcing,
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
 */
package org.petalslink.dsb.kernel.monitor.util;

import java.io.Serializable;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author aruffie - EBM WebSourcing
 * @param <E>
 * Represent a FIFO stack, its allow to
 * store N elements. N correspond to
 * a specified size in constructor, this
 * size can not be exceeded. If an
 * element is insert when the stack is
 * full, the head element is remove
 * in order to release space to the new
 * element.
 */
public class BufferedLinkedBlockingQueue<E> extends LinkedBlockingQueue<E> implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -8991131805508182804L;
    
    /*
     * Mutex that allow synchronization
     */
    private final Object mutex;
    
    public BufferedLinkedBlockingQueue(final int size) {
        super(size);
        // Just use one byte for mutex
        this.mutex = 0x0001;
    }

    /* (non-Javadoc)
     * @see java.util.concurrent.LinkedBlockingQueue#put(java.lang.Object)
     */
    @Override
    public synchronized void put(E o) throws InterruptedException{
        // Take the mutex
        synchronized (this.mutex) {
            /*
             * If the size queue is reached,
             * removes the head of this
             * queue, in order to release
             * space
             */
            if(this.remainingCapacity() <= 0){
                super.remove();
            }
            super.put(o);
        }
    }

    /* (non-Javadoc)
     * @see java.util.concurrent.LinkedBlockingQueue#poll()
     */
    @Override
    public synchronized E poll() {
        // Take the mutex
        synchronized (this.mutex) {
            return super.poll();
        }
    }

    /* (non-Javadoc)
     * @see java.util.concurrent.LinkedBlockingQueue#remove(java.lang.Object)
     */
    @Override
    public synchronized boolean remove(Object o) {
        // Take the mutex
        synchronized (this.mutex) {
            return super.remove(o);
        }
    }
  
    /* (non-Javadoc)
     * @see java.util.AbstractQueue#remove()
     */
    @Override
    public synchronized E remove() {
        // Take the mutex
        synchronized (this.mutex) {
            return super.remove();
        }
    }

    /* (non-Javadoc)
     * @see java.util.concurrent.LinkedBlockingQueue#size()
     */
    @Override
    public synchronized int size() {
        // Take the mutex
        synchronized (this.mutex) {
            return super.size();
        }
    }

    /* (non-Javadoc)
     * @see java.util.concurrent.LinkedBlockingQueue#toArray()
     */
    @Override
    public synchronized Object[] toArray() {
        // Take the mutex
        synchronized (this.mutex) {
            return super.toArray();
        }
    }

    /* (non-Javadoc)
     * @see java.util.concurrent.LinkedBlockingQueue#toArray(T[])
     */
    @Override
    public synchronized <T> T[] toArray(T[] a) {
        // Take the mutex
        synchronized (this.mutex) {
            return super.toArray(a);
        }
    } 
}
