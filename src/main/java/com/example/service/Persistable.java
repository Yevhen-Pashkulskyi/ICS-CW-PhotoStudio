package com.example.service;

import java.io.IOException;

/**
 * Інтерфейс (п. 1.4) для класів, що підтримують збереження
 * та завантаження даних у файл.
 * * Оголошує 'throws IOException', оскільки операції
 * з файлами можуть спричинити виняткові ситуації.
 */
public interface Persistable {

    /**
     * Зберігає поточний стан даних (колекцій) у файл.
     * @param path Шлях до файлу (напр., "data.json").
     * @throws IOException
     */
    void saveDataToFile(String path) throws IOException;

    /**
     * Завантажує та відновлює стан даних (колекцій) з файлу.
     * @param path Шлях до файлу (напр., "data.json").
     * @throws IOException
     */
    void loadDataFromFile(String path) throws IOException;
}
