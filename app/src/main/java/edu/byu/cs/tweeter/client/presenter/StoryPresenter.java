package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.services.StoryService;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class StoryPresenter extends PagedPresenter<Status> {


    public StoryPresenter(View view, User user) {
        super(view, user);
    }

    @Override
    protected void getData() {
        StoryService storyService = new StoryService(this);
        storyService.getStory(Cache.getInstance().getCurrUserAuthToken(), user, PAGE_SIZE, lastItem,this);
    }
    @Override
    public void handlerFail(String message) {
        view.showErrorMessage(message);
    }
}
