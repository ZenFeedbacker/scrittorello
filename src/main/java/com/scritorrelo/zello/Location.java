package com.scritorrelo.zello;

import lombok.ToString;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;

@ToString(callSuper = true)
public class Location extends Message {

    float longitude;
    float latitude;
    String formattedAddress;
    int accuracy;

    public Location(JSONObject obj, LocalDateTime timestamp) throws JSONException {
        super(obj, timestamp);
    }
}
