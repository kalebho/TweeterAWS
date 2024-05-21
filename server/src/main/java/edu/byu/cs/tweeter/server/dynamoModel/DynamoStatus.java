package edu.byu.cs.tweeter.server.dynamoModel;

import java.util.List;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@DynamoDbBean
public class DynamoStatus {
    /**
     * Text for the status.
     */
    public String post;
    /**
     * User who sent the status.
     */
    public String alias;
    public String firstName;
    public String lastName;
    public String image;
    /**
     * String representation of the date/time at which the status was sent.
     */
    public Long timestamp;
    /**
     * URLs contained in the post text.
     */
    public List<String> urls;
    /**
     * User mentions contained in the post text.
     */
    public List<String> mentions;

    public DynamoStatus() {
    }

    public DynamoStatus(String post, String alias, String firstName, String lastName, String image, Long timestamp, List<String> urls, List<String> mentions) {
        this.post = post;
        this.alias = alias;
        this.firstName = firstName;
        this.lastName = lastName;
        this.image = image;
        this.timestamp = timestamp;
        this.urls = urls;
        this.mentions = mentions;
    }

    public void setPost(String post) {
        this.post = post;
    }

    @DynamoDbPartitionKey
    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }

    public void setMentions(List<String> mentions) {
        this.mentions = mentions;
    }

    @DynamoDbSortKey
    public Long getTimestamp() {
        return timestamp;
    }

    public String getPost() {
        return post;
    }

    public List<String> getUrls() {
        return urls;
    }

    public List<String> getMentions() {
        return mentions;
    }
}
