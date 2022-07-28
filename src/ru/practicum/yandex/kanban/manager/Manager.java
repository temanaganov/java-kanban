package ru.practicum.yandex.kanban.manager;

import ru.practicum.yandex.kanban.models.Epic;
import ru.practicum.yandex.kanban.models.Subtask;
import ru.practicum.yandex.kanban.models.Task;
import ru.practicum.yandex.kanban.models.TaskStatus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class Manager {
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Epic> epics;
    private final HashMap<Integer, Subtask> subtasks;
    private int id;

    public Manager() {
        this.id = 0;
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subtasks = new HashMap<>();
    }

    public int getNextId() {
        return ++id;
    }

    public Collection<Task> getAllTasks() {
        return tasks.values();
    }

    public Collection<Epic> getAllEpics() {
        return epics.values();
    }

    public Collection<Subtask> getAllSubtasks() {
        return subtasks.values();
    }

    public Task getTaskById(Integer id) {
        return tasks.get(id);
    }

    public Task createTask(Task task) {
        task.setId(getNextId());
        tasks.put(task.getId(), task);

        return task;
    }

    public Epic createEpic(Epic epic) {
        epic.setId(getNextId());
        epics.put(epic.getId(), epic);

        return epic;
    }

    public Subtask createSubtask(Subtask subtask) {
        Epic epic = epics.get(subtask.getEpicId());

        subtask.setId(getNextId());
        subtasks.put(subtask.getId(), subtask);
        epic.addSubtask(subtask);
        updateEpicStatus(subtask.getEpicId());

        return subtask;
    }

    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    private void updateEpicStatus(Integer epicId) {
        Epic epic = epics.get(epicId);

        if (epic.getSubtasks().isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }

        int newCount = 0;
        int doneCount = 0;

        for (Integer subtaskId : epic.getSubtasks()) {
            if (subtasks.get(subtaskId).getStatus() == TaskStatus.NEW) newCount += 1;
            if (subtasks.get(subtaskId).getStatus() == TaskStatus.DONE) doneCount += 1;
        }

        if (epic.getSubtasks().size() == newCount) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }
        if (epic.getSubtasks().size() == doneCount) {
            epic.setStatus(TaskStatus.DONE);
            return;
        }

        epic.setStatus(TaskStatus.IN_PROGRESS);
    }

    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        updateEpicStatus(subtask.getEpicId());
    }

    public void removeTaskById(Integer id) {
        tasks.remove(id);
    }

    public void removeEpicById(Integer epicId) {
        Epic epic = epics.get(epicId);

        for (Integer subtaskId : epic.getSubtasks()) {
            subtasks.remove(subtaskId);
        }

        epics.remove(epicId);
    }

    public void removeSubtaskById(Integer subtaskId) {
        Subtask subtask = subtasks.get(subtaskId);
        Epic epic = epics.get(subtask.getEpicId());

        epic.removeSubtask(subtask);
        subtasks.remove(subtaskId);
        updateEpicStatus(subtask.getEpicId());
    }

    public void removeAllTasks() {
        tasks.clear();
        epics.clear();
        subtasks.clear();
    }

    public ArrayList<Subtask> getSubtasksByEpicId(Integer epicId) {
        ArrayList<Subtask> subtasksOfEpic = new ArrayList<>();
        Epic epic = epics.get(epicId);

        for (Integer subtaskId : epic.getSubtasks()) {
            subtasksOfEpic.add(subtasks.get(subtaskId));
        }

        return subtasksOfEpic;
    }
}
