package channel;

import java.nio.ByteBuffer;

import com.google.api.services.drive.Drive;

public class DriveClient implements Client {

    private final Drive drive;

    public DriveClient(final Drive drive) {
        this.drive = drive;
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
