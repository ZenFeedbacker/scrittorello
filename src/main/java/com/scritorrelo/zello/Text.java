package com.scritorrelo.zello;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;

import static java.util.Objects.isNull;

public class Text extends Message{

    String text;

    public Text(JSONObject obj, LocalDateTime timestamp) throws JSONException {
        super(obj, timestamp);
        text = obj.getString("text");
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("Timestamp: ").append(timestamp.toString()).append("\n");
        stringBuilder.append("Channel: ").append(channel).append("\n");
        stringBuilder.append("From: ").append(fromUser).append("\n");
        stringBuilder.append("Message ID: ").append(id).append("\n");
        stringBuilder.append("Text: ").append(text).append("\n");

        if (!isNull(forUser)) {
            stringBuilder.append("For:").append(forUser).append("\n");
        }

        return stringBuilder.toString();
    }
}
