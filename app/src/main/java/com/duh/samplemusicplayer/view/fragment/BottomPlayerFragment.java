package com.duh.samplemusicplayer.view.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
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

public class BottomPlayerFragment extends Fragment implements SeekBar.OnSeekBarChangeListener {
    private PlayerViewModel playerViewModel;
    private ImageButton buttonPlay;
    private ImageButton buttonNext;
    private ImageButton buttonPrevious;
    private TextView endDurationTextView;
    private TextView currentDurationTextView;
    private SeekBar seekBarDuration;
    private PlayerModel currentPlayerModel = new PlayerModel();
    private boolean isSeeking;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bottom_player, container, false);
        PlayerViewModel.Factory factory = new PlayerViewModel.Factory
                (((MusicApp) getActivity().getApplication()).serviceManager.getMusicService());
        playerViewModel = new ViewModelProvider(requireActivity(), factory).get(PlayerViewModel.class);
        buttonPlay = view.findViewById(R.id.buttonPlay);
        buttonNext = view.findViewById(R.id.buttonNext);
        buttonPrevious = view.findViewById(R.id.buttonPrevious);
        currentDurationTextView = view.findViewById(R.id.currentDurationTextView);
        endDurationTextView = view.findViewById(R.id.endDurationTextView);
        seekBarDuration = view.findViewById(R.id.seekBarSong);
        buttonPlay.setOnClickListener(this::onPlayClicked);
        buttonNext.setOnClickListener(this::onNextClicked);
        buttonPrevious.setOnClickListener(this::onPreviousClicked);
        seekBarDuration.setOnSeekBarChangeListener(this);
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
                buttonPlay.setImageDrawable(getActivity().getDrawable(R.drawable.ic_pause));
                break;
            case STOPPED:
                endDurationTextView.setText("");
                buttonPlay.setImageDrawable(getActivity().getDrawable(R.drawable.ic_play));
                break;
            case PAUSED:
                buttonPlay.setImageDrawable(getActivity().getDrawable(R.drawable.ic_play));
                break;
        }
        endDurationTextView.setText(DateUtils.formatElapsedTime(playerModel.getCurrentSong().getDuration() / 1000));
        seekBarDuration.setMax((int) playerModel.getCurrentSong().getDuration() / 1000);
        currentPlayerModel = playerModel;
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
