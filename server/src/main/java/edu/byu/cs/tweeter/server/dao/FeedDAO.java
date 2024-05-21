package edu.byu.cs.tweeter.server.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.server.dynamoModel.DynamoFeed;
import edu.byu.cs.tweeter.util.Pair;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.BatchWriteItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.BatchWriteResult;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.WriteBatch;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

public class FeedDAO extends BaseDAO implements IFeedDAO {

    private DynamoFeed dynamoFeed;
    private static final String FeedTableName = "feed";
    private static final String FollowerAliasAttr = "followerAlias";
    private static final String TimeAttr = "timestamp";
    static DynamoDbTable<DynamoFeed> feedTable = enhancedClient.table(FeedTableName, TableSchema.fromBean(DynamoFeed.class));


    public List<String> extractMentions(String post) {
        List<String> mentions = new ArrayList<>();

        // Define the pattern to find substrings starting with "@"
        Pattern pattern = Pattern.compile("@\\w+");

        // Create a matcher for the input string
        Matcher matcher = pattern.matcher(post);

        // Find all occurrences of the pattern
        while (matcher.find()) {
            mentions.add(matcher.group());
        }

        return mentions;
    }

    public List<String> extractUrls(String post) {
        List<String> urls = new ArrayList<>();

        // Define the pattern to find substrings starting with "@"
        Pattern pattern = Pattern.compile("https://\\S+");

        // Create a matcher for the input string
        Matcher matcher = pattern.matcher(post);

        // Find all occurrences of the pattern
        while (matcher.find()) {
            urls.add(matcher.group());
        }

        return urls;
    }




    @Override
    public Pair<List<Status>, Boolean> getFeedStatuses(String token, String targetUserAlias, int limit, Status lastStatus) {

        //Check authToken
        if (!isValidAuthToken(token)) {
            return null;
        }

        //1 query the data for the right statuses of the target user
        Key key = Key.builder()
                .partitionValue(targetUserAlias)
                .build();

        //Added
        QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(key))
                .scanIndexForward(false)
                .limit(limit);

        //Added
        if(lastStatus != null) {
            // Build up the Exclusive Start Key (telling DynamoDB where you left off reading items)
            Map<String, AttributeValue> startKey = new HashMap<>();
            startKey.put(FollowerAliasAttr, AttributeValue.builder().s(targetUserAlias).build());
            startKey.put(TimeAttr, AttributeValue.builder().n(String.valueOf(lastStatus.getTimestamp())).build());

            requestBuilder.exclusiveStartKey(startKey);
        }

        //Added
        QueryEnhancedRequest queryEnhancedRequest = requestBuilder.build();
        List<Status> allStatuses = new ArrayList<>();

        try {
            SdkIterable<Page<DynamoFeed>> sdkIterable = feedTable.query(queryEnhancedRequest);
            PageIterable<DynamoFeed> pages = PageIterable.create(sdkIterable);

            pages.stream()
                    .limit(1)
                    .forEach(page -> {
                        List<DynamoFeed> statuses = page.items();
                        statuses.forEach(dynamoFeed -> {
                            String post = dynamoFeed.getPost();
                            User user = new User(dynamoFeed.getFirstName(), dynamoFeed.getLastName(), dynamoFeed.getAlias(), dynamoFeed.getImage());
                            Status status = new Status(dynamoFeed.getPost(), user, dynamoFeed.getTimestamp(), extractUrls(post), extractMentions(post));
                            allStatuses.add(status);
                        });
                    });
        }
        catch (DynamoDbException e) {
            e.printStackTrace();
        }

//        Iterator<DynamoFeed> results = feedTable.query(queryEnhancedRequest).items().iterator();

//        while (results.hasNext()) {
//            dynamoFeed = results.next();
//            String post = dynamoFeed.getPost();
//            User user = new User(dynamoFeed.getFirstName(), dynamoFeed.getLastName(), dynamoFeed.getAlias(), dynamoFeed.getImage());
//            Status status = new Status(dynamoFeed.getPost(), user, dynamoFeed.getTimestamp(), extractUrls(post), extractMentions(post));
//            //2 in the for loop need to add the status to a list of statuses if is right keys
//            allStatuses.add(status);
//        }

//        List<Status> responseStatuses = new ArrayList<>(limit);
//        boolean hasMorePages = false;

//        if(limit > 0) {
//            if (allStatuses.size() > 0) {
//                int startingStatusIndex = getStatusStartingIndex(lastStatus, allStatuses);
//
//                for(int limitCounter = 0; startingStatusIndex < allStatuses.size() && limitCounter < limit; startingStatusIndex++, limitCounter++) {
//                    responseStatuses.add(allStatuses.get(startingStatusIndex));
//                }
//
//                hasMorePages = startingStatusIndex < allStatuses.size();
//            }
//        }

