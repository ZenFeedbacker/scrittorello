package com.scritorrelo.zello.message.audio;

import com.scritorrelo.opus.packet.CommentHeaderPacket;
import com.scritorrelo.opus.packet.DataPacket;
import com.scritorrelo.opus.packet.IDHeaderPacket;
import com.scritorrelo.opus.*;
import com.scritorrelo.zello.message.Message;
import lombok.Builder;
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

@Slf4j
@SuperBuilder
@ToString(callSuper = true)
public class Audio extends Message implements Serializable {

    private static final String AUDIO_FOLDER = "audios\\";

    private static final String SQL_STATEMENT = "INSERT INTO AUDIO (UUID,ID,CHANNEL,FROM_USER,FOR_USER,TIMESTAMP,TYPE,CODEC,CODEC_HEADER,PACKET_DURATION) VALUES (?,?,?,?,?,?,?,?,?,?)";
    private static final long serialVersionUID = -5260559179736969656L;

    private final String type;
    private final String codec;
    private final String codecHeader;
    private final int packetDuration;
    private final ArrayList<AudioFrame> audioFrames;

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

    public OpusStream getOpusStream() {

        var opusStream = new OpusStream();

        opusStream.setIdHeaderPacket(createIDHeader());
        opusStream.addCommentPacket(createCommentHeader());

        audioFrames.forEach(packet -> opusStream.addDataPacket(new DataPacket(packet.getData())));

        return opusStream;
    }

    private IDHeaderPacket createIDHeader() {

        return IDHeaderPacket.
                builder().
                signature(IDHeaderPacket.OPUS_ID_HEADER).
                version(1).
                channelCount(1).
                outputGain((short) 0).
                channelMappingFamily(0).
                sampleRate(48000).
                preskip((short) 0).
                build();
    }

    public void writeToFile() {

        var path = getPath();

        try (var f = new FileOutputStream(path);
             var o = new ObjectOutputStream(f)) {

            o.writeObject(this);
            log.info("Wrote file " + path);
        } catch (IOException e) {
            log.warn("Failed to write Audio object to file {}: {}", path, e.getMessage());
        }

    }

    public Audio readFromFile() {

        var path = getPath();

        try (var f = new FileInputStream(path);
             var o = new ObjectInputStream(f)) {

            log.info("Read File " + path);

            return (Audio) o.readObject();

        } catch (IOException | ClassNotFoundException e) {
            log.warn("Failed to read Audio object from file {}: {}", path, e.getMessage());
            return null;
        }
    }

    private CommentHeaderPacket createCommentHeader() {

        return CommentHeaderPacket.
                builder().
                signature(CommentHeaderPacket.OPUS_COMMENT_HEADER).
                vendorStr("").
                vendorStrLen(0).
                userCommentLens(new ArrayList<>()).
                userCommentListLen(0).
                build();
    }

    private String getPath() {
        return System.getProperty("user.dir") + MESSAGE_FOLDER + "audioObjects\\" + uuid.toString() + ".ser";
    }
}
