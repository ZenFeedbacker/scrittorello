package com.scritorrelo.zello.message;

import com.healthmarketscience.sqlbuilder.InsertQuery;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbColumn;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbSchema;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbSpec;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbTable;
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


        DbSpec spec = new DbSpec();
        DbSchema schema = spec.addDefaultSchema();

        // add table with basic customer info
        DbTable customerTable = schema.addTable("text");
        DbColumn custIdCol = customerTable.addColumn("uuid", "uuid", null);
        DbColumn custNameCol = customerTable.addColumn("name", "varchar", 255);
        String insertCustomerQuery =
                new InsertQuery(customerTable)
                        .addColumn(custIdCol, 1)
                        .addColumn(custNameCol, "bob")
                        .validate().toString();
        System.out.println(insertCustomerQuery);


        return statement;
    }
}
