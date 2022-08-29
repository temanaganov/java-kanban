package ru.practicum.yandex.kanban.managers;

import ru.practicum.yandex.kanban.models.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node<Task>> history = new HashMap<>();
    private final CustomLinkedList historyList = new CustomLinkedList();

    @Override
    public void add(Task task) {
        Node<Task> node = historyList.linkLast(task);

        if (history.containsKey(task.getId())) {
            historyList.removeNode(history.get(task.getId()));
        }

        history.put(task.getId(), node);
    }

    @Override
    public void remove(int id) {
        historyList.removeNode(history.get(id));
        history.remove(id);
    }

    @Override
    public void clear() {
        history.clear();
        historyList.clear();
    }

    @Override
    public List<Task> getHistory() {
        return historyList.getTasks();
    }
}

class CustomLinkedList {
    private Node<Task> head;
    private Node<Task> tail;

    public Node<Task> linkLast(Task task) {
        Node<Task> newNode = new Node<>(task, tail, null);

        if (tail == null) {
            head = newNode;
        } else {
            tail.next = newNode;
        }

        tail = newNode;

        return newNode;
    }

    public List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        Node<Task> current = head;

        while (current != null) {
            tasks.add(current.value);
            current = current.next;
        }

        return tasks;
    }

    public void removeNode(Node<Task> node) {
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

    public void clear() {
        head = null;
        tail = null;
    }
}

class Node<T> {
    T value;
    Node<T> prev;
    Node<T> next;

    Node(T value, Node<T> prev, Node<T> next) {
        this.value = value;
        this.prev = prev;
        this.next = next;
    }
}
