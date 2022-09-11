package ru.practicum.yandex.kanban.models;

import java.util.Objects;

public class Subtask extends Task {
    private final Integer epicId;

    public Subtask(String title, String description, Integer epicId) {
        super(title, description);
        this.epicId = epicId;
    }

    public Subtask(Integer id, TaskStatus status, String title, String description, Integer epicId) {
        super(id, status, title, description);
        this.epicId = epicId;
    }

    public Integer getEpicId() {
        return epicId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Subtask subtask = (Subtask) o;
        return Objects.equals(epicId, subtask.epicId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
    }

    @Override
    public String toString() {
        return id + "," +
                TaskType.SUBTASK + "," +
                title + "," +
                status + "," +
                description + "," +
                epicId;
    }
}
