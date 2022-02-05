package com.duh.samplemusicplayer.view.fragment;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.duh.samplemusicplayer.R;
import com.duh.samplemusicplayer.app.MusicApp;
import com.duh.samplemusicplayer.media.player.PlayerModel;
import com.duh.samplemusicplayer.media.player.state.MediaPlayerStates;
import com.duh.samplemusicplayer.viewmodel.PlayerViewModel;
import com.google.android.material.imageview.ShapeableImageView;

public class BottomPlayerFragment extends Fragment implements SeekBar.OnSeekBarChangeListener {
    private PlayerViewModel playerViewModel;
    private ImageButton playButton;
    private ImageButton nextButton;
    private ImageButton buttonPrevious;
    private TextView endDurationTextView;
    private TextView currentDurationTextView;
    private SeekBar seekBarDuration;
    private ShapeableImageView imageViewAlbumCover;
    private PlayerModel currentPlayerModel = new PlayerModel();
    private boolean isSeeking;
    private TextView songTextView;
    private TextView artistTextView;
    private TextView albumTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bottom_player, container, false);
        PlayerViewModel.Factory factory = new PlayerViewModel.Factory
                (((MusicApp) getActivity().getApplication()).serviceManager.getMusicService());
        playerViewModel = new ViewModelProvider(requireActivity(), factory).get(PlayerViewModel.class);
        playButton = view.findViewById(R.id.bottomFragmentPlayButton);
        nextButton = view.findViewById(R.id.bottomFragmentNextButton);
        buttonPrevious = view.findViewById(R.id.buttomFragmentPreviousButton);
        currentDurationTextView = view.findViewById(R.id.buttonFragmentCurrDurationTextView);
        endDurationTextView = view.findViewById(R.id.buttomFragmentEndDurationTextView);
        seekBarDuration = view.findViewById(R.id.buttomFragmentSeekBar);
        imageViewAlbumCover = view.findViewById(R.id.bottomPlayerAlbumCoverImageView);
        songTextView = view.findViewById(R.id.bottomPlayerSongTextView);
        artistTextView = view.findViewById(R.id.bottomPlayerArtistTextView);
        albumTextView = view.findViewById(R.id.bottomPlayerAlbumTextView);

        playButton.setOnClickListener(this::onPlayClicked);
        nextButton.setOnClickListener(this::onNextClicked);
        buttonPrevious.setOnClickListener(this::onPreviousClicked);
        seekBarDuration.setOnSeekBarChangeListener(this);
        makeTextViewMarquee(songTextView);
        makeTextViewMarquee(artistTextView);
        makeTextViewMarquee(albumTextView);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        playerViewModel.getPlayerLiveData().observe(getViewLifecycleOwner(), this::onPlayerEvent);
        playerViewModel.getDurationLiveData().observe(getViewLifecycleOwner(), this::onDurationChanged);
    }

    private void onDurationChanged(long value) {
        if (!isSeeking) {
            if (value != -1) {
                currentDurationTextView.setText(DateUtils.formatElapsedTime(value / 1000));
                seekBarDuration.setProgress((int) value / 1000);

            } else {
                seekBarDuration.setProgress(0);
                currentDurationTextView.setText("00:00");
            }
        }
    }

    private void onNextClicked(View view) {
        playerViewModel.startNextMusic();
    }

    private void onPreviousClicked(View view) {
        playerViewModel.startPreviousMusic();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void onPlayClicked(View view) {
        if (currentPlayerModel.getCurrentState() == MediaPlayerStates.STARTED) {
            playerViewModel.pauseMusic();
        } else {
            playerViewModel.startMusic();
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void onPlayerEvent(PlayerModel playerModel) {
        switch (playerModel.getCurrentState()) {
            case STARTED:
            case IDLE:
                playButton.setImageDrawable(getActivity().getDrawable(R.drawable.ic_pause));
                break;
            case STOPPED:
                endDurationTextView.setText("");
                playButton.setImageDrawable(getActivity().getDrawable(R.drawable.ic_play));
                break;
            case PAUSED:
                playButton.setImageDrawable(getActivity().getDrawable(R.drawable.ic_play));
                break;
        }
        endDurationTextView.setText(DateUtils.formatElapsedTime(playerModel.getCurrentSong().getDuration() / 1000));
        seekBarDuration.setMax((int) playerModel.getCurrentSong().getDuration() / 1000);
        Bitmap albumCover = playerViewModel.getAlbumCover(playerModel.getCurrentSong().getPath());
        if (albumCover == null) {
            albumCover = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.sample_album_cover);
        }
        imageViewAlbumCover.setImageBitmap(albumCover);
        songTextView.setText(playerModel.getCurrentSong().getSongTitle());
        artistTextView.setText(playerModel.getCurrentSong().getSongArtist());
        albumTextView.setText(playerModel.getCurrentSong().getSongArtist());
        currentPlayerModel = playerModel;
    }

    private void makeTextViewMarquee(TextView textView) {
        textView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        textView.setSelected(true);
        textView.setSingleLine(true);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int value, boolean fromUser) {
        if (fromUser) {
            isSeeking = true;
            currentDurationTextView.setText(DateUtils.formatElapsedTime(value));
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (isSeeking) {
            playerViewModel.startMusic(seekBar.getProgress());

            isSeeking = false;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
