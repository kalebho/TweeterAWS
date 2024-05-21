package edu.byu.cs.tweeter.client.model.services.backgroundTask;

import android.os.Handler;

import java.io.IOException;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.AuthenticateResponse;
import edu.byu.cs.tweeter.util.Pair;

/**
 * Background task that creates a new user account and logs in the new user (i.e., starts a session).
 */
public class RegisterTask extends AuthenticateTask implements Runnable {

//    /**
//     * The user's first name.
//     */
//    private String firstName;
//    /**
//     * The user's last name.
//     */
//    private String lastName;
//    /**
//     * The base-64 encoded bytes of the user's profile image.
//     */
//    private String image;

    private RegisterRequest request;
    private AuthenticateResponse response;

    public RegisterTask(String firstName, String lastName, String alias, String password, String image, Handler messageHandler) {
        super(alias, password, messageHandler);
        request = new RegisterRequest(firstName, lastName, alias, password, image);
    }


    @Override
    protected Pair<User, AuthToken> doLoginRegister() {
        try {
            response = serverFacade.register(request, "/register");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (TweeterRemoteException e) {
            throw new RuntimeException(e);
        }

        return new Pair<>(response.getUser(), response.getAuthToken());
    }
}
