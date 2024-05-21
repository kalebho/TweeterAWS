package edu.byu.cs.tweeter.client.model.services.backgroundTask;

import android.os.Handler;

import java.io.IOException;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
import edu.byu.cs.tweeter.util.Pair;

/**
 * Background task that retrieves a page of followers.
 */
public class GetFollowersTask extends PagedUserTask implements Runnable {

    private FollowersRequest request;
    private FollowersResponse response;

    public GetFollowersTask(AuthToken authToken, User targetUser, int limit, User lastFollower, Handler messageHandler) {
        super(authToken, targetUser, limit, lastFollower, messageHandler);
        if (lastFollower != null) {
            request = new FollowersRequest(authToken, targetUser.getAlias(), limit, lastFollower.getAlias());
        }
        else {
            request = new FollowersRequest(authToken, targetUser.getAlias(), limit,null);
        }
    }

    @Override
    protected Pair<List<User>, Boolean> getItems() {

        try {
            response = serverFacade.getFollowers(request, "/getfollowers");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (TweeterRemoteException e) {
            throw new RuntimeException(e);
        }
        return new Pair<>(response.getFollowers(), response.getHasMorePages());
    }

}
