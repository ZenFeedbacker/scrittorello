package com.scritorrelo.zello.message.audio;

import com.scritorrelo.Client;
import com.scritorrelo.opus.*;
import com.scritorrelo.zello.message.Message;
import lombok.ToString;
import org.gagravarr.opus.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@ToString(callSuper = true)
public class Audio extends Message {

    private final String type;
    private final String codec;
    private final String codecHeader;
    private final int packetDuration;
    private final List<AudioFrame> audioFrames;

    public Audio(JSONObject json, LocalDateTime timestamp) throws JSONException {

        super(json, timestamp);

        type = json.getString("type");
        codec = json.getString("codec");
        codecHeader = json.getString("codec_header");
        packetDuration = json.getInt("packet_duration");
        audioFrames = new ArrayList<>();
    }

    @Override
    public PreparedStatement getSqlStatement(Connection conn) throws SQLException {

        String sqlStatement = "INSERT INTO AUDIO (UUID,ID,CHANNEL,FROM_USER,FOR_USER,TIMESTAMP,TYPE,CODEC,CODEC_HEADER,PACKET_DURATION) VALUES (?,?,?,?,?,?,?,?,?,?)";

        PreparedStatement statement = conn.prepareStatement(sqlStatement);

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

        return conn.prepareStatement(sqlStatement);
    }

    public void addFrame(AudioFrame frame) {
        audioFrames.add(frame);
    }

    public Stream getOpusStream() {
        Stream opusStream = new Stream();

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

    public void toFile() throws IOException {

        OutputStream out = new FileOutputStream(Client.outputFile);

        OpusFile opus = new OpusFile(out);
        opus.getInfo().setSampleRate(48000);
        opus.getInfo().setNumChannels(2);
        opus.getInfo().setOutputGain(0);
        opus.getInfo().setPreSkip(0);
        opus.getTags().addComment("title", "Test Dummy Audio");

        for (AudioFrame af : audioFrames) {
            opus.writeAudioData(new OpusAudioData(af.getData()));
        }

        opus.close();
    }
}
