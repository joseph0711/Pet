package com.example.pet;

import android.util.Log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DatabaseHelperClass {
    ConnectionClass connectionClass = new ConnectionClass();
    Connection con;

    /*
    public void connect() {

    }*/

    public boolean register(String userName, String email, String password) {
        //connect();
        con = connectionClass.CONN();
        String sql = "INSERT INTO user (Name, Email, Password) VALUES (?, ?, ?);";
        try {
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setString(1, userName);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, password);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                // Login successful
               return true;
            } else {
                // Login failed
                return false;
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }


    public void loginCheck(String email, String password) {
        String sql = "Select * from users where email = ? and password = ?";

    }
}
