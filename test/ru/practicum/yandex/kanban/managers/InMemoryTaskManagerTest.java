package ru.practicum.yandex.kanban.managers;

import org.junit.jupiter.api.BeforeEach;

public class InMemoryTaskManagerTest extends TaskManagerTest {
    @BeforeEach
    public void beforeEach() {
        manager = new InMemoryTaskManager();
    }
}
