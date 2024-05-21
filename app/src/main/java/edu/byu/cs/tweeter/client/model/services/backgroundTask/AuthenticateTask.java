package edu.byu.cs.tweeter.client.model.services.backgroundTask;

import android.os.Bundle;
import android.os.Handler;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.util.Pair;

public abstract class AuthenticateTask extends Task{

    /**
     * The user's username (or "alias" or "handle"). E.g., "@susan".
     */
    protected String username;
    /**
     * The user's password.
     */
    protected String password;



    private User user;
    private AuthToken authToken;

    public static final String USER_KEY = "user";
    public static final String AUTH_TOKEN_KEY = "auth-token";

    public AuthenticateTask(String username, String password, Handler messageHandler) {
        super(messageHandler);
        this.username = username;
        this.password = password;
    }

    public void runTask() {
        Pair<User, AuthToken> loginRegisterResult = doLoginRegister();

        user = loginRegisterResult.getFirst();
        authToken = loginRegisterResult.getSecond();

        sendSuccessMessage();

    }

    protected void loadSuccessBundle(Bundle msgBundle) {
        msgBundle.putSerializable(USER_KEY, user);
        msgBundle.putSerializable(AUTH_TOKEN_KEY, authToken);
    }

    protected abstract Pair<User, AuthToken> doLoginRegister();


}
