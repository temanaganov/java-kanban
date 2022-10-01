package ru.practicum.yandex.kanban.managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.yandex.kanban.models.Task;
import ru.practicum.yandex.kanban.models.TaskStatus;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HistoryManagerUtilsTest {
    private HistoryManager manager;

    @BeforeEach
    public void beforeEach() {
        manager = new InMemoryHistoryManager();
    }

    @Test
    public void shouldConvertEmptyHistoryToString() {
        assertEquals("0", HistoryManagerUtils.historyToString(manager));
    }

    @Test
    public void shouldConvertHistoryToString() {
        Task task1 = new Task(1, TaskStatus.NEW, "Title", "Description", Instant.now(), 0);
        Task task2 = new Task(2, TaskStatus.NEW, "Title", "Description", Instant.now(), 0);
        Task task3 = new Task(3, TaskStatus.NEW, "Title", "Description", Instant.now(), 0);

        manager.add(task1);
        manager.add(task2);
        manager.add(task3);

        assertEquals(task1.getId() + "," + task2.getId() + "," + task3.getId(), HistoryManagerUtils.historyToString(manager));
    }

    @Test
    public void shouldConvertEmptyStringToHistory() {
        assertEquals(Collections.EMPTY_LIST, HistoryManagerUtils.historyFromString("0"));
    }

    @Test
    public void shouldConvertStringToHistory() {
        assertEquals(List.of(1, 2, 3), HistoryManagerUtils.historyFromString("1,2,3"));
    }
}
