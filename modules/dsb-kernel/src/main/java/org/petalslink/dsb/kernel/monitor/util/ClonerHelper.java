/**
 * 
 */
package org.petalslink.dsb.kernel.monitor.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @author adrien ruffie
 * This helper allow you to clone
 * an object (byte by byte).
 * 
 * JavaBean Requirements:
 * 
 * The class must implements at
 * least one of two Serializable
 * or Externalizable interfaces.
 * 
 * The class must have a no
 * arguments constructor
 */
public class ClonerHelper {
    
    private ClonerHelper(){
        //NOP
    }
    
    /**
     * Allow to clone an object
     * @param <O>
     * @param Object obj
     * @return Object
     * @throws IOException
     * @throws ClassNotFoundException
     */
    @SuppressWarnings({ "unchecked" })
    public static <O> O clone(final O obj) throws IOException, ClassNotFoundException{
        final ByteArrayOutputStream bin = new ByteArrayOutputStream();
        final ObjectOutputStream out = new ObjectOutputStream(bin);
        out.writeObject(obj);
        final byte[] bytes =  bin.toByteArray();
        final byte[] copy = copyByteToByte(bytes);
        final ByteArrayInputStream bout = new ByteArrayInputStream(copy);
        final ObjectInputStream in = new ObjectInputStream(bout);
        return (O)in.readObject();
    }
    
    /**
     * Allow to copy an array
     * byte to byte
     * @param byte[] bytes
     * @return byte[]
     */
    private static byte[] copyByteToByte(final byte[] bytes){
        final byte[] copy = new byte[bytes.length];
        for(int i = 0 ; i < bytes.length ; i++){
            copy[i] = bytes[i];
        }
        return copy;
    }
}
