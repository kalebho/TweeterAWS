package edu.byu.cs.tweeter.server.dao;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.util.Pair;

public interface IFeedDAO {
    Pair<List<Status>, Boolean> getFeedStatuses(String token, String targetUserAlias, int limit, Status lastStatus);
    void updateFeeds(Status status, List<String> followers);
}
