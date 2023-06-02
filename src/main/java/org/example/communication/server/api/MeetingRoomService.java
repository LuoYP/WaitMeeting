package org.example.communication.server.api;

import org.example.room.MeetingRoom;

import java.time.LocalDateTime;

public interface MeetingRoomService {

    MeetingRoom createMeetingRoom(String masterName, LocalDateTime meetingStartTime);

    boolean joinRoom(String roomNumber, String username);


}
