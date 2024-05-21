package edu.byu.cs.tweeter.client.presenter;

import androidx.dynamicanimation.animation.SpringAnimation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.Arrays;
import java.util.List;

import edu.byu.cs.tweeter.client.model.services.MainService;
import edu.byu.cs.tweeter.client.model.services.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class MainPresenterUnitTest {

    private MainPresenter.View mockView;
    private MainService mockService;
    private MainPresenter mainPresenterSpy;
    private Status status;
    private User user;
    private Long timestamp;


    @BeforeEach
    public void setup() {

        //create mocks
        mockView = Mockito.mock(MainPresenter.View.class);
        mockService = Mockito.mock(MainService.class);
        //create spy for presenter with all its methods (we will test just the post status)
        mainPresenterSpy = Mockito.spy(new MainPresenter(mockView));

        //when creating a main service, we want to pass in the mock service and not the real one
        //Factory method within the MainPresenter that is used in the postStatus method
        Mockito.when(mainPresenterSpy.factoryMethod()).thenReturn(mockService);

        //init fake data
        user = new User("Kaleb", "Ho Ching", "url");
        timestamp = 999L;
        List<String> urls = Arrays.asList("papito.com");
        List<String> mentions = Arrays.asList("@traceyBoy");
        status = new Status("my post", user, timestamp, urls, mentions);
    }

    @Test
    public void postStatusSuccess() {
        //create an answer for when the mock service calls postObserverSuccess
        Answer answer = new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                MainService.PostStatusObserver observer = invocation.getArgument(2, MainService.PostStatusObserver.class);
                Status myStatus = invocation.getArgument(1,Status.class);
                Assertions.assertEquals(myStatus.post, "My Post");
                Assertions.assertEquals(myStatus.user, user);
                Assertions.assertEquals(myStatus.timestamp, timestamp);
                observer.PostStatusObserverSuccess();
                return null;
            }
        };

        //When the mockService calls postStatus, it will have a certain thing that it does to test the observer
        Mockito.doAnswer(answer).when(mockService).postStatus(Mockito.any(), Mockito.any(), Mockito.any());

        //Start the test for the entire posting of the status
        mainPresenterSpy.postStatus(new AuthToken(), "My Post", user, timestamp);

        //Used to verify that the test was a success with the write amount of calls to the right methods
        Mockito.verify(mockView).showPostingToast("Posting Status...");

        //When success happens
        Mockito.verify(mockView).hideInfoMessage();
        Mockito.verify(mockView).showInfoMessage("Successfully Posted!");
    }

    @Test
    public void postStatusFail() {
        //create an answer for when the mock service calls postObserverSuccess
        Answer answer = new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                MainService.PostStatusObserver observer = invocation.getArgument(2, MainService.PostStatusObserver.class);
                observer.handlerFail("the error/exception message");
                return null;
            }
        };

        //When the mockService calls postStatus, it will have a certain thing that it does to test the observer
        Mockito.doAnswer(answer).when(mockService).postStatus(Mockito.any(), Mockito.any(), Mockito.any());

        //Start the test for the entire posting of the status
        mainPresenterSpy.postStatus(new AuthToken(), "My Post", user, timestamp);

        //Used to verify that the test was a success with the write amount of calls to the right methods
        //When initially posting
        Mockito.verify(mockView).showPostingToast("Posting Status...");

        //When fail happens
        Mockito.verify(mockView).showErrorMessage("the error/exception message");

    }
}
