package channel;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxEntry;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxWriteMode;

import configutils.Constants;

public class DropboxClient implements Client {

    private final static Logger logger = Logger.getLogger(DropboxClient.class);

    private final DbxClient client;

    public DropboxClient(final DbxClient client) {
        this.client = client;
    }

    @Override
    public void read(final ByteBuffer buffer, final long id, final long offset,
            final long length) {
        logger.info("Dropbox read chunk " + id);
        if (!isExisting(id)) {
            logger.info("chunk has not been written returning 0s");
            return;
        }
        InputStream readStream = fetchChunk(id);
        try {
            assert readStream != null;
            readStream.skip(offset);
            byte[] readBuf = new byte[(int) length];
            readStream.read(readBuf, 0, (int) length);
            buffer.put(readBuf);
        } catch (IOException e) {
            logger.error(e);
            throw new RuntimeException(e);
        }

    }

    @Override
    public void write(final ByteBuffer buffer, final long id,
            final long offset, final long start, final long length) {
        // TODO Auto-generated method stub

        logger.info("Dropbox write chunk " + id + " off " + offset + " len "
                + length);
        byte[] arr = new byte[(int) length];
        buffer.position((int) start);
        buffer.get(arr, 0, (int) length);

        ByteBuffer buff = ByteBuffer.allocate((int) Constants.CHUNK_SIZE);
        if (isExisting(id)) {
            read(buff, id, 0, Constants.CHUNK_SIZE);
        }
        buff.position((int) offset);
        buff.put(arr);

        InputStream in = new ByteArrayInputStream(buff.array());
        putData(id, in, Constants.CHUNK_SIZE);

    }

    private void putData(final long id, final InputStream in, final long length) {
        try {
            client.uploadFile("/" + id, DbxWriteMode.add(), length, in);
        } catch (DbxException e) {
            logger.error(e);
            throw new RuntimeException(e);
        } catch (IOException e) {
            logger.error(e);
            throw new RuntimeException(e);
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                logger.error(e);

            }
        }
    }

    private InputStream fetchChunk(final long id) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(
                (int) Constants.CHUNK_SIZE);

        DbxEntry.File downloadedFile;
        try {
            downloadedFile = client.getFile("/" + id, null, outputStream);
            logger.debug("Metadata: " + downloadedFile.toString());

        } catch (DbxException | IOException e) {
            logger.error(e);
            throw new RuntimeException(e);
        }
        return new ByteArrayInputStream(outputStream.toByteArray());
    }

    private boolean isExisting(final long id) {
        String name = Long.toString(id);
        DbxEntry.WithChildren mEntry;
        try {
            mEntry = client.getMetadataWithChildren("/");
            for (DbxEntry c : mEntry.children) {
                if (name.equals(c.name)) {
                    logger.info("found file " + c.name);
                    return true;
                }
            }
            logger.info("Not found file " + id);
            return false;
        } catch (DbxException e) {
            logger.error(e);
            throw new RuntimeException(e);
        }

    }
}
