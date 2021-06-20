package com.scritorrelo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.gagravarr.ogg.tools.OggAudioInfoTool;
import org.gagravarr.opus.OpusFile;
import org.gagravarr.opus.OpusStatistics;

/**
 * A tool for looking at the innards of an Opus File
 */
class OpusInfoTool extends OggAudioInfoTool {
    public static void main(String[] args) throws Exception {
        handleMain(args, new OpusInfoTool());
    }

    @Override
    public String getToolName() {
        return "OpusInfoTool";
    }
    @Override
    public String getDefaultExtension() {
        return "opus";
    }

    @Override
    public void process(File file, boolean debugging) throws IOException {
        InfoPacketReader r = new InfoPacketReader(
                new FileInputStream(file));
        OpusFile of = new OpusFile(r);

        System.out.println("Processing file \"" + file + "\"");

        System.out.println("");
        System.out.println("Opus Headers:");
        System.out.println("  Version: " + of.getInfo().getVersion());
        System.out.println("  Vendor: " + of.getTags().getVendor());
        System.out.println("  Channels: " + of.getInfo().getNumChannels());
        System.out.println("  Rate: " + of.getInfo().getRate() + "Hz");
        System.out.println("  Pre-Skip: " + of.getInfo().getPreSkip());
        System.out.println("  Playback Gain: " + of.getInfo().getOutputGain() + "dB");
        System.out.println("");

        System.out.println("User Comments:");
        listTags(of);
        System.out.println("");

        OpusStatistics stats = new OpusStatistics(of);
        stats.calculate();
        System.out.println("");
        System.out.println("Opus Audio:");
        System.out.println("  Total Data Packets: " + stats.getAudioPacketsCount());
        System.out.println("  Total Data Length: " + stats.getAudioDataSize());
        System.out.println("  Audio Length Seconds: " + stats.getDurationSeconds());
        System.out.println("  Audio Length: " + stats.getDuration());
        System.out.println("  Packet duration: "+ format2(stats.getMaxPacketDuration())+"ms (max), "
                + format2(stats.getAvgPacketDuration())+"ms (avg), " + format2(stats.getMinPacketDuration())+"ms (min)");
        System.out.println("  Page duration:   "+ format2(stats.getMaxPageDuration())+"ms (max), "
                + format2(stats.getAvgPageDuration())+"ms (avg), " + format2(stats.getMinPageDuration())+"ms (min)");
        System.out.println("  Total data length: "+ stats.getAudioDataSize() + " (overhead: " + format1(stats.getOggOverheadPercentage())+"%)");
        System.out.println("  Playback length: "+ stats.getDuration());
        String cbr = "";
        if (stats.getMinPacketDuration() == stats.getMaxPacketDuration()
                && stats.getMinPacketBytes() == stats.getMaxPacketBytes()) {
            cbr= " (hard-CBR)";
        }
        System.out.println("  Average bitrate: "
                +formatBitrate(stats.getAverageOverallBitrate())
                +", w/o overhead: "
                +formatBitrate(stats.getAverageAudioBitrate())+cbr);
    }
}
