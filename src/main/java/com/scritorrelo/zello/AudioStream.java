package com.scritorrelo.zello;

import com.scritorrelo.opus.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static java.util.Objects.isNull;

public class AudioStream {

    String type;
    String codec;
    String codecHeader;
    int packetDuration;
    int streamID;
    String channel;
    String fromUser;
    String forUser;
    LocalDateTime timestamp;
    List<AudioFrame> audioFrames;

    public AudioStream(JSONObject json, LocalDateTime timestamp) throws JSONException {

        type = json.getString("type");
        codec = json.getString("codec");
        codecHeader = json.getString("codec_header");
        streamID = json.getInt("stream_id");
        channel = json.getString("channel");
        fromUser = json.getString("from");
        packetDuration = json.getInt("packet_duration");
        this.timestamp = timestamp;
        if (json.has("for")) {
            forUser = json.getString("for");
        }
        audioFrames = new ArrayList<>();
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
        stringBuilder.append("Stream ID: ").append(streamID).append("\n");
        stringBuilder.append("Packet Duration: ").append(packetDuration).append("\n");
        stringBuilder.append("Nums of Packets: ").append(audioFrames.size()).append("\n");

        if (!isNull(forUser)) {
            stringBuilder.append("For:").append(forUser).append("\n");
        }

        return stringBuilder.toString();
    }

    public Stream getOpusStream() {
        Stream opusStream = new Stream();

        opusStream.addPacket(createIDHeader());
        opusStream.addPacket(createCommentHeader());

        audioFrames.forEach(packet -> opusStream.addPacket(new DataPacket(packet.getData())));

        return opusStream;
    }

    public int randomStreamSerialNumber(){

        return  ThreadLocalRandom.current().nextInt();
    }

    public IDHeaderPacket createIDHeader() {

        return IDHeaderPacket.
                builder().
                signature(IDHeaderPacket.OPUS_ID_HEADER).
                version(1)
                .channelCount(1).
                outputGain(0).
                channelMappingFamily(0).
                sampleRate(48000).
                preskip(0).
                build();
    }

    private CommentHeaderPacket createCommentHeader() {

        return CommentHeaderPacket.
                builder().
                signature(CommentHeaderPacket.OPUS_COMMENT_HEADER).
                vendorStr("").
                vendorStrLen(0).
                build();
    }
}
