package com.scritorrelo.ogg;

import com.scritorrelo.Utils;
import com.scritorrelo.opus.Packet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Stream {

    List<Page> pages;

    public Stream(com.scritorrelo.opus.Stream opusStream) {

        pages = new ArrayList<>();

        List<Packet> packetList = opusStream.getDataPackets();

        int serialNumber = Utils.randomStreamSerialNumber();

        for (int i = 0; i < packetList.size() + 2; i++) {

            Packet packet;
            boolean BoS = false;
            boolean EoS = false;
            if (i == 0) {
                packet = opusStream.getIdHeaderPacket();
                BoS = true;
            }
            else if (i == 1) {
                packet = opusStream.getCommentHeaderPackets().get(0);
            } else {
                packet = packetList.get(i-2);

            }
            if(i == packetList.size() + 1){
                EoS = true;
            }

            Page oggPage = Page.
                            builder().
                            pageSequenceNumber(i).
                            bitstreamSerialNumber(serialNumber).
                            numberPageSegments(1).
                            EoS(EoS).
                            BoS(BoS).
                            segmentTable(Collections.singletonList(packet.toByteArray().length)).
                            packets(Collections.singletonList(packet)).
                            build();

            pages.add(oggPage);
        }

    }
}
