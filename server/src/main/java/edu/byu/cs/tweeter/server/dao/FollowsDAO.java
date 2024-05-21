package edu.byu.cs.tweeter.server.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.server.dynamoModel.DynamoFollow;
import edu.byu.cs.tweeter.server.dynamoModel.DynamoUser;
import edu.byu.cs.tweeter.util.Pair;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.BatchWriteItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.BatchWriteResult;
import software.amazon.awssdk.enhanced.dynamodb.model.DeleteItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.WriteBatch;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

/**
 * A DAO for accessing 'following' data from the database.
 */
public class FollowsDAO extends BaseDAO implements IFollowsDAO {

    private static final String FollowsTable = "follows";
    private static final String UserTable = "user";
    public static final String IndexName = "follows_index";
    private final DynamoDbTable<DynamoFollow> followTable = enhancedClient.table(FollowsTable, TableSchema.fromBean(DynamoFollow.class));
    private final DynamoDbIndex<DynamoFollow> secondaryIndex = followTable.index(IndexName);
    private static final String FollowerAttr = "follower_handle";
    private static final String FolloweeAttr = "followee_handle";
    //UserDAO to help with getting the follow counts
    private static final DynamoDAOFactory factory = new DynamoDAOFactory();
    private static final IUserDAO userDAO = factory.getUserDAO();
    //AuthDAO to help getting the user in the follow and unfollow actions


    private static boolean isNonEmptyString(String value) {
        return (value != null && value.length() > 0);
    }


    /**
     * Gets the users from the database that the user specified in the request is following. Uses
     * information in the request object to limit the number of followees returned and to return the
     * next set of followees after any that were returned in a previous request. The current
     * implementation returns generated data and doesn't actually access a database.
     *
     * @param alias the alias of the user whose followees are to be returned
     * @param limit the number of followees to be returned in one page
     * @param lastFolloweeAlias the alias of the last followee in the previously retrieved page or
     *                          null if there was no previous request.
     * @return the followees.
     */
    @Override
    public Pair<List<User>, Boolean> getFollowees(String token, String alias, int limit, String lastFolloweeAlias) {

        //Check authToken
        if (!isValidAuthToken(token)) {
            return null;
        }

        //1 query the data for the right statuses of the target user
        Key key = Key.builder()
                .partitionValue(alias)
                .build();

        //Added
        QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(key))
                .limit(limit);

        //Added
        if(isNonEmptyString(lastFolloweeAlias)) {
            // Build up the Exclusive Start Key (telling DynamoDB where you left off reading items)
            Map<String, AttributeValue> startKey = new HashMap<>();
            startKey.put(FollowerAttr, AttributeValue.builder().s(alias).build());
            startKey.put(FolloweeAttr, AttributeValue.builder().s(lastFolloweeAlias).build());

            requestBuilder.exclusiveStartKey(startKey);
        }

        //Added
        QueryEnhancedRequest queryEnhancedRequest = requestBuilder.build();
        List<User> allFollowees = new ArrayList<>();

        Iterator<DynamoFollow> results = followTable.query(queryEnhancedRequest).items().iterator();

        while (results.hasNext()) {
            DynamoFollow dynamoFollow = results.next();
//            String post = dynamoFeed.getPost();
            User user = new User(dynamoFollow.getFolloweeFirstName(), dynamoFollow.getFolloweeLastName(), dynamoFollow.getFollowee_handle(), dynamoFollow.getFolloweeImage());
//            Status status = new Status(dynamoFeed.getPost(), user, dynamoFeed.getTimestamp(), extractUrls(post), extractMentions(post));
//            //2 in the for loop need to add the status to a list of statuses if is right keys
            allFollowees.add(user);
        }


//        try {
//            SdkIterable<Page<DynamoFollow>> sdkIterable = followTable.query(queryEnhancedRequest);
//            PageIterable<DynamoFollow> pages = PageIterable.create(sdkIterable);
//
//            pages.stream()
//                    .limit(1)
//                    .forEach(page -> {
//                        List<DynamoFollow> follows = page.items();
//                        follows.forEach(follow -> allFollowees.add(userDAO.getUser(token, follow.getFollowee_handle())));
//                    });
//        }catch (DynamoDbException e) {
//            e.printStackTrace();
//        }

        boolean hasMorePages = (allFollowees.size() == limit);

