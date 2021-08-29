package com.scritorrelo.zello.message.audio;

import com.scritorrelo.zello.message.Message;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@SuperBuilder
@ToString(callSuper = true)
public class Audio extends Message implements Serializable {

    private static final String AUDIO_FOLDER = "audios" + File.separator;

    private static final String SQL_STATEMENT = "INSERT INTO AUDIO (UUID,ID,CHANNEL,FROM_USER,FOR_USER,TIMESTAMP,TYPE,CODEC,CODEC_HEADER,PACKET_DURATION) VALUES (?,?,?,?,?,?,?,?,?,?)";
    private static final long serialVersionUID = -5260559179736969656L;

    private final String type;
    private final String codec;
    private final String codecHeader;
    private final int packetDuration;
    private final List<AudioFrame> audioFrames;

    public Audio(JSONObject json, LocalDateTime timestamp) {

        super(json, timestamp);

        type = json.optString("type");
        codec = json.optString("codec");
        codecHeader = json.optString("codec_header");
        packetDuration = json.optInt("packet_duration");
        audioFrames = new ArrayList<>();
    }

    @Override
    public PreparedStatement getSqlStatement(Connection conn) throws SQLException {

        var statement = conn.prepareStatement(SQL_STATEMENT);

        statement.setObject(1, uuid);
        statement.setInt(2, id);
        statement.setString(3, channel);
        statement.setString(4, fromUser);
        statement.setString(5, forUser);
        statement.setTimestamp(6, Timestamp.valueOf(timestamp));
        statement.setString(7, type);
        statement.setString(8, codec);
        statement.setString(9, codecHeader);
        statement.setInt(10, packetDuration);

        return statement;
    }

    public void addFrame(AudioFrame frame) {
        audioFrames.add(frame);
    }

    public void write(){
        writeToFile();
        convertToWav();
    }

    private void writeToFile() {

        try (var f0 = new FileWriter(getFilePath() + ".pcm")) {

            for (byte[] data : audioFrames.stream().map(AudioFrame::getData).collect(Collectors.toList())) {
                String encoded = Base64.getEncoder().encodeToString(data);
                f0.write(encoded);
                f0.write("\n");
            }
            f0.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void convertToWav() {

        var commands = new ArrayList<String>();

        commands.add("./audios");
        commands.add("-f");
        commands.add(getFilePath() + ".pcm");
        commands.add("-o");
        commands.add(getFilePath() + ".wav");

        runShellCommand(commands);

        commands = new ArrayList<>();
        commands.add("rm");
        commands.add(getFilePath() + ".pcm");

        //runShellCommand(commands);
    }

    private void runShellCommand(List<String> comm){

        var builder = new ProcessBuilder(comm).inheritIO();

        builder.directory(new File(System.getProperty("user.dir") + "/src/main/resources"));

        Process process;

        try {
            process = builder.start();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        try {
            process.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }

    private String getFilePath() {
        return System.getProperty("user.dir") + MESSAGE_FOLDER + AUDIO_FOLDER + uuid;
    }
}
