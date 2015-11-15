package fileSystem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.apache.log4j.Logger;

import configutils.Constants;

public class MountExt4 {

    private final static Logger logger = Logger.getLogger(MountExt4.class);

    public static void makeExt4() {
        makeDirectory(Constants.MOUNT_PATH);

        try {
            final ProcessBuilder pb = new ProcessBuilder("mkfs", "-t",
                    Constants.EXT4, Constants.NBD_DEVICE);
            final StringBuilder err = new StringBuilder();

            final Process startClientProcess = pb.start();
            final BufferedReader stdErr = new BufferedReader(
                    new InputStreamReader(startClientProcess.getErrorStream(),
                            StandardCharsets.UTF_8));
            final BufferedReader stdOut = new BufferedReader(
                    new InputStreamReader(startClientProcess.getInputStream(),
                            StandardCharsets.UTF_8));

            startClientProcess.waitFor();
            errorHandler(startClientProcess,stdErr,stdOut,err);

        } catch (final IOException e) {
            throw new RuntimeException(e);
        } catch (final InterruptedException e) {
            throw new RuntimeException(e);
        }

        mountExt4();
    }

    public static void mountExt4() {
        makeDirectory(Constants.MOUNT_PATH);

        try {
            final ProcessBuilder pb = new ProcessBuilder("mount",
                    Constants.NBD_DEVICE, Constants.MOUNT_PATH);
            final StringBuilder err = new StringBuilder();

            final Process startClientProcess = pb.start();
            final BufferedReader stdErr = new BufferedReader(
                    new InputStreamReader(startClientProcess.getErrorStream(),
                            StandardCharsets.UTF_8));
            final BufferedReader stdOut = new BufferedReader(
                    new InputStreamReader(startClientProcess.getInputStream(),
                            StandardCharsets.UTF_8));

            startClientProcess.waitFor();
            errorHandler(startClientProcess,stdErr,stdOut,err);

        } catch (final IOException e) {
            throw new RuntimeException(e);
        } catch (final InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void makeDirectory(final String path) {
        try {
            final ProcessBuilder pb = new ProcessBuilder("mkdir", "-p", path);
            final StringBuilder err = new StringBuilder();

            final Process startClientProcess = pb.start();
            final BufferedReader stdErr = new BufferedReader(
                    new InputStreamReader(startClientProcess.getErrorStream(),
                            StandardCharsets.UTF_8));
            final BufferedReader stdOut = new BufferedReader(
                    new InputStreamReader(startClientProcess.getInputStream(),
                            StandardCharsets.UTF_8));

            startClientProcess.waitFor();
            errorHandler(startClientProcess,stdErr,stdOut,err);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        } catch (final InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static void errorHandler(Process startClientProcess,BufferedReader stdErr,
                                     BufferedReader stdOut,StringBuilder err ) {
        try {
            if (startClientProcess.exitValue() != 0) {
                String s;
                while ((s = stdErr.readLine()) != null) {
                    err.append(s);
                }
                stdErr.close();
                stdOut.close();

                logger.error("failed to make filesystem \n" + err);
                throw new RuntimeException();
            } else {
                String s = "";
                while ((s = stdErr.readLine()) != null) {
                    err.append(s);
                }
                while ((s = stdOut.readLine()) != null) {
                    err.append(s);
                }
                stdErr.close();
                stdOut.close();
                if (err.length() != 0) {

                    logger.error("failed to make filesystem \n" + err);
                    throw new RuntimeException();
                }
            }

        } catch (final IOException e) {
            throw new RuntimeException(e);
        }

    }

}
