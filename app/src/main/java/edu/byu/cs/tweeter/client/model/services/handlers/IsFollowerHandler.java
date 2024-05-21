package edu.byu.cs.tweeter.client.model.services.handlers;

import android.os.Message;

import edu.byu.cs.tweeter.client.model.services.MainService;
import edu.byu.cs.tweeter.client.model.services.backgroundTask.IsFollowTask;

public class IsFollowerHandler extends BackgroundTaskHandler<MainService.IsFollowerObserver> {

    public IsFollowerHandler(MainService.IsFollowerObserver observer) {
        super(observer);
    }

    @Override
    protected void handleSuccessMessage(Message msg) {
        boolean isFollower = msg.getData().getBoolean(IsFollowTask.IS_FOLLOWER_KEY);
        observer.IsFollowerObserverSuccess(isFollower);
    }
}
