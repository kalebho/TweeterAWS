package edu.byu.cs.tweeter.client.model.services.handlers;

import android.os.Message;

import edu.byu.cs.tweeter.client.model.services.MainService;

public class LogoutHandler extends BackgroundTaskHandler<MainService.LogoutObserver> {

    public LogoutHandler(MainService.LogoutObserver observer) {
        super(observer);
    }

    @Override
    protected void handleSuccessMessage(Message msg) {
        observer.LogoutObserverSuccess();
    }
}
