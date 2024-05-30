package manager;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

abstract class TaskManagerTest {
    static TaskManager tm;
    static Task task1;
    static Task task2;
    static SubTask subTask1;
    static SubTask subTask2;
    static SubTask subTask3;
    static Epic epic1;
    static Epic epic2;

    public abstract TaskManager getTaskManager();

    @BeforeEach
    void createTasks() {
        epic1 = new Epic("Epic1", "1111", Status.NEW, Duration.ofMinutes(30), LocalDateTime.of(2024, Month.JUNE, 24, 12, 30, 30));
        epic2 = new Epic("Epic2", "2222eeee", Status.NEW, Duration.ofMinutes(30), LocalDateTime.of(2024, Month.JUNE, 23, 6, 34, 23));
        task1 = new Task("Task1", "NNN", Status.NEW, null, null);
        task2 = new Task("Task2", "NNN", Status.NEW, Duration.ofMinutes(30), LocalDateTime.of(2024, Month.JUNE, 24, 23, 23, 15));
        subTask1 = new SubTask("SubTask1", "1111", Status.NEW, Duration.ofMinutes(200), LocalDateTime.of(2024, Month.AUGUST, 23, 9, 23, 43), 1);
        subTask2 = new SubTask("SubTask2", "2222", Status.DONE, Duration.ofMinutes(25), LocalDateTime.of(2024, Month.JUNE, 15, 11, 34, 23), 1);
        subTask3 = new SubTask("SubTask3", "3333", Status.IN_PROGRESS, Duration.ofMinutes(5), LocalDateTime.of(2024, Month.JUNE, 15, 13, 34, 23), 1);


    }

    @Test
    void addTask() {
        tm = getTaskManager();
        Assertions.assertEquals(tm.addTask(task1), task1);
    }

    @Test
    void addSubTask() {
        tm = getTaskManager();
        tm.addEpic(epic1);
        tm.addSubTask(subTask1);
        SubTask actualdSubTask = tm.getSubTaskById(2);
        Assertions.assertEquals(subTask1, actualdSubTask);

    }

    @Test
    void addEpic() {
        tm = getTaskManager();
        Assertions.assertEquals(tm.addEpic(epic1), epic1);
        tm.addTask(task1);
        int expectedId = (task1.getId() + 1);
        tm.addEpic(epic2);
        Assertions.assertEquals(expectedId, epic2.getId());
    }

    @Test
    void getAllSubTasks() {
        tm = getTaskManager();
        tm.addEpic(epic1);
        tm.addSubTask(subTask1);
        tm.addSubTask(subTask2);
        ArrayList<SubTask> expectedArray = new ArrayList<>();
        expectedArray.add(subTask1);
        expectedArray.add(subTask2);
        Assertions.assertEquals(expectedArray, tm.getAllSubTasks());
    }

    @Test
    void getTaskById() {
        tm = getTaskManager();
        tm.addTask(task1);
        tm.addTask(task2);
        Assertions.assertEquals(task2, tm.getTaskById(2));
    }

    @Test
    void deleteAllEpics() {
        tm = getTaskManager();
        tm.addEpic(epic1);
        tm.addEpic(epic2);
        tm.addSubTask(subTask1);
        tm.addSubTask(subTask2);
        tm.deleteAllEpics();
        Assertions.assertTrue(tm.getAllEpics().isEmpty());
        Assertions.assertTrue(tm.getAllSubTasks().isEmpty());

    }

    @Test
    void deleteSubTaskById() {
        tm = getTaskManager();
        tm.addEpic(epic1);
        tm.addSubTask(subTask1);
        tm.addSubTask(subTask2);
        tm.deleteSubTaskById(2);
        Assertions.assertEquals(1, tm.getAllSubTasks().size());
        assertEquals(1, tm.getSubTasksLinkedToEpic(epic1).size());

    }

    @Test
    void getSubTasksLinkedToEpic() {
        tm = getTaskManager();
        tm.addEpic(epic1);
        tm.addSubTask(subTask1);
        tm.addSubTask(subTask2);
        ArrayList<SubTask> expectedSubtaskList = new ArrayList<>();
        expectedSubtaskList.add(subTask1);
        expectedSubtaskList.add(subTask2);
        assertEquals(expectedSubtaskList, tm.getSubTasksLinkedToEpic(epic1));

    }

    @Test
    void AreThereConflictsBetweenAutoGeneratedAndManuallyPointedIds() {
        //проверяем, что при добавлении задачи переданный id игнорируется и автогенерируется новый
        tm = getTaskManager();
        tm.addTask(task1);
        Task TaskWithDuplicatedId = new Task("EditedTask1", "Some text", 1, Status.NEW, Duration.ofMinutes(24), LocalDateTime.of(2024, 1, 23, 4, 5, 6));
        tm.addTask(TaskWithDuplicatedId);
        assertEquals((task1.getId() + 1), TaskWithDuplicatedId.getId());
    }

    @Test
    void taskFieldsShouldNotChangeWheAddToManager() {
        tm = getTaskManager();
        tm.addTask(task1);
        Task addedTask = tm.getTaskById(task1.getId());
        assertEquals(task1.getName(), addedTask.getName());
        assertEquals(task1.getDescription(), addedTask.getDescription());
        assertEquals(task1.getStatus(), addedTask.getStatus());
    }

    @Test
    void isEpicStatusCalculatedCorrect() {
        tm = getTaskManager();
        tm.addEpic(epic1);
        tm.addSubTask(subTask1);
        assertEquals(Status.NEW, epic1.getStatus());
        tm.addSubTask(subTask2);
        assertEquals(Status.IN_PROGRESS, epic1.getStatus());
        SubTask subTask1Updated = new SubTask("st1U", "UUUUU", 2, Status.DONE, Duration.ofMinutes(10), LocalDateTime.of(2024, 12, 23, 12, 35, 20), 1);
        tm.updateSubTask(subTask1Updated);
        assertEquals(Status.DONE, epic1.getStatus());
        tm.addSubTask(subTask3);
        assertEquals(Status.IN_PROGRESS, epic1.getStatus());

    }

    @Test
    void isOverlapse() {
        tm = getTaskManager();
        task1 = new Task("Task1", "NNN", Status.NEW, Duration.ofMinutes(120), LocalDateTime.of(2024, Month.JUNE, 24, 23, 20, 15));
        task2 = new Task("Task2", "NNN", Status.NEW, Duration.ofMinutes(30), LocalDateTime.of(2024, Month.JUNE, 24, 23, 23, 15));
        assertEquals(task1, tm.addTask(task1));
        assertEquals(null, tm.addTask(task2));
    }

}

