package com.scritorrelo.opus;

import com.scritorrelo.opus.packet.CommentHeaderPacket;
import com.scritorrelo.opus.packet.DataPacket;
import com.scritorrelo.opus.packet.IDHeaderPacket;
import com.scritorrelo.opus.packet.Packet;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class OpusStream {

    @Setter
    private IDHeaderPacket idHeaderPacket;
    private final List<CommentHeaderPacket> commentHeaderPackets;
    @Setter
    private List<Packet> dataPackets;

    public OpusStream() {

        commentHeaderPackets = new ArrayList<>();
        dataPackets = new ArrayList<>();
    }

    public void addCommentPacket(CommentHeaderPacket packet){
        commentHeaderPackets.add(packet);
    }

    public void addDataPacket(DataPacket packet){
        dataPackets.add(packet);
    }
}
