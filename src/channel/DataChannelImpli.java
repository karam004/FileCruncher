package channel;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import configutils.ConfigHelper;

public class DataChannelImpli implements DataChannel {
    private static final Logger logger = Logger
            .getLogger(DataChannelImpli.class);
    private static final int CHUNK_SIZE = 4 * 1024;

    private final Clients clients;

    public DataChannelImpli(final Clients clients) {
        this.clients = clients;
    }

    @Override
    public long getDeviceSize() {
        try {
            return ConfigHelper.createConfig().getTotalMem();
        } catch (IllegalArgumentException | IOException e) {
            logger.error(e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void read(final ByteBuffer buffer, final long offset,
            final long length) {
        int chunk_id = (int) offset / CHUNK_SIZE;
        if ((chunk_id % 3) == 0) {

        } else if (((chunk_id - 1) % 3) == 0) {

        } else if (((chunk_id - 2) % 3) == 0) {

        }
    }

    @Override
    public void write(final byte[] buffer, final long offset, final long length) {
        // TODO Auto-generated method stub

    }

}
