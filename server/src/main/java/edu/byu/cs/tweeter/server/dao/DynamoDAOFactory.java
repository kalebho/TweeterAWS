package edu.byu.cs.tweeter.server.dao;

public class DynamoDAOFactory implements IDAOFactory {

    private AuthTokenDAO authTokenDAO;
    private FollowsDAO followDAO;
    private FeedDAO feedDAO;
    private StatusDAO statusDAO;
    private UserDAO userDAO;


    @Override
    public IAuthTokenDAO getAuthDAO() {
        if (authTokenDAO == null) {
            return new AuthTokenDAO();
        }
        else {
            return authTokenDAO;
        }
    }

    @Override
    public IFeedDAO getFeedDAO() {
        if (feedDAO == null) {
            return new FeedDAO();
        }
        else {
            return feedDAO;
        }
    }

    @Override
    public IFollowsDAO getFollowsDAO() {
        if (followDAO == null) {
            return new FollowsDAO();
        }
        else {
            return followDAO;
        }
    }

    @Override
    public IStatusDAO getStatusDAO() {
        if (statusDAO == null) {
            return new StatusDAO();
        }
        else {
            return statusDAO;
        }
    }

    @Override
    public IUserDAO getUserDAO() {
        if (userDAO == null) {
            return new UserDAO();
        }
        else {
            return userDAO;
        }
    }


}
