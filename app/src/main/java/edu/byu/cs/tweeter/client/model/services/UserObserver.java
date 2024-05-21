package edu.byu.cs.tweeter.client.model.services;

import edu.byu.cs.tweeter.client.model.services.ServiceObserver;
import edu.byu.cs.tweeter.model.domain.User;

public interface UserObserver extends ServiceObserver {
    void getUserSuccessful(User user);
}
