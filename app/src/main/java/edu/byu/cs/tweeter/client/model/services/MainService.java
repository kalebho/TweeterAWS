package edu.byu.cs.tweeter.client.model.services;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.services.backgroundTask.BackgroundTaskUtils;
import edu.byu.cs.tweeter.client.model.services.backgroundTask.FollowTask;
import edu.byu.cs.tweeter.client.model.services.backgroundTask.GetFollowersCountTask;
import edu.byu.cs.tweeter.client.model.services.backgroundTask.GetFollowingCountTask;
import edu.byu.cs.tweeter.client.model.services.backgroundTask.IsFollowTask;
import edu.byu.cs.tweeter.client.model.services.backgroundTask.LogoutTask;
import edu.byu.cs.tweeter.client.model.services.backgroundTask.PostStatusTask;
import edu.byu.cs.tweeter.client.model.services.backgroundTask.UnfollowTask;
import edu.byu.cs.tweeter.client.model.services.handlers.FollowHandler;
import edu.byu.cs.tweeter.client.model.services.handlers.GetFollowerCountHandler;
import edu.byu.cs.tweeter.client.model.services.handlers.GetFollowingCountHandler;
import edu.byu.cs.tweeter.client.model.services.handlers.IsFollowerHandler;
import edu.byu.cs.tweeter.client.model.services.handlers.LogoutHandler;
import edu.byu.cs.tweeter.client.model.services.handlers.PostStatusHandler;
import edu.byu.cs.tweeter.client.model.services.handlers.UnfollowHandler;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class MainService {

    private BackgroundTaskUtils taskUtils = new BackgroundTaskUtils();

    public interface FollowCountObserver extends ServiceObserver {
        void FollowingCountSuccess(int followingCount);
        void FollowerCountSuccess(int followerCount);
    }


    // Get count of users Followers or Followees
    public void getFollowerCount(AuthToken authToken, User selectedUser, FollowCountObserver observer) {
        GetFollowersCountTask followersCountTask = new GetFollowersCountTask(authToken, selectedUser, new GetFollowerCountHandler(observer));
        taskUtils.runTask(followersCountTask);
    }

    public void getFollowingCount(AuthToken authToken, User selectedUser, FollowCountObserver observer) {
        GetFollowingCountTask followingCountTask = new GetFollowingCountTask(Cache.getInstance().getCurrUserAuthToken(), selectedUser, new GetFollowingCountHandler(observer));
        taskUtils.runTask(followingCountTask);
    }



    public interface IsFollowerObserver extends ServiceObserver {
        void IsFollowerObserverSuccess(boolean isFollower);
    }

    public void isFollower(AuthToken authToken, User currUser, User selectedUser, IsFollowerObserver observer) {
        IsFollowTask isFollowerTask = new IsFollowTask(authToken, currUser, selectedUser, new IsFollowerHandler(observer));
        taskUtils.runTask(isFollowerTask);
    }


    public interface FollowActionObserver extends ServiceObserver {
        void FollowActionObserverSuccess(boolean performed);
    }

    public void unfollow(AuthToken authToken, User selectedUser, FollowActionObserver observer) {
        UnfollowTask unfollowTask = new UnfollowTask(authToken, selectedUser, new UnfollowHandler(observer));
        taskUtils.runTask(unfollowTask);
    }


    public void follow(AuthToken authToken, User selectedUser, FollowActionObserver observer) {
        FollowTask followTask = new FollowTask(authToken, selectedUser, new FollowHandler(observer));
        taskUtils.runTask(followTask);
    }


    public interface LogoutObserver extends ServiceObserver{
        void LogoutObserverSuccess();
    }

    public void logout(AuthToken authToken, LogoutObserver observer) {
        LogoutTask logoutTask = new LogoutTask(authToken, new LogoutHandler(observer));
        taskUtils.runTask(logoutTask);
    }


    public interface PostStatusObserver extends ServiceObserver{
        void PostStatusObserverSuccess();
    }

    public void postStatus(AuthToken authToken, Status newStatus, PostStatusObserver observer) {
        PostStatusTask statusTask = new PostStatusTask(authToken, newStatus, new PostStatusHandler(observer));
        taskUtils.runTask(statusTask);
    }

}
