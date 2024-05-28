package manager;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    static TaskManager tm;
    static Task task1;
    static Task task2;
    static SubTask subTask1;
    static SubTask subTask2;
    static Epic epic1;
    static Epic epic2;
    static ArrayList<Task> expectedHistoryList;

    @BeforeAll
    static void createTM() {
        tm = Managers.getDefault();
    }

    @BeforeEach
    void prepareDataForTest() {
        epic1 = new Epic("Epic1", "1111", Status.NEW, Duration.ofMinutes(30), LocalDateTime.of(2024, 10, 12, 2, 34, 21));
        task1 = new Task("Task1", "NNN", Status.NEW, Duration.ofMinutes(40), LocalDateTime.of(2024, 11, 12, 2, 34, 21));
        subTask1 = new SubTask("SubTask1", "1111", Status.NEW, Duration.ofMinutes(30), LocalDateTime.of(2024, 12, 12, 2, 31, 21), 1);
        expectedHistoryList = new ArrayList<>();
        tm.addEpic(epic1);
        expectedHistoryList.add(tm.getEpicById(epic1.getId()));
        tm.addTask(task1);
        expectedHistoryList.add(tm.getTaskById(task1.getId()));
        tm.addSubTask(subTask1);
        expectedHistoryList.add(tm.getSubTaskById(subTask1.getId()));

    }

    @AfterEach
    void clearData() {
        tm.deleteAllTasks();
        tm.deleteAllSubTasks();
        tm.deleteAllEpics();
        tm.resetIdCounter();
    }


    @Test
    void checkAddTasksToHistoryAndGetHistory() {

        //проверим получение истории просмотра
        assertEquals(expectedHistoryList, tm.getHistory());
        //проверим порядок сохранения задач в историю просмотра
        assertEquals(epic1, tm.getHistory().getFirst());
        assertEquals(subTask1, tm.getHistory().getLast());
        //Проверим,что повторно просмотренная задача встала в конец,
        // а размер мапы не изменился (предыдущий просмотр удален)
        tm.getTaskById(task1.getId());
        assertEquals(3, tm.getHistory().size());
        assertEquals(task1, tm.getHistory().getLast());
    }

    @Test
    void checkRemoveTaskFromHistory() {
        //Проверим удаление из истории задачи
        tm.deleteTaskById(task1.getId());
        expectedHistoryList.remove(task1);
        assertEquals(expectedHistoryList.size(), tm.getHistory().size());
        //проверим, что при удалении эпика удалится и эпик и подзадача
        expectedHistoryList.clear();
        tm.deleteEpicById(epic1.getId());
        assertEquals(expectedHistoryList.size(), tm.getHistory().size());
    }

}