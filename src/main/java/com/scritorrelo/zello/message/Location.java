package com.scritorrelo.zello.message;

import com.healthmarketscience.sqlbuilder.InsertQuery;
import lombok.ToString;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.time.LocalDateTime;

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

    @Override
    public String getSqlStatement() {
        return new InsertQuery(schema.LOCATION_TABLE)
                .addColumn(schema.UUID_LOCATION, uuid)
                .addColumn(schema.ID_LOCATION, 1)
                .addColumn(schema.CHANNEL_LOCATION, channel)
                .addColumn(schema.FOR_USER_LOCATION, forUser)
                .addColumn(schema.FROM_USER_LOCATION, fromUser)
                .addColumn(schema.TIMESTAMP_LOCATION, Timestamp.valueOf(timestamp))
                .addColumn(schema.LONGITUDE_LOCATION, longitude)
                .addColumn(schema.LATITUDE_LOCATION, latitude)
                .addColumn(schema.ACCURACY_LOCATION, accuracy)
                .addColumn(schema.FORMATTED_ADDRESS_LOCATION, formattedAddress)
                .validate()
                .toString();    }
}
