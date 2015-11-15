package channel;

import java.nio.ByteBuffer;

public interface Client {

    public void read(ByteBuffer buffer, long id, long offset, long len);

    public void write(ByteBuffer buffer, long id, long offset, long start,
            long len);
}
