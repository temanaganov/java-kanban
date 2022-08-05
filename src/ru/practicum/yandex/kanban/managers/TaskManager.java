package ru.practicum.yandex.kanban.managers;

import ru.practicum.yandex.kanban.models.Epic;
import ru.practicum.yandex.kanban.models.Subtask;
import ru.practicum.yandex.kanban.models.Task;

import java.util.Collection;
import java.util.List;

public interface TaskManager {
    Collection<Task> getAllTasks();

    Collection<Epic> getAllEpics();

    Collection<Subtask> getAllSubtasks();

    Task getTask(Integer id);

    Epic getEpic(Integer id);

    Task getSubtask(Integer id);

    Task createTask(Task task);

    Epic createEpic(Epic epic);

    Subtask createSubtask(Subtask subtask);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    void removeTask(Integer id);

    void removeEpic(Integer epicId);

    void removeSubtask(Integer subtaskId);

    void removeAllTasks();

    List<Subtask> getSubtasksByEpicId(Integer epicId);

    List<Task> getHistory();
}
