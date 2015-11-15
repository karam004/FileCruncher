package channel;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import configutils.ConfigHelper;
import configutils.Constants;

public class DataChannelImpli implements DataChannel {
    private static final Logger logger = Logger
            .getLogger(DataChannelImpli.class);
    private static final long CHUNK_SIZE = Constants.CHUNK_SIZE;

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
        long chunk_id_begin = offset / CHUNK_SIZE;
        long chunk_id_end = (offset + length) / CHUNK_SIZE;
        if (((offset + length) % CHUNK_SIZE) == 0) {
            chunk_id_end--;
        }
        logger.debug("Read Request for len :" + length + " off " + offset);
        for (long i = chunk_id_begin; i <= chunk_id_end; i++) {
            long off_into_chunk = 0;

            if (offset > (CHUNK_SIZE * i)) {
                off_into_chunk = offset - (CHUNK_SIZE * i);
            }

            long datalength = getlengthToRead(i, offset, length);

            Clients.getClient(i).read(buffer, i, off_into_chunk, datalength);

        }
    }

    @Override
    public void write(final byte[] buffer, final long offset, final long length) {
        long chunk_id_begin = offset / CHUNK_SIZE;
        long chunk_id_end = (offset + CHUNK_SIZE) / CHUNK_SIZE;
        if (((offset + CHUNK_SIZE) % CHUNK_SIZE) == 0) {
            chunk_id_end--;
        }

        ByteBuffer buff = ByteBuffer.wrap(buffer);

        if (chunk_id_begin == chunk_id_end) {
            long offset_into_chunk = offset - CHUNK_SIZE * chunk_id_begin;
            logger.debug("write to one chunk " + chunk_id_begin);
            logger.debug("write to one chunk off " + offset + " len " + length);
            Clients.getClient(chunk_id_begin).write(buff, chunk_id_begin,
                    offset_into_chunk, 0, length);
        } else {
            long offset_into_chunk = offset - CHUNK_SIZE * chunk_id_begin;
            long len = (chunk_id_end) * CHUNK_SIZE - offset;

            logger.debug("write to two chunk " + chunk_id_begin + ","
                    + chunk_id_end);
            logger.debug("write to two chunk off " + offset + " len " + length);
            Clients.getClient(chunk_id_begin).write(buff, chunk_id_begin,
                    offset_into_chunk, 0, len);
            // write to next chunk
            offset_into_chunk = 0;
            Clients.getClient(chunk_id_end).write(buff, chunk_id_end,
                    offset_into_chunk, len, length - len);

        }
    }

    private long getlengthToRead(final long chunkid, final long offset,
            final long len) {
        long end;
        long start;
        if ((offset + len) > ((chunkid + 1) * CHUNK_SIZE)) {
            end = ((chunkid + 1) * CHUNK_SIZE);
        } else {
            end = offset + len;
        }

        if ((offset < (chunkid) * CHUNK_SIZE)) {
            start = chunkid * CHUNK_SIZE;
        } else {
            start = offset;
        }

        logger.info("reading data of len " + (end - start) + " from chunk :"
                + chunkid);

        return end - start;
    }
}
