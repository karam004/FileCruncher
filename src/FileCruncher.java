/**
 * Created by abulla on 19/9/15.
 */
import java.util.Locale;

import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxEntry;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;

public class FileCruncher {
    public static void main(final String[] args) {
        final String APP_KEY = "fagw6bv9tiwtz1x";
        final String APP_SECRET = "9gdd7axf8jdbuhm";
        final String APP_TOKEN = "sEg5tPt40asAAAAAAAAJ_ChD09Cjj10LVuJJGR54_KQ8brS9-7_uBuI73j2CJ-Rc";

        DbxAppInfo appInfo = new DbxAppInfo(APP_KEY, APP_SECRET);

        DbxRequestConfig config = new DbxRequestConfig("FileCruncher/1.0",
                Locale.getDefault().toString());
        // DbxWebAuthNoRedirect webAuth = new DbxWebAuthNoRedirect(config,
        // appInfo);

        // String authorizeUrl = webAuth.start();/*
        // System.out.println("1. Go to: " + authorizeUrl);
        // System.out.println("2. Click \"Allow\" (you might have to log in first)");
        // System.out.println("3. Copy the authorization code.");*/
        try {
            // String code = new BufferedReader(new
            // InputStreamReader(System.in)).readLine().trim();
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
