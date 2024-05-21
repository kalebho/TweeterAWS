package edu.byu.cs.tweeter.client.model.services.backgroundTask;

import android.os.Handler;

import java.io.IOException;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.StatusRequest;
import edu.byu.cs.tweeter.model.net.response.StatusResponse;
import edu.byu.cs.tweeter.util.Pair;

/**
 * Background task that retrieves a page of statuses from a user's feed.
 */
public class GetFeedTask extends PagedStatusTask implements Runnable {

    private StatusRequest request;
    private StatusResponse response;

    public GetFeedTask(AuthToken authToken, User targetUser, int limit, Status lastStatus, Handler messageHandler) {
        super(authToken, targetUser, limit, lastStatus, messageHandler);
        request = new StatusRequest(authToken, targetUser.getAlias(), limit, lastStatus);
    }
    @Override
    protected Pair<List<Status>, Boolean> getItems() {

        try {
            response = serverFacade.getFeed(request, "/getfeed");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (TweeterRemoteException e) {
            throw new RuntimeException(e);
        }
        return new Pair<>(response.getStatuses(), response.getHasMorePages());
//        return getFakeData().getPageOfStatus(getLastItem(), getLimit());
    }

}
