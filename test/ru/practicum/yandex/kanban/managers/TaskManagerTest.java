package ru.practicum.yandex.kanban.managers;

import org.junit.jupiter.api.Test;
import ru.practicum.yandex.kanban.models.Epic;
import ru.practicum.yandex.kanban.models.Subtask;
import ru.practicum.yandex.kanban.models.Task;
import ru.practicum.yandex.kanban.models.TaskStatus;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest {
    TaskManager manager;

    @Test
    public void shouldCreateTask() {
        Task task = manager.createTask(new Task("Title", "Description", Instant.now(), 0));

        List<Task> tasks = manager.getAllTasks();

        assertNotNull(task.getId());
        assertEquals(TaskStatus.NEW, task.getStatus());
        assertEquals(List.of(task), tasks);
    }

    @Test
    public void shouldCreateEpic() {
        Epic epic = manager.createEpic(new Epic("Title", "Description"));

        List<Epic> epics = manager.getAllEpics();

        assertNotNull(epic.getId());
        assertEquals(TaskStatus.NEW, epic.getStatus());
        assertEquals(Collections.EMPTY_LIST, epic.getSubtasks());
        assertEquals(List.of(epic), epics);
    }

    @Test
    public void shouldCreateSubtask() {
        Epic epic = manager.createEpic(new Epic("Title", "Description"));
        Subtask subtask = manager.createSubtask(new Subtask("Title", "Description", epic.getId(), Instant.now(), 0));

        List<Subtask> subtasks = manager.getAllSubtasks();

        assertNotNull(subtask.getId());
        assertEquals(epic.getId(), subtask.getEpicId());
        assertEquals(TaskStatus.NEW, subtask.getStatus());
        assertEquals(List.of(subtask), subtasks);
        assertEquals(List.of(subtask.getId()), epic.getSubtasks());
    }

    @Test
    public void shouldUpdateTaskStatus() {
        Task task = manager.createTask(new Task("Title", "Description", Instant.now(), 0));

        task.setStatus(TaskStatus.IN_PROGRESS);
        manager.updateTask(task);

        assertEquals(TaskStatus.IN_PROGRESS, manager.getTask(task.getId()).getStatus());
    }

    @Test
    public void shouldUpdateEpicTitle() {
        Epic epic = manager.createEpic(new Epic("Title", "Description"));

        epic.setTitle("New title");
        manager.updateEpic(epic);

        assertEquals("New title", manager.getEpic(epic.getId()).getTitle());
    }

    @Test
    public void shouldUpdateSubtaskStatusToInProgress() {
        Epic epic = manager.createEpic(new Epic("Title", "Description"));
        Subtask subtask = manager.createSubtask(new Subtask("Title", "Description", epic.getId(), Instant.now(), 0));

        subtask.setStatus(TaskStatus.IN_PROGRESS);
        manager.updateSubtask(subtask);

        assertEquals(TaskStatus.IN_PROGRESS, manager.getSubtask(subtask.getId()).getStatus());
        assertEquals(TaskStatus.IN_PROGRESS, manager.getEpic(epic.getId()).getStatus());
    }

    @Test
    public void shouldUpdateSubtaskStatusToDone() {
        Epic epic = manager.createEpic(new Epic("Title", "Description"));
        Subtask subtask = manager.createSubtask(new Subtask("Title", "Description", epic.getId(), Instant.now(), 0));

        subtask.setStatus(TaskStatus.DONE);
        manager.updateSubtask(subtask);

        assertEquals(TaskStatus.DONE, manager.getSubtask(subtask.getId()).getStatus());
        assertEquals(TaskStatus.DONE, manager.getEpic(epic.getId()).getStatus());
    }

    @Test
    public void shouldUpdateEpicStatusToInProgress() {
        Epic epic = manager.createEpic(new Epic("Title", "Description"));
        Subtask subtask1 = manager.createSubtask(new Subtask("Title", "Description", epic.getId(), Instant.now(), 0));
        Subtask subtask2 = manager.createSubtask(new Subtask("Title", "Description", epic.getId(), Instant.now(), 0));

        subtask1.setStatus(TaskStatus.DONE);
        subtask2.setStatus(TaskStatus.IN_PROGRESS);
        manager.updateSubtask(subtask1);
        manager.updateSubtask(subtask2);

        assertEquals(TaskStatus.IN_PROGRESS, manager.getEpic(epic.getId()).getStatus());
    }

    @Test
    public void shouldRemoveAllTasksEpicsSubtasks() {
        manager.createTask(new Task("Title", "Description", Instant.now(), 0));
        Epic epic = manager.createEpic(new Epic("Title", "Description"));
        manager.createSubtask(new Subtask("Title", "Description", epic.getId(), Instant.now(), 0));

        manager.removeAllTasks();

        assertEquals(Collections.EMPTY_LIST, manager.getAllTasks());
        assertEquals(Collections.EMPTY_LIST, manager.getAllEpics());
        assertEquals(Collections.EMPTY_LIST, manager.getAllSubtasks());
    }

    @Test
    public void shouldCalculateStartAndEndTimeOfEpicWith1Subtask() {
        Epic epic = manager.createEpic(new Epic("Title", "Description"));
        Subtask subtask1 = manager.createSubtask(new Subtask("Title", "Description", epic.getId(), Instant.ofEpochMilli(0), 0));

        assertEquals(subtask1.getStartTime(), epic.getStartTime());
        assertEquals(subtask1.getEndTime(), epic.getEndTime());
    }

    @Test
    public void shouldCalculateStartAndEndTimeOfEpicWith2Subtasks() {
        Epic epic = manager.createEpic(new Epic("Title", "Description"));
        Subtask subtask1 = manager.createSubtask(new Subtask("Title", "Description", epic.getId(), Instant.ofEpochMilli(0), 0));
        Subtask subtask2 = manager.createSubtask(new Subtask("Title", "Description", epic.getId(), Instant.now(), 0));

        assertEquals(subtask1.getStartTime(), epic.getStartTime());
        assertEquals(subtask2.getEndTime(), epic.getEndTime());
    }

    @Test
    public void shouldRemoveTask() {
        Task task = manager.createTask(new Task("Title", "Description", Instant.now(), 0));

        manager.removeTask(task.getId());

        assertEquals(Collections.EMPTY_LIST, manager.getAllTasks());
    }

    @Test
    public void shouldNotRemoveTaskIfBadId() {
        Task task = manager.createTask(new Task("Title", "Description", Instant.now(), 0));
        manager.removeTask(0);

        assertEquals(List.of(task), manager.getAllTasks());
    }

    @Test
    public void shouldRemoveEpic() {
        Epic epic = manager.createEpic(new Epic("Title", "Description"));
        manager.createSubtask(new Subtask("Title", "Description", epic.getId(), Instant.now(), 0));
        manager.createSubtask(new Subtask("Title", "Description", epic.getId(), Instant.now(), 0));

        manager.removeEpic(epic.getId());

        assertEquals(Collections.EMPTY_LIST, manager.getAllEpics());
        assertEquals(Collections.EMPTY_LIST, manager.getAllSubtasks());
    }

    @Test
    public void shouldNotRemoveEpicIfBadId() {
        Epic epic = manager.createEpic(new Epic("Title", "Description"));
        Subtask subtask1 = manager.createSubtask(new Subtask("Title", "Description", epic.getId(), Instant.now(), 0));
        Subtask subtask2 = manager.createSubtask(new Subtask("Title", "Description", epic.getId(), Instant.now(), 0));

        manager.removeEpic(0);

        assertEquals(List.of(epic), manager.getAllEpics());
        assertEquals(List.of(subtask1, subtask2), manager.getAllSubtasks());
    }

    @Test
    public void shouldRemoveSubtask() {
        Epic epic = manager.createEpic(new Epic("Title", "Description"));
        Subtask subtask = manager.createSubtask(new Subtask("Title", "Description", epic.getId(), Instant.now(), 0));

        manager.removeSubtask(subtask.getId());

        assertEquals(Collections.EMPTY_LIST, manager.getAllSubtasks());
        assertEquals(Collections.EMPTY_LIST, manager.getEpic(epic.getId()).getSubtasks());
    }

    @Test
    public void shouldNotRemoveSubtaskIfBadId() {
        Epic epic = manager.createEpic(new Epic("Title", "Description"));
        Subtask subtask = manager.createSubtask(new Subtask("Title", "Description", epic.getId(), Instant.now(), 0));

        manager.removeSubtask(0);

        assertEquals(List.of(subtask), manager.getAllSubtasks());
        assertEquals(List.of(subtask.getId()), manager.getEpic(epic.getId()).getSubtasks());
    }

    @Test
    public void shouldReturnSubtasksByEpicId() {
        Epic epic = manager.createEpic(new Epic("Title", "Description"));
        Subtask subtask1 = manager.createSubtask(new Subtask("Title", "Description", epic.getId(), Instant.now(), 0));
        Subtask subtask2 = manager.createSubtask(new Subtask("Title", "Description", epic.getId(), Instant.now(), 0));
        Subtask subtask3 = manager.createSubtask(new Subtask("Title", "Description", epic.getId(), Instant.now(), 0));

        assertEquals(List.of(subtask1, subtask2, subtask3), manager.getSubtasksByEpicId(epic.getId()));
    }

    @Test
    public void shouldReturnEmptyHistory() {
        assertEquals(Collections.EMPTY_LIST, manager.getHistory());
    }

    @Test
    public void shouldReturnHistoryWith3Tasks() {
        Task task = manager.createTask(new Task("Title", "Description", Instant.now(), 0));
        Epic epic = manager.createEpic(new Epic("Title", "Description"));
        Subtask subtask = manager.createSubtask(new Subtask("Title", "Description", epic.getId(), Instant.now(), 0));

        manager.getTask(task.getId());
        manager.getEpic(epic.getId());
        manager.getSubtask(subtask.getId());

        assertEquals(List.of(task, epic, subtask), manager.getHistory());
    }

    @Test
    public void shouldReturnHistoryWith1TaskAfterRemoving() {
        Task task = manager.createTask(new Task("Title", "Description", Instant.now(), 0));
        Epic epic = manager.createEpic(new Epic("Title", "Description"));
        manager.createSubtask(new Subtask("Title", "Description", epic.getId(), Instant.now(), 0));

        manager.getTask(task.getId());
        manager.removeEpic(epic.getId());

        assertEquals(List.of(task), manager.getHistory());
    }

    @Test
    public void shouldConvertEmptyTasksToEmptyString() {
        assertEquals("", TaskManager.tasksToString(manager));
    }

    @Test
    public void shouldConvertTasksToString() {
        Task task = manager.createTask(new Task("Title", "Description", Instant.now(), 0));
        Epic epic = manager.createEpic(new Epic("Title", "Description"));
        Subtask subtask = manager.createSubtask(new Subtask("Title", "Description", epic.getId(), Instant.now(), 0));

        assertEquals(task + "\n" + epic + "\n" + subtask + "\n", TaskManager.tasksToString(manager));
    }

    @Test
    public void shouldThrowExceptionIfTasksAreIntersect() {
        assertThrows(ManagerValidateException.class, () -> {
            manager.createTask(new Task("Title", "Description", Instant.ofEpochMilli(0), 1000));
            manager.createTask(new Task("Title", "Description", Instant.ofEpochMilli(100), 1000));
        });
    }
}
