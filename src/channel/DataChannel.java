package channel;

import java.nio.ByteBuffer;

public interface DataChannel {

    public long getDeviceSize();

    public void read(ByteBuffer buffer, long offset, long length);

    public void write();

}
