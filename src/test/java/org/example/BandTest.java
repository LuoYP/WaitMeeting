package org.example;

import cn.hutool.core.io.FileUtil;

import javax.sound.sampled.*;

public class BandTest {
    public static void main(String[] args) throws Exception{
        AudioFormat audioFormat = new AudioFormat(44100.0F, 16, 2, true, false);
        DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
        TargetDataLine line = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
        line.open(audioFormat);
        line.start();
        AudioInputStream audioInputStream = new AudioInputStream(line);

        DataLine.Info dataInfo = new DataLine.Info(SourceDataLine.class, audioFormat);
        SourceDataLine sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataInfo);
        sourceDataLine.open(audioFormat);
        sourceDataLine.start();
        byte[] bytes = new byte[1024];
        int readLength = 0;
        while ((readLength = audioInputStream.read(bytes, 0, 1024)) != -1) {
            sourceDataLine.write(bytes, 0, readLength);
        }
        sourceDataLine.stop();
    }
}
