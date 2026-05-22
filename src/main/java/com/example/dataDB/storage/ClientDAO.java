package com.example.dataDB.storage;

import com.example.dataDB.DataBaseConnection;
import com.example.entity.Client;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ClientDAO {

    public void saveClients(Client client) {
        String sql = """
                INSERT INTO clients (full_name, phone, email, is_regular_client, discount_rate)
                VALUES (?, ?, ?, ?, ?)
                """;
        try(Connection conn = DataBaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)){

            pstmt.setString(1, client.getFullName());
            pstmt.setString(2, client.getPhone());
            pstmt.setString(3, client.getEmail());
            pstmt.setBoolean(4, client.isRegular());
            pstmt.setDouble(5, client.getDiscountRate());

            pstmt.executeUpdate();
            System.out.println("Клієнта успішно збережено в БД!");

        }catch (SQLException e){
            System.err.println("Помилка при збереженні клієнта: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private void createTableClient() {
        String sql = """
                CREATE TABLE IF NOT EXISTS clients (
                id bigserial PRIMARY KEY,
                full_name varchar(255) not null,
                phone varchar(255) not null,
                email varchar(255) ,
                is_regular_client boolean not null,
                discount_rate decimal not null
                ); 
                """;
        try {} catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
