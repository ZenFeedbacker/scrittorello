package com.scritorrelo.zello;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ChannelStatus {

    private final String name;
    private final boolean status;
    private final int usersOnline;
    private final boolean imagesSupported;
    private final boolean textingSupported;
    private final boolean locationsSupported;
    private final String error;
    private final String errorType;

    public ChannelStatus(JSONObject obj) throws JSONException {

        name = obj.getString("channel");
        status = getChannelStatus(obj);
        usersOnline = obj.getInt("users_online");
        imagesSupported = obj.optBoolean("images_supported");
        textingSupported = obj.optBoolean("texting_supported");
        locationsSupported = obj.optBoolean("locations_supported");
        error = obj.optString("error");
        errorType = obj.optString("error_type");
    }

    private boolean getChannelStatus(JSONObject obj){

        var statusStr = obj.optString("status");
        return "online".equals(statusStr);
    }

    @Override
    public String toString(){

        var fields = new ArrayList<String>();

        fields.add("name=" + name);

        if(status) {

            fields.add("users online=" + usersOnline);

            if (imagesSupported) {
                fields.add("support images");
            }

            if (textingSupported) {
                fields.add("support texts");
            }

            if (locationsSupported) {
                fields.add("support locations");
            }

            if(!StringUtils.isEmpty(error)){
                fields.add("error=" + error);
                fields.add("error type=" + errorType);
            }
        }

        return  "ChannelStatus(" + StringUtils.join(fields, ", ") + ")";
    }
}
