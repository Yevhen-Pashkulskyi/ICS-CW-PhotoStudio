package com.example.control;

import com.example.dataDB.storage.*;
import com.example.entity.Client;
import com.example.entity.Photo;
import com.example.entity.Photographer;
import com.example.entity.Payment;
import com.example.entity.Order;
import com.example.entity.SessionType;
import com.example.util.OrderStatus;
import lombok.Getter;
import org.postgresql.core.ConnectionFactory;

import java.util.List;

public class DatabaseManager {

    private final ClientDAO clientDAO = new ClientDAO();
    private final OrderDAO orderDAO = new OrderDAO();
    private final PhotographerDAO photographerDAO = new PhotographerDAO();
    private final SessionTypeDAO sessionTypeDAO = new SessionTypeDAO();
    private final PaymentDAO paymentDAO = new PaymentDAO();
    private final PhotoDAO photoDAO = new PhotoDAO();

    @Getter
    private final ReportDAO reportDAO = new ReportDAO();

    public DatabaseManager() {
        initializeDatabase();
    }

    public Client getClientByPhone(String phone) {
        return getAllClients().stream()
                .filter(c -> c.getPhone().equals(phone))
                .findFirst().orElse(null);
    }

    public boolean saveOrder(Order o) {
        try {
            orderDAO.saveOrder(o);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void updateOrderStatus(Long orderId, OrderStatus orderStatus) {
        orderDAO.updateOrderStatus(orderId, orderStatus);
    }

    public boolean savePayment(Payment p) {
        try {
            paymentDAO.savePayment(p);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void saveClient(Client client) {
        clientDAO.saveClients(client);
    }

    public void updateClient(Client client) {
        clientDAO.updateClient(client);
    }

    public void savePhoto(Photo photo) {
        photoDAO.savePhoto(photo);
    }

    public List<Client> getAllClients() {
        return clientDAO.getClients();
    }

    public List<Order> getAllOrders() {
        return orderDAO.getAllOrders();
    }

    public List<Photographer> getAllPhotographers() {
        return photographerDAO.getAllPhotographers();
    }

    public List<SessionType> getAllSessionTypes() {
        return sessionTypeDAO.getSessionType();
    }

    public List<Photo> getPhotosByOrderId(Long orderId) {
        return photoDAO.loadPhotosByOrder(orderId);
    }

    private void initializeDatabase() {
        try {
            System.out.println("Перевірка та ініціалізація таблиць БД...");
            clientDAO.createTableClient();
            photographerDAO.createTablePhotographer();
            sessionTypeDAO.createTableSessionType();
            orderDAO.createdTableOrder();
            photoDAO.createTablePhoto();
            paymentDAO.createTablePayment();
            System.out.println("Ініціалізація бази даних успішно завершена!");
        } catch (Exception e) {
            System.err.println("КРИТИЧНА ПОМИЛКА: Не вдалося ініціалізувати таблиці бази даних!");
            e.printStackTrace();
        }
    }
}