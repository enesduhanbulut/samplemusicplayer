package com.duh.samplemusicplayer.model;

public class Song {
    private String songName;
    private String singerName;
    private String albumCoverPath;

    public Song(String songName, String singerName, String albumCoverPath) {
        this.songName = songName;
        this.singerName = singerName;
        this.albumCoverPath = albumCoverPath;
    }

    public String getSongName() {
        return songName;
    }

    public String getSingerName() {
        return singerName;
    }

    public String getAlbumCoverPath() {
        return albumCoverPath;
    }
}
