package com.scritorrelo.zello.message.audio;

import com.scritorrelo.zello.message.Message;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;

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
@ToString(callSuper = true)
public class Audio extends Message {

    private static final String SQL_STATEMENT = "INSERT INTO AUDIO (UUID,ID,CHANNEL,FROM_USER,FOR_USER,TIMESTAMP,TYPE,CODEC,CODEC_HEADER,PACKET_DURATION,FILE) VALUES (?,?,?,?,?,?,?,?,?,?,?)";

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
        statement.setString(11, framesToString());

        return statement;
    }

    private String framesToString(){

        var buffer = new StringBuilder();


        for (byte[] data : audioFrames.stream().map(AudioFrame::getData).collect(Collectors.toList())) {
            String encoded = Base64.getEncoder().encodeToString(data);
            buffer.append(encoded).append("\n");
        }

        return  buffer.toString();
    }

    public void addFrame(AudioFrame frame) {
        audioFrames.add(frame);
    }
}
