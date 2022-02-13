package com.duh.samplemusicplayer.media.player.state;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;

import com.duh.samplemusicplayer.IMusicPlayerListener;
import com.duh.samplemusicplayer.media.AudioProvider;
import com.duh.samplemusicplayer.media.player.PlayerUtils;
import com.duh.samplemusicplayer.model.MediaPlayerQueueModel;
import com.duh.samplemusicplayer.model.Song;
import com.duh.samplemusicplayer.utils.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.observers.DisposableObserver;

public class MediaPlayerManager {
    private final IStateChanger stateChanger;
    private final List<Song> songList = new ArrayList<>();
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final AudioProvider audioProvider;
    private Song currentSong;
    private IdleState idleState = null;
    private InitializedState initializedState;
    private PreparedState preparedState;
    private StartedState startedState;
    private PausedState pausedState;
    private StoppedState stoppedState;
    private CompletedState completedState;
    private ErrorState errorState;
    private MediaPlayer mediaPlayer;
    private IMediaPlayerState currentState;
    private Queue<MediaPlayerQueueModel> queue;
    private AtomicBoolean isInProgress = new AtomicBoolean(false);
    public MediaPlayerManager(Context context, AudioProvider audioProvider) {
        this.audioProvider = audioProvider;
        this.stateChanger = this::onStateChange;
        initStates(context);
        observeSongList();
        currentState = idleState;
        queue = new ConcurrentLinkedQueue<>();
    }

    private void initStates(Context context) {
        this.idleState = new IdleState(context, initializedMediaPlayer -> {
            this.mediaPlayer = initializedMediaPlayer;
            this.mediaPlayer.setOnErrorListener(this::onError);
        });
        this.initializedState = new InitializedState(preparedMediaPlayer -> {
            this.mediaPlayer = preparedMediaPlayer;
            this.mediaPlayer.setOnErrorListener(this::onError);
        });
        this.preparedState = new PreparedState();
        this.startedState = new StartedState();
        this.pausedState = new PausedState();
        this.stoppedState = new StoppedState();
        this.completedState = new CompletedState();
        this.errorState = new ErrorState();
    }

    public void handleEvent(boolean isFromService, MediaPlayerEvents event, Bundle bundle, IMusicPlayerListener playerListener) throws RemoteException {
        queue.add(new MediaPlayerQueueModel(event, bundle, playerListener));
        if (!isInProgress.get() || isFromService) {
            isInProgress.set(true);
            MediaPlayerQueueModel queueModel = queue.poll();
            switch (queueModel.getEvent()) {
                case START:
                    if (currentSong == null && PlayerUtils.getSongFromBundle(queueModel.getBundle()) == null) {
                        queueModel.setBundle(getPreviousSongBundle());
                    }
                    break;
                case NEXT:
                    queueModel.setBundle(getNextSongBundle());
                    break;
                case PREVIOUS:
                    queueModel.setBundle(getPreviousSongBundle());
                    break;
                default:
                    break;
            }
            currentState.handle(queueModel.getEvent(), queueModel.getBundle(), mediaPlayer, stateChanger, new IMusicPlayerListener() {
                @Override
                public void onStarted(Bundle songBundle) throws RemoteException {
                    queueModel.getPlayerListener().onStarted(songBundle);
                    isInProgress.set(false);
                    handleNextEvent();
                }

                @Override
                public void onPaused() throws RemoteException {
                    queueModel.getPlayerListener().onPaused();
                    isInProgress.set(false);
                    handleNextEvent();
                }

                @Override
                public void onStopped() throws RemoteException {
                    queueModel.getPlayerListener().onStopped();
                    isInProgress.set(false);
                    handleNextEvent();
                }

                @Override
                public void onListChanged(Bundle listBundle) throws RemoteException {
                    queueModel.getPlayerListener().onListChanged(listBundle);
                    isInProgress.set(false);
                    handleNextEvent();
                }

                @Override
                public void onError(int errorCode, String errorMessage) throws RemoteException {
                    queueModel.getPlayerListener().onError(errorCode, errorMessage);
                    isInProgress.set(false);
                    handleNextEvent();
                }

                @Override
                public IBinder asBinder() {
                    return null;
                }
            });

        }
    }

    public void handleEvent(MediaPlayerEvents event, Bundle bundle, IMusicPlayerListener playerListener) throws RemoteException {
        handleEvent(false, event, bundle, playerListener);
    }

    private void handleNextEvent() throws RemoteException {
        MediaPlayerQueueModel queueModel = queue.poll();
        if (queueModel != null) {
            handleEvent(queueModel.getEvent(), queueModel.getBundle(), queueModel.getPlayerListener());
        }
    }

    private void onStateChange(MediaPlayerEvents event, Bundle bundle, MediaPlayerStates mediaPlayerState, IMusicPlayerListener playerListener) throws RemoteException {
        switch (mediaPlayerState) {
            case IDLE:
                this.currentState = idleState;
                break;
            case INITIALIZED:
                this.currentState = initializedState;
                break;
            case PAUSED:
                this.currentState = pausedState;
                break;
            case STARTED:
                this.currentState = startedState;
                break;
            case STOPPED:
                this.currentState = stoppedState;
                break;
            case PREPARED:
                this.currentState = preparedState;
                break;
            case COMPLETED:
                this.currentState = completedState;
                break;
            default:
                return;
        }
        handleEvent(true, event, bundle, playerListener);
        currentSong = startedState.getCurrentSong();
    }

    public long getCurrentDuration() {
        if (currentState instanceof StartedState || currentState instanceof PausedState) {
            return mediaPlayer.getCurrentPosition();
        } else {
            return -1;
        }
    }

    public List<Song> getSongList() {
        return this.songList;
    }

    private Bundle getNextSongBundle() {
        Bundle bundle = new Bundle();
        int nextSongIndex = songList.size() - 1;
        if (currentSong != null) {
            int currIndex = songList.indexOf(currentSong);
            if (currIndex + 1 < songList.size()) {
                nextSongIndex = currIndex + 1;
            }
        }
        bundle.putParcelable(Constants.SONG, songList.get(nextSongIndex));
        return bundle;
    }

    private Bundle getPreviousSongBundle() {
        Bundle bundle = new Bundle();
        int previousSongIndex = 0;
        if (currentSong != null) {
            int currIndex = songList.indexOf(currentSong);
            if (currIndex - 1 > -1) {
                previousSongIndex = currIndex - 1;
            }
        }
        bundle.putParcelable(Constants.SONG, songList.get(previousSongIndex));
        return bundle;
    }

    public Song getCurrentSong() {
        return currentSong;
    }

    private void observeSongList() {
        compositeDisposable.add(audioProvider.getSongObservable()
                .subscribeWith(new DisposableObserver<Song>() {
                    @Override
                    public void onNext(@NonNull Song song) {
                        songList.add(song);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {

                    }
                }));
        audioProvider.getSongs();
    }

    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        return false;
    }

    public void release() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        compositeDisposable.clear();
    }

    public boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }

    public interface IMediaPlayerListener {
        void onChanged(MediaPlayer mediaPlayer);
    }
}
