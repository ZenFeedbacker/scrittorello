package com.scritorrelo.zello.message;

import lombok.ToString;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@ToString(callSuper = true)
public class Location extends Message {

    double longitude;
    double latitude;
    double accuracy;
    String formattedAddress;

    public Location(JSONObject obj, LocalDateTime timestamp) throws JSONException {

        super(obj, timestamp);

        longitude = obj.optDouble("longitude");
        latitude = obj.optDouble("latitude");
        accuracy = obj.optDouble("accuracy");
        formattedAddress = obj.optString("formatted_address");
    }

    @Override
    public PreparedStatement getPreparedStatement(Connection conn) throws SQLException {

        PreparedStatement st = conn.prepareStatement("insert into LOCATIONS (uid, id, ts, channel, fromUser, forUser, formattedAddress, longitude, latitude, accuracy) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");

        st.setObject(1, uuid);
        st.setInt(2, id);
        st.setTimestamp(3, Timestamp.valueOf(timestamp));
        st.setString(4, channel);
        st.setString(5, fromUser);
        st.setString(6, forUser);
        st.setString(7, formattedAddress);
        st.setDouble(8, longitude);
        st.setDouble(9, latitude);
        st.setDouble(10, accuracy);

        return st;
    }
}
