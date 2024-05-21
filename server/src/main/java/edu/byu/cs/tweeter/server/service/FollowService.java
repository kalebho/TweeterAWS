package edu.byu.cs.tweeter.server.service;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.request.FollowersCountRequest;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingCountRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.request.IsFollowRequest;
import edu.byu.cs.tweeter.model.net.request.UnfollowRequest;
import edu.byu.cs.tweeter.model.net.response.FollowResponse;
import edu.byu.cs.tweeter.model.net.response.FollowersCountResponse;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingCountResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.model.net.response.IsFollowResponse;
import edu.byu.cs.tweeter.model.net.response.UnfollowResponse;
import edu.byu.cs.tweeter.server.dao.DynamoDAOFactory;
import edu.byu.cs.tweeter.server.dao.FollowsDAO;
import edu.byu.cs.tweeter.server.dao.IAuthTokenDAO;
import edu.byu.cs.tweeter.server.dao.IDAOFactory;
import edu.byu.cs.tweeter.server.dao.IFollowsDAO;
import edu.byu.cs.tweeter.server.dao.IUserDAO;
import edu.byu.cs.tweeter.server.dynamoModel.DynamoUser;
import edu.byu.cs.tweeter.util.Pair;

/**
 * Contains the business logic for getting the users a user is following.
 */
public class FollowService {

    private static final IDAOFactory factory = new DynamoDAOFactory();
    private static final IFollowsDAO followsDAO = factory.getFollowsDAO();
    private static final IUserDAO userDAO = factory.getUserDAO();
    //AuthDAO to help getting the user in the follow and unfollow actions
    private static final IAuthTokenDAO authDAO = factory.getAuthDAO();

    /**
     * Returns the users that the user specified in the request is following. Uses information in
     * the request object to limit the number of followees returned and to return the next set of
     * followees after any that were returned in a previous request. Uses the {@link FollowsDAO} to
     * get the followees.
     *
     * @param request contains the data required to fulfill the request.
     * @return the followees.
     */
    public FollowingResponse getFollowees(FollowingRequest request) {
        if(request.getFollowerAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a follower alias");
        } else if(request.getLimit() <= 0) {
            throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
        }

        try{
            Pair<List<User>, Boolean> pair = followsDAO.getFollowees(request.getAuthToken().getToken(), request.getFollowerAlias(), request.getLimit(), request.getLastFolloweeAlias());
            return new FollowingResponse(pair.getFirst(), pair.getSecond());
        }
        catch (Exception e) {
            throw new RuntimeException("[Server Error] " + e.getMessage());
        }
    }

    /**
     * Returns an instance of {@link FollowsDAO}. Allows mocking of the FollowDAO class
     * for testing purposes. All usages of FollowDAO should get their FollowDAO
     * instance from this method to allow for mocking of the instance.
     *
     * @return the instance.
     */
    FollowsDAO getFollowingDAO() {
        return new FollowsDAO();
    }


    public FollowersResponse getFollowers(FollowersRequest request) {

        if(request.getTargetUserAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a target user alias");
        } else if(request.getLimit() <= 0) {
            throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
        } else if (request.getAuthToken() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have an authtoken");
        }


        try{
            Pair<List<User>, Boolean> pair = followsDAO.getFollowers(request.getAuthToken().getToken(), request.getTargetUserAlias(), request.getLimit(), request.getLastFollowerAlias());
            return new FollowersResponse(pair.getFirst(), pair.getSecond());
        }
        catch (Exception e) {
            throw new RuntimeException("[Server Error] " + e.getMessage());
        }
    }

    public FollowersCountResponse getFollowersCount(FollowersCountRequest request) {
        if(request.getAuthToken() == null){
            throw new RuntimeException("[Bad Request] Missing a Authtoken");
        }
        else if (request.getTargetUserAlias() == null) {
            throw new RuntimeException("[Bad Request] Missing target user alias");
        }

        try{
            //Get the user using the userDAO
            DynamoUser targetUser = userDAO.getUserForCount(request.getAuthToken().getToken(), request.getTargetUserAlias());
            //Check if null user
            if (targetUser == null) {
                throw new RuntimeException("User is null");
            }

            int count = followsDAO.getFollowersCount(request.getAuthToken().getToken(), request.getTargetUserAlias(), targetUser);
            return new FollowersCountResponse(count);
        }
        catch (Exception e) {
            throw new RuntimeException("[Server Error] " + e.getMessage());
        }
    }

