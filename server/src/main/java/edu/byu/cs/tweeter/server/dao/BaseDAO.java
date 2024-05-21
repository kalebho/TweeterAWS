package edu.byu.cs.tweeter.server.dao;

import java.util.Iterator;

import edu.byu.cs.tweeter.server.dynamoModel.DynamoAuthToken;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public abstract class BaseDAO {

    // DynamoDB client
    private static DynamoDbClient dynamoDbClient = DynamoDbClient.builder()
            .region(Region.US_EAST_1)
            .build();

    protected static DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
            .dynamoDbClient(dynamoDbClient)
            .build();

    public static boolean isValidAuthToken(String token) {
        DynamoAuthToken dynamoAuthToken = null;
        //Find authToken with a query
        //make sure expiration is not bigger than current time
        //if it isnt then return true

        DynamoDbTable<DynamoAuthToken> table = enhancedClient.table("authToken", TableSchema.fromBean(DynamoAuthToken.class));
        Key key = Key.builder()
                .partitionValue(token)
                .build();

        QueryEnhancedRequest queryEnhancedRequest = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(key))
                .build();

        Iterator<DynamoAuthToken> results = table.query(queryEnhancedRequest).items().iterator();
        //If the result of the query is empty then return an empty list of statuses
        if (results.hasNext()) {
            dynamoAuthToken = results.next();
            if (dynamoAuthToken.getExpiration() < System.currentTimeMillis()) {
                return false;
            }
            else {
                return true;
            }
        }
        return false;
    }
}
