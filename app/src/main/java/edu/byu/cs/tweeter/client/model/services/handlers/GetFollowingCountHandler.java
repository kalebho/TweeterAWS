package edu.byu.cs.tweeter.client.model.services.handlers;

import android.os.Message;

import edu.byu.cs.tweeter.client.model.services.MainService;
import edu.byu.cs.tweeter.client.model.services.backgroundTask.GetFollowersCountTask;

public class GetFollowingCountHandler extends BackgroundTaskHandler<MainService.FollowCountObserver>{

    public GetFollowingCountHandler(MainService.FollowCountObserver observer) {
        super(observer);
    }

    @Override
    protected void handleSuccessMessage(Message msg) {
        int count = msg.getData().getInt(GetFollowersCountTask.COUNT_KEY);
        observer.FollowingCountSuccess(count);
    }
}
