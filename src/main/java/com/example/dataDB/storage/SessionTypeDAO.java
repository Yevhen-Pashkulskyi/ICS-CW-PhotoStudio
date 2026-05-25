package com.example.dataDB.storage;

import com.example.dataDB.DataBaseConnection;
import com.example.service.SessionType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SessionTypeDAO {

    public void saveSessionType(SessionType sessionType) {
        String sql = """
                insert into session_type (session_name, durations_hours, price)
                values (?, ?, ?)
                """;
        try(Connection connection = DataBaseConnection.getConnection();
            PreparedStatement pstm = connection.prepareStatement(sql)){

            pstm.setString(1, sessionType.getSessionName());
            pstm.setInt(2,sessionType.getDurationHours());
            pstm.setDouble(3,sessionType.getPrice());

            pstm.executeUpdate();


        }catch (SQLException e){
            System.err.println("Помилка при збереженні сесії: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public List<SessionType> getSessionType() {
        List<SessionType> sessionTypeList = new ArrayList<>();
        String sql = """
                select * from session_type;
        """;
        try (Connection connection = DataBaseConnection.getConnection();
        PreparedStatement pstm = connection.prepareStatement(sql);
        ResultSet rs = pstm.executeQuery()){
            while (rs.next()) {
                SessionType st = new SessionType(
                        rs.getString("session_name"),
                        rs.getInt("durations_hours"),
                        rs.getDouble("price")
                );
                st.setId(rs.getLong("id"));
                sessionTypeList.add(st);
            }

        }catch (SQLException e){
            e.printStackTrace();
        }

        return   sessionTypeList;
    }

    public void createTableSessionType(){
        String sql = """
                create table if not exists session_type (
                id bigSerial primary key,
                session_name varchar(255) not null,
                durations_hours integer not null,
                price decimal(20, 2) not null);
        """;
        try(Connection connection = DataBaseConnection.getConnection();
            Statement stmt = connection.createStatement()){

            stmt.execute(sql);
            System.out.println("Таблиця створена");
        }catch (SQLException e){
            System.err.println("Помилка при створенні таблиці: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
