package edu.byu.cs.tweeter.client.model.services.backgroundTask;

import android.os.Bundle;
import android.os.Handler;

import java.io.IOException;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.IsFollowRequest;
import edu.byu.cs.tweeter.model.net.response.IsFollowResponse;

/**
 * Background task that determines if one user is following another.
 */
public class IsFollowTask extends AuthenticatedTask implements Runnable {
//    private final String LOG_TAG = "IsFollowerTask";
    public static final String IS_FOLLOWER_KEY = "is-follower";
    /**
     * The alleged follower.
     */
    private User follower;
    /**
     * The alleged followee.
     */
    private User followee;
    /**
     * Message handler that will receive task results.
     */

    private boolean isFollower;

    private IsFollowRequest request;
    private IsFollowResponse response;

    public IsFollowTask(AuthToken authToken, User follower, User followee, Handler messageHandler) {
        super(authToken, messageHandler);
        request = new IsFollowRequest(authToken, follower.getAlias(), followee.getAlias());
    }

    @Override
    protected void runTask() {

        try {
            response = serverFacade.isFollow(request, "/isfollow");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (TweeterRemoteException e) {
            throw new RuntimeException(e);
        }
        isFollower = response.isFollow();

        if (response.isSuccess()) {
            sendSuccessMessage();
        }

    }

    @Override
    protected void loadSuccessBundle(Bundle msgBundle) {
        msgBundle.putBoolean(IS_FOLLOWER_KEY, isFollower);
    }

}
