package ru.practicum.yandex.kanban.server;

import com.sun.net.httpserver.HttpServer;
import ru.practicum.yandex.kanban.managers.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    public HttpTaskServer(TaskManager taskService) throws IOException {
        HttpServer httpServer = HttpServer.create(new InetSocketAddress(8080), 0);
        httpServer.createContext("/tasks", new TasksHandler(taskService));

        httpServer.start();
    }
}
