package ru.practicum.yandex.kanban.managers;

import ru.practicum.yandex.kanban.models.Epic;
import ru.practicum.yandex.kanban.models.Subtask;
import ru.practicum.yandex.kanban.models.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    List<Task> getAllTasks();

    List<Epic> getAllEpics();

    List<Subtask> getAllSubtasks();

    Task getTask(Integer id);

    Epic getEpic(Integer id);

    Task getSubtask(Integer id);

    Task createTask(Task task);

    Epic createEpic(Epic epic);

    Subtask createSubtask(Subtask subtask);

    Task updateTask(int id, Task task);

    Epic updateEpic(int id, Epic epic);

    Subtask updateSubtask(int id, Subtask subtask);

    void removeTask(int id);

    void removeEpic(int epicId);

    void removeSubtask(int subtaskId);

    void removeAllTasks();

    List<Subtask> getSubtasksByEpicId(int epicId);

    List<Task> getHistory();

    static String tasksToString(TaskManager manager) {
        StringBuilder sb = new StringBuilder();
        List<Task> tasksList = new ArrayList<>();

        tasksList.addAll(manager.getAllTasks());
        tasksList.addAll(manager.getAllEpics());
        tasksList.addAll(manager.getAllSubtasks());

        for (Task task : tasksList) {
            sb.append(task).append(System.lineSeparator());
        }

        return sb.toString();
    }
}
