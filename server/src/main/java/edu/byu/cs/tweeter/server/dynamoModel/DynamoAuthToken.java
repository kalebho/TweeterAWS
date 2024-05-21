package edu.byu.cs.tweeter.server.dynamoModel;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@DynamoDbBean
public class DynamoAuthToken {

    public String token;
    public long timestamp;

    public String alias;
    public long expiration;

    public DynamoAuthToken() {
    }


    @DynamoDbPartitionKey
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }


    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }


    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public long getExpiration() {
        return expiration;
    }

    public void setExpiration(long timestamp) {
        //Create expiration dat from timestamp
        Instant instant = Instant.ofEpochMilli(timestamp);
        Instant expirationInstant = instant.plus(1, ChronoUnit.DAYS);
        expiration = expirationInstant.toEpochMilli();
    }
}
