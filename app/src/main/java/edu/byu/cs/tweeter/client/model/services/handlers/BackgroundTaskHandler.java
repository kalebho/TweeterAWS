package edu.byu.cs.tweeter.client.model.services.handlers;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import edu.byu.cs.tweeter.client.model.services.ServiceObserver;
import edu.byu.cs.tweeter.client.model.services.backgroundTask.Task;

public abstract class BackgroundTaskHandler<T extends ServiceObserver> extends Handler {


    protected T observer;

    public BackgroundTaskHandler(T observer) {
        super(Looper.getMainLooper());
        this.observer = observer;
    }


    public void handleMessage(@NonNull Message msg) {
        boolean success = msg.getData().getBoolean(Task.SUCCESS_KEY);
        if (success) {
            handleSuccessMessage(msg);
        } else if (msg.getData().containsKey(Task.MESSAGE_KEY)) {
            String message = msg.getData().getString(Task.MESSAGE_KEY);
            observer.handlerFail(message);
        } else if (msg.getData().containsKey(Task.EXCEPTION_KEY)) {
            Exception ex = (Exception) msg.getData().getSerializable(Task.EXCEPTION_KEY);
            observer.handlerFail("Failed to login because of exception: " + ex.getMessage());
        }
    }

    protected abstract void handleSuccessMessage(Message msg);



}
