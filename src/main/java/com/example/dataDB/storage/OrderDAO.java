package com.example.dataDB.storage;

import com.example.dataDB.DataBaseConnection;
import com.example.model.Order;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class OrderDAO {

    public void saveOrder(Order order) {

        String sql = """
                INSERT INTO orders (client_id, photographer_id, session_id, created_date, event_date,
                delivery_date, order_status, total_cost)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?);
        """;

        try(Connection connection = DataBaseConnection.getConnection();
            PreparedStatement psmt = connection.prepareStatement(sql)){

            psmt.setLong(1,order.getClient().getId());
            psmt.setLong(2,order.getPhotographer().getId());
            psmt.setLong(3, order.getSessionType().getId());
            psmt.setTimestamp(4, order.getCreatedDate());
            psmt.setTimestamp(5, order.getEventDate());
            psmt.setTimestamp(6, order.getDeliveryDate());
            psmt.setString(7, order.getStatus().name());
            psmt.setDouble(8, order.getTotalCost());

            psmt.executeUpdate();
            System.out.println("Ордер створено");

        }catch (SQLException e){
            System.err.println("Помилка при збереження ордеру: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private void createdTableOrder() {
        String sql = """
                CREATE TABLE IF NOT EXISTS orders (
                id bigserial PRIMARY KEY,
                client_id bigint not null references clients(id),
                photographer_id bigint not null references photographer(id),
                session_id bigint not null references session_type(id),
                created_date timestamp not null,
                event_date timestamp not null,
                delivery_date timestamp not null,
                order_status varchar(20) not null,
                total_cost decimal (20,2) not null);
        """;
    }
}
