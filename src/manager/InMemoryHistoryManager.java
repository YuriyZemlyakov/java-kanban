package manager;
import model.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager{
    private List<Task> history;
    private Map<Integer, Node> historyMap;
    private Node head;
    private Node tail;
    private  int size = 0;


    public InMemoryHistoryManager()  {
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
        size++;
        int taskId = task.getId();
        if (historyMap.containsKey(taskId)) {
            removeNode(historyMap.get(taskId));
            historyMap.remove(task.getId());
        }
        historyMap.put(task.getId(),newNode);
    }

    private void removeNode(Node node) {
        if (node != null) {
            final Task task = node.task;
            final Node next = node.next;
            final Node prev = node.prev;

            if (prev == null) {
                head = next;
            } else {
                prev.next = next;
                node.prev = null;
            }

            if (next == null) {
                tail = prev;
            } else {
                next.prev = prev;
                node.next = null;
            }
            node.task = null;
            size--;
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
        linkLast(task);
    }

    @Override
    public ArrayList<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void remove(int taskId) {
        removeNode(historyMap.get(taskId));
        historyMap.remove(taskId);
    }
}
