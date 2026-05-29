package com.example.dataDB.storage;

import com.example.dataDB.DataBaseConnection;
import com.example.entity.Client;
import com.example.entity.Photographer;
import com.example.entity.Order;
import com.example.entity.SessionType;
import com.example.util.OrderStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static java.sql.Types.*;

public class OrderDAO {

    public void saveOrder(Order order) {

        String sql = """
                        INSERT INTO orders (client_id, photographer_id, session_id, created_date, event_date,
                        delivery_date, order_status, total_cost)
                        VALUES (?, ?, ?, ?, ?, ?, ?, ?);
                """;

        try (Connection connection = DataBaseConnection.getConnection();
             PreparedStatement psmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);) {

            psmt.setLong(1, order.getClient().getId());
            psmt.setLong(2, order.getPhotographer().getId());
            psmt.setLong(3, order.getSessionType().getId());
            psmt.setTimestamp(4, order.getCreatedDate());
            psmt.setTimestamp(5, order.getEventDate());
            if (order.getDeliveryDate() != null) {
                psmt.setTimestamp(6, order.getDeliveryDate());
            } else {
                psmt.setNull(6, TIMESTAMP);
            }
            psmt.setString(7, order.getStatus().name());
            psmt.setDouble(8, order.getTotalCost());

            psmt.executeUpdate();

            // Витягуємо згенерований базою ID і записуємо в наш Order
            try (var generatedKeys = psmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    long generatedId = generatedKeys.getLong(1);
                    order.setId(generatedId);
                    System.out.println("Ордер створено в БД з ID: " + generatedId);
                } else {
                    throw new SQLException("Не вдалося отримати згенерований ID для замовлення.");
                }
            }

        } catch (SQLException e) {
            System.err.println("Помилка при збереження ордеру: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void updateOrderStatus(Long orderId, OrderStatus orderStatus) {
        String sql = """
                        UPDATE orders
                        set order_status = ?
                        WHERE id = ?;
                """;
        try (Connection connection = DataBaseConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, orderStatus.name());
            pstmt.setLong(2, orderId);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        String sql = """
                SELECT o.*,
                       c.id AS cid, c.full_name AS cname, c.phone AS cphone, c.email AS cemail, c.is_regular_client, c.discount_rate,
                       p.id AS pid, p.full_name AS pname, p.phone AS pphone, p.specialization, p.base_rate,
                       s.id AS sid, s.session_name, s.durations_hours, s.price
                FROM orders o
                JOIN clients c ON o.client_id = c.id
                JOIN photographer p ON o.photographer_id = p.id
                JOIN session_type s ON o.session_id = s.id
                """;
        try (Connection connectionq = DataBaseConnection.getConnection();
             PreparedStatement pstmt = connectionq.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Client client = new Client(rs.getLong("cid"), rs.getString("cname"), rs.getString("cphone"),
                        rs.getString("cemail"), rs.getBoolean("is_regular_client"), rs.getDouble("discount_rate"));
                Photographer photographer = new Photographer(rs.getLong("pid"), rs.getString("pname"), rs.getString("pphone"),
                        rs.getString("specialization"), rs.getDouble("base_rate"));
                SessionType sessionType = new SessionType(rs.getLong("sid"), rs.getString("session_name"),
                        rs.getInt("durations_hours"), rs.getDouble("price"));

                Order order = new Order(
                        rs.getLong("id"),
                        client,
                        photographer,
                        sessionType,
                        rs.getTimestamp("created_date"),
                        rs.getTimestamp("event_date"),
                        rs.getTimestamp("delivery_date"),
                        OrderStatus.valueOf(rs.getString("order_status")),
                        rs.getDouble("total_cost"));

                orders.add(order);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    public void createdTableOrder() {
        String sql = """
                        CREATE TABLE IF NOT EXISTS orders (
                        id bigserial PRIMARY KEY,
                        client_id bigint not null references clients(id),
                        photographer_id bigint not null references photographer(id),
                        session_id bigint not null references session_type(id),
                        created_date timestamp not null,
                        event_date timestamp not null,
                        delivery_date timestamp,
                        order_status varchar(20) not null,
                        total_cost decimal (20,2) not null);
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
