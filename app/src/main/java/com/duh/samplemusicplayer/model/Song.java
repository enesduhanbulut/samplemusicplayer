package com.duh.samplemusicplayer.model;

import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Song implements Parcelable {

    public static final Creator<Song> CREATOR = new Creator<Song>() {


        @SuppressWarnings({
                "unchecked"
        })
        public Song createFromParcel(android.os.Parcel in) {
            return new Song(in);
        }

        public Song[] newArray(int size) {
            return (new Song[size]);
        }

    };
    @SerializedName("songId")
    @Expose
    private long songId;
    @SerializedName("songTitle")
    @Expose
    private String songTitle;
    @SerializedName("songArtist")
    @Expose
    private String songArtist;
    @SerializedName("path")
    @Expose
    private String path;
    @SerializedName("genre")
    @Expose
    private String genre;
    @SerializedName("duration")
    @Expose
    private long duration;
    @SerializedName("album")
    @Expose
    private String album;

    protected Song(android.os.Parcel in) {
        this.songId = ((long) in.readValue((long.class.getClassLoader())));
        this.songTitle = ((String) in.readValue((String.class.getClassLoader())));
        this.songArtist = ((String) in.readValue((String.class.getClassLoader())));
        this.path = ((String) in.readValue((String.class.getClassLoader())));
        this.genre = ((String) in.readValue((String.class.getClassLoader())));
        this.duration = ((long) in.readValue((long.class.getClassLoader())));
        this.album = ((String) in.readValue((String.class.getClassLoader())));
    }

    private Song(long songId, String songTitle, String songArtist, String path, String genre, long duration, String album) {
        this.songId = songId;
        this.songTitle = songTitle;
        this.songArtist = songArtist;
        this.path = path;
        this.genre = genre;
        this.duration = duration;
        this.album = album;
    }

    private Song() {
    }

    public long getSongId() {
        return songId;
    }

    public void setSongId(long songId) {
        this.songId = songId;
    }

    public String getSongTitle() {
        return songTitle;
    }

    public void setSongTitle(String songTitle) {
        this.songTitle = songTitle;
    }

    public String getSongArtist() {
        return songArtist;
    }

    public void setSongArtist(String songArtist) {
        this.songArtist = songArtist;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    @Override
    public String toString() {
        String nullValue = "<null>";
        StringBuilder sb = new StringBuilder();
        sb.append(Song.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("songId");
        sb.append('=');
        sb.append(this.songId);
        sb.append(',');
        sb.append("songTitle");
        sb.append('=');
        sb.append(((this.songTitle == null) ? nullValue : this.songTitle));
        sb.append(',');
        sb.append("songArtist");
        sb.append('=');
        sb.append(((this.songArtist == null) ? nullValue : this.songArtist));
        sb.append(',');
        sb.append("path");
        sb.append('=');
        sb.append(((this.path == null) ? nullValue : this.path));
        sb.append(',');
        sb.append("genre");
        sb.append('=');
        sb.append(((this.genre == null) ? nullValue : this.genre));
        sb.append(',');
        sb.append("duration");
        sb.append('=');
        sb.append(this.duration);
        sb.append(',');
        sb.append("album");
        sb.append('=');
        sb.append(((this.album == null) ? nullValue : this.album));
        sb.append(',');
        if (sb.charAt((sb.length() - 1)) == ',') {
            sb.setCharAt((sb.length() - 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = ((result * 31) + ((int) (this.duration ^ (this.duration >>> 32))));
        result = ((result * 31) + ((this.songTitle == null) ? 0 : this.songTitle.hashCode()));
        result = ((result * 31) + ((this.path == null) ? 0 : this.path.hashCode()));
        result = ((result * 31) + ((this.album == null) ? 0 : this.album.hashCode()));
        result = ((result * 31) + ((this.songArtist == null) ? 0 : this.songArtist.hashCode()));
        result = ((result * 31) + ((this.genre == null) ? 0 : this.genre.hashCode()));
        result = ((result * 31) + ((int) (this.songId ^ (this.songId >>> 32))));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Song)) {
            return false;
        }
        Song rhs = ((Song) other);
        return (((((((this.duration == rhs.duration) && ((this.songTitle == rhs.songTitle) || ((this.songTitle != null) && this.songTitle.equals(rhs.songTitle)))) && ((this.path == rhs.path) || ((this.path != null) && this.path.equals(rhs.path)))) && ((this.album == rhs.album) || ((this.album != null) && this.album.equals(rhs.album)))) && ((this.songArtist == rhs.songArtist) || ((this.songArtist != null) && this.songArtist.equals(rhs.songArtist)))) && ((this.genre == rhs.genre) || ((this.genre != null) && this.genre.equals(rhs.genre)))) && (this.songId == rhs.songId));
    }

    public void writeToParcel(android.os.Parcel dest, int flags) {
        dest.writeValue(songId);
        dest.writeValue(songTitle);
        dest.writeValue(songArtist);
        dest.writeValue(path);
        dest.writeValue(genre);
        dest.writeValue(duration);
        dest.writeValue(album);
    }

    public int describeContents() {
        return 0;
    }

    public static class Builder {
        private long songId;
        private String songTitle;
        private String songArtist;
        private String path;
        private String genre;
        private long duration;
        private String album;

        public Builder setSongId(long songId) {
            this.songId = songId;
            return this;
        }

        public Builder setSongTitle(String songTitle) {
            this.songTitle = songTitle;
            return this;
        }

        public Builder setSongArtist(String songArtist) {
            this.songArtist = songArtist;
            return this;
        }

        public Builder setPath(String path) {
            this.path = path;
            return this;
        }

        public Builder setGenre(String genre) {
            this.genre = genre;
            return this;
        }

        public Builder setDuration(long duration) {
            this.duration = duration;
            return this;
        }

        public Builder setAlbum(String album) {
            this.album = album;
            return this;
        }

        public Song createSong() {
            return new Song(songId, songTitle, songArtist, path, genre, duration, album);
        }
    }

}
