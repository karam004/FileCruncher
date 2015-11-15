package service;

import java.io.IOException;

import signup.Registration;
import channel.Clients;
import channel.DriveClient;

import com.google.api.services.drive.Drive;

public class FileChruncherService {

    public static void main(final String[] args) {
        try {
            Drive drive = Registration.getDriveService();
            DriveClient driveClient = new DriveClient(drive);
            Clients.addClient(driveClient);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
