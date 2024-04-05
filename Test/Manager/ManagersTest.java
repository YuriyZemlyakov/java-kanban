package Manager;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {
    static InMemoryTaskManager tm;
    static HistoryManager history;

    @Test
    void getDefault() {
        tm = Managers.getDefault();
        assertTrue(tm instanceof InMemoryTaskManager);
    }

    @Test
    void getDefaultHistory() {
        history = Managers.getDefaultHistory();
        assertTrue(history instanceof HistoryManager);
    }
}