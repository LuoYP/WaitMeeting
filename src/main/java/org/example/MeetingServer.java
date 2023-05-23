package org.example;

import cn.hutool.core.io.FileUtil;

import javax.sound.sampled.*;
import java.io.File;

/**
 * Hello world!
 */
public class MeetingServer {
    public static void main(String[] args) throws Exception {
        AudioFormat audioFormat = new AudioFormat(84100.0F, 16, 2, true, false);
        DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
        TargetDataLine line = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
        line.open(audioFormat);
        line.start();
        AudioInputStream inputStream = new AudioInputStream(line);
        File touch = FileUtil.touch("G:\\1.wav");
        AudioSystem.write(inputStream, AudioFileFormat.Type.WAVE, touch);
        System.out.println(1);
    }
}
