package com.scritorrelo.zello;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Command {

    CHANNEL_STATUS ("on_channel_status"),
    STREAM_START ("on_stream_start"),
    STREAM_STOP ("on_stream_stop"),
    ERROR ("on_error"),
    IMAGE ("on_image"),
    TEXT ("on_text_message"),
    LOCATION ("on_location");

    private final String label;

    public static Command valueOfLabel(String label) {
        for (Command e : values()) {
            if (e.label.equals(label)) {
                return e;
            }
        }
        return null;
    }
}
