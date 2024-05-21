package edu.byu.cs.tweeter.client.model.services;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.services.backgroundTask.BackgroundTaskUtils;
import edu.byu.cs.tweeter.client.model.services.backgroundTask.GetStoryTask;
import edu.byu.cs.tweeter.client.model.services.handlers.PagedStatusHandler;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class StoryService extends PagedStatusHandler {

    private BackgroundTaskUtils taskUtils = new BackgroundTaskUtils();

    public StoryService(PagedObserver observer) {
        super(observer);
    }


    public void getStory(AuthToken authToken, User user, int pageSize, Status lastStatus, PagedObserver observer) {
        GetStoryTask getStoryTask = new GetStoryTask(Cache.getInstance().getCurrUserAuthToken(), user, pageSize, lastStatus, new PagedStatusHandler(observer));
        taskUtils.runTask(getStoryTask);
    }

}
