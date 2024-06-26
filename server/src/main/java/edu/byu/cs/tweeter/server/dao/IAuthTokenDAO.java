package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.model.domain.AuthToken;

public interface IAuthTokenDAO {
    AuthToken addAuthToken(String alias);
    String getAuthToken(String token);
}
