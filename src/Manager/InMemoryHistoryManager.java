package Manager;

import Model.Task;
import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager{
    private ArrayList<Task> history;
    private final int maxHistorySize = 10;

    public InMemoryHistoryManager()  {
        this.history = new ArrayList<>();
    }

    @Override
    public void addTaskToHistory(Task task) {
        if (history.size() <= maxHistorySize) {
            history.add(task);
        } else history.add(0, task);
    }

    @Override
    public ArrayList<Task> getHistory() {
        return history;
    }
}
