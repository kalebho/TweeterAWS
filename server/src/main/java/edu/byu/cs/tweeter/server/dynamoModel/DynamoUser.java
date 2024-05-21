package edu.byu.cs.tweeter.server.dynamoModel;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@DynamoDbBean
public class DynamoUser {

    private String firstName;
    private String lastName;
    private String alias;
    private String password;
    private String imageUrl;
    private int numFollowers;
    private int numFollowees;

    /**
     * Allows construction of the object from Json. Private so it won't be called by other code.
     */
    public DynamoUser() {}

//    public DynamoUser(String firstName, String lastName, String imageURL) {
//        this(firstName, lastName, String.format("@%s%s", firstName, lastName), imageURL);
//    }

    public DynamoUser(String firstName, String lastName, String alias, String password, String imageURL, int numFollowees, int numFollowers) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.alias = alias;
        this.password = password;
        this.imageUrl = imageURL;
        this.numFollowees = numFollowees;
        this.numFollowers = numFollowers;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getName() {
        return String.format("%s %s", firstName, lastName);
    }

    @DynamoDbPartitionKey
    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getNumFollowers() {
        return numFollowers;
    }

    public void setNumFollowers(int numFollowers) {
        this.numFollowers = numFollowers;
    }

    public int getNumFollowees() {
        return numFollowees;
    }

    public void setNumFollowees(int numFollowees) {
        this.numFollowees = numFollowees;
    }
}
