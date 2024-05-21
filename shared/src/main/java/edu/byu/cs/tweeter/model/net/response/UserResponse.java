package edu.byu.cs.tweeter.model.net.response;

import edu.byu.cs.tweeter.model.domain.User;

public class UserResponse extends Response{

    private User user;

    public UserResponse(boolean success, String message) {
        super(false, message);
    }

    public UserResponse(User user) {
        super(true);
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
