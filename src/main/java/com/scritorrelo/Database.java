package com.scritorrelo;

import com.scritorrelo.zello.message.Message;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.sql.*;

public class Database {

    public static void addMessage(Message message) {

        try (Connection conn = getConnection()) {

            DSLContext create = DSL.using(conn, SQLDialect.H2);

           // Result<Record> result = create.select().from(USERS).fetch(); // (3)


            PreparedStatement st = message.getPreparedStatement(conn);
            System.out.println(st.execute());
            //int[] updates = st.executeBatch();
            //System.out.println("Inserted [=" + updates.length + "] records into the database");
        } catch (SQLException e) {
            System.out.println("SQLException" + e);
        }

        try (Connection conn = DriverManager.getConnection("jdbc:h2:~/mydatabase")) {

            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("select * from TEXTS");

            while (rs.next()) {
                System.out.println(rs.getString("text"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static Connection getConnection() throws SQLException {
        return  DriverManager.getConnection("jdbc:h2:file:./data/db");
    }
}
