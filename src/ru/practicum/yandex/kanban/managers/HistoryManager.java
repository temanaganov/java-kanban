package ru.practicum.yandex.kanban.managers;

import ru.practicum.yandex.kanban.models.Task;

import java.util.List;

public interface HistoryManager {
    void add(Task task);
    void remove(int id);
    void clear();
    List<Task> getHistory();
}
