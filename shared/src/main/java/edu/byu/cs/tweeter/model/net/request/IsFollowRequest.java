package edu.byu.cs.tweeter.model.net.request;

import java.awt.geom.AffineTransform;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class IsFollowRequest {

    private AuthToken authToken;
    private String targetUserAlias;
    private String followeeAlias;

    public IsFollowRequest() {}

    public IsFollowRequest(AuthToken authToken, String targetUserAlias, String followeeAlias) {
        this.authToken = authToken;
        this.targetUserAlias = targetUserAlias;
        this.followeeAlias = followeeAlias;
    }

    public AuthToken getAuthToken() {
        return authToken;
    }

    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }

    public String getTargetUserAlias() {
        return targetUserAlias;
    }

    public void setTargetUserAlias(String targetUserAlias) {
        this.targetUserAlias = targetUserAlias;
    }

    public String getFolloweeAlias() {
        return followeeAlias;
    }

    public void setFolloweeAlias(String followeeAlias) {
        this.followeeAlias = followeeAlias;
    }
}
