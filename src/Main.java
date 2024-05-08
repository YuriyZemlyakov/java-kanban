import manager.Managers;
import manager.TaskManager;
import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;

public class Main {
    static TaskManager tm;

    public static void main(String[] args) {

        tm = Managers.getDefault();

        System.out.println("Поехали!");

        System.out.println("Создадим 2 задачи");
        Task task1 = new Task("Deal1", "Lets do something", Status.valueOf("NEW"));
        System.out.println("Задача 1 " + tm.addTask(task1) + " создана");
        Task task2 = new Task("Deal2", "Lets do something new", Status.valueOf("NEW"));
        System.out.println("Задача " + tm.addTask(task2) + " создана");

        System.out.println("Создадим 2 эпика");
        Epic epic1 = new Epic("Model.Epic 1", "A task which contains subtasks", Status.valueOf("NEW"));
        System.out.println("Эпик " + tm.addEpic(epic1) + " создан");
        Epic epic2 = new Epic("Model.Epic 2", "A task 2 which contains subtasks", Status.valueOf("NEW"));
        System.out.println("Эпик " + tm.addEpic(epic2) + " создан");
        System.out.println("Создадим 3 поздачи, привязанные к эпику 1");
        SubTask subTask1 = new SubTask("Subtask 1", "Some text", Status.valueOf("NEW"), epic1.getId());
        tm.addSubTask(subTask1);
        SubTask subTask2 = new SubTask("Subtask 2", "Some text", Status.valueOf("NEW"), epic1.getId());
        tm.addSubTask(subTask2);
        SubTask subTask3 = new SubTask("Subtask 3", "Some text", Status.valueOf("NEW"), epic1.getId());
        tm.addSubTask(subTask3);
        System.out.println("Полный список подзадач теперь выглядит так :" + tm.getAllSubTasks());
        System.out.println("Проверим, что в истории сохраняются только уникальные значения");
        tm.getTaskById(1);
        tm.getTaskById(2);
        tm.getEpicById(3);
        tm.getEpicById(4);
        tm.getTaskById(1);
        tm.getTaskById(2);
        tm.getEpicById(3);
        tm.getEpicById(4);
        tm.getTaskById(1);
        tm.getSubTaskById(5);
        tm.getSubTaskById(6);
        for (Task task : tm.getHistory()) {
            System.out.println(task);
        }
        System.out.println("Проверим, что задачи сохраняются в историю в нужном порядке");
        tm.getSubTaskById(6);
        tm.getSubTaskById(5);
        tm.getEpicById(4);
        for (Task task : tm.getHistory()) {
            System.out.println(task);
        }
        System.out.println("Проверим, что при удалении задачи, она удаляется из истории");
        tm.deleteTaskById(1);
        System.out.println(tm.getHistory());
        System.out.println("Проверим, что при удалении эпика из истории удалится и сам эпик и его подзадачи");
        tm.deleteEpicById(3);
        for (Task task : tm.getHistory()) {
            System.out.println(task);
        }


    }
}
