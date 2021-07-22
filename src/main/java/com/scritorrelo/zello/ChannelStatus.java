package com.scritorrelo.zello;

import lombok.ToString;
import org.json.JSONException;
import org.json.JSONObject;

@ToString
public class ChannelStatus {

    private final String name;
    private final boolean status;
    private final int usersOnline;
    private final boolean imagesSupported;
    private final boolean textingSupported;
    private final boolean locationsSupported;
    private final String error;
    private String errorType;

    public ChannelStatus(JSONObject obj) throws JSONException {

        name = obj.getString("channel");
        status = getChannelStatus(obj);
        usersOnline = obj.getInt("users_online");
        imagesSupported = obj.optBoolean("images_supported");
        textingSupported = obj.optBoolean("texting_supported");
        locationsSupported = obj.optBoolean("locations_supported");
        error = obj.optString("error");
    }

    private boolean getChannelStatus(JSONObject obj){

        String statusStr = obj.optString("status");
        return "online".equals(statusStr);
    }
}
