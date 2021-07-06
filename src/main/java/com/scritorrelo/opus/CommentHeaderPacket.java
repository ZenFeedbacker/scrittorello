package com.scritorrelo.opus;

import com.scritorrelo.Utils;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.io.EOFException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

@Builder
@AllArgsConstructor
public class CommentHeaderPacket extends Packet {

    public final static String OPUS_COMMENT_HEADER = "OpusTags";

    String signature;
    int vendorStrLen;
    String vendorStr;
    int userCommentListLen;
    List<Integer> userCommentLens;
    List<String> userComments;

    public CommentHeaderPacket(byte[] data) throws EOFException {

        super(data);

        signature = Utils.readByteStreamToString(stream, 8);
        vendorStrLen = Utils.readByteStreamToInt(stream, 4);
        vendorStr = Utils.readByteStreamToString(stream, vendorStrLen);
        userCommentListLen = Utils.readByteStreamToInt(stream, 4);

        userComments = new ArrayList<>();
        userCommentLens = new ArrayList<>();

        for (int i = 0; i < userCommentListLen; i++) {
            int len = Utils.readByteStreamToInt(stream, 4);
            userCommentLens.add(len);
            userComments.add(Utils.readByteStreamToString(stream, len));
        }
    }

    @Override
    public byte[] toByteArray(){

        ByteBuffer bb = ByteBuffer.allocate(20);

        bb.put(signature.getBytes());
        bb.putInt(vendorStrLen);
        bb.put(vendorStr.getBytes());
        bb.putInt(userCommentListLen);

        return bb.array();
    }

    @Override
    public String toString() {

        StringBuilder str = new StringBuilder(
                "-------Opus Comment Header-------\n" +
                        "Length: " + length + "\n" +
                        "Signature: " + signature + "\n" +
                        "Vendor String Length: " + vendorStrLen + "\n" +
                        "Vendor String: " + vendorStr + "\n" +
                        "User Comment List Length: " + userCommentListLen + "\n");

        for (int i = 0; i < userCommentListLen; i++) {
            str.append("Comment #").append(i).
                    append(" Length: ").append(userCommentLens.get(i)).append("\n").
                    append("Comment #").append(i).append(": ").append(userComments.get(i)).append("\n");
        }

        return str.toString();
    }
}
