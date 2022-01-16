package com.duh.samplemusicplayer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.duh.samplemusicplayer.app.MusicApp;
import com.duh.samplemusicplayer.service.MusicPlayerServiceImp;
import com.duh.samplemusicplayer.viewmodel.SongListViewModel;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.observers.DisposableObserver;

public class MainActivity extends AppCompatActivity {
    private SongListViewModel songListViewModel;
    private ImageButton buttonPlay;
    private ImageButton buttonNext;
    private ImageButton buttonPrevious;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPermissions();
    }

    private void init() {
        setContentView(R.layout.fragment_main);
        SongListViewModel.Factory factory = new SongListViewModel.Factory
                (((MusicApp) getApplication()).serviceManager.getMusicService());
        songListViewModel = new ViewModelProvider(this, factory).get(SongListViewModel.class);
        initViews();
        listenMusicList();
    }

    private void listenMusicList() {
        songListViewModel.getSongSelectPublisher()
                .subscribeWith(new DisposableObserver<Boolean>() {
                    @SuppressLint("UseCompatLoadingForDrawables")
                    @Override
                    public void onNext(@NonNull Boolean aBoolean) {
                        buttonPlay.setImageDrawable(getDrawable(R.drawable.ic_pause));
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void initViews() {
        buttonPlay = findViewById(R.id.buttonPlay);
        buttonNext = findViewById(R.id.buttonNext);
        buttonPrevious = findViewById(R.id.buttonPrevious);
        buttonPlay.setOnClickListener(this::onPlayClicked);
        buttonNext.setOnClickListener(this::onNextClicked);
        buttonPrevious.setOnClickListener(this::onPreviousClicked);

    }

    private void onNextClicked(View view) {
        songListViewModel.startNextMusic();
    }

    private void onPreviousClicked(View view) {
        songListViewModel.startPreviousMusic();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void onPlayClicked(View view) {
        if (songListViewModel.isPlaying()) {
            songListViewModel.pauseMusic();
            buttonPlay.setImageDrawable(getDrawable(R.drawable.ic_play));
        } else {
            songListViewModel.startMusic();
            buttonPlay.setImageDrawable(getDrawable(R.drawable.ic_pause));
        }
    }


    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 101);
        } else {
            onRequestPermissionsResult(101, new String[]{""}, new int[]{0});
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 101:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onPermissionGranted();
                } else {
                    // TODO: 14.01.2022 implement here
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void onPermissionGranted() {
        ((MusicApp) getApplication()).serviceManager.bindService(getApplicationContext(), MusicPlayerServiceImp.class.getName())
                .subscribeWith(new DisposableObserver<Boolean>() {
                    @Override
                    public void onNext(@NonNull Boolean aBoolean) {
                        init();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ((MusicApp) getApplication()).serviceManager.unbind(getApplicationContext());
    }

}