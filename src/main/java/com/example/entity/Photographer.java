package com.example.entity;

import com.example.service.Schedule;

import java.io.Serializable;

/**
 * Клас, що представляє фотографа.
 * Успадковує клас Person та використовує композицію (Schedule).
 */
public class Photographer extends Person implements Serializable {
    private String specialization;

    // Композиція: розклад є невід'ємною частиною фотографа
    private Schedule schedule;

    public Photographer(String name, String phoneNumber, String specialization) {
        super(name, phoneNumber);
        this.specialization = specialization;
        // Об'єкт розкладу створюється разом з фотографом
        this.schedule = new Schedule();
    }

    public String getSpecialization() { return specialization; }
    public Schedule getSchedule() { return schedule; }
}