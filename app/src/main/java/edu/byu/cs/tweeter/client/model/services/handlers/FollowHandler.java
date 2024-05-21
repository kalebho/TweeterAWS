package edu.byu.cs.tweeter.client.model.services.handlers;

import android.os.Message;

import edu.byu.cs.tweeter.client.model.services.MainService;

public class FollowHandler extends BackgroundTaskHandler<MainService.FollowActionObserver> {

    public FollowHandler(MainService.FollowActionObserver observer) {
        super(observer);
    }

    @Override
    protected void handleSuccessMessage(Message msg) {
        observer.FollowActionObserverSuccess(false);
    }
}
