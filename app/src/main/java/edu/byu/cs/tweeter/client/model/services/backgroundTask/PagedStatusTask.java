package edu.byu.cs.tweeter.client.model.services.backgroundTask;

import java.util.List;
import android.os.Handler;
import java.util.stream.Collectors;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.util.Pair;

public class PagedStatusTask extends PagedTasks<Status> {


    protected PagedStatusTask(AuthToken authToken, User targetUser, int limit, Status lastItem, Handler messageHandler) {
        super(authToken, targetUser, limit, lastItem, messageHandler);
    }


    @Override
    protected Pair<List<Status>, Boolean> getItems() {
        return null;
    }

    @Override
    protected final List<User> getUsersForItems(List<Status> items) {
        return items.stream().map(x -> x.user).collect(Collectors.toList());
    }
}
