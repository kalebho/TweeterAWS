package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import java.io.ByteArrayInputStream;
import java.util.Base64;
import java.util.Iterator;

import edu.byu.cs.tweeter.server.dynamoModel.DynamoUser;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;

public class S3ImageDAO extends BaseDAO implements ImageDAO{

    private static final String Table = "user";
    @Override
    public String uploadImage(String image, String alias) {

        DynamoDbTable<DynamoUser> table = enhancedClient.table(Table, TableSchema.fromBean(DynamoUser.class));
        Key key = Key.builder()
                .partitionValue(alias)
                .build();

        QueryEnhancedRequest queryEnhancedRequest = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(key))
                .build();

        Iterator<DynamoUser> results = table.query(queryEnhancedRequest).items().iterator();
        //check to see if user already exists
        if (results.hasNext()) {
            throw new RuntimeException("User alias already taken");
        }

        AmazonS3 s3 = AmazonS3ClientBuilder
                .standard()
                .withRegion("us-east-1")
                .build();

        byte[] byteArray = Base64.getDecoder().decode(image);

        ObjectMetadata data = new ObjectMetadata();

        data.setContentLength(byteArray.length);

        data.setContentType("image/jpeg");

        PutObjectRequest request = new PutObjectRequest("myunique340tweeterbucket", alias, new ByteArrayInputStream(byteArray), data).withCannedAcl(CannedAccessControlList.PublicRead);

        s3.putObject(request);

        String link = "https://myunique340tweeterbucket.s3.us-east-1.amazonaws.com/"+ alias;
        return link;
    }
}
