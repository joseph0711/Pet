package com.example.pet;

import android.util.Log;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConnectionMysqlClass {
    protected static String dp = ["Database_Name"];
    protected static String ip = ["Database_URL"];
    protected static String port = ["Database_Port"];
    protected static String username = ["Database_Username"];
    protected static String password = ["Database_Password"];

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
