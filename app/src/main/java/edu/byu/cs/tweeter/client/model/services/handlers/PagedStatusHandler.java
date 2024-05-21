package edu.byu.cs.tweeter.client.model.services.handlers;

import android.os.Message;

import java.util.List;

import edu.byu.cs.tweeter.client.model.services.PagedObserver;
import edu.byu.cs.tweeter.client.model.services.backgroundTask.GetStoryTask;
import edu.byu.cs.tweeter.client.model.services.handlers.BackgroundTaskHandler;
import edu.byu.cs.tweeter.model.domain.Status;

public class PagedStatusHandler extends BackgroundTaskHandler<PagedObserver> {

    public PagedStatusHandler(PagedObserver observer) {
        super(observer);
    }

    @Override
    protected void handleSuccessMessage(Message msg) {
        List<Status> statuses = (List<Status>) msg.getData().getSerializable(GetStoryTask.ITEMS_KEY);
        boolean hasMorePages = msg.getData().getBoolean(GetStoryTask.MORE_PAGES_KEY);
        Status lastStatus = (statuses.size() > 0) ? statuses.get(statuses.size() - 1) : null;
        observer.pagedObserverSuccess(statuses, hasMorePages);
    }


}
