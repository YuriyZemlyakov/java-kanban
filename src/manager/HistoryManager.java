package manager;

import model.Task;

import java.util.ArrayList;

public interface HistoryManager {
    void addTaskToHistory(Task task);

    ArrayList<Task> getHistory();

    void remove(int taskId);
}
