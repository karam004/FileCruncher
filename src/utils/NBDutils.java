package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;

import configutils.Constants;

public class NBDutils {

    private final static Logger logger = Logger.getLogger(NBDutils.class);
    private static final int MAX_THREADS = 1;
    private final static ExecutorService executor = Executors
            .newFixedThreadPool(MAX_THREADS);

    public static void unmountNBD() {

        Runnable nbdShutdownTask = new Runnable() {
            // Best effort cleanup
            @Override
            public void run() {
                try {
                    final ProcessBuilder pb = new ProcessBuilder("nbd-client",
                            "-d", Constants.NBD_DEVICE);
                    final StringBuilder err = new StringBuilder();

                    final Process startClientProcess = pb.start();
                    final BufferedReader stdErr = new BufferedReader(
                            new InputStreamReader(
                                    startClientProcess.getErrorStream(),
                                    StandardCharsets.UTF_8));
                    final BufferedReader stdOut = new BufferedReader(
                            new InputStreamReader(
                                    startClientProcess.getInputStream(),
                                    StandardCharsets.UTF_8));

                    startClientProcess.waitFor();
                    if (startClientProcess.exitValue() != 0) {
                        String s;
                        while ((s = stdErr.readLine()) != null) {
                            err.append(s);
                        }
                        while ((s = stdOut.readLine()) != null) {
                            err.append(s);
                        }

                        stdErr.close();
                        stdOut.close();
                        logger.error(err);
                        throw new RuntimeException();
                    }

                    stdErr.close();
                    stdOut.close();
                } catch (final IOException e) {
                    logger.error(e);
                    throw new RuntimeException(e);
                } catch (final InterruptedException e) {
                    logger.error(e);
                    throw new RuntimeException(e);
                }
            }
        };

        Future<?> nbdShutdownTaskTaskfuture = executor.submit(nbdShutdownTask);

        try {
            nbdShutdownTaskTaskfuture.get(1000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            logger.error(e);
        }
    }
}
