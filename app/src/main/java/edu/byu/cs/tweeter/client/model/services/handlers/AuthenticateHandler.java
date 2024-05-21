package edu.byu.cs.tweeter.client.model.services.handlers;

import android.os.Message;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.services.UserService;
import edu.byu.cs.tweeter.client.model.services.backgroundTask.LoginTask;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class AuthenticateHandler extends BackgroundTaskHandler<UserService.AuthenticateObserver> {


    public AuthenticateHandler(UserService.AuthenticateObserver observer) {
        super(observer);
    }

    @Override
    public void handleSuccessMessage(Message msg) {
        User loggedInUser = (User) msg.getData().getSerializable(LoginTask.USER_KEY);
        AuthToken authToken = (AuthToken) msg.getData().getSerializable(LoginTask.AUTH_TOKEN_KEY);

        // Cache user session information
        Cache.getInstance().setCurrUser(loggedInUser);
        Cache.getInstance().setCurrUserAuthToken(authToken);

        observer.authenticateSuccess(authToken, loggedInUser);
    }
}
