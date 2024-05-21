package edu.byu.cs.tweeter.client.model.services;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.services.backgroundTask.BackgroundTaskUtils;
import edu.byu.cs.tweeter.client.model.services.backgroundTask.GetFollowersTask;
import edu.byu.cs.tweeter.client.model.services.backgroundTask.GetFollowingTask;
import edu.byu.cs.tweeter.client.model.services.handlers.PagedUserHandler;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

//Used to do UI logic that gets followers and following
public class FollowService extends PagedUserHandler {

    private BackgroundTaskUtils taskUtils = new BackgroundTaskUtils();

    public FollowService(PagedObserver observer) {
        super(observer);
    }

    public void getFollowing(AuthToken authToken, User user, int pageSize, User lastFollowee, PagedObserver observer) {
        GetFollowingTask getFollowingTask = new GetFollowingTask(Cache.getInstance().getCurrUserAuthToken(), user, pageSize, lastFollowee, new PagedUserHandler(observer));
        taskUtils.runTask(getFollowingTask);
    }


    public void getFollowers(AuthToken authToken, User user, int pageSize, User lastFollower, PagedObserver observer) {
        GetFollowersTask getFollowersTask = new GetFollowersTask(authToken, user, pageSize, lastFollower, new PagedUserHandler(observer));
        taskUtils.runTask(getFollowersTask);
    }

}
