package service;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;

import org.apache.log4j.Logger;

import server.NBDClient;
import server.ServerTask;
import signup.Registration;
import utils.NBDutils;
import channel.Clients;
import channel.DataChannel;
import channel.DataChannelImpli;
import channel.DriveClient;

import com.google.api.services.drive.Drive;

import configutils.ConfigHelper;
import configutils.Constants;

public class FileChruncherService {

    private static final Logger logger = Logger
            .getLogger(FileChruncherService.class);

    private final static int wildcardPort = 0;
    private final static int backlog = 10;
    private final static int sleeptime = 10;

    public static void main(final String[] args) {

        if (!checkregistration()) {
            logger.error("User not register for atleast one service");
            throw new RuntimeException(
                    "User not register for atleast one service");
        }

        if (!isRootUser()) {
            logger.error("Not running as root user");
            throw new RuntimeException("application needs root privilage");
        }

        try {
            Drive drive = Registration.getDriveService();
            DriveClient driveClient = new DriveClient(drive);
            Clients.addClient(driveClient);

            int port = startNBDserver();

            startNBDclient(port);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private static int startNBDserver() throws IOException {
        ServerSocket serverSocket = openServerSocket();
        int port = serverSocket.getLocalPort();

        DataChannel dataChannel = new DataChannelImpli();

        ServerTask serverTask = new ServerTask(serverSocket, dataChannel);
        Thread serverThread = new Thread(serverTask);

        serverThread.start();

        try {
            Thread.sleep(sleeptime);
            while (!serverThread.getState().equals(Thread.State.RUNNABLE)) {
                Thread.sleep(sleeptime);
            }

        } catch (InterruptedException e) {
            logger.error("Thread intrupption when stating nbd server thread  ",
                    e);
            throw new RuntimeException(e);
        }

        return port;
    }

    private static void startNBDclient(final int port) {
        NBDClient nbdClient = new NBDClient(port, Constants.NBD_DEVICE);
        Thread clientthread = new Thread(nbdClient);

        clientthread.start();
        try {
            Thread.sleep(sleeptime);
            while (!clientthread.getState().equals(Thread.State.RUNNABLE)) {
                Thread.sleep(sleeptime);
            }

        } catch (InterruptedException e) {
            logger.error("Thread intrupption when stating nbd client thread  ",
                    e);
            throw new RuntimeException(e);
        }
    }

    private static ServerSocket openServerSocket() throws IOException {
        try {
            ServerSocket serverSocket = new ServerSocket(wildcardPort, backlog,
                    InetAddress.getByName("127.0.0.1"));
            return serverSocket;
        } catch (IOException e) {
            logger.error("IO exception on socket open ", e);
            throw e;
        }
    }

    private static boolean checkregistration() {

        try {
            if (ConfigHelper.createConfig().getTotalMem() != 0) {
                return true;
            }
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    private static boolean isRootUser() {
        String user = System.getProperty("user.name");
        if ("root".equals(user)) {
            return true;
        }
        return false;
    }

    static void addShutDownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

            @Override
            public void run() {
                NBDutils.unmountNBD();
            }
        }));
    }
}
