package edu.byu.cs.tweeter.client.model.services.backgroundTask;

import android.os.Handler;

import java.io.IOException;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;

/**
 * Background task that posts a new status sent by a user.
 */
public class PostStatusTask extends AuthenticatedTask implements Runnable {

//    /**
//     * The new status being sent. Contains all properties of the status,
//     * including the identity of the user sending the status.
//     */
//    private Status status;

    private PostStatusRequest request;
    private PostStatusResponse response;

    public PostStatusTask(AuthToken authToken, Status status, Handler messageHandler) {
        super(authToken, messageHandler);
        request = new PostStatusRequest(authToken, status);
    }

    @Override
    public void runTask() {

        try {
            response = serverFacade.postStatus(request, "/poststatus");
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
