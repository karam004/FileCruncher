/**
 * Created by abulla on 19/9/15.
 */
import java.io.IOException;
import java.util.Locale;

import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxEntry;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxWebAuthNoRedirect;

import configutils.ConfigHelper;
import configutils.Constants;

public class FileCruncher {
    public static void main(final String[] args) {
        String APP_KEY = null;

        String APP_SECRET = null;
        try {
            APP_KEY = ConfigHelper.createConfig().getAppKey();
            APP_SECRET = ConfigHelper.createConfig().getAppSecret();
        } catch (IllegalArgumentException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        final String APP_TOKEN = "zPUEEYD26GsAAAAAAAAMQP3SZLabjQ4A4gmLQ2a0ays";

        DbxAppInfo appInfo = new DbxAppInfo(APP_KEY, APP_SECRET);

        DbxRequestConfig config = new DbxRequestConfig(Constants.APP_NAME,
                Locale.getDefault().toString());
        DbxWebAuthNoRedirect webAuth = new DbxWebAuthNoRedirect(config, appInfo);

        // String authorizeUrl = webAuth.start();
        // System.out.println("1. Go to: " + authorizeUrl);
        // System.out
        // .println("2. Click \"Allow\" (you might have to log in first)");
        // System.out.println("3. Copy the authorization code.");
        try {
            // String code = new BufferedReader(new
            // InputStreamReader(System.in))
            // .readLine().trim();
            // DbxAuthFinish authFinish = webAuth.finish(code);
            // System.out.println(authFinish.accessToken);
            // String accessToken = authFinish.accessToken;
            DbxClient client = new DbxClient(config, APP_TOKEN);
            System.out.println("Linked account: "
                    + client.getAccountInfo().userId);

            DbxEntry.WithChildren mEntry = client.getMetadataWithChildren("/");
            for (DbxEntry c : mEntry.children) {
                System.out.println(c.name);
            }

        } catch (DbxException e) {
            e.printStackTrace();
        }

    }
}
