package com.scritorrelo;

import com.scritorrelo.zello.message.Message;

import java.sql.*;

public class Database {

    public static void addMessage(Message message) {

        try (Connection conn = DriverManager.getConnection("jdbc:h2:~/mydatabase;AUTO_SERVER=TRUE;INIT=runscript from './create.sql'")) {

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
}
