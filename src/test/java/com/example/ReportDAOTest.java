package com.example;

import com.example.dataDB.DataBaseConnection;
import com.example.dataDB.storage.ReportDAO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * СТУДЕНТ 3 тестує код СТУДЕНТА 2 (Клас ReportDAO)
 * Метод Білої Скриньки для getClientsByPhotographer(Long photographerId)
 */
public class ReportDAOTest {

    private ReportDAO reportDAO;
    private Connection mockConnection;
    private PreparedStatement mockPreparedStatement;
    private ResultSet mockResultSet;
    private MockedStatic<DataBaseConnection> mockedStaticDb;

    @BeforeEach
    void setUp() throws SQLException {
        reportDAO = new ReportDAO();
        mockConnection = mock(Connection.class);
        mockPreparedStatement = mock(PreparedStatement.class);
        mockResultSet = mock(ResultSet.class);

        mockedStaticDb = mockStatic(DataBaseConnection.class);
        mockedStaticDb.when(DataBaseConnection::getConnection).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
    }

    @AfterEach
    void tearDown() {
        mockedStaticDb.close();
    }

    // ТЕСТ 1: Шлях 1-3-6 (Фотограф без клієнтів)
    @Test
    void testGetClientsByPhotographer_NoClients() throws SQLException {
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        List<String[]> result = reportDAO.getClientsByPhotographer(5L);

        assertTrue(result.isEmpty(), "Список має бути порожнім");
        verify(mockPreparedStatement).setLong(1, 5L);
    }

    // ТЕСТ 2: Шлях 1-3-4-5A-5 (Клієнт із вказаною датою події)
    @Test
    void testGetClientsByPhotographer_WithValidDate() throws SQLException {
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true).thenReturn(false);

        Timestamp validDate = Timestamp.valueOf("2026-10-20 14:00:00");
        when(mockResultSet.getTimestamp("event_date")).thenReturn(validDate);
        when(mockResultSet.getString("full_name")).thenReturn("Іван Іванов");
        when(mockResultSet.getString("session_name")).thenReturn("Портрет");

        List<String[]> result = reportDAO.getClientsByPhotographer(5L);

        assertEquals(1, result.size());
        assertEquals("Іван Іванов", result.get(0)[0]);
        assertEquals("2026-10-20 14:00:00.0", result.get(0)[1], "Дата має бути конвертована у рядок");
    }

    // ТЕСТ 3: Шлях 1-3-4-5B-5 (Клієнт без дати)
    @Test
    void testGetClientsByPhotographer_WithNullDate() throws SQLException {
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true).thenReturn(false);

        // Симулюємо null у базі даних
        when(mockResultSet.getTimestamp("event_date")).thenReturn(null);
        when(mockResultSet.getString("full_name")).thenReturn("Марія Коваленко");
        when(mockResultSet.getString("session_name")).thenReturn("Весілля");

        List<String[]> result = reportDAO.getClientsByPhotographer(5L);

        assertEquals(1, result.size());
        assertEquals("Не вказано", result.get(0)[1], "Тернарний оператор має підставити заглушку");
    }

    // ТЕСТ 4: Перевірка на Null (Виявлення бага)
    @Test
    void testGetClientsByPhotographer_NullIdThrowsException() {
        assertThrows(NullPointerException.class, () -> {
            reportDAO.getClientsByPhotographer(null);
        }, "Передача null у setLong викличе NPE (знайдений баг!)");
    }
}