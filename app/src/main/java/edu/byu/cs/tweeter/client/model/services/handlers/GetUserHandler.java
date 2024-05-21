package edu.byu.cs.tweeter.client.model.services.handlers;

import android.os.Message;

import edu.byu.cs.tweeter.client.model.services.UserObserver;
import edu.byu.cs.tweeter.client.model.services.backgroundTask.GetUserTask;
import edu.byu.cs.tweeter.model.domain.User;

public class GetUserHandler extends BackgroundTaskHandler<UserObserver> {

    public GetUserHandler(UserObserver observer) {
        super(observer);
    }

    @Override
    protected void handleSuccessMessage(Message msg) {
        User user = (User) msg.getData().getSerializable(GetUserTask.USER_KEY);
        observer.getUserSuccessful(user);
    }

}
