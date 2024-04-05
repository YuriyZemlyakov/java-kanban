package Manager;

import Model.Epic;
import Model.SubTask;
import Model.Task;

import java.util.ArrayList;

public interface TaskManager {
    // Методы добавления новых задач
    Task addTask(Task task);

    SubTask addSubTask(SubTask subTask);

    Epic addEpic(Epic epic);

    //Методы получения списка всех задач каждого типа
    ArrayList<Task> getAllTasks();

    ArrayList<SubTask> getAllSubTasks();

    ArrayList<Epic> getAllEpics();

    // Методы получения задачи по идентификтору
    Task getTaskById(int id);

    SubTask getSubTaskById(int id);

    Epic getEpicById(int id);

    // Методы удаления всех задач
    void deleteAllTasks();

    void deleteAllSubTasks();

    void deleteAllEpics();

    //Методы удаления задач по идентификтору
    void deleteTaskById(int id);

    void deleteSubTaskById(int id);

    void deleteEpicById(int id);

    //Методы обновления задач
    void updateTask(Task task);

    void updateSubTask(SubTask subTask);

    void updateEpic(Epic epic);

    // Метод получения списка подзадач эпика
    ArrayList<SubTask> getSubTasksLinkedToEpic(Epic epic);
    ArrayList<Task> getHistory();

    void resetIdCounter(); //добавил для юнит-тестов


}
