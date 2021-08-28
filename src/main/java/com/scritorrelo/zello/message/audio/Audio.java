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
import java.util.List;

@Slf4j
@SuperBuilder
@ToString(callSuper = true)
public class Audio extends Message implements Serializable {

    private static final String AUDIO_FOLDER = "audios";

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
}
