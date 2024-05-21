package edu.byu.cs.tweeter.client.model.services.backgroundTask;

import android.os.Handler;

import java.io.IOException;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.response.FollowResponse;

/**
 * Background task that establishes a following relationship between two users.
 */
public class FollowTask extends AuthenticatedTask implements Runnable {



    /**
     * The user that is being followed.
     */
    private User followee;

    private FollowRequest request;
    private FollowResponse response;

    public FollowTask(AuthToken authToken, User followee, Handler messageHandler) {
        super(authToken, messageHandler);
        this.followee = followee;
        request = new FollowRequest(authToken, followee.getAlias());
    }

    @Override
    public void runTask() {

        try {
            response = serverFacade.follow(request, "/follow");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (TweeterRemoteException e) {
            throw new RuntimeException(e);
        }
        if (response.isSuccess()) {
            sendSuccessMessage();
        }
    }

}
