package com.duh.samplemusicplayer.media.player;

import android.os.Bundle;

import com.duh.samplemusicplayer.model.Song;
import com.duh.samplemusicplayer.utils.Constants;

import java.util.List;

public class PlayerUtils {
    private PlayerUtils() {
    }

    public static Bundle createSongBundle(Song song) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.SONG, song);
        return bundle;
    }


    public static Bundle createSeekBundle(int seekTo) {
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.SEEK_TO, seekTo);
        return bundle;
    }


    public static int getSeekFromBundle(Bundle bundle) {
        return bundle.getInt(Constants.SEEK_TO, -1);
    }

    public static Song getSongFromBundle(Bundle bundle) {
        return bundle.getParcelable(Constants.SONG);
    }

    public static List<Song> getSongListFromBundle(Bundle bundle) {
        return bundle.getParcelableArrayList(Constants.SONG_LIST);
    }

}
