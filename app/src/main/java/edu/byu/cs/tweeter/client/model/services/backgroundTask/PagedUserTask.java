package edu.byu.cs.tweeter.client.model.services.backgroundTask;

import android.os.Handler;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.util.Pair;

public class PagedUserTask extends PagedTasks<User> {

    protected PagedUserTask(AuthToken authToken, User targetUser, int limit, User lastItem, Handler messageHandler) {
        super(authToken, targetUser, limit, lastItem, messageHandler);
    }

    @Override
    protected final List<User> getUsersForItems(List<User> items) {
        return items;
    }

    @Override
    protected Pair<List<User>, Boolean> getItems() {
        return null;
    }
}
