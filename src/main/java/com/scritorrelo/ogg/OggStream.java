package com.scritorrelo.ogg;

import com.scritorrelo.Utils;
import com.scritorrelo.opus.OpusStream;
import com.scritorrelo.opus.packet.Packet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OggStream {

    final List<OggPage> oggPages;

    public OggStream(OpusStream opusStream) {

        oggPages = new ArrayList<>();

        List<Packet> packetList = opusStream.getDataPackets();

        int serialNumber = Utils.randomStreamSerialNumber();

        for (int i = 0; i < packetList.size() + 2; i++) {

            Packet packet;
            boolean bos = false;
            boolean eos = false;

            if (i == 0) {
                packet = opusStream.getIdHeaderPacket();
                bos = true;
            }
            else if (i == 1) {
                packet = opusStream.getCommentHeaderPackets().get(0);
            } else {
                packet = packetList.get(i-2);

            }
            if(i == packetList.size() + 1){
                eos = true;
            }

            OggPage oggPage = OggPage.
                            builder().
                            pageSequenceNumber(i).
                            bitstreamSerialNumber(serialNumber).
                            numberPageSegments(1).
                    eos(eos).
                    bos(bos).
                            segmentTable(Collections.singletonList(packet.toByteArray().length)).
                            packets(Collections.singletonList(packet)).
                            build();

            oggPage.generateChecksum();

            oggPages.add(oggPage);
        }
    }
}
