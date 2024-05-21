package edu.byu.cs.tweeter.client.model.services.backgroundTask;

import android.os.Handler;

import java.io.IOException;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.UnfollowRequest;
import edu.byu.cs.tweeter.model.net.response.UnfollowResponse;

/**
 * Background task that removes a following relationship between two users.
 */
public class UnfollowTask extends AuthenticatedTask implements Runnable {
    /**
     * The user that is being followed.
     */
    private User followee;


    private UnfollowRequest request;
    private UnfollowResponse response;

    public UnfollowTask(AuthToken authToken, User followee, Handler messageHandler) {
        super(authToken, messageHandler);
        this.followee = followee;
        request = new UnfollowRequest(authToken, followee.getAlias());
    }

    @Override
    public void runTask() {

        try {
            response = serverFacade.unfollow(request, "/unfollow");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (TweeterRemoteException e) {
            throw new RuntimeException(e);
        }
        if(response.isSuccess()){
            sendSuccessMessage();
        }
    }
}
