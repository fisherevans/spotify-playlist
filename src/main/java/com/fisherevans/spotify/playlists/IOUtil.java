package com.fisherevans.spotify.playlists;

import com.fisherevans.spotify.playlists.SpotifyUtil.Song;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IOUtil {
    private static final Scanner scanner = new Scanner(System.in);

    public static String promptUser(String prompt, String defaultValue) {
        synchronized(scanner) {
            if(defaultValue == null || defaultValue.length() == 0) {
                System.out.print(prompt + ": ");
            } else {
                System.out.print(prompt + " (" + defaultValue + "): ");
            }
            String line = scanner.nextLine();
            line = line.replaceAll("[\n\r]", "").trim();
            return line.length() > 0 ? line : defaultValue;
        }
    }

    public static List<Song> readPlaylistFile(File file) throws Exception {
        Scanner in = new Scanner(file);
        String line;
        Pattern pattern = Pattern.compile("~([^~]*)~,~([^~]*)~");
        List<Song> songs = new ArrayList();
        while(in.hasNextLine()) {
            line = in.nextLine().trim();
            if(line.startsWith("#"))
                continue;
            Matcher matcher = pattern.matcher(line);
            if(matcher.find()) {
                String[] split = new String[2];
                split[0] = matcher.group(1).replaceAll("\\(.*\\)", "").replaceAll("feat\\..*", "");
                split[1] = matcher.group(2).replaceAll("\\(.*\\)", "").replaceAll("feat\\..*", "");
                songs.add(new Song(split[0], split[1]));
            } else {
                throw new RuntimeException("Failed to parse line: " + line);
            }
        }
        in.close();
        return songs;
    }
}
