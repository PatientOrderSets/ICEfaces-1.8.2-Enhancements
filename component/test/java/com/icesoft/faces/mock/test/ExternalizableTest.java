/*
 * TODO
 */
package com.icesoft.faces.mock.test;

import com.icesoft.faces.context.effects.Appear;
import com.icesoft.faces.context.effects.Effect;
import com.icesoft.faces.context.effects.EffectQueue;
import com.icesoft.faces.context.effects.Move;
import com.icesoft.faces.context.effects.Scale;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 *
 * @author fye
 */
public class ExternalizableTest extends TestCase {

    public static Test suite() {
        return new TestSuite(ExternalizableTest.class);
    }

    public void testSerializable() {
//        readAndWriteCompress();
//        readAndWrite();
    }

    public void readAndWriteCompress() {
        try {
            GZIPOutputStream zos = null;
            ObjectOutputStream oos = null;
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            zos = new GZIPOutputStream(bos);
            oos = new ObjectOutputStream(zos);
            Move shake0 = new Move(99, 299, "relative");
            shake0.setId("001");
            Appear shake1 = new Appear(3.09f, 4.09f);
            shake1.setId("002");
            shake1.setQueue("front");
            shake1.setDelay(1.01f);
            shake1.setId("test1.shake1");
            shake1.setDuration(10.09f);
            shake1.setFps(2.02f);
            //shake1.setFrom(3.03f);

            Scale shake2 = new Scale(19.09f);
            shake2.setId("003");

            EffectQueue effectQueue = new EffectQueue("move");
            effectQueue.add(shake0);
            effectQueue.add(shake1);
            effectQueue.add(shake2);
            effectQueue.setId("queueId");
            


            oos.writeObject(effectQueue);
            oos.close();
            byte[] bytes = bos.toByteArray();
            InputStream in = new ByteArrayInputStream(bytes);
            GZIPInputStream gis = new GZIPInputStream(in);
            ObjectInputStream ois = new ObjectInputStream(gis);

            Effect readEffect = (Effect) ois.readObject();
            //String message = "@@" + shake2.getFunctionName();
            String message = "combined";
            print(message, readEffect);



            //assertEquals(1.01f, shake2.getDelay(), Float.MIN_VALUE);

        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ExternalizableTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ExternalizableTest.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void readAndWrite() {
        ObjectOutputStream oos = null;
        try {
            ByteArrayOutputStream bos = null;
            ByteArrayInputStream bis = null;
            bos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(bos);

            Move shake1 = new Move(99, 299, "relative");

            shake1.setQueue("front");
            shake1.setDelay(1.01f);
            shake1.setId("test1.shake1");
            shake1.setFps(2.02f);
            shake1.setFrom(3.03f);

            oos.writeObject(shake1);
            oos.close();
            byte[] bytes = bos.toByteArray();
            InputStream in = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(in);

            Effect shake2 = (Effect) ois.readObject();
            String message = "@@" + shake2.getFunctionName();
            print(message, shake2);



            assertEquals(1.01f, shake2.getDelay(), Float.MIN_VALUE);

        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ExternalizableTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ExternalizableTest.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                oos.close();
            } catch (IOException ex) {
                Logger.getLogger(ExternalizableTest.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    private void print(String message, Effect effect) {

        Logger.getLogger(ExternalizableTest.class.getName()).log(Level.INFO, message +
                "\n" + effect.getId()+
                "\n" + effect.toString() +
                "\n" + String.valueOf(effect.hashCode()) +
                "\n" + effect.getClass().getName());
    }
}
