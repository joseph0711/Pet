package com.example.pet;

import android.util.Log;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConnectionMysqlClass {
    protected static String dp = ["Database Name"];
    protected static String ip = ["Database IP Address"];
    protected static String port = ["Database Port Number"];
    protected static String username = ["Database Username"];
    protected static String password = ["Database Password"];

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
