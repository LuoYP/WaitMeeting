package org.example.service;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.RandomUtil;
import org.example.common.annotation.RpcService;
import org.example.container.MeetingSquare;
import org.example.room.MeetingRoom;

import java.time.LocalDateTime;

@RpcService
public class MeetingRoomService {

    private static final String BASE_RANDOM = "0123456789";

    /**
     * 创建会议室
     *
     * @param masterName       会议主持人
     * @param meetingStartTime 会议计划开始时间，如果是在当前时间之后的话，创建定时任务，定时通知客户端会议开始
     * @return roomNumber
     */
    public String createMeetingRoom(String masterName, LocalDateTime meetingStartTime) {
        String roomNumber = generateRandomRoomNumber(3);
        MeetingRoom meetingRoom = MeetingRoom.build(roomNumber, masterName, meetingStartTime);
        if (meetingStartTime.isAfter(LocalDateTime.now())) {

        }
        MeetingSquare.addMeeting(meetingRoom);
        return roomNumber;
    }

    public String generateRandomRoomNumber(int partLength) {
        String head = RandomUtil.randomString(BASE_RANDOM, partLength);
        String body = RandomUtil.randomString(BASE_RANDOM, partLength);
        String tail = RandomUtil.randomString(BASE_RANDOM, partLength);
        return CharSequenceUtil.join(StrPool.DASHED, head, body, tail);
    }
}
