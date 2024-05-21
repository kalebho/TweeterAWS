package edu.byu.cs.tweeter.client.model.services;

import android.os.Message;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.tweeter.client.model.services.backgroundTask.BackgroundTaskUtils;
import edu.byu.cs.tweeter.client.model.services.backgroundTask.GetUserTask;
import edu.byu.cs.tweeter.client.model.services.backgroundTask.LoginTask;
import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.services.backgroundTask.RegisterTask;
import edu.byu.cs.tweeter.client.model.services.handlers.AuthenticateHandler;
import edu.byu.cs.tweeter.client.model.services.handlers.GetUserHandler;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class UserService{

    private BackgroundTaskUtils taskUtils = new BackgroundTaskUtils();

    public interface AuthenticateObserver extends ServiceObserver{
        void authenticateSuccess(AuthToken authToken, User user);
    }



    public void login(String alias, String password, AuthenticateObserver observer) {
        // Send the login request.
        LoginTask loginTask = new LoginTask(alias, password, new AuthenticateHandler(observer));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(loginTask);
    }



    //Register a new user
    public void register(String firstname, String lastname, String alias, String password, String imageBytesBase64, AuthenticateObserver observer) {
        RegisterTask registerTask = new RegisterTask(firstname, lastname, alias, password, imageBytesBase64, new AuthenticateHandler(observer));
        taskUtils.runTask(registerTask);
    }





    public void getUser(AuthToken authToken, String alias, UserObserver observer) {
        GetUserTask getUserTask = new GetUserTask(authToken, alias, new GetUserHandler(observer));
        taskUtils.runTask(getUserTask);
    }


}
