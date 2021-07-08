package com.scritorrelo.zello.message.audio;

import com.scritorrelo.Client;
import com.scritorrelo.opus.*;
import com.scritorrelo.zello.message.Message;
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

import static java.util.Objects.isNull;

public class AudioStream extends Message {

    String type;
    String codec;
    String codecHeader;
    int packetDuration;
    List<AudioFrame> audioFrames;

    public AudioStream(JSONObject json, LocalDateTime timestamp) throws JSONException {

        super(json, timestamp);

        type = json.getString("type");
        codec = json.getString("codec");
        codecHeader = json.getString("codec_header");
        packetDuration = json.getInt("packet_duration");
        audioFrames = new ArrayList<>();
    }

    @Override
    public PreparedStatement getPreparedStatement(Connection conn) throws SQLException {
        PreparedStatement st = conn.prepareStatement("insert into AUDIOS (uid, id, ts, channel, fromUser, forUser, type, codec, codecHeader, packetDuration) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");

        st.setObject(1, uuid);
        st.setInt(2, id);
        st.setTimestamp(3, Timestamp.valueOf(timestamp));
        st.setString(4, channel);
        st.setString(5, fromUser);
        st.setString(6, forUser);
        st.setString(7, type);
        st.setString(8, codec);
        st.setString(9, codecHeader);
        st.setInt(10, packetDuration);

        return st;
    }

    public void addFrame(AudioFrame frame) {
        audioFrames.add(frame);
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("Timestamp: ").append(timestamp.toString()).append("\n");
        stringBuilder.append("Type: ").append(type).append("\n");
        stringBuilder.append("Codec: ").append(codec).append("\n");
        stringBuilder.append("Codec Header: ").append(codecHeader).append("\n");
        stringBuilder.append("Packet Duration: ").append(packetDuration).append("\n");
        stringBuilder.append("Channel: ").append(channel).append("\n");
        stringBuilder.append("From: ").append(fromUser).append("\n");
        stringBuilder.append("Stream ID: ").append(id).append("\n");
        stringBuilder.append("Packet Duration: ").append(packetDuration).append("\n");
        stringBuilder.append("Nums of Packets: ").append(audioFrames.size()).append("\n");

        if (!isNull(forUser)) {
            stringBuilder.append("For:").append(forUser).append("\n");
        }

        return stringBuilder.toString();
    }

    public Stream getOpusStream() {
        Stream opusStream = new Stream();

        opusStream.setIdHeaderPacket(createIDHeader());
        opusStream.addCommentPacket(createCommentHeader());

        audioFrames.forEach(packet -> opusStream.addDataPacket(new DataPacket(packet.getData())));

        return opusStream;
    }

    public IDHeaderPacket createIDHeader() {

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
            opus.writeAudioData(new OpusAudioData(af.data));
        }

        opus.close();
    }
}
