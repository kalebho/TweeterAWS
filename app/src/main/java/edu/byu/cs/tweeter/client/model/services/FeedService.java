package edu.byu.cs.tweeter.client.model.services;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.services.backgroundTask.BackgroundTaskUtils;
import edu.byu.cs.tweeter.client.model.services.backgroundTask.GetFeedTask;
import edu.byu.cs.tweeter.client.model.services.handlers.PagedStatusHandler;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class FeedService extends PagedStatusHandler {

    private BackgroundTaskUtils taskUtils = new BackgroundTaskUtils();

    public FeedService(PagedObserver observer) {
        super(observer);
    }


    public void getFeed(AuthToken authToken, User user, int pageSize, Status lastStatus, PagedObserver observer) {
        GetFeedTask getFeedTask = new GetFeedTask(Cache.getInstance().getCurrUserAuthToken(), user, pageSize, lastStatus, new PagedStatusHandler(observer));
        taskUtils.runTask(getFeedTask);
    }

}
