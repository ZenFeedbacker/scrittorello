package com.scritorrelo.zello.message;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.json.JSONException;
import org.json.JSONObject;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import java.util.UUID;

@MappedSuperclass
@ToString
@Getter @Setter
@NoArgsConstructor
public abstract class Message {

    @Id
    @GeneratedValue
    protected UUID uuid;
    protected int id;
    protected String channel;
    protected String fromUser;
    protected String forUser;
    protected LocalDateTime timestamp;

    public Message(JSONObject obj, LocalDateTime timestamp) throws JSONException {

        this.timestamp = timestamp;

        channel = obj.getString("channel");
        fromUser = obj.getString("from");
        forUser = obj.optString("for");

        id = obj.optInt("message_id") != 0 ? obj.optInt("message_id") : obj.optInt("stream_id");
    }
}
