package edu.byu.cs.tweeter.client.service;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.client.presenter.BasePresenter;
import edu.byu.cs.tweeter.client.presenter.MainPresenter;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.StatusRequest;
import edu.byu.cs.tweeter.model.net.response.AuthenticateResponse;
import edu.byu.cs.tweeter.model.net.response.StatusResponse;

public class StoryServiceTest {

    private AuthToken authToken;
    private User user;
    private MainPresenter mainPresenterSpy;
    private BasePresenter.View viewMock;
    private CountDownLatch countDownLatch;
    private ServerFacade serverFacade;


    @BeforeEach
    public void setup() {
        //Login to get the user and the authToken
        LoginRequest loginRequest = new LoginRequest("@headUser", "head");
        AuthenticateResponse response;

        serverFacade = new ServerFacade();
        try {
            response = serverFacade.login(loginRequest, "/login");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (TweeterRemoteException e) {
            throw new RuntimeException(e);
        }
        user = response.getUser();
        authToken = response.getAuthToken();


        viewMock = Mockito.mock(BasePresenter.View.class);
        mainPresenterSpy = Mockito.spy(new MainPresenter(viewMock));

        // Prepare the countdown latch
        resetCountDownLatch();
    }

    //Sets up the instance of latch to handle just 1 thread always
    private void resetCountDownLatch() {
        countDownLatch = new CountDownLatch(1);
    }

    //
    private void awaitCountDownLatch() throws InterruptedException {
        //Wait for latch to go to 0....means that thread is finished
        countDownLatch.await();
        //When finished...Reset latch again to handle 1 thread
        resetCountDownLatch();
    }


    /**
     * Verify that for successful requests, the
     * asynchronous method eventually returns the same result as the {@link ServerFacade}.
     */
    @Test
    public void postStatusSuccess() throws InterruptedException {


        Answer answer = new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                countDownLatch.countDown();
                return null;
            }
        };

        mainPresenterSpy.postStatus(authToken, "passoff", user, System.currentTimeMillis());
        Mockito.doAnswer(answer).when(viewMock).showInfoMessage(Mockito.anyString());
        awaitCountDownLatch();

        //Check if the showInfoMessage method was called
        Mockito.verify(viewMock).showInfoMessage("Successfully Posted!");

        //Get the story
        StatusResponse response;
        StatusRequest statusRequest = new StatusRequest(authToken, user.getAlias(), 10, null);
        try {
            response = serverFacade.getStory(statusRequest, "/getstory");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (TweeterRemoteException e) {
            throw new RuntimeException(e);
        }

        List<Status> statuses = response.getStatuses();
        Status status = statuses.get(0);
        String actualPost = status.getPost();
        String actualAlias = status.getUser().getAlias();
        Assertions.assertEquals("passoff", actualPost);
        Assertions.assertEquals("@headUser", actualAlias);

    }
}
