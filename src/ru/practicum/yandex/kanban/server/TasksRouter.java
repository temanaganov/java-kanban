package ru.practicum.yandex.kanban.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.practicum.yandex.kanban.managers.TaskManager;

public class TasksRouter implements HttpHandler {
    TasksController tasksController;

    public TasksRouter(TaskManager taskService) {
        tasksController = new TasksController(taskService);
    }

    @Override
    public void handle(HttpExchange httpExchange) {
        Router router = new Router(httpExchange);

        router.get("/tasks/task", tasksController::getAllTasks);
        router.get("/tasks/epic", tasksController::getAllEpics);
        router.get("/tasks/subtask", tasksController::getAllSubtasks);
        router.get("/tasks/task/:id", tasksController::getTaskById);
        router.get("/tasks/epic/:id", tasksController::getEpicById);
        router.get("/tasks/subtask/:id", tasksController::getSubtaskById);
        router.get("/tasks/epic/:id/subtasks", tasksController::getSubtasksByEpicId);
        router.get("/tasks/history", tasksController::getHistory);

        router.post("/tasks/task", tasksController::createTask);
        router.post("/tasks/epic", tasksController::createEpic);
        router.post("/tasks/subtask", tasksController::createSubtask);

        router.put("/tasks/task/:id", tasksController::updateTask);
        router.put("/tasks/epic/:id", tasksController::updateEpic);
        router.put("/tasks/subtask/:id", tasksController::updateSubtask);

        router.delete("/tasks", tasksController::removeAllTasks);
        router.delete("/tasks/task/:id", tasksController::removeTask);
        router.delete("/tasks/epic/:id", tasksController::removeEpic);
        router.delete("/tasks/subtask/:id", tasksController::removeSubtask);
    }
}
