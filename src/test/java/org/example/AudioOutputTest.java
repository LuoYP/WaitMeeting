package org.example;

import cn.hutool.core.io.FileUtil;

import javax.sound.sampled.*;

public class AudioOutputTest {

    public static void main(String[] args) throws Exception {
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(FileUtil.file("G:\\1.wav"));
        //使用与原始音频使用相同的设置避免失真
        AudioFormat audioFormat = audioInputStream.getFormat();
        DataLine.Info dataInfo = new DataLine.Info(SourceDataLine.class, audioFormat);
        SourceDataLine sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataInfo);
        sourceDataLine.open(audioFormat);
        sourceDataLine.start();
        byte[] bytes = new byte[10];
        int readLength = 0;
        while ((readLength = audioInputStream.read(bytes, 0, 10)) != -1) {
            sourceDataLine.write(bytes, 0, readLength);
        }
        sourceDataLine.stop();
    }

}
