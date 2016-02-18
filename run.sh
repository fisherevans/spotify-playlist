#!/bin/bash
mvn clean install
mvn exec:java -Dexec.mainClass="com.fisherevans.spotify.playlists.Importer"
