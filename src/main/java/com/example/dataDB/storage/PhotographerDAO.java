package com.example.dataDB.storage;

import com.example.dataDB.DataBaseConnection;
import com.example.entity.Photographer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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

    private void createTablePhotographer() {
        String sql = """
            create table if not exists photographer(
            id bigSerial primary key,
            full_name varchar(255) not null,
            phone varchar(255) not null,
            specialization varchar(255) not null,
            base_rate decimal(5,2) not null);
        """;
    }
}
