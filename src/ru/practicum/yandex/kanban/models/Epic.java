package ru.practicum.yandex.kanban.models;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Epic extends Task {
    private final List<Integer> subtasks = new ArrayList<>();
    private Instant endTime = Instant.ofEpochMilli(0);

    public Epic(String title, String description) {
        super(title, description, Instant.ofEpochMilli(0), 0);
    }

    public Epic(int id, TaskStatus status, String title, String description, Instant startTime, long duration) {
        super(title, description, startTime, duration);
        this.id = id;
        this.status = status;
        this.endTime = super.getEndTime();
    }

    public Epic(Epic epic){
        this(epic.title, epic.description);
    }

    public List<Integer> getSubtasks() {
        return subtasks;
    }

    public void addSubtask(Subtask subtask) {
        subtasks.add(subtask.getId());
    }

    public void removeSubtask(Subtask subtask) {
        subtasks.remove(subtask.getId());
    }

    public void update(Map<Integer, Subtask> allSubtasks) {
        if (getSubtasks().isEmpty()) {
            this.status = TaskStatus.NEW;
            return;
        }

        int newCount = 0;
        int doneCount = 0;
        Instant startTime = allSubtasks.get(subtasks.get(0)).getStartTime();
        Instant endTime = allSubtasks.get(subtasks.get(0)).getEndTime();

        for (Integer subtaskId : getSubtasks()) {
            Subtask subtask = allSubtasks.get(subtaskId);
            if (subtask.getStatus() == TaskStatus.NEW) newCount += 1;
            if (subtask.getStatus() == TaskStatus.DONE) doneCount += 1;
            if (subtask.getStartTime().isBefore(startTime)) startTime = subtask.getStartTime();
            if (subtask.getEndTime().isAfter(endTime)) endTime = subtask.getEndTime();
        }

        this.startTime = startTime;
        this.endTime = endTime;
        this.duration = Duration.between(startTime, endTime).toMinutes();

        if (getSubtasks().size() == newCount) {
            this.status = TaskStatus.NEW;
            return;
        }

        if (getSubtasks().size() == doneCount) {
            this.status = TaskStatus.DONE;
            return;
        }

        this.status = TaskStatus.IN_PROGRESS;
    }

    @Override
    public Instant getEndTime() {
        return endTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subtasks, epic.subtasks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtasks);
    }

    @Override
    public String toString() {
        return id + "," +
                TaskType.EPIC + "," +
                title + "," +
                status + "," +
                description + "," +
                startTime.toEpochMilli() + "," +
                duration;
    }
}
