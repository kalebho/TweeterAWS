package edu.byu.cs.tweeter.server.service;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.request.UserRequest;
import edu.byu.cs.tweeter.model.net.response.AuthenticateResponse;
import edu.byu.cs.tweeter.model.net.response.UserResponse;
import edu.byu.cs.tweeter.server.dao.DynamoDAOFactory;
import edu.byu.cs.tweeter.server.dao.IAuthTokenDAO;
import edu.byu.cs.tweeter.server.dao.IDAOFactory;
import edu.byu.cs.tweeter.server.dao.IUserDAO;
import edu.byu.cs.tweeter.server.dao.ImageDAO;
import edu.byu.cs.tweeter.server.dao.ImageDAOFactory;
import edu.byu.cs.tweeter.server.dao.S3ImageDAOFactory;

public class UserService {

    //DAO Factory to get all the DAOs needed
    private IDAOFactory factory = new DynamoDAOFactory();
    private ImageDAOFactory imageFactory = new S3ImageDAOFactory();
    //AuthTokenDAO used to get the authTokens
    private IAuthTokenDAO authTokenDAO = factory.getAuthDAO();

    //UserDAO used to get the user
    private IUserDAO userDAO = factory.getUserDAO();
    private ImageDAO imageDAO = imageFactory.getImageDAO();

    public AuthenticateResponse login(LoginRequest request) {
        if(request.getUsername() == null){
            throw new RuntimeException("[Bad Request] Missing a username");
        } else if(request.getPassword() == null) {
            throw new RuntimeException("[Bad Request] Missing a password");
        }

        try {
            AuthToken authToken = authTokenDAO.addAuthToken(request.getUsername());
            User user = userDAO.getUserAuthenticate(request.getUsername(), request.getPassword());
            return new AuthenticateResponse(user, authToken);
        }
        catch (Exception e) {
            throw new RuntimeException("[Server Error]" + e.getMessage());
        }
    }

    public UserResponse getUser(UserRequest request) {
        if(request.getAuthToken() == null) {
            throw new RuntimeException("[Bad Request] Missing an authtoken");
        }
        else if (request.getAlias() == null) {
            throw new RuntimeException("[Bad Request] Missing an alias");
        }

        try {
            User user = userDAO.getUser(request.getAuthToken().getToken(), request.getAlias());
            return new UserResponse(user);
        }
        catch (Exception e) {
            throw new RuntimeException("[Server Error] " + e.getMessage());
        }
    }

    public AuthenticateResponse register(RegisterRequest request) {
        if(request.getFirstName() == null) {
            throw new RuntimeException("[Bad Request] Missing first name");
        }
        if(request.getLastName() == null) {
            throw new RuntimeException("[Bad Request] Missing last name");
        }
        else if (request.getAlias() == null) {
            throw new RuntimeException("[Bad Request] Missing an alias");
        }
        else if (request.getPassword() == null) {
            throw new RuntimeException("[Bad Request] Missing a password");
        }
        else if (request.getImage() == null) {
            throw new RuntimeException("[Bad Request] Missing an image");
        }

        try {

            //Store image in s3 bucket
            String imageUrl = imageDAO.uploadImage(request.getImage(), request.getAlias());
            //Add user and authToken to table
            userDAO.addUser(request.getFirstName(), request.getLastName(), request.getAlias(), request.getPassword(), imageUrl);

            AuthToken authToken = authTokenDAO.addAuthToken(request.getAlias());


            //Login person
            User user = userDAO.getUser(authToken.getToken(), request.getAlias());
            return new AuthenticateResponse(user, authToken);
        }
        catch (Exception e) {
            throw new RuntimeException("[Server Error] " + e.getMessage());
        }
    }

}
