package edu.byu.cs.tweeter.client.model.services.backgroundTask;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.response.LogoutResponse;

/**
 * Background task that logs out a user (i.e., ends a session).
 */
public class LogoutTask extends AuthenticatedTask implements Runnable {

    private LogoutRequest logoutRequest;
    private LogoutResponse logoutResponse;
    public LogoutTask(AuthToken authToken, Handler messageHandler) {
        super(authToken, messageHandler);
        logoutRequest = new LogoutRequest(authToken);
    }

    @Override
    public void runTask() {
        try {
            logoutResponse = serverFacade.logout(logoutRequest, "/logout");
            sendSuccessMessage();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (TweeterRemoteException e) {
            throw new RuntimeException(e);
        }
        if (logoutResponse.isSuccess()) {
            sendSuccessMessage();
        }

    }

}
