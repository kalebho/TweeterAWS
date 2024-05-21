package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.services.UserService;
import edu.byu.cs.tweeter.client.model.services.FeedService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class FeedPresenter extends PagedPresenter<Status>{

    public FeedPresenter(View view, User user) {
        super(view, user);
    }

    @Override
    protected void getData() {
        FeedService feedService = new FeedService(this);
        feedService.getFeed(Cache.getInstance().getCurrUserAuthToken(), user, PAGE_SIZE, lastItem, this);
    }

    @Override
    public void handlerFail(String message) {
        view.showErrorMessage(message);
    }

}
