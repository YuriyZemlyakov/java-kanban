package Manager;

import Model.Epic;
import Model.Status;
import Model.SubTask;
import Model.Task;

import java.util.ArrayList;
import java.util.HashMap;


public class TaskManager {
    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, SubTask> subTasks;
    private HashMap<Integer,Epic> epics;
    private int idCounter = 0;

    public TaskManager() {
        tasks = new HashMap<>();
        subTasks = new HashMap<>();
        epics = new HashMap<>();
    }
// Методы добавления новых задач
    public Task addTask(Task task) {
        idCounter++;
        task.setId(idCounter);
        tasks.put(task.getId(), task);
        return task;
    }
    public SubTask addSubTask(SubTask subTask) {
        if (epics.containsKey(subTask.getEpicLink())) {
            idCounter++;
            subTask.setId(idCounter);
            subTasks.put(subTask.getId(), subTask);
            epics.get(subTask.getEpicLink()).addLink(subTask.getId());
            epics.get(subTask.getEpicLink()).setStatus(calculateEpicStatus(subTask.getEpicLink()));
            return subTask;
        } else {
            System.out.println("Эпика с таким id не найдено");
            return null;
        }

    }
    public Epic addEpic(Epic epic) {
        idCounter++;
        epic.setId(idCounter);
        epics.put(epic.getId(), epic);
        return epic;
    }

    //Методы получения списка всех задач каждого типа
    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> allTasks = new ArrayList<>(tasks.values());
        return allTasks;
    }
    public ArrayList<SubTask> getAllSubTasks() {
        ArrayList<SubTask> allSubTasks = new ArrayList<>(subTasks.values());
        return allSubTasks;
    }
    public ArrayList<Epic> getAllEpics() {
        ArrayList<Epic> allEpics = new ArrayList<>(epics.values());
        return allEpics;
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
        // Удалеям связи с эпиками
        for (Epic epic : epics.values()) {
            epic.deleteAllLinks();
            epic.setStatus(Status.NEW);
        }
        //Удаляем сами подзадачи
        subTasks.clear();
    }
    public void deleteAllEpics() {
        //Удаляем все связанные подзадачи
        subTasks.clear();
        //теперь удаляем сами эпики
        epics.clear();
    }

    //Методы удаления задач по идентификтору
    public void deleteTaskById(int id) {
        tasks.remove(id);
    }
    public void deleteSubTaskById(int id) {
        if (subTasks.containsKey(id)) {
            int epicLink = subTasks.get(id).getEpicLink();
            //удаляем связь с эпиком
            epics.get(epicLink).deleteLinkById(id);
            // пересчитываем статус эпика
            epics.get(epicLink).setStatus(calculateEpicStatus(subTasks.get(id).getEpicLink()));
        //удаляем саму подзадачу
            subTasks.remove(id);
       } else {
            System.out.println("Такого id не существует");
            return;
        }
        }
    public void deleteEpicById(int id) {
        if (epics.containsKey(id)) {
            //удаляем связанные позадачи
            for (Integer subTasksLink : epics.get(id).getSubTasksLinks()) {
                subTasks.remove(subTasksLink);
            }
            //удаляем сам эпик
            epics.remove(id);
        } else {
            System.out.println("Такого id не существует");
            return;
        }
    }

    //Методы обновления задач
    public void updateTask(Task task) {
        if (tasks.containsValue(task)) {
            tasks.put(task.getId(), task);
        } else {
            System.out.println("Задачи для обновления в хранилище не найдено");
            return;
        }
    }
    public void updateSubTask(SubTask subTask) {
        if (subTasks.containsValue(subTask)) {
            //Проверяем что привязка к эпику не поменялась
            if (subTasks.get(subTask.getId()).getEpicLink() == subTask.getEpicLink()) {
                subTasks.put(subTask.getId(), subTask);
            } else {
                System.out.println("Менять привязку к эпику недопустимо");
                return;
            }
            //Обновляем статус эпика
            epics.get(subTask.getEpicLink()).setStatus(calculateEpicStatus(subTask.getEpicLink()));
        } else {
            System.out.println("Задачи для обновления в хранилище не найдено");
            return;
        }
    }
    public void updateEpic(Epic epic) {
        if (epics.containsValue(epic)) {
            epics.get(epic.getId()).setName(epic.getName());
            epics.get(epic.getId()).setDescription(epic.getDescription());
        } else {
            System.out.println("Задачи для обновления в хранилище не найдено");
            return;
        }
    }

    // Метод получения списка подзадач эпика
    public ArrayList<SubTask> getSubTasksLinkedToEpic(Epic epic) {
        ArrayList<SubTask> subTasksLinkedToEpic = new ArrayList<>();
        for (Integer subTasksLink : epic.getSubTasksLinks()) {
            subTasksLinkedToEpic.add(subTasks.get(subTasksLink));
        }
        return subTasksLinkedToEpic;
    }
    //метод для расчета статуса эпика
    private Status calculateEpicStatus(int epicId) {
        //определяем перечеь связанных подзадач, у которых нужно проверить статус
        ArrayList<Integer> linkedSubTasks = epics.get(epicId).getSubTasksLinks();
        Status calculatedStatus = Status.NEW;
        if (!linkedSubTasks.isEmpty()) {
            boolean isStatusIN_PROGRESS = false;
            boolean statusListIncludeNEW = false;
            boolean statusListIncludeDONE = false;
            for (Integer linkedSubTask : linkedSubTasks) {
                if (subTasks.get(linkedSubTask).getStatus() == Status.IN_PROGRESS) {
                    isStatusIN_PROGRESS = true;
                    break;
                } else if (subTasks.get(linkedSubTask).getStatus() == Status.NEW) {
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
