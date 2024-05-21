package edu.byu.cs.tweeter.client.model.services.backgroundTask;

import android.os.Handler;

import java.io.IOException;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.FollowingCountRequest;
import edu.byu.cs.tweeter.model.net.response.FollowingCountResponse;

/**
 * Background task that queries how many other users a specified user is following.
 */
public class GetFollowingCountTask extends GetCountTask implements Runnable {

    private FollowingCountRequest request;
    private FollowingCountResponse response;

    public GetFollowingCountTask(AuthToken authToken, User targetUser, Handler messageHandler) {
        super(authToken, targetUser, messageHandler);
        request = new FollowingCountRequest(authToken, targetUser.getAlias());
    }

    @Override
    protected int getCount() {
        try {
            response = serverFacade.getFollowingCount(request, "/getfollowingcount");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (TweeterRemoteException e) {
            throw new RuntimeException(e);
        }
        return response.getFollowingCount();
    }

}
