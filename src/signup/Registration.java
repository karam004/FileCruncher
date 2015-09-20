package signup;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Locale;

import org.apache.log4j.Logger;

import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.DbxAuthFinish;
import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxWebAuthNoRedirect;

import configutils.ConfigHelper;

public class Registration {

    private static final Logger LOG = Logger.getLogger(Registration.class);
    private final ConfigHelper configHelper;
    private final HashMap<Long, String> accountTokenMap = new HashMap<>();

    private final File accessTokenTable;

    public Registration() throws IllegalArgumentException, IOException {
        this.configHelper = ConfigHelper.createConfig();
        this.accessTokenTable = new File(configHelper.getAccesskeyFilepath());
    }

    public void dropboxAuthentication() {

        DbxAppInfo appInfo = new DbxAppInfo(configHelper.getAppKey(),
                configHelper.getAppSecret());

        DbxRequestConfig config = new DbxRequestConfig("FileCruncher/1.0",
                Locale.getDefault().toString());
        DbxWebAuthNoRedirect webAuth = new DbxWebAuthNoRedirect(config, appInfo);

        String authorizeUrl = webAuth.start();
        System.out.println("1. Go to: " + authorizeUrl);
        System.out
                .println("2. Click \"Allow\" (you might have to log in first)");
        System.out.println("3. Copy the authorization code.");
        try {
            String code = new BufferedReader(new InputStreamReader(System.in))
                    .readLine().trim();
            DbxAuthFinish authFinish = webAuth.finish(code);

            System.out.println(authFinish.accessToken);
            String accessToken = authFinish.accessToken;

            DbxClient client = new DbxClient(config, accessToken);

            Long userId = client.getAccountInfo().userId;
            accountTokenMap.put(userId, accessToken);

        } catch (DbxException e) {
            LOG.error(e);
        } catch (IOException e) {
            LOG.error(e);
        }

    }

    public void persistTokenToFile() throws IOException {
        FileOutputStream tokenFile = new FileOutputStream(accessTokenTable);
        ObjectOutputStream tokenmap = new ObjectOutputStream(tokenFile);
        tokenmap.writeObject(tokenmap);

        tokenFile.close();
        tokenmap.close();
    }
}
