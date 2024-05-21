package edu.byu.cs.tweeter.server.dao;

public interface IDAOFactory {
    IAuthTokenDAO getAuthDAO();
    IFeedDAO getFeedDAO();
    IFollowsDAO getFollowsDAO();
    IStatusDAO getStatusDAO();
    IUserDAO getUserDAO();
}
