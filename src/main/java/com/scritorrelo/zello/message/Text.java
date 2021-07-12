package com.scritorrelo.zello.message;

import ch.qos.logback.core.subst.Token;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@ToString(callSuper = true)
public class Text extends Message {

    private final String text;

    public Text(JSONObject obj, LocalDateTime timestamp) throws JSONException {
        super(obj, timestamp);
        text = obj.getString("text");
    }

    @Override
    public PreparedStatement getSqlStatement(Connection conn) throws SQLException, IllegalAccessException {

        StringBuilder sqlStatement = new StringBuilder("insert into ");

        sqlStatement.append(this.getClass().getSimpleName());

        sqlStatement.append(" (");

        List<Field> fields = getFieldList();
        sqlStatement.append(fields.stream().map(Field::getName).collect(Collectors.joining(", ")));

        sqlStatement.append(") values (");

        sqlStatement.append(StringUtils.repeat("?",",",fields.size()));

        sqlStatement.append(")");

        System.out.println(sqlStatement);

        PreparedStatement statement = conn.prepareStatement(sqlStatement.toString());

        for(int i = 0; i < fields.size(); i++){
            Field field = fields.get(i);


            System.out.println(i + " " + field.getType().getSimpleName());
            switch (field.getType().getSimpleName()){
                case "String":
                    statement.setString(i+1, (String) field.get(this));
                    break;
                case "int":
                    statement.setInt(i+1, (Integer) field.get(this));
                    break;
                case "UUID":
                    statement.setBytes(i+1, uuidToByteArray());
                    break;
                case "LocalDateTime":
                    statement.setTimestamp(i+1, Timestamp.valueOf((LocalDateTime) field.get(this)));
                    break;
                default:
                    break;
            }
        }

        return statement;
    }
}
