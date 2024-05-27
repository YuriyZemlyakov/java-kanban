package manager;

import model.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


public class InMemoryTaskManager implements TaskManager {
    protected HashMap<Integer, Task> tasks;
    protected HashMap<Integer, SubTask> subTasks;
    protected HashMap<Integer, Epic> epics;
    protected int idCounter = 0;


    private final HistoryManager history;
    protected TreeSet<Task> prioritizedTasks;


    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        subTasks = new HashMap<>();
        epics = new HashMap<>();
        history = Managers.getDefaultHistory();
        prioritizedTasks = new TreeSet<>(new ComparatorByStartTime());

    }

    // Методы добавления новых задач
    @Override
    public Task addTask(Task task) {
        idCounter++;
        task.setId(idCounter);
        tasks.put(task.getId(), task);
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
            if (isOverlapse(task)) {
                System.out.println("Задача пересекается по времени выполнения с другой задачей, добавление невозможно");
                return null;
            }
        }
        return task;
    }

    @Override
    public SubTask addSubTask(SubTask subTask) {
        if (epics.containsKey(subTask.getEpicLink())) {
            idCounter++;
            subTask.setId(idCounter);
            subTasks.put(subTask.getId(), subTask);
            if (subTask.getStartTime() != null) {
                prioritizedTasks.add(subTask);
                if (isOverlapse(subTask)) {
                    System.out.println("Задача пересекается по времени выполнения с другой задачей, добавление невозможно");
                    return null;
                }
            }
            epics.get(subTask.getEpicLink()).addLink(subTask.getId());
            recalculateEpicFields(subTask);
            return subTask;
        } else {
            System.out.println("Эпика с таким id не найдено");
            return null;
        }

    }

    @Override
    public void resetIdCounter() {
        idCounter = 0;
    }

    @Override
    public Epic addEpic(Epic epic) {
        idCounter++;
        epic.setId(idCounter);
        epics.put(epic.getId(), epic);
        return epic;
    }

    //Методы получения списка всех задач каждого типа
    @Override
    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> allTasks = new ArrayList<>(tasks.values());
        return allTasks;
    }

    @Override
    public ArrayList<SubTask> getAllSubTasks() {
        ArrayList<SubTask> allSubTasks = new ArrayList<>(subTasks.values());
        return allSubTasks;
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        ArrayList<Epic> allEpics = new ArrayList<>(epics.values());
        return allEpics;
    }

    // Методы получения задачи по идентификтору
    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        history.addTaskToHistory(task);
        return task;
    }

    @Override
    public SubTask getSubTaskById(int id) {
        SubTask subTask = subTasks.get(id);
        history.addTaskToHistory(subTask);
        return subTask;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        history.addTaskToHistory(epic);
        return epic;
    }

    // Методы удаления всех задач
    @Override
    public void deleteAllTasks() {
        for (Task task : history.getHistory()) {
            if (!(task instanceof Epic || task instanceof SubTask)) {
                history.remove(task.getId());
            }
            tasks.clear();
            prioritizedTasks = (TreeSet) prioritizedTasks.stream()
                    .filter(t -> t instanceof SubTask)
                    .collect(Collectors.toCollection(() -> new TreeSet<>(new ComparatorByStartTime())));

        }
    }

    @Override
    public void deleteAllSubTasks() {
        // Удалеям связи с эпиками
        for (Epic epic : epics.values()) {
            epic.deleteAllLinks();
            epic.setStatus(Status.NEW);
            epic.setEpicStartTime(null);
            epic.setEpicDuration(Duration.ofMinutes(0));
        }
        //Удаляем сами подзадачи

        for (Task task : history.getHistory()) {
            if (task instanceof SubTask) {
                history.remove(task.getId());
            }
            subTasks.clear();
            //удаляем подзадачи из TreeSet
            prioritizedTasks = (TreeSet) prioritizedTasks.stream()
                    .filter(t -> t instanceof Task)
                    .collect(Collectors.toCollection(() -> new TreeSet<>(new ComparatorByStartTime())));

        }
    }

    @Override
    public void deleteAllEpics() {
        //Удаляем все связанные подзадачи
        subTasks.clear();
        //теперь удаляем сами эпики
        for (Task task : history.getHistory()) {
            if (task instanceof Epic || task instanceof SubTask) {
                history.remove(task.getId());
            }
        }
        epics.clear();
        prioritizedTasks = (TreeSet) prioritizedTasks.stream()
                .filter(t -> t instanceof Task)
                .collect(Collectors.toCollection(() -> new TreeSet<>(new ComparatorByStartTime())));
    }

    //Методы удаления задач по идентификтору
    @Override
    public void deleteTaskById(int id) {
        if (tasks.get(id).getStartTime() != null) {
            prioritizedTasks.remove(tasks.get(id));
        }
        tasks.remove(id);
        history.remove(id);

    }

    @Override
    public void deleteSubTaskById(int id) {
        if (subTasks.containsKey(id)) {
            SubTask subTask = subTasks.get(id);
            Epic epic = epics.get(subTask.getEpicLink());
            //удаляем связь с эпиком
            epic.deleteLinkById(id);
            // пересчитываем статус эпика
            recalculateEpicFields(subTask);
            //удаляем саму подзадачу
            subTasks.remove(id);
            if (subTask.getStartTime() != null)
                prioritizedTasks.remove(subTask);
            history.remove(id);
        } else {
            System.out.println("Такого id не существует");
            return;
        }
    }

    @Override
    public void deleteEpicById(int id) {
        if (epics.containsKey(id)) {
            //удаляем связанные позадачи
            for (Integer subTasksLink : epics.get(id).getSubTasksLinks()) {
                if (subTasks.get(subTasksLink).getStartTime() != null) {
                    prioritizedTasks.remove(subTasks.get(subTasksLink));
                }
                subTasks.remove(subTasksLink);

                history.remove(subTasksLink);
            }
            //удаляем сам эпик
            epics.remove(id);
            history.remove(id);

        } else {
            System.out.println("Такого id не существует");
            return;
        }
    }

    //Методы обновления задач
    @Override
    public void updateTask(Task task) {
        if (tasks.containsValue(task)) {
            tasks.put(task.getId(), task);
        } else {
            System.out.println("Задачи для обновления в хранилище не найдено");
            return;
        }
    }

    @Override
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
            recalculateEpicFields(subTask);
        } else {
            System.out.println("Задачи для обновления в хранилище не найдено");
            return;
        }
    }

    @Override
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
    @Override
    public ArrayList<SubTask> getSubTasksLinkedToEpic(Epic epic) {
        return epic.getSubTasksLinks().stream()
                .map(link -> subTasks.get(link))
                .collect(Collectors.toCollection(() -> new ArrayList<>()));
    }

    //метод для расчета статуса эпика
    private Status calculateEpicStatus(int epicId) {
        //определяем перечеь связанных подзадач, у которых нужно проверить статус
        ArrayList<Integer> linkedSubTasks = epics.get(epicId).getSubTasksLinks();
        Status calculatedStatus = Status.NEW;
        if (!linkedSubTasks.isEmpty()) {
            boolean isStatusInProgress = false;
            boolean statusListIncludeNew = false;
            boolean statusListIncludeDone = false;
            for (Integer linkedSubTask : linkedSubTasks) {
                if (subTasks.get(linkedSubTask).getStatus() == Status.IN_PROGRESS) {
                    isStatusInProgress = true;
                    break;
                } else if (subTasks.get(linkedSubTask).getStatus() == Status.NEW) {
                    statusListIncludeNew = true;
                } else {
                    statusListIncludeDone = true;
                }
            }
            if (isStatusInProgress) {
                calculatedStatus = Status.IN_PROGRESS;
            } else if (statusListIncludeNew && !statusListIncludeDone) {
                calculatedStatus = Status.NEW;
            } else if (statusListIncludeDone && !statusListIncludeNew) {
                calculatedStatus = Status.DONE;
            } else {
                calculatedStatus = Status.IN_PROGRESS;
            }
        }
        return calculatedStatus;
    }

    private Duration calculateEpicDuration(Epic epic) {
        Duration epicDuration = Duration.ofMinutes(0);
        for (SubTask subTask : getSubTasksLinkedToEpic(epic)) {
            epicDuration = epicDuration.plus(subTask.getDuration());
        }
        return epicDuration;
    }

    private Optional<LocalDateTime> calculateEpicStartTime(Epic epic) {
        return getSubTasksLinkedToEpic(epic).stream()
                .map(subTask -> subTask.getStartTime())
                .min(LocalDateTime::compareTo);
    }

    private void recalculateEpicFields(SubTask subTask) {
        Epic epic = epics.get(subTask.getEpicLink());
        epic.setStatus(calculateEpicStatus(epic.getId()));
        epic.setEpicDuration(calculateEpicDuration(epic));
        calculateEpicStartTime(epic).ifPresent(epic::setEpicStartTime);

    }

    private boolean isOverlapse(Task task) {
        boolean isOverlapsed = false;
        for (Task prioritizedTask : prioritizedTasks) {
            if (prioritizedTask.getStartTime().isAfter(task.getStartTime()) &&
                    prioritizedTask.getStartTime().isBefore(task.getEndTime()) ||
                    prioritizedTask.getStartTime().isBefore(task.getStartTime()) &&
                            prioritizedTask.getEndTime().isAfter(task.getStartTime())
                    ) {
                isOverlapsed = true;
            }
        }
        return isOverlapsed;
    }

    @Override
    public ArrayList<Task> getHistory() {
        return history.getHistory();
    }

    @Override
    public ArrayList<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }
}
