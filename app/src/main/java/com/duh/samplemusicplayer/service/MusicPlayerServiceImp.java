package com.duh.samplemusicplayer.service;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;

import androidx.annotation.Nullable;

import com.duh.samplemusicplayer.IMusicPlayerListener;
import com.duh.samplemusicplayer.IMusicPlayerServiceAIDL;

public class MusicPlayerServiceImp extends Service implements IMusicPlayerService{
    private IMusicPlayerServiceAIDL.Stub binder = new IMusicPlayerServiceAIDL.Stub() {
        @Override
        public void startMusic(Bundle bundle, IMusicPlayerListener playerListener) throws RemoteException {
        }

        @Override
        public void pauseMusic(IMusicPlayerListener playerListener) throws RemoteException {

        }

        @Override
        public void stopMusic(IMusicPlayerListener playerListener) throws RemoteException {

        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: 8.01.2022 implement this actions
        return binder;
    }


}

