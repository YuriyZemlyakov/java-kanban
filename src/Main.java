
public class Main {
    static TaskManager tm;

    public static void main(String[] args) {
        System.out.println("Поехали!");
        tm = new TaskManager();
        System.out.println("Создадим 2 задачи");
        Task task1 = new Task("Deal1", "Lets do something", Status.valueOf("NEW"));
        System.out.println("Задача 1 " + tm.addTask(task1) + " создана");
        Task task2 = new Task("Deal2", "Lets do something new", Status.valueOf("NEW"));
        System.out.println("Задача " + tm.addTask(task2) + " создана");

        System.out.println("Создадим 2 эпика");
        Epic epic1 = new Epic("Epic 1", "A task which contains subtasks", Status.valueOf("NEW"));
        System.out.println("Эпик " + tm.addEpic(epic1) + " создан");
        Epic epic2 = new Epic("Epic 2", "A task 2 which contains subtasks", Status.valueOf("NEW"));
        System.out.println("Эпик " + tm.addEpic(epic2) + " создан");
        System.out.println("Создадим 2 поздачи, привязанные к эпику 1");
        SubTask subTask1 = new SubTask("Subtask 1", "Some text", Status.valueOf("NEW"), epic1.id);
        tm.addSubTask(subTask1);
        SubTask subTask2 = new SubTask("Subtask 2", "Some text", Status.valueOf("NEW"), epic1.id);
        tm.addSubTask(subTask2);

        System.out.println("Созданы позадачи: " + tm.getAllSubTasks());
        System.out.println("Создадим позадачу, привязанную к эпику №2");
        SubTask subTask3 = new SubTask("Subtask 3", "Some text", Status.valueOf("NEW"), epic2.id);
        tm.addSubTask(subTask3);
        System.out.println("Полный список подзадач теперь выглядит так :" + tm.getAllSubTasks());
        System.out.println("Список позадач, привязанных к эпику1: " + tm.getSubTasksLinkedToEpic(epic1));
        System.out.println("Список позадач, привязанных к эпику2: " + tm.getSubTasksLinkedToEpic(epic2));
        System.out.println("Удалим позадачу 1");
        tm.deleteSubTaskById(subTask1.id);
        System.out.println("Cписок позадач теперь выглядит так :" + tm.getAllSubTasks());
        System.out.println("Список позадач эпика 1 выглядит теперь так: " + tm.getSubTasksLinkedToEpic(epic1));
        System.out.println("Удалим эпик");
        tm.deleteEpicById(epic1.id);
        System.out.println("Список эпиков теперь такой: " + tm.getAllEpics());
        System.out.println("Список подзадач теперь такой: " + tm.getAllSubTasks());
        System.out.println("Статус эпика сейчас: " + epic2.status);
        System.out.println("Изменим статус подзадачи");
        subTask3 = new SubTask("Updated task", "Add something new", 7, Status.DONE,epic2.id);
        tm.updateSubTask(subTask3);
        System.out.println("Теперь статус эпика: " + epic2.status);
        System.out.println("Добавим еще одну подзадачу");
        SubTask subTask4 = new SubTask("SubTask4", "New text", Status.NEW, epic2.id);
        tm.addSubTask(subTask4);
        System.out.println("Подзадачи эпика2: " + tm.getSubTasksLinkedToEpic(epic2));
        System.out.println("Теперь статус эпика: " + epic2.status);


    }
}
