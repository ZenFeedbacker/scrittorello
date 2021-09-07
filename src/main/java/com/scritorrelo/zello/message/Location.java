package com.scritorrelo.zello.message;

import lombok.ToString;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@ToString(callSuper = true)
public class Location extends Message {

    private static final String SQL_STATEMENT = "INSERT INTO LOCATION (UUID,ID,CHANNEL,FROM_USER,FOR_USER, TIMESTAMP,LONGITUDE,LATITUDE,ACCURACY,FORMATTED_ADDRESS) VALUES (?,?,?,?,?,?,?,?,?,?)";

    private final double longitude;
    private final double latitude;
    private final double accuracy;
    private final String formattedAddress;

    public Location(JSONObject obj, LocalDateTime timestamp) {

        super(obj, timestamp);

        longitude = obj.optDouble("longitude");
        latitude = obj.optDouble("latitude");
        accuracy = obj.optDouble("accuracy");
        formattedAddress = obj.optString("formatted_address");
    }

    @Override
    public PreparedStatement getSqlStatement(Connection conn) throws SQLException {

        var statement = conn.prepareStatement(SQL_STATEMENT);

        statement.setObject(1, uuid);
        statement.setInt(2, id);
        statement.setString(3, channel);
        statement.setString(4, fromUser);
        statement.setString(5, forUser);
        statement.setTimestamp(6, Timestamp.valueOf(timestamp));
        statement.setDouble(7, longitude);
        statement.setDouble(8, latitude);
        statement.setDouble(9, accuracy);
        statement.setString(10, formattedAddress);

        return statement;
    }
}
