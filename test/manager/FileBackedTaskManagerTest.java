package manager;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest extends TaskManagerTest {
    public static Path file;
    public static TaskManager tm2;

    @Override
    public TaskManager getTaskManager() throws RuntimeException {
        file = null;
        try {
            file = Files.createTempFile("file", ".txt");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return FileBackedTaskManager.loadFromFile(file);
    }

    @BeforeAll
    public static void createTaskManager() throws RuntimeException, IOException {
        file = null;
        try {
            file = Files.createTempFile("file", ".txt");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        tm = FileBackedTaskManager.loadFromFile(file);
    }

    @AfterEach
    public void clearData() {
        tm.deleteAllTasks();
        tm.deleteAllSubTasks();
        tm.deleteAllEpics();
        tm.resetIdCounter();

    }

    @Test
    public void areTasksSavedInFile() {
        //проверим что сохраненных данных в менеджере нет

        assertEquals(0, tm.getAllTasks().size());
        //Добавим в менеджжер новые задачи
        Task task1 = new Task("task1", "aaaa", Status.NEW, Duration.ofMinutes(30), LocalDateTime.of(2024, Month.JUNE, 26, 0, 0, 0));
        tm.addTask(task1);
        Epic epic1 = new Epic("epic1", "eeeee", Status.NEW, null, null);
        tm.addEpic(epic1);
        SubTask subTask1 = new SubTask("subtask1", "sssss", Status.IN_PROGRESS, Duration.ofMinutes(10), LocalDateTime.of(2024, Month.MAY, 30, 0, 0, 0), 2);
        tm.addSubTask(subTask1);
        SubTask subTask2 = new SubTask("subtask2", "yyyyyy", Status.DONE, Duration.ofMinutes(50), LocalDateTime.of(2024, Month.MAY, 29, 10, 1, 2), 2);
        tm.addSubTask(subTask2);

        List<Task> expectedTasksList = tm.getAllTasks();
        List<Epic> expectedEpicsList = tm.getAllEpics();
        List<SubTask> expectedSubTasksList = tm.getAllSubTasks();
        List<SubTask> expectedSubTasksLinkedToEpic = tm.getSubTasksLinkedToEpic(epic1);
        tm2 = FileBackedTaskManager.loadFromFile(file);
        assertArrayEquals(expectedTasksList.toArray(), tm2.getAllTasks().toArray());
        assertArrayEquals(expectedEpicsList.toArray(), tm2.getAllEpics().toArray());
        assertArrayEquals(expectedSubTasksList.toArray(), tm2.getAllSubTasks().toArray());
        assertArrayEquals(expectedSubTasksLinkedToEpic.toArray(), tm2.getSubTasksLinkedToEpic(epic1).toArray());
        //проверим, что счеткик id работает корректно после восстановления данных из файла
        Task task2 = new Task("task2", "kjkljlkj", Status.NEW, Duration.ofMinutes(90), LocalDateTime.of(2024, 12, 30, 12, 34, 54));
        tm2.addTask(task2);
        assertEquals(5, task2.getId());

    }


}
