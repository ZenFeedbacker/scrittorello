package com.scritorrelo.zello.message;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.json.JSONException;
import org.json.JSONObject;

import javax.persistence.Entity;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
@ToString(callSuper = true)
public class Location extends Message {

    private final double longitude;
    private final double latitude;
    private final double accuracy;
    private final String formattedAddress;

    public Location(JSONObject obj, LocalDateTime timestamp) throws JSONException {

        super(obj, timestamp);

        longitude = obj.optDouble("longitude");
        latitude = obj.optDouble("latitude");
        accuracy = obj.optDouble("accuracy");
        formattedAddress = obj.optString("formatted_address");
    }
}
