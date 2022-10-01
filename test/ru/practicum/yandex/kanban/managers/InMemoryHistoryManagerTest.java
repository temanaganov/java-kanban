package ru.practicum.yandex.kanban.managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.yandex.kanban.models.Task;
import ru.practicum.yandex.kanban.models.TaskStatus;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryHistoryManagerTest {
    private HistoryManager manager;
    private int id = 0;

    @BeforeEach
    public void beforeEach() {
        id = 0;
        manager = new InMemoryHistoryManager();
    }

    @Test
    public void shouldAddTasksToHistory() {
        Task task1 = createTask();
        Task task2 = createTask();
        Task task3 = createTask();

        manager.add(task1);
        manager.add(task2);
        manager.add(task3);

        assertEquals(List.of(task1, task2, task3), manager.getHistory());
    }

    @Test
    public void shouldMoveToEndTaskIfIsAlreadyPresent() {
        Task task1 = createTask();
        Task task2 = createTask();
        Task task3 = createTask();

        manager.add(task1);
        manager.add(task2);
        manager.add(task3);
        manager.add(task1);

        assertEquals(List.of(task2, task3, task1), manager.getHistory());
    }

    @Test
    public void shouldRemoveTask() {
        Task task1 = createTask();
        Task task2 = createTask();
        Task task3 = createTask();

        manager.add(task1);
        manager.add(task2);
        manager.add(task3);

        manager.remove(task2.getId());

        assertEquals(List.of(task1, task3), manager.getHistory());
    }

    @Test
    public void shouldRemoveOnlyOneTask() {
        Task task = createTask();

        manager.add(task);
        manager.remove(task.getId());

        assertEquals(Collections.EMPTY_LIST, manager.getHistory());
    }

    @Test
    public void shouldNotRemoveTaskWithBadId() {
        Task task = createTask();

        manager.add(task);
        manager.remove(0);

        assertEquals(List.of(task), manager.getHistory());
    }

    @Test
    public void shouldClearHistory() {
        manager.add(createTask());
        manager.add(createTask());
        manager.add(createTask());

        manager.clear();

        assertEquals(Collections.EMPTY_LIST, manager.getHistory());
    }

    private Task createTask() {
        return new Task(++id, TaskStatus.NEW, "Title", "Description", Instant.now(), 0);
    }
}
