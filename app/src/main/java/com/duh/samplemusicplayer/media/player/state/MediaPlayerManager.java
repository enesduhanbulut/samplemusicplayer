package com.duh.samplemusicplayer.media.player.state;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;

public class MediaPlayerManager {
    private final IStateChanger stateChanger;
    private final IdleState idleState;
    private final InitializedState initializedState;
    private final PreparedState preparedState;
    private final StartedState startedState;
    private final PausedState pausedState;
    private final StoppedState stoppedState;
    private final CompletedState completedState;
    private final ErrorState errorState;
    private MediaPlayer mediaPlayer;
    private IMediaPlayerState currentState;

    public MediaPlayerManager(Context context) {
        this.stateChanger = this::onStateChange;
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

        currentState = idleState;
    }

    public void handleEvent(MediaPlayerEvents event, Bundle bundle) {
        currentState.handle(event, bundle, mediaPlayer, stateChanger);
    }

    private void onStateChange(MediaPlayerEvents event, Bundle bundle, MediaPlayerStates mediaPlayerState) {
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
        handleEvent(event, bundle);
    }

    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        return false;
    }

    public void release() {
        mediaPlayer.release();
        mediaPlayer = null;
    }

    public interface IMediaPlayerListener {
        void onChanged(MediaPlayer mediaPlayer);
    }
}
