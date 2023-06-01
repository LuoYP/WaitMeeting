package org.example.container;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MeetingSession {

    private static final Map<String, String> USERS = new ConcurrentHashMap<>();

    public static void addUser(String userAddress, String username) {
        USERS.put(username, userAddress);
    }

    public static String getAddress(String username) {
        return USERS.get(username);
    }

    public static boolean exist(String username) {
        return USERS.containsKey(username);
    }
}
