package edu.byu.cs.tweeter.model.net.response;

public class IsFollowResponse extends Response{

    private boolean follow;

    public IsFollowResponse(String message) {
        super(false, message);
    }

    public IsFollowResponse(boolean follow) {
        super(true);
        this.follow = follow;
    }

    public boolean isFollow() {
        return follow;
    }

    public void setFollow(boolean follow) {
        this.follow = follow;
    }
}
