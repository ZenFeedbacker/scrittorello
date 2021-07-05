package com.scritorrelo.zello.message.error;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;

public class Error {

    ErrorCode code;
    final LocalDateTime timestamp;

    public Error(JSONObject obj, LocalDateTime timestamp) throws JSONException {
        this.timestamp = timestamp;
        code = ErrorCode.valueOfCode(obj.getString("error"));
    }
}
