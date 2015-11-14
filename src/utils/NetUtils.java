package utils;

import java.io.IOException;
import java.io.InputStream;

public class NetUtils {

    public static int receiveAll(final InputStream in, final byte[] buf)
            throws IOException {
        int offset = 0;
        int byteread = 0;

        while ((byteread = in.read(buf, offset, buf.length - offset)) != -1) {
            offset += byteread;
            if (offset >= buf.length) {
                break;
            }
        }

        return offset;
    }
}
