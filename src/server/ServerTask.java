package server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import utils.NetUtils;
import channel.DataChannel;

public class ServerTask implements Runnable {
    private static Logger logger = Logger.getLogger(ServerTask.class);
    private static final int CLIENT_MAGIC_NO = 0x25609513;
    private static final int SERVER_MAGIC_NO = 0x67446698;
    private static final long HANDSHAKE_MAGIC_NO = 72578530415187L;
    private static final int NO_ZEROS = 128;
    private static final int WRITE_BUFF_SIZE = 4 * 1024;
    private static final int GIB_TO_BYTES = 1024 * 1024 * 1024;
    private static final int REQUEST_LENGTH = 28;

    private static final int READ = 0;
    private static final int WRITE = 1;
    private static final int CLOSE = 2;

    private final ServerSocket serverSocket;
    private Socket clientSocket;

    private final DataChannel dataChannel;

    public ServerTask(final ServerSocket serverSocket,
            final DataChannel dataChannel) {
        this.serverSocket = serverSocket;
        this.dataChannel = dataChannel;
    }

    @Override
    public void run() {

        try {
            clientSocket = this.serverSocket.accept();
        } catch (IOException e) {
            logger.error(e);
            throw new RuntimeException(e);
        }
        closeServerSocket();

        try {
            serveClient();
        } catch (IOException e) {
            logger.error(e);
            throw new RuntimeException(e);
        }

    }

    private void serveClient() throws IOException {
        OutputStream out = clientSocket.getOutputStream();
        InputStream in = clientSocket.getInputStream();

        // perform handshake first
        handshake(out);

        while (true) {
            NbdClientRequestHeader header = getHeader(in);
            if (header.requestType == READ) {
                handleRead(header, out);
            } else if (header.requestType == CLOSE) {
                logger.info("closing nbd-server");
                out.close();
                in.close();
                break;
            } else if (header.requestType == WRITE) {
                handleWrite(header, in, out);
            } else {
                logger.error("Nbd server only servers READ(0) request, client requested : "
                        + header.requestType);
                throw new RuntimeException(
                        "Nbd server only servers READ(0) request, client requested : "
                                + header.requestType);
            }
        }
    }

    private void handleRead(final NbdClientRequestHeader header,
            final OutputStream outputStream) throws IOException {
        if (header.magic != CLIENT_MAGIC_NO) {
            logger.error("NBD mafic number mismatch\n");
            logger.error("expected " + CLIENT_MAGIC_NO + " got " + header.magic);
            throw new RuntimeException();
        }

        DataOutputStream out = new DataOutputStream(outputStream);

        int flag = 0;

        ByteBuffer data = ByteBuffer.allocate(header.length);
        this.dataChannel.read(data, header.offset, header.length);

        try {
            out.writeInt(SERVER_MAGIC_NO);
            out.writeInt(flag);
            out.writeLong(header.handle);
            out.write(data.array());
            out.flush();
        } catch (IOException e) {
            logger.error("IO exception when sending data to client ", e);
            throw e;
        }
    }

    private void handleWrite(final NbdClientRequestHeader header,
            final InputStream in, final OutputStream outputStream)
            throws IOException {

        DataOutputStream out = new DataOutputStream(outputStream);
        byte[] buff = new byte[WRITE_BUFF_SIZE];
        int flag = 0;
        int total = 0;
        int recvd = 0;
        try {
            while (total < header.length) {
                recvd = in.read(buff);
                total += recvd;
            }
            // send Acknowledgment
            out.writeInt(SERVER_MAGIC_NO);
            out.writeInt(flag);
            out.writeLong(header.handle);
            out.flush();
        } catch (IOException e) {
            logger.error("IO exception when getting write request ", e);
            throw e;
        }
    }

    private void handshake(final OutputStream out) throws IOException {
        DataOutputStream sock = new DataOutputStream(out);
        byte[] magic = ("NBDMAGIC").getBytes("UTF-8");
        long num = HANDSHAKE_MAGIC_NO;
        long size = dataChannel.getDeviceSize();
        byte[] flag = ByteBuffer.allocate(NO_ZEROS).put((byte) 0).array();

        sock.write(magic);
        sock.writeLong(num);
        sock.writeLong(size);
        sock.write(flag);

        sock.flush();
    }

    private NbdClientRequestHeader getHeader(final InputStream in)
            throws IOException {
        NbdClientRequestHeader header = new NbdClientRequestHeader();
        try {
            byte[] headerbuf = new byte[REQUEST_LENGTH];
            int bytesrecieved = NetUtils.receiveAll(in, headerbuf);
            if (bytesrecieved == REQUEST_LENGTH) {
                ByteBuffer headerdata = ByteBuffer.wrap(headerbuf);
                // Order is important --NBD protocol
                header.magic = headerdata.getInt();
                header.requestType = headerdata.getInt();
                header.handle = headerdata.getLong();
                header.offset = headerdata.getLong();
                header.length = headerdata.getInt();

                if (header.magic != CLIENT_MAGIC_NO) {
                    logger.error("NBD protocol error expected : "
                            + CLIENT_MAGIC_NO + " got " + header.magic);
                    logger.info("request type " + header.requestType);
                    logger.info("offset  " + header.offset);
                    logger.info("rlength " + header.length);
                    throw new RuntimeException("NBD protocol error expected : "
                            + CLIENT_MAGIC_NO + " got " + header.magic);
                }
            } else {
                logger.error("not able to read full header data from socket");
                logger.error("expected header length " + REQUEST_LENGTH
                        + " got " + bytesrecieved);

                throw new RuntimeException(
                        "not able to read full header data from socket, expected "
                                + REQUEST_LENGTH + " got " + bytesrecieved
                                + " bytes");
            }

        } catch (IOException e) {
            logger.error("IO exception when recirving header \n", e);
            throw (e);
        }

        return header;
    }

    public void closeServerSocket() {
        try {
            logger.info("closing serversocket");
            this.serverSocket.close();
        } catch (IOException e) {
            logger.error("IO exception on server socket close ", e);
        }
    }

    public static class NbdClientRequestHeader {
        private int requestType;
        private int magic;
        private long handle;
        private long offset;
        private int length;

        @Override
        public String toString() {
            return String.format("OFF:%-12d LEN:%-12d", offset, length);
        }
    }
}
