package com.scritorrelo.zello.message.error;

import lombok.ToString;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.UUID;

import static java.util.UUID.randomUUID;

@ToString
public class Error {

    private final UUID uuid;
    private ErrorCode code;
    private final LocalDateTime timestamp;

    public Error(JSONObject obj, LocalDateTime timestamp) throws JSONException {
        uuid = randomUUID();
        this.timestamp = timestamp;
        code = ErrorCode.valueOfCode(obj.getString("error"));
    }
}
