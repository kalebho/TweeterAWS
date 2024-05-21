package edu.byu.cs.tweeter.client.model.services.handlers;

import android.os.Message;

import edu.byu.cs.tweeter.client.model.services.MainService;

public class PostStatusHandler extends BackgroundTaskHandler<MainService.PostStatusObserver> {

    public PostStatusHandler(MainService.PostStatusObserver observer) {
        super(observer);
    }

    @Override
    protected void handleSuccessMessage(Message msg) {
        observer.PostStatusObserverSuccess();
    }
}
