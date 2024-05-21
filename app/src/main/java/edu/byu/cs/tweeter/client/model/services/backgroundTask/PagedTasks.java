package edu.byu.cs.tweeter.client.model.services.backgroundTask;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.Serializable;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.util.Pair;

public abstract class PagedTasks<T> extends AuthenticatedTask {

    public static final String MORE_PAGES_KEY = "more-pages";
    public static final String ITEMS_KEY = "items";

    /**
     * The user whose feed is being retrieved.
     * (This can be any user, not just the currently logged-in user.)
     */
    private final User targetUser;
    /**
     * Maximum number of statuses to return (i.e., page size).
     */
    private final int limit;
    /**
     * The last "status" or "last person being followed/following" returned in the previous page of results (can be null).
     * This allows the new page to begin where the previous page ended.
     */
    private final T lastItem;

    /**
     * The items returned in the current page of results.
     */
    private List<T> items;

    /**
     * Indicates whether there are more pages of items that can be retrieved on subsequent calls.
     */
    private boolean hasMorePages;

    protected PagedTasks(AuthToken authToken, User targetUser, int limit, T lastItem, Handler messageHandler) {
        super(authToken, messageHandler);
        this.targetUser = targetUser;
        this.limit = limit;
        this.lastItem = lastItem;
    }

    protected User getTargetUser() {
        return targetUser;
    }
    protected int getLimit() {
        return limit;
    }

    protected T getLastItem() {
        return lastItem;
    }

    public final void runTask() {
        Pair<List<T>, Boolean> pageOfItems = getItems();

        items = pageOfItems.getFirst();
        hasMorePages = pageOfItems.getSecond();

        sendSuccessMessage();
    }

    @Override
    protected final void loadSuccessBundle(Bundle msgBundle) {
        msgBundle.putSerializable(ITEMS_KEY, (Serializable) items);
        msgBundle.putBoolean(MORE_PAGES_KEY, hasMorePages);
    }

    protected abstract Pair<List<T>, Boolean> getItems();
    protected abstract List<User> getUsersForItems(List<T> items);


}
