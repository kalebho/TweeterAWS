package edu.byu.cs.tweeter.server.dao;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.server.dynamoModel.DynamoUser;

public interface IUserDAO {
    User getUserAuthenticate(String alias, String password);
    User getUser(String token, String alias);
    DynamoUser getUserForCount(String token, String alias);
    int getUserNumFollowers(String token, String alias);
    int getUserNumFollowees(String token, String alias);
    void addUser(String firstName, String lastName, String alias, String password, String image);
    boolean logout(String token);
    void addUserBatch(List<DynamoUser> dynamoUsers);
}
