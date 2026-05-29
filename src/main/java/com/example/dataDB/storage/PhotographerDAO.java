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

        try (Connection connection = DataBaseConnection.getConnection();
             PreparedStatement psmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            psmt.setString(1, photographer.getFullName());
            psmt.setString(2, photographer.getPhone());
            psmt.setString(3, photographer.getSpecialization());
            psmt.setDouble(4, photographer.getBaseRate());

            psmt.executeUpdate();

            try (var generatedKeys = psmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    photographer.setId(generatedKeys.getLong(1));
                }
            }
            System.out.println("Фотографа успішно збережено з ID: " + photographer.getId());

        } catch (SQLException e) {
            System.err.println("Помилка при збереженні фотографа: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public List<Photographer> getAllPhotographers() {
        String sql = """
                        select * from photographer;
                """;
        List<Photographer> photographers = new ArrayList<>();
        try (Connection connection = DataBaseConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                photographers.add(new Photographer(
                                rs.getLong("id"),
                                rs.getString("full_name"),
                                rs.getString("phone"),
                                rs.getString("specialization"),
                                rs.getDouble("base_rate")
                        )

                );
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
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
        try (Connection connection = DataBaseConnection.getConnection();
             Statement stmt = connection.createStatement()) {

            stmt.execute(sql);
            System.out.println("Таблиця створена");
        } catch (SQLException e) {
            System.err.println("Помилка при створенні таблиці: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}

