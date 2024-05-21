package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;

import edu.byu.cs.tweeter.server.service.MainService;

public class PostUpdateFeedMessagesHandler implements RequestHandler<SQSEvent, Void> {

    @Override
    public Void handleRequest(SQSEvent event, Context context) {
        MainService mainService = new MainService();
        mainService.postUpdateFeedMessages(event);
        return null;
    }
    //Get the message from the post status queue
    //Grab the number of followers of the target User
    //Send to the SQS Update Feed Queue a list of 500 users and the serialized message

}
