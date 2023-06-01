package org.example.container;

import org.example.room.MeetingRoom;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MeetingSquare {

    private static final Map<String, MeetingRoom> MEETING_SQUARE = new ConcurrentHashMap<>();

    public static void addMeeting(MeetingRoom meetingRoom) {
        MEETING_SQUARE.put(meetingRoom.roomNumber(), meetingRoom);
    }

    public static void destroyedRoom(String roomNumber) {
        MEETING_SQUARE.remove(roomNumber);
    }

    public static MeetingRoom getMeetingRoom(String roomNumber) {
        return MEETING_SQUARE.get(roomNumber);
    }
}
