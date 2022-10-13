package ru.practicum.yandex.kanban.managers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import ru.practicum.yandex.kanban.models.Epic;
import ru.practicum.yandex.kanban.models.Subtask;
import ru.practicum.yandex.kanban.models.Task;
import ru.practicum.yandex.kanban.server.KVTaskClient;
import ru.practicum.yandex.kanban.utils.GsonUtils;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpTaskManager extends FileBackedTasksManager {
    URI uri;
    Gson gson = GsonUtils.getInstance();
    KVTaskClient taskClient;

    public HttpTaskManager(URI uri) {
        super(null);
        try {
            this.uri = uri;
            taskClient = new KVTaskClient(uri);
        } catch (IOException | InterruptedException e) {
            throw new ManagerSaveException("Ошибка при подключении к KVServer");
        }
    }

    public void load() {
        try {
            Map<Integer, Task> tasks = gson.fromJson(
                    taskClient.load("tasks"),
                    new TypeToken<HashMap<Integer, Task>>() {
                    }.getType()
            );
            Map<Integer, Epic> epics = gson.fromJson(
                    taskClient.load("epics"),
                    new TypeToken<HashMap<Integer, Epic>>() {
                    }.getType()
            );
            Map<Integer, Subtask> subtasks = gson.fromJson(
                    taskClient.load("subtasks"),
                    new TypeToken<HashMap<Integer, Subtask>>() {
                    }.getType()
            );
            List<Task> historyList = gson.fromJson(
                    taskClient.load("history"),
                    new TypeToken<List<Task>>() {
                    }.getType()
            );
            HistoryManager history = new InMemoryHistoryManager();
            historyList.forEach(history::add);

            int startId = Integer.parseInt(taskClient.load("startId"));

            this.tasks = tasks;
            this.epics = epics;
            this.subtasks = subtasks;
            this.history = history;
            this.prioritizedTasks.addAll(tasks.values());
            this.prioritizedTasks.addAll(epics.values());
            this.prioritizedTasks.addAll(subtasks.values());
            this.id = startId;
        } catch (IOException | InterruptedException exception) {
            System.out.println("Ошибка при восстановлении данных");
        }
    }

    @Override
    protected void save() {
        try {
            taskClient.put("tasks", gson.toJson(tasks));
            taskClient.put("epics", gson.toJson(epics));
            taskClient.put("subtasks", gson.toJson(subtasks));
            taskClient.put("history", gson.toJson(history.getHistory()));
            taskClient.put("startId", gson.toJson(id));
        } catch (IOException | InterruptedException err) {
            throw new ManagerSaveException("Ошибка при сохранении данных");
        }
    }
}
