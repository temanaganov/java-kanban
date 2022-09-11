package ru.practicum.yandex.kanban.managers;

import ru.practicum.yandex.kanban.models.Task;

import java.util.ArrayList;
import java.util.List;

public interface HistoryManager {
    void add(Task task);
    void remove(int id);
    void clear();
    List<Task> getHistory();

    static String historyToString(HistoryManager manager) {
        StringBuilder sb = new StringBuilder();

        for (Task task : manager.getHistory()) {
            sb.append(task.getId()).append(",");
        }

        if (sb.isEmpty()) {
            sb.append(0);
        } else {
            sb.setLength(sb.length() - 1);
        }

        return sb.toString();
    }

    static List<Integer> historyFromString(String value) {
        if (value.equals("0")) return new ArrayList<>();

        List<Integer> history = new ArrayList<>();

        for (String id : value.split(",")) {
            history.add(Integer.parseInt(id));
        }

        return history;
    }
}
