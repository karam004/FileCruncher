package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.apache.log4j.Logger;

public class NBDClient implements Runnable {

    private static final Logger logger = Logger.getLogger(NBDClient.class);

    private final int port;
    private final String dev;

    public NBDClient(final int port, final String dev) {
        this.port = port;
        this.dev = dev;
    }

    @Override
    public void run() {
        runNBDclient();

    }

    private void runNBDclient() {
        try {
            ProcessBuilder pb = new ProcessBuilder("nbd-client", "127.0.0.1",
                    Integer.toString(port), dev);
            StringBuilder err = new StringBuilder();

            Process startClientProcess = pb.start();
            BufferedReader stdErr = new BufferedReader(
                    new InputStreamReader(startClientProcess.getErrorStream(),
                            StandardCharsets.UTF_8));

            startClientProcess.waitFor();
            if (startClientProcess.exitValue() != 0) {
                String s;
                while ((s = stdErr.readLine()) != null) {
                    err.append(s);
                }
                stdErr.close();
                logger.error("failed to start nbd-client :" + err);
                throw new RuntimeException(err.toString());
            }
            stdErr.close();
        } catch (IOException e) {
            logger.error("Io exception on starting NBD client", e);
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            logger.error("not able to start nbd-client process", e);
            throw new RuntimeException(e);
        }

    }

}
