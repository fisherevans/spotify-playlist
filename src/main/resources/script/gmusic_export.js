// Generates playlists from Google Music pages
// add this as a book mark, and run it to download the playlist file
javascript:
    if(window.madebig == "true") {
        var download = function (name, content) {
            var element = document.createElement('a');
            element.setAttribute('href', 'data:text/plain;charset=utf-8,' + encodeURIComponent(content));
            element.setAttribute('download', name);
            element.style.display = 'none';
            document.body.appendChild(element);
            element.click();
            document.body.removeChild(element);
        };
        var name = document.querySelector(".playlist-view .info .title").textContent;
        if(name == undefined || name == "") {
            alert("You need to be on a playlist page")
        } else {
            var rows = document.querySelectorAll("table.song-table tr.song-row");
            var m3u = "# Playlist: " + name + "\n";
            for (var i = 0; i < rows.length; i++) {
                var row = rows[i];
                var title = row.querySelector("td[data-col=title] .column-content").textContent;
                var artist = row.querySelector("td[data-col=artist] .column-content").textContent;
                var entry = "~" + title + "~,~" + artist + "~\n";
                m3u += entry;
            }
            download(name + ".txt", m3u);
        }
        document.querySelector("body.material").style.removeProperty("height");
        delete window.madebig;
    } else {
        document.querySelector("body.material").style.height = "99999px";
        window.madebig = "true";
        alert("Prep done. Run it again to download the current playlist")
    }
