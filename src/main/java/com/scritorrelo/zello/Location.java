package com.scritorrelo.zello;

import lombok.ToString;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;

@ToString(callSuper = true)
public class Location extends Message {

    double longitude;
    double latitude;
    String formattedAddress;
    double accuracy;

    public Location(JSONObject obj, LocalDateTime timestamp) throws JSONException {

        super(obj, timestamp);
        longitude = obj.optDouble("longitude");
        latitude = obj.optDouble("latitude");
        accuracy = obj.optDouble("accuracy");
        formattedAddress = obj.optString("formatted_address");
    }
}
