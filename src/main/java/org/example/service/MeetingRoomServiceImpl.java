package org.example.service;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.RandomUtil;
import org.example.common.annotation.RpcService;
import org.example.common.context.Factory;
import org.example.communication.server.api.MeetingRoomService;
import org.example.container.MeetingSquare;
import org.example.room.MeetingRoom;
import org.example.schedule.TimeWheel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Objects;

@RpcService
public class MeetingRoomServiceImpl implements MeetingRoomService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MeetingRoomServiceImpl.class);

    private static final String BASE_RANDOM = "0123456789";

    private final TimeWheel timeWheel = (TimeWheel) Factory.getBean(TimeWheel.class);

    /**
     * 创建会议室
     *
     * @param masterName       会议主持人
     * @param meetingStartTime 会议计划开始时间，如果是在当前时间之后的话，创建定时任务，定时通知客户端会议开始
     * @return roomNumber
     */
    @Override
    public MeetingRoom createMeetingRoom(String masterName, LocalDateTime meetingStartTime) {
        String roomNumber = generateRandomRoomNumber(3);
        MeetingRoom meetingRoom = MeetingRoom.build(roomNumber, masterName, meetingStartTime);
        MeetingSquare.addMeeting(meetingRoom);
        return meetingRoom;
    }

    @Override
    public boolean joinRoom(String roomNumber, String username) {
        MeetingRoom meetingRoom = MeetingSquare.getMeetingRoom(roomNumber);
        if (Objects.isNull(meetingRoom)) {
            throw new RuntimeException("meet number is illegal!");
        }
        return meetingRoom.memberNames().add(username);
    }

    public String generateRandomRoomNumber(int partLength) {
        String head = RandomUtil.randomString(BASE_RANDOM, partLength);
        String body = RandomUtil.randomString(BASE_RANDOM, partLength);
        String tail = RandomUtil.randomString(BASE_RANDOM, partLength);
        return CharSequenceUtil.join(StrPool.DASHED, head, body, tail);
    }
}
