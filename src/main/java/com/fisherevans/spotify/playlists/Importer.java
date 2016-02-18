package com.fisherevans.spotify.playlists;

import com.fisherevans.spotify.playlists.SpotifyUtil.Song;
import com.wrapper.spotify.Api;
import com.wrapper.spotify.models.AuthorizationCodeCredentials;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.StdErrLog;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class Importer {
    private static final File IMPORTS_DIR = new File("imports");

    public Settings settings;
    public Api api;

    public Importer() {
        settings = Settings.load();
        api = SpotifyUtil.getAPI(settings);
    }

    public void start() {
        AuthListener.listen(this);
        String authURL = api.createAuthorizeURL(
                Arrays.asList("playlist-modify-public", "playlist-modify-private"),
                "state");
        System.out.println("Please authorize here: " + authURL);
        System.out.println("Waiting for response from Spotify...");
    }

    public void doImport(String authCode) {
        try {
            AuthorizationCodeCredentials creds = api.authorizationCodeGrant(authCode).build().get();
            api.setAccessToken(creds.getAccessToken());
            api.setRefreshToken(creds.getRefreshToken());
        } catch (Exception e) {
            System.err.println("Failed to authorize you! " + e.getLocalizedMessage());
            System.exit(1);
        }
        for(File file:IMPORTS_DIR.listFiles()) {
            if(file.isFile())
                importFile(file);
        }
    }

    private void importFile(File file) {
        try {
            System.out.println("\nImporting: " + file.getCanonicalPath());
            System.out.println("Enter nothing to skip this file.");
            String playlistName = IOUtil.promptUser("Playlist Name", "");
            if (playlistName.length() == 0) {
                System.out.println("Skipping file...");
            } else {
                List<Song> songs = IOUtil.readPlaylistFile(file);
                SpotifyUtil.createPlaylist(api, settings, playlistName, songs);
            }
        } catch (Exception e) {
            System.err.println("Failed to import playlist! " + e.getLocalizedMessage());
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        ((StdErrLog)Log.getRootLogger()).setLevel(StdErrLog.LEVEL_WARN);
        System.out.println("Create a Spotify app here: https://developer.spotify.com/my-applications/#!/applications");
        System.out.println("Make sure to add the redirectURL: " + Settings.redirectURI);
        System.out.println("Add the Chrome bookmark found here: src/resources/script/gmusic_export.js");
        System.out.println("Use the bookmark to download playlists, save them to the imports folder in this project");
        System.out.println("");
        new Importer().start();
    }
}
