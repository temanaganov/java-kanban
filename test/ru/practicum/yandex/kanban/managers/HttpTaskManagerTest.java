package ru.practicum.yandex.kanban.managers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.yandex.kanban.models.Epic;
import ru.practicum.yandex.kanban.models.Subtask;
import ru.practicum.yandex.kanban.models.Task;
import ru.practicum.yandex.kanban.server.HttpTaskServer;
import ru.practicum.yandex.kanban.server.KVServer;
import ru.practicum.yandex.kanban.utils.GsonUtils;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskManagerTest extends TaskManagerTest {
    KVServer kvServer;
    HttpTaskServer taskServer;
    Gson gson = GsonUtils.getInstance();

    @BeforeEach
    public void beforeEach() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
        manager = new HttpTaskManager(URI.create("http://localhost:8078"));
        taskServer = new HttpTaskServer(manager);
    }

    @AfterEach
    public void afterEach() {
        kvServer.stop();
        taskServer.stop();
    }

    @Test
    public void shouldCreateAllTypesOfTasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        Task task = new Task("Task", "Task description", Instant.ofEpochMilli(12345678), 25);
        HttpRequest taskCreateRequest = HttpRequest
                .newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/task/"))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .build();
        client.send(taskCreateRequest, HttpResponse.BodyHandlers.ofString());

        Epic epic = new Epic("Epic", "Epic description");
        HttpRequest epicCreateRequest = HttpRequest
                .newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/epic/"))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();
        int epicId = gson.fromJson(client.send(epicCreateRequest, HttpResponse.BodyHandlers.ofString()).body(), Epic.class).getId();

        Subtask subtask = new Subtask("Subtask", "Subtask description", epicId, Instant.ofEpochMilli(87654321), 25);
        HttpRequest subtaskCreateRequest = HttpRequest
                .newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/subtask/"))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask)))
                .build();
        client.send(subtaskCreateRequest, HttpResponse.BodyHandlers.ofString());

        HttpRequest getTasksRequest = HttpRequest
                .newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/task/"))
                .GET()
                .build();
        HttpRequest getEpicsRequest = HttpRequest
                .newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/epic/"))
                .GET()
                .build();
        HttpRequest getSubtasksRequest = HttpRequest
                .newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/subtask/"))
                .GET()
                .build();

        List<Task> tasksResponse = gson.fromJson(
                client.send(getTasksRequest, HttpResponse.BodyHandlers.ofString()).body(),
                new TypeToken<List<Task>>() {}.getType()
        );

        List<Epic> epicsResponse = gson.fromJson(
                client.send(getEpicsRequest, HttpResponse.BodyHandlers.ofString()).body(),
                new TypeToken<List<Epic>>() {}.getType()
        );

        List<Subtask> subtasksResponse = gson.fromJson(
                client.send(getSubtasksRequest, HttpResponse.BodyHandlers.ofString()).body(),
                new TypeToken<List<Subtask>>() {}.getType()
        );

        assertEquals(1, tasksResponse.size());
        assertEquals(1, tasksResponse.get(0).getId());
        assertEquals("Task", tasksResponse.get(0).getTitle());

        assertEquals(1, epicsResponse.size());
        assertEquals(2, epicsResponse.get(0).getId());
        assertEquals("Epic", epicsResponse.get(0).getTitle());

        assertEquals(1, subtasksResponse.size());
        assertEquals(3, subtasksResponse.get(0).getId());
        assertEquals("Subtask", subtasksResponse.get(0).getTitle());
    }

    @Test
    public void shouldCreateAndDeleteAllTasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        Task task = new Task("Task", "Task description", Instant.ofEpochMilli(12345678), 25);
        HttpRequest taskCreateRequest = HttpRequest
                .newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/task/"))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .build();
        int taskId = gson.fromJson(client.send(taskCreateRequest, HttpResponse.BodyHandlers.ofString()).body(), Task.class).getId();

        Epic epic = new Epic("Epic", "Epic description");
        HttpRequest epicCreateRequest = HttpRequest
                .newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/epic/"))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();
        int epicId = gson.fromJson(client.send(epicCreateRequest, HttpResponse.BodyHandlers.ofString()).body(), Epic.class).getId();

        Subtask subtask = new Subtask("Subtask", "Subtask description", epicId, Instant.ofEpochMilli(87654321), 25);
        HttpRequest subtaskCreateRequest = HttpRequest
                .newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/subtask/"))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask)))
                .build();
        client.send(subtaskCreateRequest, HttpResponse.BodyHandlers.ofString());

        HttpRequest deleteTaskRequest = HttpRequest
                .newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/task/" + taskId))
                .DELETE()
                .build();
        HttpRequest deleteEpic = HttpRequest
                .newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/epic/" + epicId))
                .DELETE()
                .build();

        client.send(deleteTaskRequest, HttpResponse.BodyHandlers.ofString());
        client.send(deleteEpic, HttpResponse.BodyHandlers.ofString());

        HttpRequest getTasksRequest = HttpRequest
                .newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/task/"))
                .GET()
                .build();
        HttpRequest getEpicsRequest = HttpRequest
                .newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/epic/"))
                .GET()
                .build();
        HttpRequest getSubtasksRequest = HttpRequest
                .newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/subtask/"))
                .GET()
                .build();

        List<Task> tasksResponse = gson.fromJson(
                client.send(getTasksRequest, HttpResponse.BodyHandlers.ofString()).body(),
                new TypeToken<List<Task>>() {}.getType()
        );

        List<Epic> epicsResponse = gson.fromJson(
                client.send(getEpicsRequest, HttpResponse.BodyHandlers.ofString()).body(),
                new TypeToken<List<Epic>>() {}.getType()
        );

        List<Epic> subtasksResponse = gson.fromJson(
                client.send(getSubtasksRequest, HttpResponse.BodyHandlers.ofString()).body(),
                new TypeToken<List<Epic>>() {}.getType()
        );

        assertEquals(0, tasksResponse.size());
        assertEquals(0, epicsResponse.size());
        assertEquals(0, subtasksResponse.size());
    }

    @Test
    public void shouldCorrectlyRestoreData() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        Task task = new Task("Task", "Task description", Instant.ofEpochMilli(12345678), 25);
        HttpRequest taskCreateRequest = HttpRequest
                .newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/task/"))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .build();
        client.send(taskCreateRequest, HttpResponse.BodyHandlers.ofString());

        Epic epic = new Epic("Epic", "Epic description");
        HttpRequest epicCreateRequest = HttpRequest
                .newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/epic/"))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();
        int epicId = gson.fromJson(client.send(epicCreateRequest, HttpResponse.BodyHandlers.ofString()).body(), Epic.class).getId();

        Subtask subtask = new Subtask("Subtask", "Subtask description", epicId, Instant.ofEpochMilli(87654321), 25);
        HttpRequest subtaskCreateRequest = HttpRequest
                .newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/subtask/"))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask)))
                .build();
        client.send(subtaskCreateRequest, HttpResponse.BodyHandlers.ofString());

        manager = new HttpTaskManager(URI.create("http://localhost:8078"));
        ((HttpTaskManager) manager).load();

        List<Task> tasks = manager.getAllTasks();
        List<Epic> epics = manager.getAllEpics();
        List<Subtask> subtasks = manager.getAllSubtasks();

        assertEquals(1, tasks.size());
        assertEquals(1, tasks.get(0).getId());
        assertEquals("Task", tasks.get(0).getTitle());

        assertEquals(1, epics.size());
        assertEquals(2, epics.get(0).getId());
        assertEquals("Epic", epics.get(0).getTitle());

        assertEquals(1, subtasks.size());
        assertEquals(3, subtasks.get(0).getId());
        assertEquals("Subtask", subtasks.get(0).getTitle());
    }
}
