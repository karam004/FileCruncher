package channel;

import java.nio.ByteBuffer;

import com.dropbox.core.DbxClient;

public class DropboxClient implements Client {

    private final DbxClient client;

    public DropboxClient(final DbxClient client) {
        this.client = client;
    }

    @Override
    public void read(final ByteBuffer buffer, final long id, final long offset,
            final long length) {
        // TODO Auto-generated method stub

    }

    @Override
    public void write(final ByteBuffer buffer, final long id,
            final long offset, final long start, final long end) {
        // TODO Auto-generated method stub

    }

}
