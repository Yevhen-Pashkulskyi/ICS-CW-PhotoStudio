package com.example.dataDB.storage;

import com.example.dataDB.DataBaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReportDAO {

    // Лістинг 2.1 – Список клієнтів заданого фотографа (наприклад, з ID = 1)
    public List<String[]> getClientsByPhotographer(Long photographerId) {
        List<String[]> result = new ArrayList<>();
        String sql = """
            SELECT c.full_name, o.event_date, s.session_name
            FROM clients c
            JOIN orders o ON c.id = o.client_id
            JOIN session_type s ON o.session_id = s.id
            WHERE o.photographer_id = ?
            ORDER BY o.event_date DESC;
            """;
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, photographerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    result.add(new String[]{
                            rs.getString("full_name"),
                            rs.getTimestamp("event_date").toString(),
                            rs.getString("session_name")
                    });
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return result;
    }

    // Лістинг 2.2 – Клієнти на літеру (наприклад, 'К%')
    public List<String[]> getClientsByLetter(String pattern) {
        List<String[]> result = new ArrayList<>();
        String sql = "SELECT full_name, phone, email FROM clients WHERE full_name LIKE ?;";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, pattern + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    result.add(new String[]{rs.getString("full_name"), rs.getString("phone"), rs.getString("email")});
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return result;
    }

    // Лістинг 2.3 – Замовлення за період
    public List<String[]> getOrdersInPeriod(Timestamp start, Timestamp end) {
        List<String[]> result = new ArrayList<>();
        String sql = "SELECT id, client_id, event_date, total_cost FROM orders WHERE event_date BETWEEN ? AND ?;";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setTimestamp(1, start);
            pstmt.setTimestamp(2, end);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    result.add(new String[]{
                            rs.getString("id"), rs.getString("client_id"),
                            rs.getTimestamp("event_date").toString(), rs.getString("total_cost")
                    });
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return result;
    }

    // Лістинг 2.4 – Кількість нових замовлень за тиждень (Агрегатна функція)
    public int getNewOrdersCountLastWeek() {
        String sql = "SELECT COUNT(id) AS new_orders_count FROM orders WHERE created_date >= CURRENT_DATE - INTERVAL '7 days';";
        try (Connection conn = DataBaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt("new_orders_count");
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    // Лістинг 2.5 – Скільки замовлень виконав кожен фотограф (Групування)
    public List<String[]> getOrdersCountPerPhotographer() {
        List<String[]> result = new ArrayList<>();
        String sql = """
            SELECT p.full_name, COUNT(o.id) AS total_orders
            FROM photographer p
            LEFT JOIN orders o ON p.id = o.photographer_id
            GROUP BY p.full_name;
            """;
        try (Connection conn = DataBaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                result.add(new String[]{rs.getString("full_name"), rs.getString("total_orders")});
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return result;
    }

    // Лістинг 2.6 – Фотографи з найбільшою кількістю замовлень (Предикат ALL)
    public List<String[]> getMostLoadedPhotographers() {
        List<String[]> result = new ArrayList<>();
        String sql = """
            SELECT p.full_name
            FROM photographer p
            JOIN orders o ON p.id = o.photographer_id
            GROUP BY p.id, p.full_name
            HAVING COUNT(o.id) >= ALL (
                SELECT COUNT(id) FROM orders GROUP BY photographer_id
            );
            """;
        try (Connection conn = DataBaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) { result.add(new String[]{rs.getString("full_name")}); }
        } catch (SQLException e) { e.printStackTrace(); }
        return result;
    }

    // Лістинг 2.7 – Найвища ставка у кожній спеціалізації (Корельований підзапит)
    public List<String[]> getTopPhotographersBySpecialization() {
        List<String[]> result = new ArrayList<>();
        String sql = """
            SELECT p1.specialization, p1.full_name, p1.base_rate
            FROM photographer p1
            WHERE p1.base_rate = (
                SELECT MAX(p2.base_rate) FROM photographer p2 WHERE p1.specialization = p2.specialization
            );
            """;
        try (Connection conn = DataBaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                result.add(new String[]{rs.getString("specialization"), rs.getString("full_name"), rs.getString("base_rate")});
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return result;
    }

    // Лістинг 2.8 (Варіант В) – Хто з фотографів не має замовлень на травень 2026 (NOT EXISTS)
    public List<String[]> getPhotographersWithNoOrdersInMay2026() {
        List<String[]> result = new ArrayList<>();
        String sql = """
            SELECT p.full_name
            FROM photographer p
            WHERE NOT EXISTS (
                SELECT 1 FROM orders o 
                WHERE o.photographer_id = p.id 
                  AND o.event_date BETWEEN '2026-05-01 00:00:00' AND '2026-05-31 23:59:59'
            );
            """;
        try (Connection conn = DataBaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) { result.add(new String[]{rs.getString("full_name")}); }
        } catch (SQLException e) { e.printStackTrace(); }
        return result;
    }

    // Лістинг 2.9 – Статус завантаженості (UNION з коментарем)
    public List<String[]> getPhotographerLoadingStatus() {
        List<String[]> result = new ArrayList<>();
        String sql = """
            SELECT p.full_name, 'Найбільш завантажений' AS status_comment
            FROM photographer p
            JOIN orders o ON p.id = o.photographer_id
            GROUP BY p.id, p.full_name
            HAVING COUNT(o.id) >= ALL (SELECT COUNT(id) FROM orders GROUP BY photographer_id)
            UNION
            SELECT p.full_name, 'Не має замовлень' AS status_comment
            FROM photographer p
            LEFT JOIN orders o ON p.id = o.photographer_id
            WHERE o.id IS NULL;
            """;
        try (Connection conn = DataBaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                result.add(new String[]{rs.getString("full_name"), rs.getString("status_comment")});
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return result;
    }
}