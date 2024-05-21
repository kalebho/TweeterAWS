package edu.byu.cs.tweeter.client.model.services.handlers;

import android.os.Message;

import edu.byu.cs.tweeter.client.model.services.MainService;

public class UnfollowHandler extends BackgroundTaskHandler<MainService.FollowActionObserver> {

    public UnfollowHandler(MainService.FollowActionObserver observer) {
        super(observer);
    }

    @Override
    protected void handleSuccessMessage(Message msg) {
        observer.FollowActionObserverSuccess(true);
    }
}
