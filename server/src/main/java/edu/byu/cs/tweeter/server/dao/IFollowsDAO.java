package edu.byu.cs.tweeter.server.dao;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.server.dynamoModel.DynamoUser;
import edu.byu.cs.tweeter.util.Pair;

public interface IFollowsDAO {
    Pair<List<User>, Boolean> getFollowers(String token, String alias, int limit, String lastFollowerAlias);
    Pair<List<String>, Boolean> getFollowersALias(String alias, int limit, String lastFollowerAlias);
    Pair<List<User>, Boolean> getFollowees(String token, String alias, int limit, String lastFolloweeAlias);
    int getFollowersCount(String token, String alias, DynamoUser targetUser);
    int getFolloweesCount(String token, String alias, DynamoUser targetUser);
    void follow(String token, String followeeAlias, String targetAlias, User targetUser, User followee, int numFollowee, int numFollower);
    void unfollow(String token, String followeeAlias, String targetAlias, User targetUser, User followee, int numFollowee, int numFollower);
    boolean isFollow(String token, String targetAlias, String followeeAlias);
    void addFollowersBatch(List<DynamoUser> dynamoUsers);
}
