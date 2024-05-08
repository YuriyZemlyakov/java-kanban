package manager;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {
    static TaskManager tm;
    static HistoryManager history;

    @Test
    void getDefault() {
        tm = Managers.getDefault();
        assertTrue(tm instanceof InMemoryTaskManager);
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Test
    void getDefaultHistory() {
        history = Managers.getDefaultHistory();
        assertTrue(history instanceof HistoryManager);
    }
}