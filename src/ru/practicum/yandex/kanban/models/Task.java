package ru.practicum.yandex.kanban.models;

import java.time.Instant;
import java.util.Objects;

public class Task {
    protected Integer id;
    protected TaskStatus status = TaskStatus.NEW;
    protected String title;
    protected String description;
    protected Instant startTime;
    protected long duration;

    public Task(String title, String description, Instant startTime, long duration) {
        this.title = title;
        this.description = description;
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(Integer id, TaskStatus status, String title, String description, Instant startTime, long duration) {
        this.id = id;
        this.status = status;
        this.title = title;
        this.description = description;
        this.startTime = startTime;
        this.duration = duration;
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

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public Instant getEndTime() {
        long SECONDS_IN_MINUTE = 60L;
        return startTime.plusSeconds(duration * SECONDS_IN_MINUTE);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(id, task.id) && Objects.equals(title, task.title) && Objects.equals(description, task.description) && status.equals(task.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, status, title, description, startTime, duration);
    }

    @Override
    public String toString() {
        return id + "," +
                TaskType.TASK + "," +
                title + "," +
                status + "," +
                description + "," +
                startTime.toEpochMilli() + "," +
                duration;
    }
}
