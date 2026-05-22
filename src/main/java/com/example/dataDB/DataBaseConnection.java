package com.example.dataDB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBaseConnection {

    // Вказуємо адресу нашої бази даних PostgreSQL
    // Зміни "photo_studio_db" на реальну назву твоєї бази даних
    private static final String URL = "";

    // логін у PostgreSQL
    private static final String USER = "";

    // пароль від бази даних
    private static final String PASSWORD = "";

    /**
     * Метод для отримання підключення до бази даних.
     * @return об'єкт Connection
     * @throws SQLException якщо підключення не вдалося
     */
    public static Connection getConnection() throws SQLException {
        // DriverManager сам знайде потрібний драйвер і відкриє з'єднання
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}