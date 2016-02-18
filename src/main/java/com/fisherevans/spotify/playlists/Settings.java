package com.fisherevans.spotify.playlists;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.Properties;

public class Settings {
    private static final File PROP_FILE = new File("defaults.properties");
    private static final String PROP_KEY_CLIENT_ID = "clientID",
                                PROP_KEY_CLIENT_SECRET = "clientSecret",
                                PROP_KEY_USERNAME = "username";

    public String clientID = "";
    public String clientSecret = "";
    public String username = "";

    public static final int jettyPort = 39393;
    public static final String redirectURI = "http://localhost:" + jettyPort;

    private Settings() { }

    public static Settings load() {
        Settings settings = new Settings();
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(PROP_FILE));
            settings.clientID = properties.getProperty(PROP_KEY_CLIENT_ID);
            settings.clientSecret = properties.getProperty(PROP_KEY_CLIENT_SECRET);
            settings.username = properties.getProperty(PROP_KEY_USERNAME);
        } catch (Exception e) {
            // probably no file.
        }
        settings.clientID = IOUtil.promptUser("Client ID", settings.clientID);
        settings.clientSecret = IOUtil.promptUser("Client Secret", settings.clientSecret);
        settings.username = IOUtil.promptUser("Username", settings.username);
        settings.save();
        return settings;
    }

    private void save() {
        Properties properties = new Properties();
        properties.put(PROP_KEY_CLIENT_ID, clientID);
        properties.put(PROP_KEY_CLIENT_SECRET, clientSecret);
        properties.put(PROP_KEY_USERNAME, username);
        try {
            properties.store(new FileOutputStream(PROP_FILE), "Last saved settings - " + (new Date()).toString());
        } catch (Exception e) {
            System.err.println("Failed to save defaults! " + e.getLocalizedMessage());
        }
    }
}
