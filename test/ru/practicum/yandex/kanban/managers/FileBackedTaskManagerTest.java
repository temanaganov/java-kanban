package ru.practicum.yandex.kanban.managers;

import org.junit.jupiter.api.*;
import ru.practicum.yandex.kanban.models.Epic;
import ru.practicum.yandex.kanban.models.Subtask;
import ru.practicum.yandex.kanban.models.Task;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest extends TaskManagerTest {
    static Path path = Path.of("data.test.csv");

    @BeforeEach
    public void beforeEach() {
        manager = new FileBackedTasksManager(path);
    }

    @AfterEach
    public void afterEach() {
        try {
            Files.delete(path);
        } catch (IOException exception) {
            System.out.println(exception.getMessage());
        }
    }

    @Test
    public void shouldCorrectlyLoadDataFromFile() {
        Task task = manager.createTask(new Task("Title", "Description", Instant.now(), 0));
        Epic epic = manager.createEpic(new Epic("Title", "Description"));
        Subtask subtask = manager.createSubtask(new Subtask("Title", "Description", epic.getId(), Instant.now(), 0));
        manager.getTask(task.getId());
        manager.getSubtask(subtask.getId());

        manager = FileBackedTasksManager.loadFromFile(path);

        assertEquals(List.of(task), manager.getAllTasks());
        assertEquals(List.of(epic), manager.getAllEpics());
        assertEquals(List.of(subtask), manager.getAllSubtasks());
        assertEquals(List.of(task, subtask), manager.getHistory());
    }

    @Test
    public void shouldThrowManagerSaveExceptionIfFileDoesNotExist() {
       assertThrows(ManagerSaveException.class, () -> FileBackedTasksManager.loadFromFile(path));
    }
}
