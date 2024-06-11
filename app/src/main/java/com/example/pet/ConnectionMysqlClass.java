package com.example.pet;

import android.util.Log;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConnectionMysqlClass {
    protected static String dp = "petassistant";
    protected static String ip = "petassistant.mysql.database.azure.com";
    protected static String port = "3306";
    protected static String username = "pet";
    protected static String password = "saGroup07";

    public Connection CONN() {
        Connection conn = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            String connectionString = "jdbc:mysql://" + ip + ":" + port + "/" + dp;
            conn = DriverManager.getConnection(connectionString, username, password);
        } catch (Exception e) {
            Log.e("ERROR", Objects.requireNonNull(e.getMessage()));
        }
        return conn;
    }
}
