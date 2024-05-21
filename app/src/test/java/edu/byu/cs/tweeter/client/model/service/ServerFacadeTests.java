package edu.byu.cs.tweeter.client.model.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.FollowersCountRequest;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.AuthenticateResponse;
import edu.byu.cs.tweeter.model.net.response.FollowersCountResponse;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
import edu.byu.cs.tweeter.util.FakeData;

public class ServerFacadeTests {

    private User dummyUser;
    private FakeData fakeData;
    private ServerFacade serverFacade;

    @BeforeEach
    public void setup() {
        fakeData = FakeData.getInstance();
        dummyUser = fakeData.getFirstUser();
        serverFacade = new ServerFacade();
    }

    @Test
    public void testRegisterSuccess() {
        //Set up register request
        RegisterRequest registerRequest = new RegisterRequest("k", "h", "@c", "k", "k");
        //Get dummy authtoken
        AuthToken authToken = fakeData.getAuthToken();
        try {
            AuthenticateResponse registerResponse = serverFacade.register(registerRequest, "/register");
            Assertions.assertEquals(dummyUser, registerResponse.getUser());
            Assertions.assertEquals(registerResponse.getAuthToken(), registerResponse.getAuthToken());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (TweeterRemoteException e) {
            throw new RuntimeException(e);
        }

    }


    @Test
    public void testGetFollowersSuccess() {
        //Get list of fake users to compare too
        List<User> listOfFollowers = fakeData.getFakeUsers();
        //Create GetFollowersRequest
        FollowersRequest followersRequest = new FollowersRequest(new AuthToken(), "@allen", 21, "@ray");
        try {
            FollowersResponse response = serverFacade.getFollowers(followersRequest, "/getfollowers");
            Assertions.assertEquals(listOfFollowers.size(), response.getFollowers().size());
            Assertions.assertEquals(listOfFollowers.get(0), response.getFollowers().get(0));
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (TweeterRemoteException e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    public void testGetFollowersCountSuccess() {
        //Create request
        FollowersCountRequest request = new FollowersCountRequest(new AuthToken(), "@24");
        //Testing the serverFacade
        try {
            FollowersCountResponse response = serverFacade.getFollowersCount(request, "/getfollowerscount");
            Assertions.assertEquals(20, response.getFollowersCount());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (TweeterRemoteException e) {
            throw new RuntimeException(e);
        }

    }



}
