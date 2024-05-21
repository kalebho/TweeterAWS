package edu.byu.cs.tweeter.client.presenter;

import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.services.MainService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class MainPresenter extends BasePresenter implements MainService.FollowCountObserver, MainService.IsFollowerObserver, MainService.FollowActionObserver, MainService.LogoutObserver, MainService.PostStatusObserver{

    public MainPresenter(View view) {
        super(view);
    }

    public void updateSelectedUserFollowingAndFollowers(AuthToken authToken, User selectedUser) {
        MainService mainService = new MainService();
        //Go to getFollowers Handler
        mainService.getFollowerCount(authToken, selectedUser, this);
        //Go to getFollowing Handler
        mainService.getFollowingCount(authToken, selectedUser, this);
    }

    //FollowCount implemented methods
    @Override
    public void FollowingCountSuccess(int followingCount) {
        view.setFollowingCount(followingCount);
    }

    @Override
    public void FollowerCountSuccess(int followerCount) {
        view.setFollowersCount(followerCount);
    }


    public void isFollower(AuthToken authToken, User currUser, User selectedUser) {
        MainService mainService = new MainService();
        mainService.isFollower(authToken, currUser, selectedUser, this);
    }

    @Override
    public void IsFollowerObserverSuccess(boolean isFollower) {
        view.setFollowButton(isFollower);
    }

    public void unFollow(AuthToken authToken, User selectedUser) {
        MainService mainService = new MainService();
        mainService.unfollow(authToken, selectedUser, this);
        view.showInfoMessage("Removing " + selectedUser.getName() + "...");
    }

    @Override
    public void FollowActionObserverSuccess(boolean performed) {
        view.showInfoMessage("Successfully updated user");
        view.updateFollowButton(performed);

    }


    public void follow(AuthToken authToken, User selectedUser) {
        MainService mainService = new MainService();
        mainService.follow(authToken, selectedUser, this);
        view.showInfoMessage("Adding " + selectedUser.getName() + "...");
    }

    public void logout(AuthToken authToken) {
        MainService mainService = new MainService();
        mainService.logout(authToken, this);
        view.showLogoutToast("Logging Out...");
    }

    @Override
    public void LogoutObserverSuccess() {
        view.hideInfoMessage();
        logoutUser();
        view.openLoginView();

    }

    private void logoutUser() {
        //Clear user data (cached data).
        Cache.getInstance().clearCache();
    }

    public void postStatus(AuthToken authToken, String post, User currUser, long timestamp) {
        Status newStatus = createStatus(post, currUser, timestamp);
        MainService mainService = factoryMethod();
        mainService.postStatus(authToken, newStatus, this);
        view.showPostingToast("Posting Status...");
    }

    private Status createStatus(String post, User currUser, long timestamp) {
        List<String> urls = parseURLs(post);
        List<String> mentions = parseMentions(post);
        Status newStatus = new Status(post, currUser, timestamp, urls, mentions);
        return newStatus;
    }

    @Override
    public void PostStatusObserverSuccess() {
        view.hideInfoMessage();
        view.showInfoMessage("Successfully Posted!");
    }


    private List<String> parseURLs(String post) {
        List<String> containedUrls = new ArrayList<>();
        for (String word : post.split("\\s")) {
            if (word.startsWith("http://") || word.startsWith("https://")) {

                int index = findUrlEndIndex(word);

                word = word.substring(0, index);

                containedUrls.add(word);
            }
        }

        return containedUrls;
    }

    private List<String> parseMentions(String post) {
        List<String> containedMentions = new ArrayList<>();

        for (String word : post.split("\\s")) {
            if (word.startsWith("@")) {
                word = word.replaceAll("[^a-zA-Z0-9]", "");
                word = "@".concat(word);

                containedMentions.add(word);
            }
        }

        return containedMentions;
    }

    private int findUrlEndIndex(String word) {
        if (word.contains(".com")) {
            int index = word.indexOf(".com");
            index += 4;
            return index;
        } else if (word.contains(".org")) {
            int index = word.indexOf(".org");
            index += 4;
            return index;
        } else if (word.contains(".edu")) {
            int index = word.indexOf(".edu");
            index += 4;
            return index;
        } else if (word.contains(".net")) {
            int index = word.indexOf(".net");
            index += 4;
            return index;
        } else if (word.contains(".mil")) {
            int index = word.indexOf(".mil");
            index += 4;
            return index;
        } else {
            return word.length();
        }
    }

    public MainService factoryMethod() {
        return new MainService();
    }

}
