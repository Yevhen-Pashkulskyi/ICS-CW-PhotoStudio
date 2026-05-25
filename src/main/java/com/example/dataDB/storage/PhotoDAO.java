package com.example.dataDB.storage;

import com.example.dataDB.DataBaseConnection;
import com.example.entity.Client;
import com.example.entity.Photo;
import com.example.model.Order;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PhotoDAO {

    public void savePhoto(Photo photo) {
        String sql = """
                insert into photos (file_path, order_id)
                values (?, ?);
                """;
        try (Connection connection = DataBaseConnection.getConnection();
             PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, photo.getFilePath());
            pstm.setLong(2, photo.getOrderId());

            pstm.executeUpdate();
            System.out.println("Фото успішно збереглось!" + photo.getFilePath());

        } catch (SQLException e) {
            System.err.println("Не вдалось зберегти фото: " + e.getMessage());
            throw new RuntimeException();
        }

    }

    public List<Photo> loadPhotosByClient(Client client) {

        List<Photo> photos = new ArrayList<>();
        String sql = """
                select id, file_path, order_id
                from photos
                join orders on photos.order_id = orders.id
                where orders.client_id=? 
                order by photos.id desc;
                """;
        try (Connection connection = DataBaseConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setLong(1, client.getId());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                photos.add(new Photo(rs.getLong("id"),
                        rs.getString("file_path"),
                        rs.getLong("order_id")));
            }

        } catch (SQLException e) {
            System.err.println("Помилка при завантаженні фото клієнта: " + e.getMessage());
            throw new RuntimeException();
        }
        return photos;
    }

    public List<Photo> loadPhotosByOrder(Long orderId) {
        List<Photo> photos = new ArrayList<>();
        String sql = """
                SELECT p.id, p.file_path, p.order_id
                FROM photos p
                JOIN orders o ON p.order_id = o.id
                WHERE p.order_id = ?
                """;
        try (Connection connection = DataBaseConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql);) {

            pstmt.setLong(1, orderId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    photos.add(new Photo(
                            rs.getLong("id"),
                            rs.getString("file_path"),
                            rs.getLong("order_id")
                    ));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return photos;
    }

    public void createTablePhoto() {
        String sql = """
                create table if not exists photos (
                id bigserial primary key,
                file_path varchar(255),
                order_id bigint not null references orders(id) on delete cascade
                );
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