        boolean hasMorePages = (allStatuses.size() == limit);
        return new Pair<>(allStatuses, hasMorePages);

    }

//    private int getStatusStartingIndex(Status lastStatus, List<Status> allStatuses) {
//
//        int startingStatusIndex = 0;
//
//        if(lastStatus != null) {
//            // This is a paged request for something after the first page. Find the first item
//            // we should return
//            for (int i = 0; i < allStatuses.size(); i++) {
//                if(lastStatus.equals(allStatuses.get(i))) {
//                    // We found the index of the last item returned last time. Increment to get
//                    // to the first one we should return
//                    startingStatusIndex = i + 1;
//                    break;
//                }
//            }
//        }
//
//        return startingStatusIndex;
//    }

    @Override
    public void updateFeeds(Status status, List<String> followers) {

        //Need to create list of feeds to post
        List<DynamoFeed> dynamoFeeds = new ArrayList<>();
        //Need to create a dynamoFeed for each user
        for (String follower : followers) {
            DynamoFeed dynamoFeed = new DynamoFeed();
            dynamoFeed.setPost(status.getPost());
            dynamoFeed.setAlias(status.getUser().getAlias());
            dynamoFeed.setFirstName(status.getUser().getFirstName());
            dynamoFeed.setLastName(status.getUser().getLastName());
            dynamoFeed.setImage(status.getUser().getImageUrl());
            dynamoFeed.setTimestamp(status.getTimestamp());
            dynamoFeed.setUrls(status.getUrls());
            dynamoFeed.setMentions(status.getMentions());
            dynamoFeed.setFollowerAlias(follower);
            dynamoFeeds.add(dynamoFeed);
        }
        addFeedBatch(dynamoFeeds);

    }

    private void addFeedBatch(List<DynamoFeed> dynamoFeeds) {

        List<DynamoFeed> batchToWrite = new ArrayList<>();
        for (DynamoFeed currFeed : dynamoFeeds) {
            batchToWrite.add(currFeed);

            if (batchToWrite.size() == 25) {
                // package this batch up and send to DynamoDB.
                writeChunkOfDynamoFeed(batchToWrite);
                // then clear the batchToWrite and start over with new batch
                batchToWrite = new ArrayList<>();
            }
        }

        // write any remaining
        if (batchToWrite.size() > 0) {
            // package this batch up and send to DynamoDB.
            writeChunkOfDynamoFeed(batchToWrite);
        }
    }

    private void writeChunkOfDynamoFeed(List<DynamoFeed> dynamoFeeds) {
        if(dynamoFeeds.size() > 25) {
            throw new RuntimeException("Too many feeds to write");
        }


        WriteBatch.Builder<DynamoFeed> writeBuilder = WriteBatch.builder(DynamoFeed.class).mappedTableResource(feedTable);
        for (DynamoFeed item : dynamoFeeds) {
            writeBuilder.addPutItem(builder -> builder.item(item));
        }
        BatchWriteItemEnhancedRequest batchWriteItemEnhancedRequest = BatchWriteItemEnhancedRequest.builder()
                .writeBatches(writeBuilder.build()).build();

        try {
            BatchWriteResult result = enhancedClient.batchWriteItem(batchWriteItemEnhancedRequest);

            // just hammer dynamodb again with anything that didn't get written this time
            if (result.unprocessedPutItemsForTable(feedTable).size() > 0) {
                writeChunkOfDynamoFeed(result.unprocessedPutItemsForTable(feedTable));
            }
            System.out.println("System wrote to table!!!");

        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }



}
