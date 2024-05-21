package edu.byu.cs.tweeter.client.model.services.backgroundTask;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.UserRequest;
import edu.byu.cs.tweeter.model.net.response.UserResponse;
import edu.byu.cs.tweeter.util.FakeData;

/**
 * Background task that returns the profile for a specified user.
 */
public class GetUserTask extends AuthenticatedTask implements Runnable {
    public static final String USER_KEY = "user";

    private User user;
    /**
     * Alias (or handle) for user whose profile is being retrieved.
     */
//    private final String alias;
    /**
     * Message handler that will receive task results.
     */

    private UserRequest request;
    private UserResponse response;

    public GetUserTask(AuthToken authToken, String alias, Handler messageHandler) {
        super(authToken, messageHandler);
//        this.alias = alias;
        request = new UserRequest(authToken, alias);
    }

    @Override
    protected void runTask() {
        try {
            response = serverFacade.getUser(request, "/getuser");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (TweeterRemoteException e) {
            throw new RuntimeException(e);
        }
        user = response.getUser();

        if (response.isSuccess()) {
            sendSuccessMessage();
        }
    }

    @Override
    protected void loadSuccessBundle(Bundle msgBundle) {
        msgBundle.putSerializable(USER_KEY, user);
    }

//    private User getUser() {
//        return getFakeData().findUserByAlias(alias);
//    }

}
