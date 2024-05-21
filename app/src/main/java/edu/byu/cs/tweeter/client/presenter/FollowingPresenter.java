package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.services.FollowService;
import edu.byu.cs.tweeter.client.model.services.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowingPresenter extends PagedPresenter<User>{


    public FollowingPresenter(View view, User user) {
        super(view, user);
    }

    @Override
    protected void getData() {
        FollowService followService = new FollowService(this);
        followService.getFollowing(Cache.getInstance().getCurrUserAuthToken(), user, PAGE_SIZE, lastItem, this);
    }

    @Override
    public void handlerFail(String message) {
        isLoading = false;
        view.endLoadingItems();
        view.showErrorMessage(message);
    }


}
