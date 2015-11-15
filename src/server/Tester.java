package server;

import java.nio.ByteBuffer;

import configutils.Constants;

/**
 * Created by karamc on 11/14/15.
 */
public class Tester {
    public static void main(final String[] args) {
        Writer writer = new Writer();
        String testString = "SdADSDWFDQWFWF#R#rrfefefhLFFhflihfHFoiFHCOINFwoeifn";
        ByteBuffer testBuf = ByteBuffer.allocate((int) Constants.CHUNK_SIZE);
        testBuf.put(testString.getBytes());
        writer.write(testBuf, 34, 5, 1, 3);
        ByteBuffer resultBuf = ByteBuffer.allocate((int) Constants.CHUNK_SIZE);
        /*
         * writer.read(resultBuf, 34, 1, 3); try { System.out.println(new
         * String(resultBuf.array(), "ASCII")); } catch
         * (UnsupportedEncodingException e) { e.printStackTrace(); }
         */

    }
}
