package com.example.model;

import com.example.entity.Client;
import com.example.entity.Photo;
import com.example.entity.Photographer;
import com.example.util.OrderStatus;
import com.example.service.SessionType;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Центральний клас "Замовлення".
 * Використовує композицію (SessionType, List<Photo>)
 * та асоціацію (Client, Photographer).
 */
public class Order implements Serializable {
    private String id;
    private LocalDateTime orderDate;
    private OrderStatus status;
    private double totalCost;

    private Client client; // Асоціація
    private Photographer photographer; // Асоціація

    private SessionType sessionType; // Композиція
    private List<Photo> photos; // Композиція

    public Order(Client client, Photographer photographer, SessionType sessionType) {
        this.id = UUID.randomUUID().toString();
        this.client = client;
        this.photographer = photographer;
        this.sessionType = sessionType;
        this.orderDate = LocalDateTime.now();
        this.status = OrderStatus.NEW;
        this.photos = new ArrayList<>(); // Ініціалізація колекції
        this.totalCost = calculateTotalCost(); // Розрахунок при створенні
    }

    /**
     * Розраховує фінальну вартість замовлення.
     * Враховує знижку 10% для постійних клієнтів.
     */
    public double calculateTotalCost() {
        double currentCost = sessionType.getBasePrice();
        if (client.isRegular()) {
            currentCost *= 0.90; // Знижка 10%
        }
        this.totalCost = currentCost;
        return totalCost;
    }

    // Геттери та сеттери (скорочено для прикладу)
    public String getId() { return id; }
    public Client getClient() { return client; }
    public Photographer getPhotographer() { return photographer; }
    public SessionType getSessionType() { return sessionType; }
    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
    public LocalDateTime getOrderDate() { return orderDate; }
    public double getTotalCost() { return totalCost; }
    public List<Photo> getPhotos() { return photos; }
}