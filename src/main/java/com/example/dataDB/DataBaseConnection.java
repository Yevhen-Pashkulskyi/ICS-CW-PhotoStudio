package com.example.dataDB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBaseConnection {

    // Вказуємо адресу нашої бази даних PostgreSQL
    private static final String URL = System.getenv().getOrDefault("DB_URL","jdbc:postgresql://localhost:5432/DB");

    // логін у PostgreSQL
    private static final String USER = System.getenv("DB_USER");

    // пароль від бази даних
    private static final String PASSWORD = System.getenv("DB_PASSWORD");

    /**
     * Метод для отримання підключення до бази даних.
     * @return об'єкт Connection
     * @throws SQLException якщо підключення не вдалося
     */
    public static Connection getConnection() throws SQLException {
        if (USER == null || PASSWORD == null) {
            throw new SQLException("Помилка конфігурації: Змінні середовища DB_USER або DB_PASSWORD не встановлені!");
        }
        // DriverManager сам знайде потрібний драйвер і відкриє з'єднання
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}