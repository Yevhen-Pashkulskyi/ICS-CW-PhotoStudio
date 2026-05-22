package com.example.dataDB.storage;

import com.example.dataDB.DataBaseConnection;
import com.example.entity.Photographer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PhotographerDAO {

    public void savePhotographer(Photographer photographer) {

        String sql = """
                insert into photographer(fullName, phone, specialization, baseRate)
                values(?, ?, ?, ?, ?)
                """;
        try(Connection connection = DataBaseConnection.getConnection();
            PreparedStatement psmt = connection.prepareStatement(sql)){

            psmt.setString(1, photographer.getFullName());
            psmt.setString(2, photographer.getPhone());
            psmt.setString(3, photographer.getSpecialization());
            psmt.setDouble(4, photographer.getBaseRate());

        }catch(SQLException e){
            System.err.println("Помилка при збереженні фотографа: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private void createTablePhotographer() {
        String sql = """
            create table if not exists photographer(
            id bigSerial primary key,
            fullName varchar(255) not null,
            phone varchar(255) not null,
            specialization varchar(255) not null,
            baseRate decimal(5,2) not null);
        """;
    }
}
