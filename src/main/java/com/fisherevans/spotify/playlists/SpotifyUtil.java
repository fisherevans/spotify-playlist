package com.fisherevans.spotify.playlists;

import com.wrapper.spotify.Api;
import com.wrapper.spotify.methods.TrackSearchRequest;
import com.wrapper.spotify.models.Page;
import com.wrapper.spotify.models.Playlist;
import com.wrapper.spotify.models.Track;

import java.util.Arrays;
import java.util.List;

public class SpotifyUtil {
    public static Api getAPI(Settings settings) {
        final Api api = Api.builder()
                .clientId(settings.clientID)
                .clientSecret(settings.clientSecret)
                .redirectURI(settings.redirectURI)
                .build();
        return api;
    }

    public static void createPlaylist(Api api, Settings settings, String playlistName, List<Song> songs) throws Exception {
        Playlist playlist = api.createPlaylist(settings.username, playlistName).build().get();
        int pass = 0, fail = 0;
        for(Song song:songs) {
            System.out.print(".");
            Track track = findTrack(api, song);
            if(track != null) {
                api.addTracksToPlaylist(settings.username, playlist.getId(), Arrays.asList(track.getUri())).build().get();
                pass++;
            } else {
                System.out.printf("\nFailed to find track for: %s - %s\n", song.title, song.artist);
                fail++;
            }
        }
        System.out.println("\nCreated playlist " + playlistName + ". Added " + pass + " songs. Failed to find " + fail + " songs.");
    }

    private static Track findTrack(Api api, Song song) throws Exception {
        Page<Track> search = api
                .searchTracks("title:" + song.title + " artist:" + song.artist)
                .limit(1)
                .build()
                .get();
        return search.getItems().isEmpty() ? null : search.getItems().get(0);
    }

    public static class Song {
        public final String title, artist;
        public Song(String title, String artist) {
            this.title = title;
            this.artist = artist;
        }
    }
}
