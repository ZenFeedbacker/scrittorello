package com.scritorrelo.zello.message.audio;

import com.scritorrelo.Client;
import com.scritorrelo.opus.*;
import com.scritorrelo.zello.message.Message;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.gagravarr.opus.*;
import org.json.JSONException;
import org.json.JSONObject;

import javax.persistence.Entity;
import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

//@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
public class AudioStream extends Message {

    private String type;
    private String codec;
    private String codecHeader;
    private int packetDuration;
    private List<AudioFrame> audioFrames;

    public AudioStream(JSONObject json, LocalDateTime timestamp) throws JSONException {

        super(json, timestamp);

        type = json.getString("type");
        codec = json.getString("codec");
        codecHeader = json.getString("codec_header");
        packetDuration = json.getInt("packet_duration");
        audioFrames = new ArrayList<>();
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
            opus.writeAudioData(new OpusAudioData(af.getData()));
        }

        opus.close();
    }
}
