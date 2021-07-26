package com.scritorrelo.zello;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ChannelList {

    private static final Map<String, String> channels = new HashMap<>();

    private static final String SOURCE_FILE = "ChannelsList.txt";

    static {
        log.info("Parsing channel list from {}", SOURCE_FILE);

        var uri = ChannelList.class.getClassLoader().getResource(SOURCE_FILE);

        if (uri != null) {

            try {
                var channelNames = Files.readAllLines(new File(uri.getFile()).toPath());

                for (int i = 0; i < channelNames.size(); i++) {
                    channels.put(channelNames.get(i), Character.toString((char) (i + 'A')));
                }
            } catch (IOException e) {
                log.error("Failed to open channel list file: {}", SOURCE_FILE);
            }
        }
    }

    public static String getChannelAlias(String channelName) {

        return channels.get(channelName) + "[" + channelName + "]";
    }

    public static Set<String> getChannelNames() {

        return channels.keySet();
    }
}
