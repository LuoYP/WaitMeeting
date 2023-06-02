package org.example.room;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

public class MeetingRoom implements Serializable {

    private String roomNumber;

    private String masterName;

    private Set<String> memberNames;

    private LocalDateTime meetingStartTime;

    public static MeetingRoom build(String roomNumber, String masterName, LocalDateTime meetingStartTime) {
        return new MeetingRoom().setRoomNumber(roomNumber).setMasterName(masterName)
                .setMeetingStartTime(meetingStartTime);
    }

    public MeetingRoom join(String member) {
        this.memberNames.add(member);
        return this;
    }

    public String roomNumber() {
        return roomNumber;
    }

    public MeetingRoom setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
        return this;
    }

    public String masterName() {
        return masterName;
    }

    public MeetingRoom setMasterName(String masterName) {
        this.masterName = masterName;
        return this;
    }

    public Set<String> memberNames() {
        return memberNames;
    }

    public MeetingRoom setMemberNames(Set<String> memberNames) {
        this.memberNames = memberNames;
        return this;
    }

    public LocalDateTime meetingStartTime() {
        return meetingStartTime;
    }

    public MeetingRoom setMeetingStartTime(LocalDateTime meetingStartTime) {
        this.meetingStartTime = meetingStartTime;
        return this;
    }
}
