package ru.practicum.yandex.kanban.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.practicum.yandex.kanban.managers.TaskManager;

import java.io.IOException;
import java.io.OutputStream;
import java.time.Instant;

public class TasksHandler implements HttpHandler {
    private final TaskManager taskService;
    private final Gson gson = new GsonBuilder().registerTypeAdapter(Instant.class, new GsonInstantAdapter()).create();

    public TasksHandler(TaskManager taskService) {
        this.taskService = taskService;
    }

    @Override
    public void handle(HttpExchange httpExchange) {
        Router router = new Router(httpExchange);

        router.get("/tasks/task", this::getAllTasks);
        router.get("/tasks/epic", this::getAllEpics);
        router.get("/tasks/subtask", this::getAllSubtasks);
        router.get("/tasks/task/:id", this::getTaskById);
        router.get("/tasks/epic/:id", this::getEpicById);
        router.get("/tasks/subtask/:id", this::getSubtaskById);
        router.get("/tasks/epic/:id/subtasks", this::getSubtasksByEpicId);
        router.get("/tasks/history", this::getHistory);

        router.post("/tasks/task", this::createTask);
        router.post("/tasks/epic", this::createEpic);
        router.post("/tasks/subtask", this::createSubtask);

        router.put("/tasks/task/:id", this::updateTask);
        router.put("/tasks/epic/:id", this::updateEpic);
        router.put("/tasks/subtask/:id", this::updateSubtask);

        router.delete("/tasks", this::removeAllTasks);
        router.delete("/tasks/task/:id", this::removeTask);
        router.delete("/tasks/epic/:id", this::removeEpic);
        router.delete("/tasks/subtask/:id", this::removeSubtask);
    }

    private void getAllTasks(HttpExchange httpExchange) {
        send(httpExchange, taskService.getAllTasks());
    }

    private void getAllEpics(HttpExchange httpExchange) {
        send(httpExchange, taskService.getAllEpics());
    }

    private void getAllSubtasks(HttpExchange httpExchange) {
        send(httpExchange, taskService.getAllSubtasks());
    }

    private void getTaskById(HttpExchange httpExchange) {
        int id = Integer.parseInt(httpExchange.getRequestURI().getPath().split("/")[3]);
        send(httpExchange, taskService.getTask(id));
    }

    private void getEpicById(HttpExchange httpExchange) {
        int id = Integer.parseInt(httpExchange.getRequestURI().getPath().split("/")[3]);
        send(httpExchange, taskService.getEpic(id));
    }

    private void getSubtaskById(HttpExchange httpExchange) {
        int id = Integer.parseInt(httpExchange.getRequestURI().getPath().split("/")[3]);
        send(httpExchange, taskService.getSubtask(id));
    }

    private void getSubtasksByEpicId(HttpExchange httpExchange) {
        int epicId = Integer.parseInt(httpExchange.getRequestURI().getPath().split("/")[3]);
        send(httpExchange, taskService.getSubtasksByEpicId(epicId));
    }

    private void createTask(HttpExchange httpExchange) {}

    private void createEpic(HttpExchange httpExchange) {}

    private void createSubtask(HttpExchange httpExchange) {}

    private void updateTask(HttpExchange httpExchange) {}

    private void updateEpic(HttpExchange httpExchange) {}

    private void updateSubtask(HttpExchange httpExchange) {}

    private void removeAllTasks(HttpExchange httpExchange) {
        taskService.removeAllTasks();
        send(httpExchange, 204);
    }

    private void removeTask(HttpExchange httpExchange) {
        int id = Integer.parseInt(httpExchange.getRequestURI().getPath().split("/")[3]);
        taskService.removeTask(id);
        send(httpExchange, 204);
    }

    private void removeEpic(HttpExchange httpExchange) {
        int id = Integer.parseInt(httpExchange.getRequestURI().getPath().split("/")[3]);
        taskService.removeEpic(id);
        send(httpExchange, 204);
    }

    private void removeSubtask(HttpExchange httpExchange) {
        int id = Integer.parseInt(httpExchange.getRequestURI().getPath().split("/")[3]);
        taskService.removeSubtask(id);
        send(httpExchange, 204);
    }

    private void getHistory(HttpExchange httpExchange) {
        send(httpExchange, taskService.getHistory());
    }

    private void send(HttpExchange httpExchange, Object data) {
        System.out.println(gson.toJson(data));
        String response = gson.toJson(data);


        try (OutputStream os = httpExchange.getResponseBody()) {
            httpExchange.sendResponseHeaders(200, 0);
            os.write(response.getBytes());
        } catch (IOException exception) {
            System.out.println("fdsfds");
        }
    }

    private void send(HttpExchange httpExchange, int code) {
        try {
            httpExchange.sendResponseHeaders(code, 0);
        } catch (IOException exception) {
            System.out.println("fdsfds");
        }
    }

    private void send(HttpExchange httpExchange, Object data, int code) {
        String response = gson.toJson(data);

        try (OutputStream os = httpExchange.getResponseBody()) {
            httpExchange.sendResponseHeaders(code, 0);
            os.write(response.getBytes());
        } catch (IOException exception) {
            System.out.println("fdsfds");
        }
    }
}
