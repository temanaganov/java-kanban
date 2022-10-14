package ru.practicum.yandex.kanban.managers;

import ru.practicum.yandex.kanban.models.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node<Task>> history = new HashMap<>();
    private Node<Task> head;
    private Node<Task> tail;

    @Override
    public void add(Task task) {
        Node<Task> node = linkLast(task);

        if (history.containsKey(task.getId())) {
            removeNode(history.get(task.getId()));
        }

        history.put(task.getId(), node);
    }

    @Override
    public void remove(int id) {
        if (history.containsKey(id)) {
            removeNode(history.get(id));
            history.remove(id);
        }
    }

    @Override
    public void clear() {
        history.clear();
        head = null;
        tail = null;
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    private List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        Node<Task> current = head;

        while (current != null) {
            tasks.add(current.value);
            current = current.next;
        }

        return tasks;
    }

    private Node<Task> linkLast(Task task) {
        Node<Task> newNode = new Node<>(task, tail, null);

        if (tail == null) {
            head = newNode;
        } else {
            tail.next = newNode;
        }

        tail = newNode;

        return newNode;
    }

    private void removeNode(Node<Task> node) {
        if (node.equals(head)) {
            head = node.next;

            if (node.next != null) {
                node.next.prev = null;
            }
        } else {
            node.prev.next = node.next;

            if (node.next != null) {
                node.next.prev = node.prev;
            }
        }
    }

    private static class Node<T> {
        T value;
        Node<T> prev;
        Node<T> next;

        Node(T value, Node<T> prev, Node<T> next) {
            this.value = value;
            this.prev = prev;
            this.next = next;
        }
    }
}
