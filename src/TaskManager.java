import java.util.ArrayList;
import java.util.HashMap;


public class TaskManager {
    private HashMap<Integer,Task> tasks;
    private HashMap<Integer,SubTask> subTasks;
    private HashMap<Integer,Epic> epics;
    private static int idCounter = 0;

    public TaskManager() {
        tasks = new HashMap<>();
        subTasks = new HashMap<>();
        epics = new HashMap<>();
    }
// Методы добавления новых задач
    public Task addTask(Task task) {
        idCounter++;
        task.setId(idCounter);
        tasks.put(task.id, task);
        return tasks.get(task.id);
    }
    public SubTask addSubTask(SubTask subTask) {
        idCounter++;
        subTask.setId(idCounter);
        subTasks.put(subTask.id, subTask);
        epics.get(subTask.epicLink).addLink(subTask.id);
        epics.get(subTask.epicLink).status = calculateEpicStatus(subTask.epicLink);
        return subTasks.get(subTask.id);
    }
    public Epic addEpic(Epic epic) {
        idCounter++;
        epic.setId(idCounter);
        epics.put(epic.id, epic);
        return epics.get(epic.id);
    }

    //Методы получения списка всех задач каждого типа
    public HashMap<Integer, Task> getAllTasks() {
        return tasks;
    }
    public HashMap<Integer, SubTask> getAllSubTasks() {
        return subTasks;
    }
    public HashMap<Integer, Epic> getAllEpics() {
        return epics;
    }

    // Методы получения задачи по идентификтору
    public Task getTaskById(int id) {
        return tasks.get(id);
    }
    public SubTask getSubTaskById(int id) {
        return subTasks.get(id);
    }
    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    // Методы удаления всех задач
    public void deleteAllTasks() {
        tasks.clear();
    }
    public void deleteAllSubTasks() {
        subTasks.clear();
    }
    public void deleteAllEpics() {
        //Удаляем все связанные подзадачи
        for (Epic epic : epics.values()) {
            for (Integer subTasksLink : epic.subTasksLinks) {
                deleteSubTaskById(subTasksLink);
            }
        }
        //теперь удаляем сами эпики
        epics.clear();
    }

    //Методы удаления задач по идентификтору
    public void deleteTaskById(int id) {
        tasks.remove(id);
    }
    public void deleteSubTaskById(int id) {
        //перед удаление подзадачи находим  связь с эпиком и удаляем ее
        int epicLink = subTasks.get(id).epicLink;
        epics.get(epicLink).subTasksLinks.remove((Integer) id);
        //удаляем саму подзадачу
        subTasks.remove(id);

    }
    public void deleteEpicById(int id) {
        //удаляем связанные позадачи
        for (Integer subTasksLink : epics.get(id).subTasksLinks) {
            subTasks.remove(subTasksLink);
        }
        //удаляем сам эпик
        epics.remove(id);
    }

    //Методы обновления задач
    public void updateTask(Task task) {
        tasks.put(task.id, task);
    }
    public void updateSubTask(SubTask subTask) {
        subTasks.put(subTask.id, subTask);
        //Обновляем статус эпика
        epics.get(subTask.epicLink).status = calculateEpicStatus(subTask.epicLink);


    }
    public void updateEpic(Epic epic) {
        epics.put(epic.id, epic);
    }

    // Метод получения списка подзадач эпика
    public ArrayList<SubTask> getSubTasksLinkedToEpic(Epic epic) {
        ArrayList<SubTask> subTasksLinkedToEpic = new ArrayList<>();
        for (Integer subTasksLink : epic.subTasksLinks) {
            subTasksLinkedToEpic.add(subTasks.get(subTasksLink));
        }
        return subTasksLinkedToEpic;
    }
    //метод для расчета статуса эпика
    private Status calculateEpicStatus(int epicId) {
        //определяем перечеь связанных подзадач, у которых нужно проверить статус
        ArrayList<Integer> linkedSubTasks = epics.get(epicId).subTasksLinks;
        Status calculatedStatus = Status.NEW;
        if (!linkedSubTasks.isEmpty()) {
            ArrayList<Status> statusList = new ArrayList<>();
            for (Integer linkedSubTask : linkedSubTasks) {
               statusList.add(subTasks.get(linkedSubTask).status);
            }
            boolean isStatusIN_PROGRESS = false;
            boolean statusListIncludeNEW = false;
            boolean statusListIncludeDONE = false;
            for (Status status : statusList) {
                if (status == Status.IN_PROGRESS) {
                    isStatusIN_PROGRESS = true;
                    break;
                } else if (status == Status.NEW) {
                        statusListIncludeNEW = true;
                } else {
                    statusListIncludeDONE = true;
                }
            }
            if (isStatusIN_PROGRESS) {
                calculatedStatus = Status.IN_PROGRESS;
            } else if (statusListIncludeNEW && !statusListIncludeDONE) {
                calculatedStatus = Status.NEW;
            } else if (statusListIncludeDONE && !statusListIncludeNEW) {
                calculatedStatus = Status.DONE;
            } else {
                calculatedStatus = Status.IN_PROGRESS;
            }
        }
        return calculatedStatus;
    }

}
