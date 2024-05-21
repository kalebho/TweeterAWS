package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.services.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;


public abstract class AuthenticatePresenter implements UserService.AuthenticateObserver {

    protected View view;

    public AuthenticatePresenter(View view) {
        this.view = view;
    }

    public interface View {
        void hideErrorMessage();
        void hideInfoMessage();
        void showErrorMessage(String message);
        void showInfoMessage(String message);
        void openMainView(User user);
    }

    @Override
    public void authenticateSuccess(AuthToken authToken, User user) {
        view.hideErrorMessage();
        view.showInfoMessage("Hello " + user.getName());
        view.openMainView(user);
    }

    @Override
    public void handlerFail(String message) {
        view.hideInfoMessage();
        view.showErrorMessage(message);
    }

}
