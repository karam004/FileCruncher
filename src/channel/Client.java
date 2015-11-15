package channel;

import java.nio.ByteBuffer;

public interface Client {

    public void read(ByteBuffer buffer, long offset, long length);

    public void write(byte[] buffer, long offset, long length);
}