    public FollowingCountResponse getFollowingCount(FollowingCountRequest request) {
        if(request.getAuthToken() == null){
            throw new RuntimeException("[Bad Request] Missing a Authtoken");
        }
        else if (request.getTargetUserAlias() == null) {
            throw new RuntimeException("[Bad Request] Missing target user alias");
        }

        try{
            //Get the user using the userDAO
            DynamoUser targetUser = userDAO.getUserForCount(request.getAuthToken().getToken(), request.getTargetUserAlias());
            //Check if null user
            if (targetUser == null) {
                throw new RuntimeException("User is null");
            }
            int count = followsDAO.getFolloweesCount(request.getAuthToken().getToken(), request.getTargetUserAlias(), targetUser);
            return new FollowingCountResponse(count);
        }
        catch (Exception e) {
            throw new RuntimeException("[Server Error] " + e.getMessage());
        }

    }

    public FollowResponse follow(FollowRequest request) {
        if(request.getAuthToken() == null){
            throw new RuntimeException("[Bad Request] Missing a Authtoken");
        }
        else if (request.getFolloweeAlias() == null) {
            throw new RuntimeException("[Bad Request] Missing followee user alias");
        }


        try {
            //Get the target user alias using the token
            String targetAlias = authDAO.getAuthToken(request.getAuthToken().getToken());
            if (targetAlias == null) {
                throw new RuntimeException("Target user is null");
            }

            //Get the target user using the alias
            User targetUser = userDAO.getUser(request.getAuthToken().getToken(), targetAlias);
            User followee = userDAO.getUser(request.getAuthToken().getToken(), request.getFolloweeAlias());
            if(followee == null) {
                throw new RuntimeException("Followee is null");
            }

            //Get the current numFollowers and numFollowees
            int numFollowee = userDAO.getUserNumFollowees(request.getAuthToken().getToken(), targetAlias);
            int numFollower = userDAO.getUserNumFollowers(request.getAuthToken().getToken(), request.getFolloweeAlias());


            followsDAO.follow(request.getAuthToken().getToken(), request.getFolloweeAlias(), targetAlias, targetUser, followee, numFollowee, numFollower);
            return new FollowResponse();
        }
        catch (Exception e) {
            throw new RuntimeException("[Server Error] " + e.getMessage());
        }
    }

    public UnfollowResponse unfollow(UnfollowRequest request) {
        if(request.getAuthToken() == null){
            throw new RuntimeException("[Bad Request] Missing a Authtoken");
        }
        else if (request.getFolloweeAlias() == null) {
            throw new RuntimeException("[Bad Request] Missing followee user alias");
        }

        try {
            //Get the target user alias using the token
            String targetAlias = authDAO.getAuthToken(request.getAuthToken().getToken());
            if (targetAlias == null) {
                throw new RuntimeException("Target user is null");
            }

            //Get the target user using the alias
            User targetUser = userDAO.getUser(request.getAuthToken().getToken(), targetAlias);
            User followee = userDAO.getUser(request.getAuthToken().getToken(), request.getFolloweeAlias());
            if(followee == null) {
                throw new RuntimeException("Followee is null");
            }

            //Get the current numFollowers and numFollowees
            int numFollowee = userDAO.getUserNumFollowees(request.getAuthToken().getToken(), targetAlias);
            int numFollower = userDAO.getUserNumFollowers(request.getAuthToken().getToken(), request.getFolloweeAlias());
            System.out.println("numFollowee: " + numFollowee);
            System.out.println("numFollower: " + numFollower);

            followsDAO.unfollow(request.getAuthToken().getToken(), request.getFolloweeAlias(), targetAlias, targetUser, followee, numFollowee, numFollower);
            return new UnfollowResponse();
        }
        catch (Exception e) {
            throw new RuntimeException("[Server Error] " + e.getMessage());
        }
    }


    public IsFollowResponse isFollow(IsFollowRequest request) {
        if(request.getAuthToken() == null){
            throw new RuntimeException("[Bad Request] Missing a Authtoken");
        }
        else if (request.getTargetUserAlias() == null) {
            throw new RuntimeException("[Bad Request] Missing target user alias");
        }
        else if (request.getFolloweeAlias() == null) {
            throw new RuntimeException("[Bad Request] Missing followee user alias");
        }


        boolean isFollower = followsDAO.isFollow(request.getAuthToken().getToken(), request.getTargetUserAlias(), request.getFolloweeAlias());
        return new IsFollowResponse(isFollower);
    }


}
