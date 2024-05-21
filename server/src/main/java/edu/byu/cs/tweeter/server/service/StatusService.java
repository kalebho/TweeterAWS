package edu.byu.cs.tweeter.server.service;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.net.request.StatusRequest;
import edu.byu.cs.tweeter.model.net.response.StatusResponse;
import edu.byu.cs.tweeter.server.dao.DynamoDAOFactory;
import edu.byu.cs.tweeter.server.dao.IDAOFactory;
import edu.byu.cs.tweeter.server.dao.IFeedDAO;
import edu.byu.cs.tweeter.server.dao.IFollowsDAO;
import edu.byu.cs.tweeter.server.dao.IStatusDAO;
import edu.byu.cs.tweeter.util.Pair;

public class StatusService {

    //DAO Factory to get all the DAOs needed
    private static final IDAOFactory factory = new DynamoDAOFactory();
    //StatusDAO used to get the statuses for feed and story
    private static final IStatusDAO statusDAO = factory.getStatusDAO();
    private static final IFeedDAO feedDAO = factory.getFeedDAO();
    private static final IFollowsDAO followsDAO = factory.getFollowsDAO();


    public StatusResponse getFeed(StatusRequest request) {
        if (request.getAuthToken() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have an authtoken");
        }
        else if (request.getLimit() <= 0) {
            throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
        }
        else if (request.getTargetUserAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a follower alias");
        }

        try {
            Pair<List<Status>, Boolean> pair = feedDAO.getFeedStatuses(request.getAuthToken().getToken(), request.getTargetUserAlias(), request.getLimit(), request.getLastStatus());
            return new StatusResponse(pair.getFirst(), pair.getSecond());
        }
        catch (Exception e) {
            throw new RuntimeException("[Server Error] " + e.getMessage());
        }
    }


    public StatusResponse getStory(StatusRequest request) {
        if (request.getAuthToken() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have an authtoken");
        }
        else if (request.getLimit() <= 0) {
            throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
        }
        else if (request.getTargetUserAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a follower alias");
        }

        try {
            Pair<List<Status>, Boolean> pair = statusDAO.getStoryStatuses(request.getAuthToken().getToken(), request.getTargetUserAlias(), request.getLimit(), request.getLastStatus());
            return new StatusResponse(pair.getFirst(), pair.getSecond());
        }
        catch (Exception e) {
            throw new RuntimeException("[Server Error] " + e.getMessage());
        }
    }



}
