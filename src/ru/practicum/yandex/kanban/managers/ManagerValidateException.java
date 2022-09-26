package ru.practicum.yandex.kanban.managers;

public class ManagerValidateException extends RuntimeException{
    public ManagerValidateException(String message) {
        super(message);
    }
}
