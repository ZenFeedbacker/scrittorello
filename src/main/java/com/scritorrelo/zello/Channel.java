package com.scritorrelo.zello;

import lombok.ToString;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;

@ToString
public class Channel {

    private final String name;
    private boolean status;
    private int usersOnline;
    private boolean imagesSupported;
    private boolean textingSupported;
    private boolean locationsSupported;
    private String error;
    private String errorType;
    private LocalDateTime lastStatus;

    public Channel(JSONObject obj, LocalDateTime timestamp) throws JSONException {

        lastStatus = timestamp;

        name = obj.getString("channel");
        status = getChannelStatus(obj);
        usersOnline = obj.getInt("users_online");
        imagesSupported = obj.optBoolean("images_supported");
        textingSupported = obj.optBoolean("texting_supported");
        locationsSupported = obj.optBoolean("locations_supported");
        error = obj.optString("error");
    }

    private boolean getChannelStatus(JSONObject obj){
        String status = obj.optString("status");
        return "online".equals(status);
    }
}
