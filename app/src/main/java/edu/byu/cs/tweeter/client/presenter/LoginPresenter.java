package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.services.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class LoginPresenter extends AuthenticatePresenter{

    public LoginPresenter(View view) {
        super(view);
    }

    public void login(String alias, String password) {

        if (validateLogin(alias, password)) {
            view.hideErrorMessage();
            view.showInfoMessage("Logging in user...");
            var userService = new UserService();
            userService.login(alias, password, this);
        }
    }

    private boolean validateLogin(String alias, String password) {
        if (alias.length() > 0 && alias.charAt(0) != '@') {
            view.showErrorMessage("Alias must begin with @.");
            return false;
        }
        if (alias.length() < 2) {
            view.showErrorMessage("Alias must contain 1 or more characters after the @.");
            return false;
        }
        if (password.length() == 0) {
            view.showErrorMessage("Password cannot be empty.");
            return false;
        }
        return true;
    }
}
