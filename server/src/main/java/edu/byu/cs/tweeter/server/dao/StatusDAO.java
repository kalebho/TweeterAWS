package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.PostUpdateFeedMessageRequest;
import edu.byu.cs.tweeter.server.dynamoModel.DynamoStatus;
import edu.byu.cs.tweeter.util.Pair;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

/**
 * A DAO for accessing 'following' data from the database.
 */
public class StatusDAO extends BaseDAO implements IStatusDAO{

    private static final String StatusTableName = "status";
    private static final String FeedTableName = "feed";
    private static final String AliasAttr = "alias";
    private static final String TimeAttr = "timestamp";
    private DynamoStatus dynamoStatus;
    private DynamoDbTable<DynamoStatus> statusTable = enhancedClient.table(StatusTableName, TableSchema.fromBean(DynamoStatus.class));
    private static final DynamoDAOFactory factory = new DynamoDAOFactory();
    private static final IUserDAO userDAO = factory.getUserDAO();



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
    public Pair<List<Status>, Boolean> getStoryStatuses(String token, String alias, int limit, Status lastStatus) {

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
                .scanIndexForward(false)
                .limit(limit);


        //Added
        if(lastStatus != null) {
            // Build up the Exclusive Start Key (telling DynamoDB where you left off reading items)
            Map<String, AttributeValue> startKey = new HashMap<>();
            startKey.put(AliasAttr, AttributeValue.builder().s(alias).build());
            startKey.put(TimeAttr, AttributeValue.builder().n(String.valueOf(lastStatus.getTimestamp())).build());

            requestBuilder.exclusiveStartKey(startKey);
        }

        //Added
        QueryEnhancedRequest queryEnhancedRequest = requestBuilder.build();
        List<Status> allStatuses = new ArrayList<>();

        try {
            SdkIterable<Page<DynamoStatus>> sdkIterable = statusTable.query(queryEnhancedRequest);
            PageIterable<DynamoStatus> pages = PageIterable.create(sdkIterable);

            pages.stream()
                    .limit(1)
                    .forEach(page -> {
                        List<DynamoStatus> statuses = page.items();
                        statuses.forEach(dynamoStatus -> {
                            String post = dynamoStatus.getPost();
                            User user = new User(dynamoStatus.getFirstName(), dynamoStatus.getLastName(), dynamoStatus.getAlias(), dynamoStatus.getImage());
                            Status status = new Status(dynamoStatus.getPost(), user, dynamoStatus.getTimestamp(), extractUrls(post), extractMentions(post));
                            allStatuses.add(status);
                        });
                    });
        }
        catch (DynamoDbException e) {
            e.printStackTrace();
        }

        boolean hasMorePages = (allStatuses.size() == limit);

        return new Pair<>(allStatuses, hasMorePages);
    }



    @Override
    public void addStatus(String token, Status status) {

        //Check authToken
        if (!isValidAuthToken(token)) {
            throw new RuntimeException("The authToken is invalid");
        }

        //Add the status to the status table
        DynamoStatus dynamoStatus = new DynamoStatus();
        dynamoStatus.setPost(status.getPost());
        dynamoStatus.setFirstName(status.getUser().getFirstName());
        dynamoStatus.setLastName(status.getUser().getLastName());
        dynamoStatus.setAlias(status.getUser().getAlias());
        dynamoStatus.setImage(status.getUser().getImageUrl());
        dynamoStatus.setTimestamp(status.getTimestamp());
        dynamoStatus.setMentions(extractMentions(status.getPost()));
        dynamoStatus.setUrls(extractUrls(status.getPost()));
        statusTable.putItem(dynamoStatus);

        PostUpdateFeedMessageRequest request = new PostUpdateFeedMessageRequest();
        request.setStatus(status);
        request.setToken(token);

        //Create message (remember to serialize to string in order to send using sqs code)
        String messageBody = JsonSerializerServer.serialize(request);
        String queueUrl = "https://sqs.us-east-1.amazonaws.com/428946460517/PostStatusQueue";

        //Send the message to the queue
        SendMessageRequest send_msg_request = new SendMessageRequest()
                .withQueueUrl(queueUrl)
                .withMessageBody(messageBody);

        AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();
        SendMessageResult send_msg_result = sqs.sendMessage(send_msg_request);

        if (send_msg_result.getMessageId() != null) {
            System.out.println("Sent to queue 1");
        }

        //return a response back to the client that it was posted
    }


}
