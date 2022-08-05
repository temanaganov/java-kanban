import ru.practicum.yandex.kanban.managers.Managers;
import ru.practicum.yandex.kanban.managers.TaskManager;
import ru.practicum.yandex.kanban.models.Epic;
import ru.practicum.yandex.kanban.models.Subtask;
import ru.practicum.yandex.kanban.models.Task;
import ru.practicum.yandex.kanban.models.TaskStatus;

public class Main {
    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();
        boolean testPassed = test(manager);

        if (testPassed) {
            System.out.println("Тест успешно пройден");
        } else {
            System.out.println("Тест не пройден");
        }
    }

    public static boolean test(TaskManager manager) {
        Task firstTask = new Task("1-я задача", "Описание 1-й задачи");
        Task secondTask = new Task("2-я задача", "Описание 2-й задачи");
        Epic firstEpic = new Epic("1-й эпик", "Описание 1-го эпика");
        Epic secondEpic = new Epic("2-й эпик", "Описание 2-го эпика");

        firstTask = manager.createTask(firstTask);
        secondTask = manager.createTask(secondTask);
        firstEpic = manager.createEpic(firstEpic);
        secondEpic = manager.createEpic(secondEpic);

        if (firstTask.getId() == null || secondTask.getId() == null || firstEpic.getId() == null || secondEpic.getId() == null) {
            return false;
        }

        Subtask firstSubtask = new Subtask("1-я подзадача 1-го эпика", "Описание 1-й подзадачи 1-го эпика", firstEpic.getId());
        Subtask secondSubtask = new Subtask("2-я подзадача 1-го эпика", "Описание 2-й подзадачи 1-го эпика", firstEpic.getId());
        Subtask thirdSubtask = new Subtask("1-я подзадача 2-го эпика", "Описание 1-й подзадачи 1-го эпика", secondEpic.getId());

        firstSubtask = manager.createSubtask(firstSubtask);
        secondSubtask = manager.createSubtask(secondSubtask);
        thirdSubtask = manager.createSubtask(thirdSubtask);

        boolean allSubtasksHaveId = firstSubtask.getId() != null && secondSubtask.getId() != null && thirdSubtask.getId() != null;

        if (!allSubtasksHaveId) {
            return false;
        }

        boolean epicsContainsSubtasks = firstEpic.getSubtasks().size() == 2 && secondEpic.getSubtasks().size() == 1;

        if (!epicsContainsSubtasks) {
            return false;
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
            return false;
        }

        firstSubtask.setStatus(TaskStatus.IN_PROGRESS);
        manager.updateSubtask(firstSubtask);

        thirdSubtask.setStatus(TaskStatus.DONE);
        manager.updateSubtask(thirdSubtask);

        boolean isStatusesUpdated = firstEpic.getStatus() == TaskStatus.IN_PROGRESS && secondEpic.getStatus() == TaskStatus.DONE;

        if (!isStatusesUpdated) {
            return false;
        }

        firstSubtask.setStatus(TaskStatus.DONE);
        manager.updateSubtask(firstSubtask);

        secondSubtask.setStatus(TaskStatus.DONE);
        manager.updateSubtask(secondSubtask);

        isStatusesUpdated = firstEpic.getStatus() == TaskStatus.DONE;

        if (!isStatusesUpdated) {
            return false;
        }

        manager.removeTask(firstTask.getId());

        if (manager.getAllTasks().size() != 1) {
            return false;
        }

        manager.removeEpic(firstEpic.getId());

        if (manager.getAllEpics().size() != 1 || manager.getAllSubtasks().size() != 1) {
            return false;
        }

        manager.getTask(firstTask.getId());
        manager.getTask(secondTask.getId());
        manager.getSubtask(firstSubtask.getId());
        manager.getSubtask(secondSubtask.getId());
        manager.getEpic(firstEpic.getId());
        manager.getEpic(secondEpic.getId());

        if (manager.getHistory().size() != 6) {
            return false;
        }

        return true;
    }
}
