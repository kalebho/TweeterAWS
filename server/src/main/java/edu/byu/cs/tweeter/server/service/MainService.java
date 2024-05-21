package edu.byu.cs.tweeter.server.service;

import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.request.PostUpdateFeedMessageRequest;
import edu.byu.cs.tweeter.model.net.request.UpdateFeedRequest;
import edu.byu.cs.tweeter.model.net.response.LogoutResponse;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.server.dao.DynamoDAOFactory;
import edu.byu.cs.tweeter.server.dao.IDAOFactory;
import edu.byu.cs.tweeter.server.dao.IFeedDAO;
import edu.byu.cs.tweeter.server.dao.IFollowsDAO;
import edu.byu.cs.tweeter.server.dao.IStatusDAO;
import edu.byu.cs.tweeter.server.dao.IUserDAO;
import edu.byu.cs.tweeter.server.dao.JsonSerializerServer;
import edu.byu.cs.tweeter.util.Pair;

public class MainService {

    //Factory specific to Dynamo DB
    private static final IDAOFactory factory = new DynamoDAOFactory();
    //DAOs created from factory
    private static final IStatusDAO statusDAO =  factory.getStatusDAO();
    private static final IFollowsDAO followsDAO = factory.getFollowsDAO();
    private static final IUserDAO userDAO = factory.getUserDAO();
    private static final IFeedDAO feedDAO = factory.getFeedDAO();

    public LogoutResponse logout(LogoutRequest request) {
        if(request.getAuthToken() == null){
            throw new RuntimeException("[Bad Request] Missing a Authtoken");
        }

        try {
            boolean success = userDAO.logout(request.getAuthToken().getToken());
            return new LogoutResponse();
        }
        catch (Exception e) {
            throw new RuntimeException("[Server Error] " + e.getMessage());
        }
    }

    public PostStatusResponse postStatus(PostStatusRequest request) {
        if (request.getAuthToken() == null) {
            throw new RuntimeException("[Bad Request] Missing a Authtoken");
        }
        else if (request.getStatus() == null) {
            throw new RuntimeException("[Bad Request] Missing a status");
        }

        List<User> followers;
        try {
            statusDAO.addStatus(request.getAuthToken().getToken(), request.getStatus());
            return new PostStatusResponse();
        }
        catch (Exception e) {
            return new PostStatusResponse("[Server Error] " + e.getMessage());
        }
    }

    public void postUpdateFeedMessages(SQSEvent event) {
        if (event == null) {
            throw new RuntimeException("[Bad Request] Event is null!!!");
        }

        for (SQSEvent.SQSMessage msg : event.getRecords()) {

            PostUpdateFeedMessageRequest request = JsonSerializerServer.deserialize(msg.getBody(), PostUpdateFeedMessageRequest.class);

            Status requestStatus = request.getStatus();
            String token = request.getToken();
            User requestUser = requestStatus.getUser();


            String lastFollower = null;
            String lastFollowerAlias = null;
            boolean hasMorePages = true;

            try {

                while (hasMorePages) {
                    //Grab the number of followers of the target User
//                    Pair<List<User>,Boolean> result = followsDAO.getFollowers(token, requestUser.getAlias(), 100, lastFollower);
                    Pair<List<String>,Boolean> result = followsDAO.getFollowersALias(requestUser.getAlias(), 100, lastFollowerAlias);
                    hasMorePages = result.getSecond();
//                    lastFollower = result.getFirst().get(result.getFirst().size() - 1).getAlias();
                    lastFollowerAlias = result.getFirst().get(result.getFirst().size() - 1);

                    UpdateFeedRequest feedRequest = new UpdateFeedRequest();
                    feedRequest.setFollowers(result.getFirst());        //Set the FeedRequest******

                    //Create status from dynamo Status
                    User user = new User(requestUser.getFirstName(), requestUser.getLastName(), requestUser.getAlias(), requestUser.getImageUrl());
                    Status status = new Status(requestStatus.getPost(), user, requestStatus.getTimestamp(), requestStatus.getUrls(), requestStatus.getMentions());
                    feedRequest.setStatus(status);                      //Set the FeedRequest******

                    //Make the feedRequest into a string to be the message body
                    //Feed request is list of followers and the status
                    String messageBody = JsonSerializerServer.serialize(feedRequest);
                    String queueUrl = "https://sqs.us-east-1.amazonaws.com/428946460517/UpdateFeedQueue";

                    SendMessageRequest send_msg_request = new SendMessageRequest()
                            .withQueueUrl(queueUrl)
                            .withMessageBody(messageBody);

                    AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();
                    SendMessageResult send_msg_result = sqs.sendMessage(send_msg_request);
                    if (send_msg_result.getMessageId() != null) {
                        System.out.println("Sent to queue 2");
                    }
                    else {
                        throw new RuntimeException("Not sent to UpdateFeedQueue");
                    }
                }
            }
            catch (Exception e) {
                System.out.println("[Server Error] " + e.getMessage());
                e.printStackTrace();
            }
        }
    }


    public void updateFeeds(SQSEvent event) {

        for (SQSEvent.SQSMessage msg : event.getRecords()) {

            //This request has the list of followers that is 100 that we will loop through
            //and it has the the status that was just posted
            UpdateFeedRequest request = JsonSerializerServer.deserialize(msg.getBody(), UpdateFeedRequest.class);

            Status status = request.getStatus();
//            List<User> followers = request.getFollowers();
            List<String> followers = request.getFollowers();
            feedDAO.updateFeeds(status, followers);
        }
    }

}
