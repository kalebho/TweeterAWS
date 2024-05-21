package edu.byu.cs.tweeter.client.presenter;


import edu.byu.cs.tweeter.client.model.services.UserService;

public class RegisterPresenter extends AuthenticatePresenter{

    public RegisterPresenter(View view) {
        super(view);
    }

    public void register(String firstname, String lastName, String alias, String password, String image) {

        if (validateRegister(firstname, lastName, alias, password, image)) {
            UserService userService = new UserService();

            userService.register(firstname, lastName, alias, password, image, this);
            view.showInfoMessage("Registering...");
        }
    }


    private boolean validateRegister(String firstname, String lastName, String alias, String password, String image) {
        if (firstname.length() == 0) {
            view.showErrorMessage("First Name cannot be empty.");
            return false;
        }
        if (lastName.length() == 0) {
            view.showErrorMessage("Last Name cannot be empty.");
            return false;
        }
        if (alias.length() == 0) {
            view.showErrorMessage("Alias cannot be empty.");
            return false;
        }
        if (alias.charAt(0) != '@') {
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
        if (image == null) {
            view.showErrorMessage("Profile image must be uploaded.");
            return false;
        }

        return true;
    }

}
