package com.duh.samplemusicplayer.view.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.duh.samplemusicplayer.R;
import com.duh.samplemusicplayer.app.MusicApp;
import com.duh.samplemusicplayer.model.Song;
import com.duh.samplemusicplayer.viewmodel.PlayerViewModel;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.observers.DisposableObserver;

public class BottomPlayerFragment extends Fragment {
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private PlayerViewModel playerViewModel;
    private ImageButton buttonPlay;
    private ImageButton buttonNext;
    private ImageButton buttonPrevious;
    private TextView endDurationTextView;
    private TextView currentDurationTextView;
    private ProgressBar progressBarDuration;

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
        progressBarDuration = view.findViewById(R.id.progressBarSong);
        buttonPlay.setOnClickListener(this::onPlayClicked);
        buttonNext.setOnClickListener(this::onNextClicked);
        buttonPrevious.setOnClickListener(this::onPreviousClicked);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        compositeDisposable.add(playerViewModel.getCurrentDurationObservable()
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<Long>() {
                    @Override
                    public void onNext(@io.reactivex.rxjava3.annotations.NonNull Long value) {
                        onDurationChanged(value);
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {

                    }
                }));

        compositeDisposable.add(playerViewModel.getCurrentSongObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<Song>() {
                    @Override
                    public void onNext(@io.reactivex.rxjava3.annotations.NonNull Song song) {
                        onSongChanged(song);
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {

                    }
                }));
    }

    private void onDurationChanged(long value) {
        if (value != -1) {
            currentDurationTextView.setText(DateUtils.formatElapsedTime(value / 1000));
            progressBarDuration.setProgress((int) value / 1000);

        } else {
            progressBarDuration.setProgress(0);
            currentDurationTextView.setText("00:00");
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
        if (playerViewModel.isPlaying()) {
            playerViewModel.pauseMusic();
        } else {
            playerViewModel.startMusic();
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void onSongChanged(Song song) {
        if (song != null) {
            endDurationTextView.setText(DateUtils.formatElapsedTime(song.getDuration() / 1000));
            progressBarDuration.setMax((int) song.getDuration() / 1000);
        } else {
            endDurationTextView.setText("");
        }
        if (playerViewModel.isPlaying()) {
            buttonPlay.setImageDrawable(getActivity().getDrawable(R.drawable.ic_pause));
        } else {
            buttonPlay.setImageDrawable(getActivity().getDrawable(R.drawable.ic_play));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}
