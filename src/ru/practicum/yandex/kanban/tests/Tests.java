package ru.practicum.yandex.kanban.tests;

import ru.practicum.yandex.kanban.managers.FileBackedTasksManager;
import ru.practicum.yandex.kanban.managers.Managers;
import ru.practicum.yandex.kanban.managers.TaskManager;
import ru.practicum.yandex.kanban.models.Epic;
import ru.practicum.yandex.kanban.models.Subtask;
import ru.practicum.yandex.kanban.models.Task;
import ru.practicum.yandex.kanban.models.TaskStatus;

import java.nio.file.Path;

public class Tests {
    public static void testInMemoryHistoryManager() {
        TaskManager manager = Managers.getDefault();

        Task firstTask = manager.createTask(new Task("1-я задача", "Описание 1-й задачи"));
        Task secondTask = manager.createTask(new Task("2-я задача", "Описание 2-й задачи"));
        Epic firstEpic = manager.createEpic(new Epic("1-й эпик", "Описание 1-го эпика"));
        Epic secondEpic = manager.createEpic(new Epic("2-й эпик", "Описание 2-го эпика"));

        boolean tasksMustBeCreated = firstTask.getId() != null && secondTask.getId() != null && firstEpic.getId() != null && secondEpic.getId() != null;

        if (!tasksMustBeCreated) {
            throw new TestException("Не все задачи созданы");
        }

        Subtask firstSubtask = manager.createSubtask(new Subtask("1-я подзадача 1-го эпика", "Описание 1-й подзадачи 1-го эпика", firstEpic.getId()));
        Subtask secondSubtask = manager.createSubtask(new Subtask("2-я подзадача 1-го эпика", "Описание 2-й подзадачи 1-го эпика", firstEpic.getId()));
        Subtask thirdSubtask = manager.createSubtask(new Subtask("1-я подзадача 2-го эпика", "Описание 1-й подзадачи 1-го эпика", secondEpic.getId()));

        boolean allSubtasksHaveId = firstSubtask.getId() != null && secondSubtask.getId() != null && thirdSubtask.getId() != null;

        if (!allSubtasksHaveId) {
            throw new TestException("Не все сабтаски имеют ID");
        }

        boolean epicsContainsSubtasks = firstEpic.getSubtasks().size() == 2 && secondEpic.getSubtasks().size() == 1;

        if (!epicsContainsSubtasks) {
            throw new TestException("Не все эпики получили свои сабтаски");
        }

        boolean allTasksHaveNewStatus =
                firstTask.getStatus() == TaskStatus.NEW &&
                        secondTask.getStatus() == TaskStatus.NEW &&
                        firstEpic.getStatus() == TaskStatus.NEW &&
                        secondEpic.getStatus() == TaskStatus.NEW &&
                        firstSubtask.getStatus() == TaskStatus.NEW &&
                        secondSubtask.getStatus() == TaskStatus.NEW &&
                        thirdSubtask.getStatus() == TaskStatus.NEW;

        if (!allTasksHaveNewStatus) {
            throw new TestException("Не все задачи обновились");
        }

        firstSubtask.setStatus(TaskStatus.IN_PROGRESS);
        manager.updateSubtask(firstSubtask);

        thirdSubtask.setStatus(TaskStatus.DONE);
        manager.updateSubtask(thirdSubtask);

        boolean isStatusesUpdated = firstEpic.getStatus() == TaskStatus.IN_PROGRESS && secondEpic.getStatus() == TaskStatus.DONE;

        if (!isStatusesUpdated) {
            throw new TestException("Не все статусы обновились (1)");
        }

        firstSubtask.setStatus(TaskStatus.DONE);
        manager.updateSubtask(firstSubtask);

        secondSubtask.setStatus(TaskStatus.DONE);
        manager.updateSubtask(secondSubtask);

        isStatusesUpdated = firstEpic.getStatus() == TaskStatus.DONE;

        if (!isStatusesUpdated) {
            throw new TestException("Не все статусы обновились (2)");
        }

        manager.getTask(firstTask.getId());
        manager.getTask(secondTask.getId());
        manager.getSubtask(firstSubtask.getId());
        manager.getSubtask(secondSubtask.getId());
        manager.getEpic(firstEpic.getId());
        manager.getEpic(secondEpic.getId());

        if (manager.getHistory().size() != 6) {
            throw new TestException("Некорректно сохраняется история");
        }

        manager.getTask(firstTask.getId());
        manager.getTask(firstTask.getId());
        manager.getSubtask(firstSubtask.getId());

        if (manager.getHistory().size() != 6) {
            throw new TestException("Дубли в истории");
        }

        manager.removeTask(firstTask.getId());

        if (manager.getAllTasks().size() != 1) {
            throw new TestException("Некорректное удаление (1)");
        }

        if (manager.getHistory().size() != 5) {
            throw new TestException("Не очищается история");
        }

        manager.removeEpic(firstEpic.getId());

        if (manager.getAllEpics().size() != 1 || manager.getAllSubtasks().size() != 1) {
            throw new TestException("Некорректное удаление (2)");
        }

        if (manager.getHistory().size() != 2) {
            throw new TestException("Не удаляется эпик с сабтасками из истории");
        }

        System.out.println("PASSED testInMemoryHistoryManager");
    }

    public static void testFileBackendTasksManager() {
        TaskManager manager =  new FileBackedTasksManager();

        Task task1 = manager.createTask(new Task("1-я задача", "Описание 1-й задачи"));
        Task task2 = manager.createTask(new Task("2-я задача", "Описание 2-й задачи"));
        Task task3 = manager.createTask(new Task("3-я задача", "Описание 3-й задачи"));
        manager.createTask(new Task("4-я задача", "Описание 4-й задачи"));
        Epic epic1 = manager.createEpic(new Epic("1-й эпик", "Описание 1-го эпика"));
        Epic epic2 = manager.createEpic(new Epic("2-й эпик", "Описание 2-го эпика"));
        manager.createEpic(new Epic("3-й эпик", "Описание 3-го эпика"));
        Subtask subtask1 = manager.createSubtask(new Subtask("1-я подзадача 1-го эпика", "Описание 1-й подзадачи 1-го эпика", epic1.getId()));
        manager.createSubtask(new Subtask("2-я подзадача 1-го эпика", "Описание 2-й подзадачи 1-го эпика", epic1.getId()));
        manager.createSubtask(new Subtask("1-я подзадача 2-го эпика", "Описание 1-й подзадачи 1-го эпика", epic2.getId()));

        manager.getTask(task1.getId());
        manager.getTask(task2.getId());
        manager.getTask(task3.getId());
        manager.getTask(task1.getId());
        manager.getEpic(epic1.getId());
        manager.getEpic(epic2.getId());
        manager.getSubtask(subtask1.getId());

        manager = FileBackedTasksManager.loadFromFile(Path.of("data.csv"));

        if (manager.getAllTasks().size() != 4) {
            throw new TestException("Неверное кол-во задач");
        }

        if (manager.getAllEpics().size() != 3) {
            throw new TestException("Неверное кол-во эпиков");
        }

        if (manager.getAllSubtasks().size() != 3) {
            throw new TestException("Неверное кол-во подзадач");
        }

        if (manager.getHistory().size() != 6) {
            throw new TestException("Неверное кол-во задач в истории");
        }

        System.out.println("PASSED testFileBackendTasksManager");
    }
}
