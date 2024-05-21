package edu.byu.cs.tweeter.client.model.services;

import java.util.List;

import edu.byu.cs.tweeter.client.model.services.ServiceObserver;
import edu.byu.cs.tweeter.model.domain.Status;

public interface PagedObserver<T> extends ServiceObserver {

    void pagedObserverSuccess(List<T> items, boolean hasMorePages);
}
