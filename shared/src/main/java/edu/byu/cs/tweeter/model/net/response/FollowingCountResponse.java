package edu.byu.cs.tweeter.model.net.response;

public class FollowingCountResponse extends Response{

    private int followingCount;


    public FollowingCountResponse(boolean success, String message) {
        super(false, message);
    }

    public FollowingCountResponse(int followingCount) {
        super(true);
        this.followingCount = followingCount;
    }

    public int getFollowingCount() {
        return followingCount;
    }

    public void setFollowingCount(int followingCount) {
        this.followingCount = followingCount;
    }
}
