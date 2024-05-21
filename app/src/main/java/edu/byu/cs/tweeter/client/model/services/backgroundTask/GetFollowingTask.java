package edu.byu.cs.tweeter.client.model.services.backgroundTask;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.StringTokenizer;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.util.FakeData;
import edu.byu.cs.tweeter.util.Pair;

/**
 * Background task that retrieves a page of other users being followed by a specified user.
 */
public class GetFollowingTask extends PagedUserTask implements Runnable {

    private FollowingRequest followingRequest;
    private FollowingResponse followingResponse;

    /**
     * The last person being followed returned in the previous page of results (can be null).
     * This allows the new page to begin where the previous page ended.
     */
    public GetFollowingTask(AuthToken authToken, User targetUser, int limit, User lastFollowee, Handler messageHandler) {
        super(authToken, targetUser, limit, lastFollowee, messageHandler);
        if (lastFollowee != null) {
            followingRequest = new FollowingRequest(authToken, targetUser.getAlias(), limit, lastFollowee.getAlias());
        }
        else {
            followingRequest = new FollowingRequest(authToken, targetUser.getAlias(), limit,null);
        }
    }

    @Override
    protected Pair<List<User>, Boolean> getItems() {
//        return getFakeData().getPageOfUsers((User) getLastItem(), getLimit(), getTargetUser());

        try {
            followingResponse = serverFacade.getFollowees(followingRequest, "/getfollowing");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (TweeterRemoteException e) {
            throw new RuntimeException(e);
        }
        return new Pair<>(followingResponse.getFollowees(), followingResponse.getHasMorePages());
    }

}
