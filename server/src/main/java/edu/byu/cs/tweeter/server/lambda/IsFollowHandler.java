package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.net.request.IsFollowRequest;
import edu.byu.cs.tweeter.model.net.response.IsFollowResponse;
import edu.byu.cs.tweeter.server.service.FollowService;

public class IsFollowHandler implements RequestHandler<IsFollowRequest, IsFollowResponse> {

    @Override
    public IsFollowResponse handleRequest(IsFollowRequest request, Context context) {
        FollowService followService = new FollowService();
        return followService.isFollow(request);
    }
}
