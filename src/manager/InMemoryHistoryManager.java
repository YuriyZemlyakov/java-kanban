package manager;

import model.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private Map<Integer, Node> historyMap;
    private Node head;
    private Node tail;


    public InMemoryHistoryManager() {
        this.historyMap = new LinkedHashMap<>();
    }

    private void linkLast(Task task) {
        final Node oldTail = tail;
        final Node newNode = new Node(oldTail, task, null);
        tail = newNode;
        if (oldTail == null)
            head = newNode;
        else
            oldTail.next = newNode;
        int taskId = task.getId();
        historyMap.put(task.getId(), newNode);
    }

    private void removeNode(int taskId) {
        Node removedNode = historyMap.remove(taskId);
        if (removedNode != null) {
            final Task task = removedNode.task;
            final Node next = removedNode.next;
            final Node prev = removedNode.prev;

            if (prev == null) {
                head = next;
            } else {
                prev.next = next;
                removedNode.prev = null;
            }

            if (next == null) {
                tail = prev;
            } else {
                next.prev = prev;
                removedNode.next = null;
            }
            removedNode.task = null;
        } else {
            return;
        }
    }

    private ArrayList<Task> getTasks() {
        ArrayList<Task> historyList = new ArrayList<>();
        for (Node node : historyMap.values()) {
            historyList.add(node.task);
        }
        return historyList;
    }

    @Override
    public void addTaskToHistory(Task task) {
        remove(task.getId());
        linkLast(task);
    }

    @Override
    public ArrayList<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void remove(int taskId) {
        removeNode(taskId);

    }
}
