package edu.byu.cs.tweeter.server.dao;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.server.dynamoModel.DynamoUser;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.BatchWriteItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.BatchWriteResult;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.WriteBatch;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

public class UserDAO extends BaseDAO implements IUserDAO{

    private static final String TableName = "user";
    private DynamoUser dynamoUser;
    private DynamoDbTable<DynamoUser> userTable = enhancedClient.table(TableName, TableSchema.fromBean(DynamoUser.class));


    private String hashPassword(String password) {
        //Need to hash the password
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(password.getBytes());
            byte[] bytes = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte aByte : bytes) {
                sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "Failed to hash";
    }




    @Override
    public User getUserAuthenticate(String alias, String password) {

        Key key = Key.builder()
                .partitionValue(alias)
                .build();

        QueryEnhancedRequest queryEnhancedRequest = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(key))
                .build();

        Iterator<DynamoUser> results = userTable.query(queryEnhancedRequest).items().iterator();
        if (results.hasNext()) {
            dynamoUser = results.next();
        }

        //Check if the user exists
        if (dynamoUser == null) {
            return null;
        }

        //Hash the input password
        String hashedPassword = hashPassword(password);

        //Check the password
        if (dynamoUser.getPassword().equals(hashedPassword)) {
            User user = new User(dynamoUser.getFirstName(), dynamoUser.getLastName(), dynamoUser.getAlias(), dynamoUser.getImageUrl());
            return user;
        }
        else {
            throw new RuntimeException("Wrong password");
        }
    }

    @Override
    public User getUser(String token, String alias) {

        //Check authToken
        if (!isValidAuthToken(token)) {
            throw new RuntimeException("The authToken is invalid");
        }

        Key key = Key.builder()
                .partitionValue(alias)
                .build();

        QueryEnhancedRequest queryEnhancedRequest = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(key))
                .build();

        Iterator<DynamoUser> results = userTable.query(queryEnhancedRequest).items().iterator();



        if (results.hasNext()) {
            dynamoUser = results.next();
        }
        else {
            throw new RuntimeException("User is null");
        }

        User user = new User(dynamoUser.getFirstName(), dynamoUser.getLastName(), dynamoUser.getAlias(), dynamoUser.getImageUrl());
        return user;
    }

    @Override
    public DynamoUser getUserForCount(String token, String alias) {

        //Check authToken
        if (!isValidAuthToken(token)) {
            return null;
        }

        Key key = Key.builder()
                .partitionValue(alias)
                .build();

        QueryEnhancedRequest queryEnhancedRequest = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(key))
                .build();

        Iterator<DynamoUser> results = userTable.query(queryEnhancedRequest).items().iterator();
        if (results.hasNext()) {
            return results.next();
        }
        else {
            return null;
        }
    }


    @Override
    public void addUser(String firstName, String lastName, String alias, String password, String image) {

        Key key = Key.builder()
                .partitionValue(alias)
                .build();

        QueryEnhancedRequest queryEnhancedRequest = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(key))
                .build();

        Iterator<DynamoUser> results = userTable.query(queryEnhancedRequest).items().iterator();
        //check to see if user already exists
        if (results.hasNext()) {
            throw new RuntimeException("User alias already taken");
        }


        //Create and add authToken for person
        DynamoUser dynamoUser = new DynamoUser();
        dynamoUser.setFirstName(firstName);
        dynamoUser.setLastName(lastName);
        dynamoUser.setAlias(alias);
        //Hash the password
        String hashedPassword = hashPassword(password);
        dynamoUser.setPassword(hashedPassword);
        dynamoUser.setImageUrl(image);
        dynamoUser.setNumFollowees(0);
        dynamoUser.setNumFollowees(0);
        userTable.putItem(dynamoUser);

        //Put the image in an s3 bucket

    }


    @Override
    public int getUserNumFollowers(String token, String alias) {

        //Check authToken
        if (!isValidAuthToken(token)) {
            return 0;
        }

        Key key = Key.builder()
                .partitionValue(alias)
                .build();

        QueryEnhancedRequest queryEnhancedRequest = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(key))
                .build();

        Iterator<DynamoUser> results = userTable.query(queryEnhancedRequest).items().iterator();
        if (results.hasNext()) {
            return results.next().getNumFollowers();
        }
        else {
            return 0;
        }
    }

    @Override
    public int getUserNumFollowees(String token, String alias) {

        //Check authToken
        if (!isValidAuthToken(token)) {
            return 0;
        }

        Key key = Key.builder()
                .partitionValue(alias)
                .build();

        QueryEnhancedRequest queryEnhancedRequest = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(key))
                .build();

        Iterator<DynamoUser> results = userTable.query(queryEnhancedRequest).items().iterator();
        if (results.hasNext()) {
            return results.next().getNumFollowees();
        }
        else {
            return 0;
        }
    }


    @Override
    public boolean logout(String token) {
        //Check authToken
        if (isValidAuthToken(token)) {
            return true;
        }
        throw new RuntimeException("Invalid Authtoken");
    }

    @Override
    public void addUserBatch(List<DynamoUser> dynamoUsers) {

        List<DynamoUser> batchToWrite = new ArrayList<>();
        for (DynamoUser currUser : dynamoUsers) {
            batchToWrite.add(currUser);

            if (batchToWrite.size() == 25) {
                // package this batch up and send to DynamoDB.
                writeChunkOfDynamoUser(batchToWrite);
                // then clear the batchToWrite and start over with new batch
                batchToWrite = new ArrayList<>();
            }
        }

        // write any remaining
        if (batchToWrite.size() > 0) {
            // package this batch up and send to DynamoDB.
            writeChunkOfDynamoUser(batchToWrite);
        }
    }



    private void writeChunkOfDynamoUser(List<DynamoUser> dynamoUsers) {
        if(dynamoUsers.size() > 25) {
            throw new RuntimeException("Too many users to write");
        }


        WriteBatch.Builder<DynamoUser> writeBuilder = WriteBatch.builder(DynamoUser.class).mappedTableResource(userTable);
        for (DynamoUser item : dynamoUsers) {
            writeBuilder.addPutItem(builder -> builder.item(item));
        }
        BatchWriteItemEnhancedRequest batchWriteItemEnhancedRequest = BatchWriteItemEnhancedRequest.builder()
                .writeBatches(writeBuilder.build()).build();

        try {
            BatchWriteResult result = enhancedClient.batchWriteItem(batchWriteItemEnhancedRequest);

            // just hammer dynamodb again with anything that didn't get written this time
            if (result.unprocessedPutItemsForTable(userTable).size() > 0) {
                writeChunkOfDynamoUser(result.unprocessedPutItemsForTable(userTable));
            }

        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
