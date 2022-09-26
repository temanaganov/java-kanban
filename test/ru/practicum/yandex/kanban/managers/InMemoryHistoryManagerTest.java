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
    HistoryManager manager;

    @BeforeEach
    public void beforeEach() {
        manager = new InMemoryHistoryManager();
    }

    @Test
    public void shouldAddTasksToHistory() {
        Task task1 = new Task(1, TaskStatus.NEW, "Title", "Description", Instant.now(), 0);
        Task task2 = new Task(2, TaskStatus.NEW, "Title", "Description", Instant.now(), 0);
        Task task3 = new Task(3, TaskStatus.NEW, "Title", "Description", Instant.now(), 0);

        manager.add(task1);
        manager.add(task2);
        manager.add(task3);

        assertEquals(List.of(task1, task2, task3), manager.getHistory());
    }

    @Test
    public void shouldMoveToEndTaskIfIsAlreadyPresent() {
        Task task1 = new Task(1, TaskStatus.NEW, "Title", "Description", Instant.now(), 0);
        Task task2 = new Task(2, TaskStatus.NEW, "Title", "Description", Instant.now(), 0);
        Task task3 = new Task(3, TaskStatus.NEW, "Title", "Description", Instant.now(), 0);

        manager.add(task1);
        manager.add(task2);
        manager.add(task3);
        manager.add(task1);

        assertEquals(List.of(task2, task3, task1), manager.getHistory());
    }

    @Test
    public void shouldRemoveTask() {
        Task task1 = new Task(1, TaskStatus.NEW, "Title", "Description", Instant.now(), 0);
        Task task2 = new Task(2, TaskStatus.NEW, "Title", "Description", Instant.now(), 0);
        Task task3 = new Task(3, TaskStatus.NEW, "Title", "Description", Instant.now(), 0);

        manager.add(task1);
        manager.add(task2);
        manager.add(task3);

        manager.remove(task2.getId());

        assertEquals(List.of(task1, task3), manager.getHistory());
    }

    @Test
    public void shouldRemoveOnlyOneTask() {
        Task task = new Task(1, TaskStatus.NEW, "Title", "Description", Instant.now(), 0);

        manager.add(task);
        manager.remove(task.getId());

        assertEquals(Collections.EMPTY_LIST, manager.getHistory());
    }

    @Test
    public void shouldNotRemoveTaskWithBadId() {
        Task task = new Task(1, TaskStatus.NEW, "Title", "Description", Instant.now(), 0);

        manager.add(task);
        manager.remove(0);

        assertEquals(List.of(task), manager.getHistory());
    }

    @Test
    public void shouldClearHistory() {
        manager.add(new Task(1, TaskStatus.NEW, "Title", "Description", Instant.now(), 0));
        manager.add(new Task(2, TaskStatus.NEW, "Title", "Description", Instant.now(), 0));
        manager.add(new Task(3, TaskStatus.NEW, "Title", "Description", Instant.now(), 0));

        manager.clear();

        assertEquals(Collections.EMPTY_LIST, manager.getHistory());
    }
}
