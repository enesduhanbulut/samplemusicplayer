package com.duh.samplemusicplayer;

import android.os.RemoteException;

public class ServiceException extends RemoteException {
    private final int errorCode;
    private final String customMessage;

    public ServiceException(int errorCode, String message) {
        this.errorCode = errorCode;
        this.customMessage = message;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getCustomMessageMessage() {
        return customMessage;
    }
}
