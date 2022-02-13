package com.duh.samplemusicplayer.view;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.duh.samplemusicplayer.app.MusicApp;
import com.duh.samplemusicplayer.service.MusicPlayerServiceImp;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.observers.DisposableObserver;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPermissions();
    }

    private void init() {
        setContentView(R.layout.fragment_main);
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
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ((MusicApp) getApplication()).serviceManager.unbind(getApplicationContext());
    }

}