package edu.byu.cs.tweeter.client.model.services.handlers;

import android.os.Message;

import java.util.List;

import edu.byu.cs.tweeter.client.model.services.PagedObserver;
import edu.byu.cs.tweeter.client.model.services.backgroundTask.GetFollowersTask;
import edu.byu.cs.tweeter.model.domain.User;

public class PagedUserHandler extends BackgroundTaskHandler<PagedObserver> {
    public PagedUserHandler(PagedObserver observer) {
        super(observer);
    }

    @Override
    protected void handleSuccessMessage(Message msg) {
        List<User> followers = (List<User>) msg.getData().getSerializable(GetFollowersTask.ITEMS_KEY);
        boolean hasMorePages = msg.getData().getBoolean(GetFollowersTask.MORE_PAGES_KEY);
        User lastFollower = (followers.size() > 0) ? followers.get(followers.size() - 1) : null;
        observer.pagedObserverSuccess(followers, hasMorePages);
    }
}
