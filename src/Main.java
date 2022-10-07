import ru.practicum.yandex.kanban.managers.FileBackedTasksManager;
import ru.practicum.yandex.kanban.server.HttpTaskServer;

import java.io.IOException;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) throws IOException {
        new HttpTaskServer(FileBackedTasksManager.loadFromFile(Path.of("data.csv")));
    }
}
