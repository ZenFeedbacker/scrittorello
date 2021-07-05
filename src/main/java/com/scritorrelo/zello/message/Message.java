package com.scritorrelo.zello.message;

import lombok.ToString;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;

@ToString
public abstract class Message {

    protected String channel;
    protected String fromUser;
    protected String forUser;
    protected int id;
    protected LocalDateTime timestamp;

    public Message(JSONObject obj, LocalDateTime timestamp) throws JSONException {

        channel = obj.getString("channel");
        fromUser = obj.getString("from");
        this.timestamp = timestamp;

        forUser = obj.optString("for");


        try {
            id = obj.getInt("message_id");
        } catch (JSONException e1) {
            try {
                id = obj.getInt("stream_id");
            } catch (JSONException ignored) {

            }
        }
    }
}
