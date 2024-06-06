package manager;

public class InMemoryTaskManagerTest extends TaskManagerTest {
    @Override
    public TaskManager getTaskManager() {
        return Managers.getDefault();
    }
}
