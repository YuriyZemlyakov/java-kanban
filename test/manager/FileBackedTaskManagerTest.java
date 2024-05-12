package manager;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest {
    public static Path file;
    public static FileBackedTaskManager fbTaskManager;
    public static FileBackedTaskManager fbTaskManager2;

    @BeforeAll
    public static void createTaskManager() throws RuntimeException, IOException {
        file = null;
        try {
            file = Files.createTempFile("file", ".txt");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        fbTaskManager = FileBackedTaskManager.loadFromFile(file);
    }

    @AfterEach
    public void clearData() {
        fbTaskManager.deleteAllTasks();
        fbTaskManager.deleteAllSubTasks();
        fbTaskManager.deleteAllEpics();

    }

    @Test
    public void areTasksSavedInFile() {
        //проверим что сохраненных данных в менеджере нет
        assertEquals(0, fbTaskManager.getAllTasks().size());
        //Добавим в менеджжер новые задачи
        Task task1 = new Task("task1", "aaaa", Status.NEW);
        fbTaskManager.addTask(task1);
        Epic epic1 = new Epic("epic1", "eeeee", Status.NEW);
        fbTaskManager.addEpic(epic1);
        SubTask subTask1 = new SubTask("subtask1", "sssss", Status.IN_PROGRESS, 2);
        fbTaskManager.addSubTask(subTask1);
        SubTask subTask2 = new SubTask("subtask2", "yyyyyy", Status.DONE, 2);
        fbTaskManager.addSubTask(subTask2);

        List<Task> expectedTasksList = fbTaskManager.getAllTasks();
        List<Epic> expectedEpicsList = fbTaskManager.getAllEpics();
        List<SubTask> expectedSubTasksList = fbTaskManager.getAllSubTasks();
        List<SubTask> expectedSubTasksLinkedToEpic = fbTaskManager.getSubTasksLinkedToEpic(epic1);
        fbTaskManager2 = FileBackedTaskManager.loadFromFile(file);
        assertArrayEquals(expectedTasksList.toArray(), fbTaskManager2.getAllTasks().toArray());
        assertArrayEquals(expectedEpicsList.toArray(), fbTaskManager2.getAllEpics().toArray());
        assertArrayEquals(expectedSubTasksList.toArray(), fbTaskManager2.getAllSubTasks().toArray());
        assertArrayEquals(expectedSubTasksLinkedToEpic.toArray(), fbTaskManager2.getSubTasksLinkedToEpic(epic1).toArray());
        //проверим, что счеткик id работает корректно после восстановления данных из файла
        Task task2 = new Task("task2", "kjkljlkj", Status.NEW);
        fbTaskManager2.addTask(task2);
        assertEquals(5, task2.getId());

    }


}