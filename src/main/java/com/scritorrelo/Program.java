package com.scritorrelo;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.concentus.*;
import org.gagravarr.opus.*;

public class Program {

    public static void main(String[] args) {

        testOne();
        testTwo();
    }

    public static void testOne() {

        try (var fileIn = new FileInputStream("C:\\Users\\lostromb\\Documents\\Visual Studio 2015\\Projects\\Concentus-git\\AudioData\\48Khz Stereo.raw")) {
            var encoder = new OpusEncoder(48000, 2, OpusApplication.OPUS_APPLICATION_AUDIO);
            encoder.setBitrate(96000);
            encoder.setSignalType(OpusSignal.OPUS_SIGNAL_MUSIC);
            encoder.setComplexity(10);

            try (var fileOut = new FileOutputStream("C:\\Users\\lostromb\\Documents\\Visual Studio 2015\\Projects\\Concentus-git\\AudioData\\out.opus")) {
                var info = new OpusInfo();
                info.setNumChannels(2);
                info.setSampleRate(48000);
                var tags = new OpusTags();
                var file = new OpusFile(fileOut, info, tags);
                var packetSamples = 960;
                var inBuf = new byte[packetSamples * 2 * 2];
                var dataPacket = new byte[1275];
                var start = System.currentTimeMillis();
                while (fileIn.available() >= inBuf.length) {
                    fileIn.read(inBuf, 0, inBuf.length);
                    var pcm = bytesToShorts(inBuf, 0, inBuf.length);
                    var bytesEncoded = encoder.encode(pcm, 0, packetSamples, dataPacket, 0, 1275);
                    var packet = new byte[bytesEncoded];
                    System.arraycopy(dataPacket, 0, packet, 0, bytesEncoded);
                    var data = new OpusAudioData(packet);
                    file.writeAudioData(data);
                }
                file.close();

                var end = System.currentTimeMillis();
                System.out.println("Time was " + (end - start) + "ms");
                System.out.println("Done!");
            }
        } catch (IOException | OpusException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void testTwo() {

        try (var fileIn = new FileInputStream("C:\\Users\\lostromb\\Documents\\Visual Studio 2015\\Projects\\Concentus-git\\AudioData\\48Khz Stereo.raw")) {
            var encoder = new OpusEncoder(48000, 2, OpusApplication.OPUS_APPLICATION_AUDIO);
            encoder.setBitrate(96000);
            encoder.setForceMode(OpusMode.MODE_CELT_ONLY);
            encoder.setSignalType(OpusSignal.OPUS_SIGNAL_MUSIC);
            encoder.setComplexity(0);

            OpusDecoder decoder = new OpusDecoder(48000, 2);

            try (var fileOut = new FileOutputStream("C:\\Users\\lostromb\\Documents\\Visual Studio 2015\\Projects\\Concentus-git\\AudioData\\out_j.raw")) {
                var packetSamples = 960;
                var inBuf = new byte[packetSamples * 2 * 2];
                var dataPacket = new byte[1275];
                var start = System.currentTimeMillis();
                while (fileIn.available() >= inBuf.length) {
                    fileIn.read(inBuf, 0, inBuf.length);
                    var pcm = bytesToShorts(inBuf, 0, inBuf.length);
                    var bytesEncoded = encoder.encode(pcm, 0, packetSamples, dataPacket, 0, 1275);

                    decoder.decode(dataPacket, 0, bytesEncoded, pcm, 0, packetSamples, false);
                    var bytesOut = shortsToBytes(pcm);
                    fileOut.write(bytesOut, 0, bytesOut.length);
                }

                var end = System.currentTimeMillis();
                System.out.println("Time was " + (end - start) + "ms");
                System.out.println("Done!");
            }
        } catch (IOException | OpusException e) {
            System.out.println(e.getMessage());
        }
    }

    public static short[] bytesToShorts(byte[] input, int offset, int length) {

        var processedValues = new short[length / 2];
        for (int c = 0; c < processedValues.length; c++) {
            var a = (short) (((int) input[(c * 2) + offset]) & 0xFF);
            var b = (short) (((int) input[(c * 2) + 1 + offset]) << 8);
            processedValues[c] = (short) (a | b);
        }

        return processedValues;
    }

    public static byte[] shortsToBytes(short[] input) {
        return shortsToBytes(input, 0, input.length);
    }

    public static byte[] shortsToBytes(short[] input, int offset, int length) {

        var processedValues = new byte[length * 2];
        for (int c = 0; c < length; c++) {
            processedValues[c * 2] = (byte) (input[c + offset] & 0xFF);
            processedValues[c * 2 + 1] = (byte) ((input[c + offset] >> 8) & 0xFF);
        }

        return processedValues;
    }
}