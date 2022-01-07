package com.duh.samplemusicplayer.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.duh.samplemusicplayer.IMusicPlayerServiceAIDL;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.PublishSubject;

public class ServiceManager {
    private final PublishSubject<Boolean> serviceBindPublisher = PublishSubject.create();
    private IMusicPlayerServiceAIDL iRemoteService;
    public final ServiceConnection musicPlayerConnector = new ServiceConnection() {
        // Called when the connection with the service is established
        public void onServiceConnected(ComponentName className, IBinder service) {
            iRemoteService = IMusicPlayerServiceAIDL.Stub.asInterface(service);
            serviceBindPublisher.onNext(true);
        }

        public void onServiceDisconnected(ComponentName className) {
            iRemoteService = null;
        }
    };

    public Observable<Boolean> bindService(Context context, String className) {
        if (className.equals(MusicPlayerServiceImp.class.getName())) {
            context.bindService(new Intent(context, MusicPlayerServiceImp.class), musicPlayerConnector, Context.BIND_AUTO_CREATE);
        }
        return serviceBindPublisher;
    }

    public IMusicPlayerServiceAIDL getMusicService() {
        return iRemoteService;
    }
}
