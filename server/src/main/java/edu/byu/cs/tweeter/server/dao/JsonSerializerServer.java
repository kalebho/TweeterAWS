package edu.byu.cs.tweeter.server.dao;

import com.google.gson.Gson;

public class JsonSerializerServer {

    public static String serialize(Object requestInfo) {
        return (new Gson()).toJson(requestInfo);
    }

    public static <T> T deserialize(String value, Class<T> returnType) {
        return (new Gson()).fromJson(value, returnType);
    }
}
