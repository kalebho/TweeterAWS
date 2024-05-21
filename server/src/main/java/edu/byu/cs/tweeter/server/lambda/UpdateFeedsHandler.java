package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;

import edu.byu.cs.tweeter.server.service.MainService;

public class UpdateFeedsHandler implements RequestHandler<SQSEvent, Void> {
    @Override
    public Void handleRequest(SQSEvent event, Context context) {
        MainService mainService = new MainService();
        mainService.updateFeeds(event);
        return null;
    }


    //Actually happening in the service in the DAO
    //Deserialize the message into Status object
    //Use the batch write code to put the status into all of the followers in Feed table

}
