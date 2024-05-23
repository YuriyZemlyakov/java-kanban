package manager;

import model.Epic;
import model.SubTask;
import model.Task;
import model.TaskType;
import model.Status;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final Path file;

    public FileBackedTaskManager(Path file) {
        this.file = file;
    }

    public void save() throws ManagerSaveException {
        List<String> allTasksStringList = new ArrayList<>();
        String headers = "id,type,name,status,description,epic";
        allTasksStringList.add(headers);
        for (Task task : getAllTasks()) {
            allTasksStringList.add(task.toFileString());
        }
        for (Epic epic : getAllEpics()) {
            allTasksStringList.add(epic.toFileString());
        }
        for (SubTask subTask : getAllSubTasks()) {
            allTasksStringList.add(subTask.toFileString());
        }
        try {
            Files.write(file, allTasksStringList);
        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось записать данные в файл");
        }
    }

    public static FileBackedTaskManager loadFromFile(Path file) {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
        try {
            List<String> tasksFromFile = Files.readAllLines(file);
            int newIdCounter = 0;
            for (int i = 1; i < tasksFromFile.size(); i++) {
                fileBackedTaskManager.fromString(tasksFromFile.get(i));
                int taskId = Integer.parseInt(tasksFromFile.get(i).split(",")[0]);
                if (taskId > newIdCounter) {
                    newIdCounter = taskId;
                }
            }

            // Проверяем, есть ли в файле данные помимо заголовков. Если есть - корректируем счетчик id
            if (tasksFromFile.size() > 1) {
                fileBackedTaskManager.idCounter = newIdCounter;
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return fileBackedTaskManager;
    }

    public Task fromString(String stringTask) throws IOException {
        String[] taskDetails = stringTask.split(",");
        TaskType taskType = null;
        //Убедимся, что в файле указан допустимый тип задачи
        try {
            taskType = TaskType.valueOf(taskDetails[1]);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }

        switch (taskType) {
            case TASK:
                Task task = new Task(taskDetails[2], taskDetails[4], Integer.parseInt(taskDetails[0]), Status.valueOf(taskDetails[3]));
                tasks.put(task.getId(), task);
                return task;
            case EPIC:
                Epic epic = new Epic(taskDetails[2], taskDetails[4], Integer.parseInt(taskDetails[0]), Status.valueOf(taskDetails[3]));
                epics.put(epic.getId(), epic);
                return epic;
            case SUBTASK:
                SubTask subTask = new SubTask(taskDetails[2], taskDetails[4], Integer.parseInt(taskDetails[0]), Status.valueOf(taskDetails[3]), Integer.parseInt(taskDetails[5]));
                subTasks.put(subTask.getId(), subTask);
                //Добавляем в эпик связь с подзадачей
                getEpicById(Integer.parseInt(taskDetails[5])).addLink(Integer.parseInt(taskDetails[0]));
                return subTask;
            default:
                throw new IOException("Не удалось прочитать строку, неизвестный тип задачи");
        }
    }

    @Override
    public Task addTask(Task task) {
        super.addTask(task);
        save();
        return task;
    }

    @Override
    public SubTask addSubTask(SubTask subTask) {
        super.addSubTask(subTask);
        save();
        return subTask;
    }

    @Override
    public Epic addEpic(Epic epic) {
        super.addEpic(epic);
        save();
        return epic;
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllSubTasks() {
        super.deleteAllSubTasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteSubTaskById(int id) {
        super.deleteSubTaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }
}


