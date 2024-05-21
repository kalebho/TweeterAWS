package edu.byu.cs.tweeter.server.dao;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Iterator;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.server.dynamoModel.DynamoAuthToken;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;

public class AuthTokenDAO extends BaseDAO implements IAuthTokenDAO{

    private static final String TableName = "authToken";
    private static final SecureRandom secureRandom = new SecureRandom(); //threadsafe
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder(); //threadsafe

    @Override
    public AuthToken addAuthToken(String alias) {

        //Create random token
        byte[] randomBytes = new byte[24];
        secureRandom.nextBytes(randomBytes);
        String randomToken = base64Encoder.encodeToString(randomBytes);

        DynamoDbTable<DynamoAuthToken> table = enhancedClient.table(TableName, TableSchema.fromBean(DynamoAuthToken.class));
        Key key = Key.builder()
                .partitionValue(randomToken)
                .build();



        //Create and add authToken for person
        DynamoAuthToken dynamoAuthToken = new DynamoAuthToken();
        dynamoAuthToken.setAlias(alias);
        dynamoAuthToken.setToken(randomToken);
        dynamoAuthToken.setTimestamp(System.currentTimeMillis());
        dynamoAuthToken.setExpiration(dynamoAuthToken.getTimestamp());
        table.putItem(dynamoAuthToken);

        AuthToken authToken = new AuthToken(dynamoAuthToken.getToken(), dynamoAuthToken.getTimestamp());
        return authToken;
    }

    @Override
    public String getAuthToken(String token) {

        DynamoDbTable<DynamoAuthToken> table = enhancedClient.table(TableName, TableSchema.fromBean(DynamoAuthToken.class));
        Key key = Key.builder()
                .partitionValue(token)
                .build();

        QueryEnhancedRequest queryEnhancedRequest = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(key))
                .build();

        Iterator<DynamoAuthToken> results = table.query(queryEnhancedRequest).items().iterator();
        if (results.hasNext()) {
            DynamoAuthToken dynamoAuthToken = results.next();
            return dynamoAuthToken.getAlias();
        }
        return null;
    }

}
