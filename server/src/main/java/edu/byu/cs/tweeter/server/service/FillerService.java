package edu.byu.cs.tweeter.server.service;

import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.server.dao.DynamoDAOFactory;
import edu.byu.cs.tweeter.server.dao.IDAOFactory;
import edu.byu.cs.tweeter.server.dao.IFollowsDAO;
import edu.byu.cs.tweeter.server.dao.IUserDAO;
import edu.byu.cs.tweeter.server.dynamoModel.DynamoUser;

public class FillerService {
    // How many follower users to add
    // We recommend you test this with a smaller number first, to make sure it works for you
    private final static int NUM_USERS = 10000;

    // The alias of the user to be followed by each user created
    // This example code does not add the target user, that user must be added separately.

    // Get instance of DAOs by way of the Abstract Factory Pattern
    private final static IDAOFactory factory = new DynamoDAOFactory();
    private final static IUserDAO userDAO = factory.getUserDAO();
    private final static IFollowsDAO followsDAO = factory.getFollowsDAO();

    public static void fillDatabase() {

        List<DynamoUser> dynamoUsers = new ArrayList<>();

        // Iterate over the number of users you will create
        for (int i = 1; i <= NUM_USERS; i++) {

            String name = "User " + i;
            String alias = "@user" + i;

            // Note that in this example, a UserDTO only has a name and an alias.
            // The url for the profile image can be derived from the alias in this example
            DynamoUser dynamoUser = new DynamoUser();
            dynamoUser.setFirstName(name);
            dynamoUser.setLastName("foo");
            dynamoUser.setAlias(alias);
            dynamoUser.setPassword("password");
            dynamoUser.setNumFollowers(0);
            dynamoUser.setNumFollowees(0);
            dynamoUser.setImageUrl("nothing");
            dynamoUsers.add(dynamoUser);
        }

        // Call the DAOs for the database logic
        if (dynamoUsers.size() > 0) {
            try {
                userDAO.addUserBatch(dynamoUsers);
            }
            catch (Exception e) {
                throw new RuntimeException("[Server Error] " + e.getMessage());
            }
        }


        if (dynamoUsers.size() > 0) {
            try {
                followsDAO.addFollowersBatch(dynamoUsers);
            }
            catch (Exception e) {
                throw new RuntimeException("[Server Error] " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        fillDatabase();
    }


}


