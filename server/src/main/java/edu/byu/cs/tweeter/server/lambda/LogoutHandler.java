package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.response.LogoutResponse;
import edu.byu.cs.tweeter.server.service.MainService;

public class LogoutHandler implements RequestHandler<LogoutRequest, LogoutResponse> {


    @Override
    public LogoutResponse handleRequest(LogoutRequest logoutRequest, Context context) {
        MainService mainService = new MainService();
        return mainService.logout(logoutRequest);
    }
}
