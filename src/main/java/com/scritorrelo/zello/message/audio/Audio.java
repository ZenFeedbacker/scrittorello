package com.scritorrelo.zello.message.audio;

import com.healthmarketscience.sqlbuilder.InsertQuery;
import com.scritorrelo.Client;
import com.scritorrelo.opus.*;
import com.scritorrelo.zello.message.Message;
import lombok.ToString;
import org.gagravarr.opus.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
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
    public String getSqlStatement() {
        return new InsertQuery(schema.AUDIO_TABLE)
                .addColumn(schema.UUID_AUDIO, uuid)
                .addColumn(schema.ID_AUDIO, 1)
                .addColumn(schema.CHANNEL_AUDIO, channel)
                .addColumn(schema.FOR_USER_AUDIO, forUser)
                .addColumn(schema.FROM_USER_AUDIO, fromUser)
                .addColumn(schema.TIMESTAMP_AUDIO, Timestamp.valueOf(timestamp))
                .addColumn(schema.TYPE_AUDIO, type)
                .addColumn(schema.CODEC_AUDIO, codec)
                .addColumn(schema.CODEC_HEADER_AUDIO, codecHeader)
                .addColumn(schema.PACKET_DURATION_AUDIO, packetDuration)
                .validate()
                .toString();
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
