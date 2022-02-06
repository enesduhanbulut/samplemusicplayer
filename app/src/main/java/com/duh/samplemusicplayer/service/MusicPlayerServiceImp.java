package com.duh.samplemusicplayer.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.os.RemoteException;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.duh.samplemusicplayer.IMusicPlayerListener;
import com.duh.samplemusicplayer.IMusicPlayerServiceAIDL;
import com.duh.samplemusicplayer.R;
import com.duh.samplemusicplayer.app.MusicApp;
import com.duh.samplemusicplayer.media.AudioProvider;
import com.duh.samplemusicplayer.media.player.PlayerUtils;
import com.duh.samplemusicplayer.media.player.state.MediaPlayerEvents;
import com.duh.samplemusicplayer.media.player.state.MediaPlayerManager;
import com.duh.samplemusicplayer.model.Song;
import com.duh.samplemusicplayer.utils.Constants;

import java.util.ArrayList;

public class MusicPlayerServiceImp extends Service implements IMusicPlayerService {
    private final IMusicPlayerListener tempClientListener = new IMusicPlayerListener() {

        @Override
        public void onStarted(Bundle songBundle) throws RemoteException {
            notificationListener.onStarted(songBundle);
        }

        @Override
        public void onPaused() throws RemoteException {
            notificationListener.onPaused();
        }

        @Override
        public void onStopped() throws RemoteException {
            notificationListener.onStopped();
        }

        @Override
        public void onListChanged(Bundle listBundle) throws RemoteException {
            notificationListener.onListChanged(listBundle);
        }

        @Override
        public void onError(int errorCode, String errorMessage) throws RemoteException {
            notificationListener.onError(errorCode, errorMessage);
        }

        @Override
        public IBinder asBinder() {
            return null;
        }
    };
    private MediaPlayerManager mediaPlayerManager;
    private IMusicPlayerListener clientPlayerListener;
    private final IMusicPlayerServiceAIDL.Stub binder = new IMusicPlayerServiceAIDL.Stub() {
        @Override
        public void startMusic(Bundle bundle) throws RemoteException {
            mediaPlayerManager.handleEvent(MediaPlayerEvents.START, bundle, tempClientListener);
        }

        @Override
        public void pauseMusic() throws RemoteException {
            mediaPlayerManager.handleEvent(MediaPlayerEvents.PAUSE, new Bundle(), tempClientListener);
        }

        @Override
        public void stopMusic() throws RemoteException {
            mediaPlayerManager.handleEvent(MediaPlayerEvents.STOP, new Bundle(), tempClientListener);
        }

        @Override
        public void next() throws RemoteException {
            mediaPlayerManager.handleEvent(MediaPlayerEvents.NEXT, new Bundle(), tempClientListener);
        }

        @Override
        public void previous() throws RemoteException {
            mediaPlayerManager.handleEvent(MediaPlayerEvents.PREVIOUS, new Bundle(), tempClientListener);
        }

        @Override
        public void getSongList() throws RemoteException {
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(Constants.SONG_LIST, (ArrayList<? extends Parcelable>) mediaPlayerManager.getSongList());
            clientPlayerListener.onListChanged(bundle);
        }

        @Override
        public void setListener(IMusicPlayerListener playerListener) throws RemoteException {
            MusicPlayerServiceImp.this.clientPlayerListener = playerListener;
        }

        @Override
        public boolean isPlaying() {
            return mediaPlayerManager.isPlaying();
        }

        @Override
        public Bundle getCurrentSong() {
            Bundle bundle = new Bundle();
            bundle.putParcelable(Constants.SONG, mediaPlayerManager.getCurrentSong());
            return bundle;
        }

        @Override
        public long getCurrentDuration() {
            return mediaPlayerManager.getCurrentDuration();
        }
    };
    private Song currentSong;
    private final IMusicPlayerListener notificationListener = new IMusicPlayerListener() {

        @Override
        public void onStarted(Bundle songBundle) throws RemoteException {
            clientPlayerListener.onStarted(songBundle);
            updateNotification(true, PlayerUtils.getSongFromBundle(songBundle));
        }

        @Override
        public void onPaused() throws RemoteException {
            clientPlayerListener.onPaused();
            updateNotification(false, null);
        }

        @Override
        public void onStopped() throws RemoteException {
            clientPlayerListener.onStopped();
            updateNotification(false, null);
        }

        @Override
        public void onListChanged(Bundle listBundle) throws RemoteException {
            clientPlayerListener.onListChanged(listBundle);
        }

        @Override
        public void onError(int errorCode, String errorMessage) throws RemoteException {
            clientPlayerListener.onError(errorCode, errorMessage);
        }

        @Override
        public IBinder asBinder() {
            return null;
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        AudioProvider audioProvider = ((MusicApp) getApplication()).audioProvider;
        mediaPlayerManager = new MediaPlayerManager(this, audioProvider);
        createNotificationChannel();
        updateNotification(false, null);
    }

    private void updateNotification(boolean isPlaying, Song songFromBundle) {
        currentSong = songFromBundle != null ? songFromBundle : currentSong;
        int layout = isPlaying ? R.layout.notification_layout_playing : R.layout.notification_layout_not_playing;
        @SuppressLint("RemoteViewLayout") RemoteViews notificationLayout = new RemoteViews(getPackageName(), layout);
        if (currentSong != null) {
            notificationLayout.setTextViewText(R.id.notificationSongTextView, currentSong.getSongTitle());
            notificationLayout.setTextViewText(R.id.notificationArtistTextView, currentSong.getSongArtist());
            notificationLayout.setTextViewText(R.id.notificationAlbumTextView, currentSong.getAlbum());
        } else {
            notificationLayout.setTextViewText(R.id.notificationSongTextView, "");
            notificationLayout.setTextViewText(R.id.notificationArtistTextView, "");
            notificationLayout.setTextViewText(R.id.notificationAlbumTextView, "");
        }
        notificationLayout.setOnClickPendingIntent(R.id.notificationPlayButton, getPendingIntent(MediaPlayerEvents.START));
        notificationLayout.setOnClickPendingIntent(R.id.notificationPauseButton, getPendingIntent(MediaPlayerEvents.PAUSE));
        notificationLayout.setOnClickPendingIntent(R.id.notificationNextButton, getPendingIntent(MediaPlayerEvents.NEXT));
        notificationLayout.setOnClickPendingIntent(R.id.notificationPreviousButton, getPendingIntent(MediaPlayerEvents.PREVIOUS));

        Notification customNotification = new NotificationCompat.Builder(this, "TEST CHANNEL")
                .setSmallIcon(R.drawable.ic_play)
                .setOngoing(true)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setCustomContentView(notificationLayout)
                .setSilent(true)
                .build();
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(66, customNotification);
    }

    private PendingIntent getPendingIntent(MediaPlayerEvents mediaPlayerEvents) {
        Intent intent = new Intent(mediaPlayerEvents.name());
        intent.setClassName(getPackageName(), getClass().getName());
        return PendingIntent.getService(this, 0, intent, 0);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String eventStr = intent.getAction();
        MediaPlayerEvents event = MediaPlayerEvents.valueOf(eventStr);
        try {
            switch (event) {
                case START:
                    mediaPlayerManager.handleEvent(MediaPlayerEvents.START, new Bundle(), notificationListener);
                    break;
                case NEXT:
                    mediaPlayerManager.handleEvent(MediaPlayerEvents.NEXT, new Bundle(), notificationListener);
                    break;
                case PREVIOUS:
                    mediaPlayerManager.handleEvent(MediaPlayerEvents.PREVIOUS, new Bundle(), notificationListener);
                    break;
                case PAUSE:
                    mediaPlayerManager.handleEvent(MediaPlayerEvents.PAUSE, new Bundle(), notificationListener);
                    break;
            }

        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Music Player";
            String description = "test açıklama";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("TEST CHANNEL", name, importance);
            channel.setDescription(description);
            channel.setSound(null, null);
            channel.enableLights(false);
            channel.enableVibration(false);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaPlayerManager.release();
    }
}

