package com.duh.samplemusicplayer.media;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.provider.MediaStore;

import com.duh.samplemusicplayer.model.Song;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.PublishSubject;

public class AudioProvider {
    private final PublishSubject<Song> songPublisher = PublishSubject.create();
    private Context context;

    public AudioProvider(Context context) {
        this.context = context;
    }

    public Observable<Song> getSongObservable() {
        return songPublisher;
    }

    public void getSongs() {
        String selection = MediaStore.Audio.AudioColumns.IS_MUSIC + "!= 0";
        String[] projection = new String[]{
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.SIZE
        };
        @SuppressLint("Recycle")
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, selection, null,
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER);

        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToNext();
            @SuppressLint("Range") Song music = new Song.Builder()
                    .setPath(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)))
                    .setSongId(cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID)))
                    .setAlbum(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)))
                    .setSongArtist(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)))
                    .setDuration(cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)))
                    .createSong();
            songPublisher.onNext(music);
        }
    }

    public Bitmap getAlbumCover(String path) {
        try (MediaMetadataRetriever mmr = new MediaMetadataRetriever()) {
            mmr.setDataSource(path);
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            return BitmapFactory.decodeFile(path, bmOptions);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void release() {
        context = null;
    }
}
