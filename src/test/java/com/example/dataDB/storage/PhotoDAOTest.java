package com.example.dataDB.storage;

import com.example.dataDB.DataBaseConnection;
import com.example.entity.Client;
import com.example.entity.Photo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * СТУДЕНТ 1 тестує код СТУДЕНТА 3 (Клас PhotoDAO)
 * Метод Білої Скриньки для getPhotosByOrder(Client client) та loadPhotosByOrder(Long orderId)
 */
public class PhotoDAOTest {

    private PhotoDAO photoDAO;
    private Connection mockConnection;
    private PreparedStatement mockPreparedStatement;
    private ResultSet mockResultSet;
    private MockedStatic<DataBaseConnection> mockedStaticDb;

    @BeforeEach
    void setUp() throws SQLException {
        photoDAO = new PhotoDAO();
        mockConnection = mock(Connection.class);
        mockPreparedStatement = mock(PreparedStatement.class);
        mockResultSet = mock(ResultSet.class);

        // Перехоплюємо статичний виклик підключення до БД
        mockedStaticDb = mockStatic(DataBaseConnection.class);
        mockedStaticDb.when(DataBaseConnection::getConnection).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
    }

    @AfterEach
    void tearDown() {
        // Закриваємо мок статичного класу після кожного тесту
        mockedStaticDb.close();
    }

    // ТЕСТ 1: Шлях 1-2-3-6 для getPhotosByOrder(Client) (Даних немає)
    @Test
    void testGetPhotosByOrder_ClientHasNoPhotos() throws SQLException {
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        Client mockClient = mock(Client.class);
        when(mockClient.getId()).thenReturn(5L);

        List<Photo> result = photoDAO.getPhotosByOrder(mockClient);

        assertNotNull(result);
        assertTrue(result.isEmpty(), "Список має бути порожнім, якщо у клієнта немає фото");
        verify(mockPreparedStatement).setLong(1, 5L);
    }

    // ТЕСТ 2: Шлях 1-2-3-4-3-6 для getPhotosByOrder(Client) (Дані є)
    @Test
    void testGetPhotosByOrder_ClientHasPhotos() throws SQLException {
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true).thenReturn(false);
        when(mockResultSet.getLong("id")).thenReturn(10L);
        when(mockResultSet.getString("file_path")).thenReturn("/images/photo1.jpg");
        when(mockResultSet.getLong("order_id")).thenReturn(1L);

        Client mockClient = mock(Client.class);
        when(mockClient.getId()).thenReturn(5L);

        List<Photo> result = photoDAO.getPhotosByOrder(mockClient);

        assertEquals(1, result.size(), "Має повернутися 1 фотографія");
        assertEquals("/images/photo1.jpg", result.get(0).getFilePath());
        verify(mockPreparedStatement).setLong(1, 5L);
    }

    // ТЕСТ 3: Тестування loadPhotosByOrder(Long orderId) (Поліморфне тестування за ID)
    @Test
    void testLoadPhotosByOrder_WithValidId() throws SQLException {
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true).thenReturn(false);
        when(mockResultSet.getLong("id")).thenReturn(100L);
        when(mockResultSet.getString("file_path")).thenReturn("/images/order100.jpg");
        when(mockResultSet.getLong("order_id")).thenReturn(15L);

        List<Photo> result = photoDAO.loadPhotosByOrder(15L);

        assertEquals(1, result.size());
        assertEquals("/images/order100.jpg", result.get(0).getFilePath());
        verify(mockPreparedStatement).setLong(1, 15L);
    }

    // ТЕСТ 4: Перевірка на Null Client у getPhotosByOrder (Виявлення бага)
    @Test
    void testGetPhotosByOrder_NullClient_ThrowsException() {
        assertThrows(NullPointerException.class, () -> {
            photoDAO.getPhotosByOrder(null);
        }, "Метод має впасти з NPE при передачі null замість Client (знайдений баг!)");
    }

    // ТЕСТ 5: Перевірка на Null ID у loadPhotosByOrder (Виявлення бага unboxing)
    @Test
    void testLoadPhotosByOrder_NullOrderId_ThrowsException() {
        assertThrows(NullPointerException.class, () -> {
            photoDAO.loadPhotosByOrder(null);
        }, "Передача null у setLong викликає unboxing і кине NPE (знайдений баг!)");
    }
}