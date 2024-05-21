package edu.byu.cs.tweeter.client.model.services.backgroundTask;

import android.os.Handler;

import java.io.IOException;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.response.AuthenticateResponse;
import edu.byu.cs.tweeter.util.Pair;

/**
 * Background task that logs in a user (i.e., starts a session).
 */
public class LoginTask extends AuthenticateTask implements Runnable {

    /**
     * A Request object for sending to API
     */
    private LoginRequest request;
    /**
     * A Response object for sending to API
     */
    private AuthenticateResponse response;

    public LoginTask(String username, String password, Handler messageHandler) {
        super(password, username, messageHandler);
        request = new LoginRequest(username, password);
    }

    @Override
    protected Pair<User, AuthToken> doLoginRegister() {
//        User loggedInUser = getFakeData().getFirstUser();
//        AuthToken authToken = getFakeData().getAuthToken();
//        return new Pair<>(loggedInUser, authToken);

        //Create the request for login
        try {
            response = serverFacade.login(request, "/login");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (TweeterRemoteException e) {
            throw new RuntimeException(e);
        }
        return new Pair<>(response.getUser(), response.getAuthToken());
    }
}
