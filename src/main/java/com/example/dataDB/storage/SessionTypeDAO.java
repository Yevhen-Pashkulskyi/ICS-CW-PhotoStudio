package com.example.dataDB.storage;

import com.example.dataDB.DataBaseConnection;
import com.example.service.SessionType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SessionTypeDAO {

    public void saveSessionType(SessionType sessionType) {
        String sql = """
                insert into session_type (session_name, durations_hours, price)
                values (?, ?, ?)
                """;
        try(Connection connection = DataBaseConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)){

            preparedStatement.setString(1, sessionType.getSessionName());


        }catch (SQLException e){
            System.err.println("Помилка при збереженні фотографа: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private void createTableSessionType(){
        String sql = """
                create table if not exist session_type (
                session_id bigSerial primary key,
                session_name varchar(255) not null,
                durations_hours bigint not null,
                price decimal(20, 2) not null
        """;
    }
}
