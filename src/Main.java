import ru.practicum.yandex.kanban.tests.Tests;

public class Main {
    public static void main(String[] args) {
        Tests.testInMemoryHistoryManager();
        Tests.testFileBackendTasksManager();
    }
}
