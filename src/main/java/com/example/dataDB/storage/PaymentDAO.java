package com.example.dataDB.storage;

import com.example.dataDB.DataBaseConnection;
import com.example.entity.Payment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class PaymentDAO {

    public void savePayment(Payment payment) {

        String sql = """
                insert into payment (order_id, payment_amount, payment_date, payment_method)
                values (?, ?, ?, ?);
                """;
        try(Connection connection = DataBaseConnection.getConnection();
            PreparedStatement psmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);) {

            psmt.setLong(1, payment.getOrderId().getId());
            psmt.setDouble(2, payment.getPaymentAmount());
            psmt.setTimestamp(3, payment.getPaymentDate());
            psmt.setString(4, payment.getPaymentMethod().toString());

            psmt.executeUpdate();

            try (var generatedKeys = psmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    payment.setId(generatedKeys.getLong(1));
                    System.out.println("Платіж успішно збережено з ID: " + payment.getId());
                }
            }

        }catch (SQLException e){
            System.err.println("Помилка при збереженні оплати: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void createTablePayment() {
        String sql = """
                create table if not exists payment (
                id bigserial primary key,
                order_id bigint not null references orders(id),
                payment_amount decimal(20,2) not null,
                payment_date timestamp not null,
                payment_method varchar(50) not null);
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
