package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.services.ServiceObserver;

public class BasePresenter implements ServiceObserver {

    protected View view;

    public BasePresenter(View view) {
        this.view = view;
    }

    @Override
    public void handlerFail(String message) {
        view.showErrorMessage(message);
    }

    public interface View {
        void showInfoMessage(String message);
        void hideInfoMessage();
        void showErrorMessage(String message);
        void setFollowersCount(int followersCount);
        void setFollowingCount(int followingCount);
        void setFollowButton(boolean isFollower);
        void updateFollowButton(boolean follow);
        void showLogoutToast(String message);
        void showPostingToast(String message);
        void openLoginView();
    }


}