        return new Pair<>(allFollowees, hasMorePages);
    }


    @Override
    public Pair<List<User>, Boolean> getFollowers(String token, String alias, int limit, String lastFollowerAlias) {

        //Check authToken
        if (!isValidAuthToken(token)) {
            return null;
        }

        //1 query the data for the right statuses of the target user
        Key key = Key.builder()
                .partitionValue(alias)
                .build();

        //Added
        QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(key))
                .limit(limit);

        //Added
        if(isNonEmptyString(lastFollowerAlias)) {
            // Build up the Exclusive Start Key (telling DynamoDB where you left off reading items)
            Map<String, AttributeValue> startKey = new HashMap<>();
            startKey.put(FolloweeAttr, AttributeValue.builder().s(alias).build());
            startKey.put(FollowerAttr, AttributeValue.builder().s(lastFollowerAlias).build());

            requestBuilder.exclusiveStartKey(startKey);
        }

        //Added
        QueryEnhancedRequest queryEnhancedRequest = requestBuilder.build();
        List<User> allFollowers = new ArrayList<>();


        try {
            SdkIterable<Page<DynamoFollow>> sdkIterable = secondaryIndex.query(queryEnhancedRequest);
            PageIterable<DynamoFollow> pages = PageIterable.create(sdkIterable);

            pages.stream()
                    .limit(1)
                    .forEach(page -> {
                        List<DynamoFollow> follows = page.items();
                            follows.forEach(follow -> allFollowers.add(userDAO.getUser(token, follow.getFollower_handle())));
        });

        }
        catch (DynamoDbException e) {
            e.printStackTrace();
        }

        boolean hasMorePages = (allFollowers.size() == limit);

        return new Pair<>(allFollowers, hasMorePages);
    }

    @Override
    public Pair<List<String>, Boolean> getFollowersALias(String alias, int limit, String lastFollowerAlias) {

        //1 query the data for the right statuses of the target user
        Key key = Key.builder()
                .partitionValue(alias)
                .build();

        //Added
        QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(key))
                .limit(limit);

        //Added
        if(isNonEmptyString(lastFollowerAlias)) {
            // Build up the Exclusive Start Key (telling DynamoDB where you left off reading items)
            Map<String, AttributeValue> startKey = new HashMap<>();
            startKey.put(FolloweeAttr, AttributeValue.builder().s(alias).build());
            startKey.put(FollowerAttr, AttributeValue.builder().s(lastFollowerAlias).build());

            requestBuilder.exclusiveStartKey(startKey);
        }

        //Added
        QueryEnhancedRequest queryEnhancedRequest = requestBuilder.build();
        List<String> allFollowersAlias = new ArrayList<>();


        try {
            SdkIterable<Page<DynamoFollow>> sdkIterable = secondaryIndex.query(queryEnhancedRequest);
            PageIterable<DynamoFollow> pages = PageIterable.create(sdkIterable);

            pages.stream()
                    .limit(1)
                    .forEach(page -> {
                        List<DynamoFollow> follows = page.items();
                        follows.forEach(follow -> allFollowersAlias.add(follow.getFollower_handle()));
                    });

        }
        catch (DynamoDbException e) {
            e.printStackTrace();
        }

        boolean hasMorePages = (allFollowersAlias.size() == limit);

        return new Pair<>(allFollowersAlias, hasMorePages);
    }


    @Override
    public int getFollowersCount(String token, String alias, DynamoUser targetUser) {
        //Check authToken
        if (!isValidAuthToken(token)) {
            throw new RuntimeException("Invalid authToken");
        }

        return targetUser.getNumFollowers();
    }

    @Override
    public int getFolloweesCount(String token, String alias, DynamoUser targetUser) {
        //Check authToken
        if (!isValidAuthToken(token)) {
            throw new RuntimeException("Invalid authToken");
        }


        return targetUser.getNumFollowees();
    }

    @Override
    public void follow(String token, String followeeAlias, String targetAlias, User targetUser, User followee, int numFollowee, int numFollower) {
        //need to update the target users followees count by 1
        //need to update the person Im following (followeeAlias) followers count by 1
        //need to add the follow to the follows table

        //Check authToken
        if (!isValidAuthToken(token)) {
            throw new RuntimeException("Invalid authToken");
        }

        DynamoDbTable<DynamoFollow> followsTable = enhancedClient.table(FollowsTable, TableSchema.fromBean(DynamoFollow.class));


        //Create the DynamoFollows
        DynamoFollow dynamoFollow = new DynamoFollow();
        dynamoFollow.setFollower_handle(targetUser.getAlias());
        dynamoFollow.setFollowee_handle(followeeAlias);
        dynamoFollow.setFollowerFirstName(targetUser.getFirstName());
        dynamoFollow.setFollowerLastName(targetUser.getLastName());
        dynamoFollow.setFolloweeFirstName(followee.getFirstName());
        dynamoFollow.setFolloweeLastName(followee.getLastName());
        dynamoFollow.setFollowerImage(targetUser.getImageUrl());
        dynamoFollow.setFolloweeImage(followee.getImageUrl());
        followsTable.putItem(dynamoFollow);


        DynamoDbTable<DynamoUser> userTable = enhancedClient.table(UserTable, TableSchema.fromBean(DynamoUser.class));
        Key targetUserKey = Key.builder()
                .partitionValue(targetAlias)
                .build();

        Key followeeUserKey = Key.builder()
                .partitionValue(followeeAlias)
                .build();

        //Update the target user followee count
        DynamoUser dynamoTargetUser = userTable.getItem(r->r.key(targetUserKey));
        dynamoTargetUser.setNumFollowees(numFollowee + 1);

        userTable.updateItem(dynamoTargetUser);

        //Update the followee user follower count
        DynamoUser dynamoFolloweeUser = userTable.getItem(r->r.key(followeeUserKey));
        dynamoFolloweeUser.setNumFollowers(numFollower + 1);
        userTable.updateItem(dynamoFolloweeUser);
    }

    @Override
    public void unfollow(String token, String followeeAlias, String targetAlias, User targetUser, User followee, int numFollowee, int numFollower) {
        //need to update the target users followees count by 1
        //need to update the person Im following (followeeAlias) followers count by 1
        //need to delete the follow from the follows table

        //Check authToken
        if (!isValidAuthToken(token)) {
            throw new RuntimeException("Invalid authToken");
        }

        DynamoDbTable<DynamoFollow> followsTable = enhancedClient.table(FollowsTable, TableSchema.fromBean(DynamoFollow.class));
        Key key = Key.builder()
                .partitionValue(targetAlias).sortValue(followeeAlias)
                .build();

        DeleteItemEnhancedRequest deleteItemRequest = DeleteItemEnhancedRequest.builder()
                .key(key)
                .build();

        //Delete the item
        followsTable.deleteItem(deleteItemRequest);

        //Update the numFollowee and numFollower for each user
        DynamoDbTable<DynamoUser> userTable = enhancedClient.table(UserTable, TableSchema.fromBean(DynamoUser.class));
        Key targetUserKey = Key.builder()
                .partitionValue(targetAlias)
                .build();

        Key followeeUserKey = Key.builder()
                .partitionValue(followeeAlias)
                .build();

        //Update the target user followee count
        DynamoUser dynamoTargetUser = userTable.getItem(r->r.key(targetUserKey));
        dynamoTargetUser.setNumFollowees(numFollowee - 1);
        userTable.updateItem(dynamoTargetUser);

        //Update the followee user follower count
        DynamoUser dynamoFolloweeUser = userTable.getItem(r->r.key(followeeUserKey));
        dynamoFolloweeUser.setNumFollowers(numFollower - 1);
        userTable.updateItem(dynamoFolloweeUser);
    }

    @Override
    public boolean isFollow(String token, String targetAlias, String followeeAlias) {

        //Check authToken
        if (!isValidAuthToken(token)) {
            throw new RuntimeException("Invalid authToken");
        }

        //Get the follow from the follows table
        DynamoDbTable<DynamoFollow> followsTable = enhancedClient.table(FollowsTable, TableSchema.fromBean(DynamoFollow.class));
        Key key = Key.builder()
                .partitionValue(targetAlias).sortValue(followeeAlias)
                .build();

        QueryEnhancedRequest queryEnhancedRequest = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(key))
                .build();

        Iterator<DynamoFollow> results = followsTable.query(queryEnhancedRequest).items().iterator();
        if (results.hasNext()) {
            return true;
        }
        else {
            return false;
        }
    }

    @Override
    public void addFollowersBatch(List<DynamoUser> dynamoUsers) {

        List<DynamoFollow> batchToWrite = new ArrayList<>();

        //Loop through the users and create dynamoFollows to put into table with all the same followee
        for (DynamoUser currUser : dynamoUsers) {
            DynamoFollow dynamoFollow = new DynamoFollow();
            dynamoFollow.setFollower_handle(currUser.getAlias());
            dynamoFollow.setFollowee_handle("@headUser");
            dynamoFollow.setFollowerFirstName(currUser.getFirstName());
            dynamoFollow.setFollowerLastName(currUser.getLastName());
            dynamoFollow.setFolloweeFirstName("head");
            dynamoFollow.setFolloweeLastName("user");
            dynamoFollow.setFollowerImage(null);
            dynamoFollow.setFolloweeImage(null);

            batchToWrite.add(dynamoFollow);

            if (batchToWrite.size() == 25) {
                // package this batch up and send to DynamoDB.
                writeChunkOfDynamoUser(batchToWrite);
                // then clear the batchToWrite and start over with new batch
                batchToWrite = new ArrayList<>();
            }
        }
    }

    private void writeChunkOfDynamoUser(List<DynamoFollow> dynamoFollows) {
        if(dynamoFollows.size() > 25) {
            throw new RuntimeException("Too many users to write");
        }

        DynamoDbTable<DynamoFollow> followsTable = enhancedClient.table(FollowsTable, TableSchema.fromBean(DynamoFollow.class));


        WriteBatch.Builder<DynamoFollow> writeBuilder = WriteBatch.builder(DynamoFollow.class).mappedTableResource(followsTable);
        for (DynamoFollow item : dynamoFollows) {
            writeBuilder.addPutItem(builder -> builder.item(item));
        }
        BatchWriteItemEnhancedRequest batchWriteItemEnhancedRequest = BatchWriteItemEnhancedRequest.builder()
                .writeBatches(writeBuilder.build()).build();

        try {
            BatchWriteResult result = enhancedClient.batchWriteItem(batchWriteItemEnhancedRequest);

            // just hammer dynamodb again with anything that didn't get written this time
            if (result.unprocessedPutItemsForTable(followsTable).size() > 0) {
                writeChunkOfDynamoUser(result.unprocessedPutItemsForTable(followsTable));
            }

        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }


}
