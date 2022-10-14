package ru.practicum.yandex.kanban.managers;

import ru.practicum.yandex.kanban.models.Task;
import ru.practicum.yandex.kanban.models.Epic;
import ru.practicum.yandex.kanban.models.Subtask;
import ru.practicum.yandex.kanban.models.TaskType;
import ru.practicum.yandex.kanban.models.TaskStatus;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private final Path path;

    public FileBackedTasksManager(Path path) {
        this.path = path;
    }

    public FileBackedTasksManager(
            Path path,
            Map<Integer, Task> tasks,
            Map<Integer, Epic> epics,
            Map<Integer, Subtask> subtasks,
            HistoryManager history,
            int startId
    ) {
        this.path = path;
        this.tasks = tasks;
        this.epics = epics;
        this.subtasks = subtasks;
        this.history = history;
        this.id = startId;
        this.prioritizedTasks.addAll(tasks.values());
        this.prioritizedTasks.addAll(epics.values());
        this.prioritizedTasks.addAll(subtasks.values());
    }

    public static FileBackedTasksManager loadFromFile(Path path) {
        if (!Files.exists(path)) {
            return new FileBackedTasksManager(path);
        }

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

                allTasks.put(task.getId(), task);

                switch (TaskType.valueOf(type)) {
                    case TASK -> {
                        tasks.put(task.getId(), task);
                    }
                    case EPIC -> {
                        epics.put(task.getId(), (Epic) task);
                    }
                    case SUBTASK -> {
                        Subtask subtask = (Subtask) task;
                        subtasks.put(task.getId(), subtask);
                        epics.get(subtask.getEpicId()).addSubtask(subtask);
                    }
                }
            }

            List<Integer> historyList = HistoryManagerUtils.historyFromString(rows[rows.length - 1]);

            historyList.forEach(id -> historyManager.add(allTasks.get(id)));

        } catch (IOException err) {
            throw new ManagerSaveException("Ошибка при восстановлении данных");
        }

        return new FileBackedTasksManager(path, tasks, epics, subtasks, historyManager, startId);
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
    public Task updateTask(int id, Task task) {
        Task updatedTask = super.updateTask(id, task);
        save();
        return updatedTask;
    }

    @Override
    public Epic updateEpic(int id, Epic epic) {
        Epic updatedEpic = super.updateEpic(id, epic);
        save();
        return updatedEpic;
    }

    @Override
    public Subtask updateSubtask(int id, Subtask subtask) {
        Subtask updatedSubtask = super.updateSubtask(id, subtask);
        save();
        return updatedSubtask;
    }

    @Override
    public void removeTask(int taskId) {
        super.removeTask(taskId);
        save();
    }

    @Override
    public void removeEpic(int epicId) {
        super.removeEpic(epicId);
        save();
    }

    @Override
    public void removeSubtask(int subtaskId) {
        super.removeSubtask(subtaskId);
        save();
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    protected void save() {
        try {
            String head = "id,type,name,status,description,start time,duration,epic" + System.lineSeparator();
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
        String[] splitValue = value.split(",");
        int id = Integer.parseInt(splitValue[0]);
        String type = splitValue[1];
        String title = splitValue[2];
        TaskStatus status = TaskStatus.valueOf(splitValue[3]);
        String description = splitValue[4];
        Instant startTime = Instant.ofEpochMilli(Long.parseLong(splitValue[5]));
        long duration = Long.parseLong(splitValue[6]);
        Integer epicId = TaskType.valueOf(type) == TaskType.SUBTASK ? Integer.parseInt(splitValue[7]) : null;

        return switch (TaskType.valueOf(type)) {
            case TASK -> new Task(id, status, title, description, startTime, duration);
            case EPIC -> new Epic(id, status, title, description, startTime, duration);
            case SUBTASK -> new Subtask(id, status, title, description, epicId, startTime, duration);
        };
    }
}
