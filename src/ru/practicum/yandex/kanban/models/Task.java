package ru.practicum.yandex.kanban.models;

import java.util.Objects;

public class Task {
    Integer id;
    TaskStatus status = TaskStatus.NEW;
    final String title;
    final String description;

    public Task(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public Task(Integer id, TaskStatus status, String title, String description) {
        this.id = id;
        this.status = status;
        this.title = title;
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(id, task.id) && Objects.equals(title, task.title) && Objects.equals(description, task.description) && status == task.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, status);
    }

    @Override
    public String toString() {
        return id + "," +
                TaskType.TASK + "," +
                title + "," +
                status + "," +
                description;
    }
}
