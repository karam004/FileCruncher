package signup;

import java.awt.*;
import java.io.*;
import java.net.URI;
import java.util.HashMap;
import java.util.Locale;

import com.google.api.services.drive.model.File;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import org.apache.log4j.Logger;

import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.DbxAuthFinish;
import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxWebAuthNoRedirect;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;

import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.*;
import com.google.api.services.drive.Drive;

import java.util.Arrays;
import java.util.List;

import configutils.ConfigHelper;

public class Registration {

    private static final Logger LOG = Logger.getLogger(Registration.class);
    private static  ConfigHelper configHelper;
    private final HashMap<Long, String> accountTokenMap = new HashMap<>();


    private static final String APPLICATION_NAME = "File Cruncher";
    private static final java.io.File DATA_STORE_DIR = new
            java.io.File(System.getProperty("user.home"), ".credentials/FileCruncher");

    private static FileDataStoreFactory DATA_STORE_FACTORY;

    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    private static HttpTransport HTTP_TRANSPORT;

    private static final List<String> SCOPES = Arrays.asList(DriveScopes.DRIVE_METADATA_READONLY);

    static {
        try {
            configHelper = ConfigHelper.createConfig();
        } catch (IOException e)
        {
            LOG.error(e);
            throw new RuntimeException(e);
        }
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }

   /* public Registration() throws IllegalArgumentException, IOException {
        //this.configHelper = ConfigHelper.createConfig();
    }*/

    public static Credential authorize() throws IOException {
        InputStream in =
                Registration.class.getResourceAsStream("client_secret.json");
        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                        .setDataStoreFactory(DATA_STORE_FACTORY)
                        .setAccessType("offline")
                        .build();
        Credential credential = new AuthorizationCodeInstalledApp(flow,
                new LocalServerReceiver()).authorize("user");
        System.out.println("Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
        return credential;
    }


    public static Drive getDriveService() throws IOException {
        Credential credential = authorize();
        return new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public static void printAbout(Drive service) {
        try {
            About about = service.about().get().execute();

            System.out.println("Current user name: " + about.getName());
            System.out.println("Root folder ID: " + about.getRootFolderId());
            System.out.println("Total quota (bytes): " + about.getQuotaBytesTotal());
            System.out.println("Used quota (bytes): " + about.getQuotaBytesUsed());
            Long freeSpace = about.getQuotaBytesTotal() - about.getQuotaBytesUsed();
            ConfigHelper.createConfig().addToAvailableSpace(freeSpace);
        } catch (IOException e) {
            System.out.println("An error occurred: " + e);
        }

    }

    public static void testDrive() throws IOException {
        Drive service = getDriveService();

        FileList result = service.files().list()
                .setMaxResults(10)
                .execute();
        List<com.google.api.services.drive.model.File> files = result.getItems();
        if (files == null || files.size() == 0) {
            System.out.println("No files found.");
        } else {
            System.out.println("Files:");
            for (com.google.api.services.drive.model.File file : files) {
                System.out.printf("%s (%s)\n", file.getTitle(), file.getId());
            }
        }
        printAbout(service);
    }

    public static void dropboxAuthentication() {

        DbxAppInfo appInfo = new DbxAppInfo(configHelper.getAppKey(),
                configHelper.getAppSecret());

        DbxRequestConfig config = new DbxRequestConfig("FileCruncher/1.0",
                Locale.getDefault().toString());
        DbxWebAuthNoRedirect webAuth = new DbxWebAuthNoRedirect(config, appInfo);

        String authorizeUrl = webAuth.start();
        System.out.println("1. Go to: " + authorizeUrl);
        if(Desktop.isDesktopSupported())
        {
            try
            {
                Desktop.getDesktop().browse(new URI(authorizeUrl));
            } catch( Exception e)
            {
                LOG.error(e);
                throw new RuntimeException(e);
            }
        }

        System.out.println("Copy the authorization code.");
        try {
            String code = new BufferedReader(new InputStreamReader(System.in))
                    .readLine().trim();
            DbxAuthFinish authFinish = webAuth.finish(code);

            System.out.println(authFinish.accessToken);
            String accessToken = authFinish.accessToken;

            DbxClient client = new DbxClient(config, accessToken);

            Long userId = client.getAccountInfo().userId;
            persistTokenToFile(accessToken);
           // accountTokenMap.put(userId, accessToken);

        } catch (DbxException e) {
            LOG.error(e);
        } catch (IOException e) {
            LOG.error(e);
        }

    }

     public static void persistTokenToFile(String accessToken){
        String path = DATA_STORE_FACTORY.getDataDirectory().getAbsolutePath();
        path = path+"/"+"DropBoxCode";
        try {

            FileOutputStream tokenFile = new FileOutputStream(path);
            tokenFile.write(accessToken.getBytes());
            tokenFile.close();
            LOG.debug("AccessCode for DropBox written on disk");
        } catch (IOException e)
        {
            LOG.error(e);
        }
    }
}
