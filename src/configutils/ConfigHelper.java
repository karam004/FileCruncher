package configutils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

public class ConfigHelper {
    private static final Logger logger = Logger.getLogger(ConfigHelper.class);
    private Properties sampleConfig;
    private String appKey;
    private String appSecret;
    private String accesskeyFile;

    private ConfigHelper(final File propertiesFile) throws IOException {
        loadProperties(propertiesFile);
    }

    public static ConfigHelper createConfig() throws IOException,
            IllegalArgumentException {

        ConfigHelper configHelper = null;

        try {
            final String currentdir = System
                    .getProperty(Constants.ACCESS_PROPERTIES_RELATIVE_PATH);

            final File accessProperties = new File(currentdir,
                    Constants.ACCESS_PROPERTIES_FILENAME);

            configHelper = new ConfigHelper(accessProperties);
        } catch (final Exception e) {
            logger.error("config file not founs", e);
            throw new FileNotFoundException(
                    "Cannot find FILECRUCHER_CONFIG file, Exiting!!!");
        }
        return configHelper;
    }

    private void loadProperties(final File propertiesFile) throws IOException {

        final FileInputStream inputStream = new FileInputStream(propertiesFile);
        try {
            sampleConfig = new Properties();
            sampleConfig.load(inputStream);

            this.appKey = sampleConfig.getProperty(ConfigKeys.APP_KEY);

            this.appSecret = sampleConfig.getProperty(ConfigKeys.APP_SECRET);

            this.accesskeyFile = sampleConfig
                    .getProperty(ConfigKeys.ACCESSKEY_FILE);
        } finally {
            inputStream.close();
        }

    }

    public String getValueFromConfig(final String key) {
        return sampleConfig.getProperty(key);
    }

    public String getAppKey() {
        return this.appKey;
    }

    public String getAppSecret() {
        return this.appSecret;
    }

    public String getAccesskeyFilepath() {
        return this.accesskeyFile;
    }
}
