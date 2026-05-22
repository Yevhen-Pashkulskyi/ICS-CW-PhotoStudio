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
            PreparedStatement pstm = connection.prepareStatement(sql)){

            pstm.setString(1, sessionType.getSessionName());
            pstm.setLong(2,sessionType.getDurationHours());
            pstm.setDouble(3,sessionType.getPrice());

            pstm.executeUpdate();


        }catch (SQLException e){
            System.err.println("Помилка при збереженні сесії: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private void createTableSessionType(){
        String sql = """
                create table if not exists session_type (
                id bigSerial primary key,
                session_name varchar(255) not null,
                durations_hours integer not null,
                price decimal(20, 2) not null);
        """;
    }
}
