package com.scritorrelo.zello;

import com.scritorrelo.socket.SocketManager;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

public class ChannelStatus {

    @Autowired
    private SocketManager socketManager;

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

        String statusStr = obj.optString("status");
        return "online".equals(statusStr);
    }

    @Override
    public String toString(){
        return "ChannelStatus(name=" + ChannelList.getChannelAlias(name)+ ", status=" + status +", usersOnline=" + usersOnline +")";
    }
}
