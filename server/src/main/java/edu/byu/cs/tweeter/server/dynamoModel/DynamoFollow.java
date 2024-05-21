package edu.byu.cs.tweeter.server.dynamoModel;

import edu.byu.cs.tweeter.server.dao.FollowsDAO;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondarySortKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@DynamoDbBean
public class DynamoFollow {
    private String follower_handle;
    private String followee_handle;
    private String followerFirstName;
    private String followerLastName;
    private String followeeFirstName;
    private String followeeLastName;
    private String followerImage;
    private String followeeImage;

    public String getFollowerImage() {
        return followerImage;
    }

    public void setFollowerImage(String followerImage) {
        this.followerImage = followerImage;
    }

    public String getFolloweeImage() {
        return followeeImage;
    }

    public void setFolloweeImage(String followeeImage) {
        this.followeeImage = followeeImage;
    }

    public String getFollowerLastName() {
        return followerLastName;
    }

    public void setFollowerLastName(String followerLastName) {
        this.followerLastName = followerLastName;
    }

    public String getFolloweeLastName() {
        return followeeLastName;
    }

    public void setFolloweeLastName(String followeeLastName) {
        this.followeeLastName = followeeLastName;
    }

    @DynamoDbPartitionKey
    @DynamoDbSecondarySortKey(indexNames = FollowsDAO.IndexName)
    public String getFollower_handle() {
        return follower_handle;
    }

    public void setFollower_handle(String followerAlias) {
        this.follower_handle = followerAlias;
    }

    @DynamoDbSortKey
    @DynamoDbSecondaryPartitionKey(indexNames = FollowsDAO.IndexName)
    public String getFollowee_handle() {
        return followee_handle;
    }

    public void setFollowee_handle(String followeeAlias) {
        this.followee_handle = followeeAlias;
    }

    public String getFollowerFirstName() {
        return followerFirstName;
    }

    public void setFollowerFirstName(String followerFirstName) {
        this.followerFirstName = followerFirstName;
    }

    public String getFolloweeFirstName() {
        return followeeFirstName;
    }

    public void setFolloweeFirstName(String followeeFirstName) {
        this.followeeFirstName = followeeFirstName;
    }

    @Override
    public String toString() {
        return "Follow{" +
                "followerAlias='" + follower_handle + '\'' +
                ", followeeAlias='" + followee_handle + '\'' +
                ", followerName=" + followerFirstName + '\'' +
                ", followeeName=" + followeeFirstName +
                '}';
    }
}
