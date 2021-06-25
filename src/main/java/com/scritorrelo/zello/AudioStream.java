package com.scritorrelo.zello;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    List<AudioFrame> packets;

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
        packets = new ArrayList<>();
    }

    public void addFrame(AudioFrame frame){
        packets.add(frame);
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
        stringBuilder.append("Nums of Packets: ").append(packets.size()).append("\n");

        if (!isNull(forUser)) {
            stringBuilder.append("For:").append(forUser).append("\n");
        }

        return stringBuilder.toString();
    }
}
