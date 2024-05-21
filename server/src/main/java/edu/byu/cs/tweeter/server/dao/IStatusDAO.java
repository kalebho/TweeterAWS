package edu.byu.cs.tweeter.server.dao;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.util.Pair;

public interface IStatusDAO {
    Pair<List<Status>, Boolean> getStoryStatuses(String token, String targetUserAlias, int limit, Status lastStatus);
    void addStatus(String token, Status status);
}
