package com.example.dataDB.storage;

import com.example.dataDB.DataBaseConnection;
import com.example.entity.Photographer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PhotographerDAO {

    public void savePhotographer(Photographer photographer) {

        String sql = """
                insert into photographer(full_name, phone, specialization, base_rate)
                values(?, ?, ?, ?)
                """;

        try(Connection connection = DataBaseConnection.getConnection();
            PreparedStatement psmt = connection.prepareStatement(sql)){

            psmt.setString(1, photographer.getFullName());
            psmt.setString(2, photographer.getPhone());
            psmt.setString(3, photographer.getSpecialization());
            psmt.setDouble(4, photographer.getBaseRate());

            psmt.executeUpdate();
            System.out.println("Фотографа збережено");

        }catch(SQLException e){
            System.err.println("Помилка при збереженні фотографа: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public List<Photographer> getAllPhotographers() {
        String  sql = """
                select * from photographer;
        """;
        List<Photographer> photographers = new ArrayList<>();
        try(Connection connection = DataBaseConnection.getConnection();
        PreparedStatement pstmt = connection.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                photographers.add(new Photographer(
                        rs.getLong(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4) ,
                        rs.getDouble(5)
                        )

                );
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return photographers;
    }

    public void createTablePhotographer() {
        String sql = """
            create table if not exists photographer(
            id bigSerial primary key,
            full_name varchar(255) not null,
            phone varchar(255) not null,
            specialization varchar(255) not null,
            base_rate decimal(10,2) not null);
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

