package com.example.dataDB.storage;

import com.example.dataDB.DataBaseConnection;
import com.example.entity.Client;
import com.example.entity.Photo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PhotoDAO {

    private static final String INSERT_PHOTO_SQL = "INSERT INTO photos (file_path, order_id) VALUES (?, ?);";
    private static final String SELECT_BY_ORDER_SQL = "SELECT id, file_path, order_id FROM photos WHERE order_id = ?;";

    public void savePhoto(Photo photo) {
        String sql = """
                insert into photos (file_path, order_id)
                values (?, ?);
                """;
        try (Connection connection = DataBaseConnection.getConnection();
             PreparedStatement insertStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);) {

            insertStatement.setString(1, photo.getFilePath());
            insertStatement.setLong(2, photo.getOrderId());

            insertStatement.executeUpdate();

            try (var generatedKeys = insertStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    photo.setId(generatedKeys.getLong(1));
                }
            }
            System.out.println("Фото успішно збереглось!" + photo.getFilePath());

        } catch (SQLException e) {
            System.err.println("Не вдалось зберегти фото: " + e.getMessage());
            throw new RuntimeException(e);
        }

    }

    public List<Photo> getPhotosByOrder(Client client) {

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
                photos.add(mapRowToPhoto(rs));
            }

        } catch (SQLException e) {
            System.err.println("Помилка при завантаженні фото клієнта: " + e.getMessage());
            throw new RuntimeException(e);
        }
        return photos;
    }

    // Метод для мапінгу
    private Photo mapRowToPhoto(ResultSet rs) throws SQLException {
        return new Photo(
                rs.getLong("id"),
                rs.getString("file_path"),
                rs.getLong("order_id")
        );
    }

    public List<Photo> loadPhotosByOrder(Long orderId) {
        List<Photo> photos = new ArrayList<>();
        String sql = """
                SELECT id, file_path, order_id
                FROM photos 
                WHERE order_id = ?
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
            System.err.println("Помилка при завантаженні фото замовлення: " + e.getMessage());
            throw new RuntimeException(e);
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
