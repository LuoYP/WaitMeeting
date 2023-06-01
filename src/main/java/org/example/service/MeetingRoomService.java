package org.example.service;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.RandomUtil;
import org.example.common.annotation.Autowired;
import org.example.common.annotation.RpcService;
import org.example.common.context.Factory;
import org.example.container.MeetingSession;
import org.example.container.MeetingSquare;
import org.example.room.MeetingRoom;
import org.example.schedule.TimeWheel;
import org.example.schedule.TimeWheelTask;
import org.example.server.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Objects;

@RpcService
public class MeetingRoomService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MeetingRoomService.class);

    private static final String BASE_RANDOM = "0123456789";

    private final TimeWheel timeWheel = (TimeWheel) Factory.getBean(TimeWheel.class);

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
            TimeWheelTask noticeTask = new TimeWheelTask(LocalDateTimeUtil.toEpochMilli(meetingStartTime), () -> {
                String ipaddress = MeetingSession.getAddress(masterName);
                if (CharSequenceUtil.isBlank(ipaddress)) {
                    LOGGER.error("the meeting master: {} not online", masterName);
                    return;
                }

            });
            timeWheel.addTask(noticeTask);
        }
        MeetingSquare.addMeeting(meetingRoom);
        return roomNumber;
    }

    public void joinRoom(String roomNumber, String username) {
        MeetingRoom meetingRoom = MeetingSquare.getMeetingRoom(roomNumber);
        if (Objects.isNull(meetingRoom)) {
            throw new RuntimeException("meet number is illegal!");
        }
        meetingRoom.memberNames().add(username);
    }

    public String generateRandomRoomNumber(int partLength) {
        String head = RandomUtil.randomString(BASE_RANDOM, partLength);
        String body = RandomUtil.randomString(BASE_RANDOM, partLength);
        String tail = RandomUtil.randomString(BASE_RANDOM, partLength);
        return CharSequenceUtil.join(StrPool.DASHED, head, body, tail);
    }
}
