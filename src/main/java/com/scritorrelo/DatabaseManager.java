package com.scritorrelo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {

    public static void main() {
        try (Connection conn = DriverManager.getConnection("jdbc:h2:file:./data/db", "sa", "password")) {

            boolean isValid = conn.isValid(0);
            System.out.println("Do we have a valid db connection? = " + isValid);
        } catch (SQLException e){

        }
    }
}
