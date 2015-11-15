package channel;

import java.nio.ByteBuffer;

public interface DataChannel {

    public long getDeviceSize();

    /**
     * 
     * @param buffer
     *            buffer filled with data
     * @param offset
     *            into block device
     * @param length
     *            total data length requested
     */
    public void read(ByteBuffer buffer, long offset, long length);

    /**
     * 
     * @param buffer
     *            buffer filled with data
     * @param offset
     *            into block device
     * @param length
     *            of bytes in buffer
     */
    public void write(byte[] buffer, long offset, long length);

}
