package com.example.dataDB.storage;

import com.example.dataDB.DataBaseConnection;
import com.example.entity.Client;

import java.sql.*;

import java.util.ArrayList;
import java.util.List;

public class ClientDAO {

    public void saveClients(Client client) {
        String sql = """
                INSERT INTO clients (full_name, phone, email, is_regular_client, discount_rate)
                VALUES (?, ?, ?, ?, ?)
                """;
        try(Connection conn = DataBaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, client.getFullName());
            pstmt.setString(2, client.getPhone());
            pstmt.setString(3, client.getEmail());
            pstmt.setBoolean(4, client.isRegular());
            pstmt.setDouble(5, client.getDiscountRate());

            pstmt.executeUpdate();

            // Витягуємо ID клієнта
            try (var generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    long generatedId = generatedKeys.getLong(1);
                    client.setId(generatedId); // Записуємо ID в об'єкт (переконайся, що клас Person має метод setId)
                    System.out.println("Клієнта успішно збережено в БД з ID: " + generatedId);
                } else {
                    throw new SQLException("Не вдалося отримати згенерований ID для клієнта.");
                }
            }

        }catch (SQLException e){
            System.err.println("Помилка при збереженні клієнта: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void updateClient(Client client) {
        String sql = """
                update clients
                set is_regular_client = ?, discount_rate = ?
                where id = ?
                """;
        try(Connection connection = DataBaseConnection.getConnection();
        PreparedStatement pstmt = connection.prepareStatement(sql)){

            pstmt.setBoolean(1, client.isRegular());
            pstmt.setDouble(2, client.getDiscountRate());
            pstmt.setLong(3, client.getId());

            pstmt.executeUpdate();
            System.out.printf("Данні %s успішно оновлені", client.getFullName());

        }catch (SQLException e){
            System.err.println("Не вдалось оновити данні! " + e.getMessage());
        }
    }

    public List<Client> getClients() {
        List<Client> list = new ArrayList<>();
        String sql = "SELECT * FROM clients";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                list.add(new Client(
                        rs.getLong("id"),
                        rs.getString("full_name"),
                        rs.getString("phone"),
                        rs.getString("email"),
                        rs.getBoolean("is_regular_client"),
                        rs.getDouble("discount_rate")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void createTableClient() {
        String sql = """
                CREATE TABLE IF NOT EXISTS clients (
                id bigserial PRIMARY KEY,
                full_name varchar(255) not null,
                phone varchar(255) not null,
                email varchar(255) ,
                is_regular_client boolean not null,
                discount_rate decimal(5,2) not null
                );
                """;
        try(Connection connection = DataBaseConnection.getConnection();
            Statement stmt = connection.createStatement()){

            stmt.execute(sql);
            System.out.println("Таблиця clients створена (або вже існує)");
        }catch (SQLException e){
            System.err.println("Помилка при створенні таблиці clients: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
