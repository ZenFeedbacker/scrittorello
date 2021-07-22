package com.scritorrelo.zello.error;

import lombok.Getter;
import lombok.ToString;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

import static java.util.UUID.randomUUID;

@ToString
public class Error {

    private final UUID uuid;
    @Getter
    private final ErrorCode code;

    public Error(JSONObject obj) {

        uuid = randomUUID();
        code = ErrorCode.valueOfCode(obj.optString("error"));
    }
}
