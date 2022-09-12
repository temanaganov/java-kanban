package ru.practicum.yandex.kanban.managers;

import ru.practicum.yandex.kanban.models.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileBackedTasksManager extends InMemoryTaskManager implements TaskManager {
    public FileBackedTasksManager() {}

    public FileBackedTasksManager(
            Map<Integer, Task> tasks,
            Map<Integer, Epic> epics,
            Map<Integer, Subtask> subtasks,
            HistoryManager history,
            int startId
    ) {
        this.tasks = tasks;
        this.epics = epics;
        this.subtasks = subtasks;
        this.history = history;
        this.id = startId;
    }

    public static FileBackedTasksManager loadFromFile(Path path) {
        Map<Integer, Task> allTasks = new HashMap<>();
        Map<Integer, Task> tasks = new HashMap<>();
        Map<Integer, Epic> epics = new HashMap<>();
        Map<Integer, Subtask> subtasks = new HashMap<>();
        HistoryManager historyManager = new InMemoryHistoryManager();
        int startId = 0;

        try {
            String file = Files.readString(path);
            String[] rows = file.split(System.lineSeparator());

            for (int i = 1; i < rows.length - 2; i++) {
                Task task = fromString(rows[i]);
                String type = rows[i].split(",")[1];

                if (task.getId() > startId) startId = task.getId();

                switch (TaskType.valueOf(type)) {
                    case TASK -> {
                        allTasks.put(task.getId(), task);
                        tasks.put(task.getId(), task);
                    }
                    case EPIC -> {
                        allTasks.put(task.getId(), task);
                        epics.put(task.getId(), (Epic) task);
                    }
                    case SUBTASK -> {
                        Subtask subtask = (Subtask) task;
                        allTasks.put(task.getId(), task);
                        subtasks.put(task.getId(), subtask);
                        epics.get(subtask.getEpicId()).addSubtask(subtask);
                    }
                }
            }

            List<Integer> historyList = HistoryManagerUtils.historyFromString(rows[rows.length - 1]);

            for (Integer id : historyList) {
                historyManager.add(allTasks.get(id));
            }

        } catch (IOException err) {
            throw new ManagerSaveException("Ошибка при восстановлении данных");
        }

        return new FileBackedTasksManager(tasks, epics, subtasks, historyManager, startId);
    }

    private void save() {
        try {
            Path path = Path.of("data.csv");
            String head = "id,type,name,status,description,epic" + System.lineSeparator();
            String data = head +
                    TaskManager.tasksToString(this) +
                    System.lineSeparator() +
                    HistoryManagerUtils.historyToString(history);

            Files.writeString(path, data);
        } catch (IOException err) {
            throw new ManagerSaveException("Ошибка при сохранении данных");
        }
    }

    static private Task fromString(String value) {
        String[] splittedValue = value.split(",");
        int id = Integer.parseInt(splittedValue[0]);
        String type = splittedValue[1];
        String title = splittedValue[2];
        TaskStatus status = TaskStatus.valueOf(splittedValue[3]);
        String description = splittedValue[4];
        Integer epicId = TaskType.valueOf(type) == TaskType.SUBTASK ? Integer.parseInt(splittedValue[5]) : null;

        return switch (TaskType.valueOf(type)) {
            case TASK -> new Task(id, status, title, description);
            case EPIC -> new Epic(id, status, title, description);
            case SUBTASK -> new Subtask(id, status, title, description, epicId);
        };
    }

    @Override
    public Task getTask(Integer id) {
        Task task = super.getTask(id);
        save();
        return task;
    }

    @Override
    public Epic getEpic(Integer id) {
        Epic epic = super.getEpic(id);
        save();
        return epic;
    }

    @Override
    public Subtask getSubtask(Integer id) {
        Subtask subtask = super.getSubtask(id);
        save();
        return subtask;
    }

    @Override
    public Task createTask(Task task) {
        Task newTask = super.createTask(task);
        save();
        return newTask;
    }

    @Override
    public Epic createEpic(Epic epic) {
        Epic newEpic = super.createEpic(epic);
        save();
        return newEpic;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        Subtask newSubtask = super.createSubtask(subtask);
        save();
        return newSubtask;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void removeTask(Integer taskId) {
        super.removeTask(taskId);
        save();
    }

    @Override
    public void removeEpic(Integer epicId) {
        super.removeEpic(epicId);
        save();
    }

    @Override
    public void removeSubtask(Integer subtaskId) {
        super.removeSubtask(subtaskId);
        save();
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }
}
