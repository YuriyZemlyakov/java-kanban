package manager;

import model.Epic;
import model.SubTask;
import model.Task;
import model.TaskType;
import model.Status;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    Path file;

    public FileBackedTaskManager(Path file) {
        this.file = file;
    }

    public void save() {
    }

    public static FileBackedTaskManager loadFromFile(Path file) {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
        try {
            List<String> tasksFromFile = Files.readAllLines(file);
            for (int i = 1; i < tasksFromFile.size(); i++) {
                try {
                    fileBackedTaskManager.fromString(tasksFromFile.get(i));
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
            fileBackedTaskManager.idCounter = tasksFromFile.size();
        } catch (IOException e) {
            e.printStackTrace();
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
            case TaskType.TASK:
                Task task = new Task(taskDetails[2], taskDetails[4], Integer.parseInt(taskDetails[0]), Status.valueOf(taskDetails[3]));
                super.tasks.put(task.getId(), task);
                return task;
            case TaskType.EPIC:
                Epic epic = new Epic(taskDetails[2], taskDetails[4], Integer.parseInt(taskDetails[0]), Status.valueOf(taskDetails[3]));
                super.epics.put(epic.getId(), epic);
                return epic;
            case TaskType.SUBTASK:
                SubTask subTask = new SubTask(taskDetails[2], taskDetails[4], Integer.parseInt(taskDetails[0]), Status.valueOf(taskDetails[3]), Integer.parseInt(taskDetails[5]));
                super.subTasks.put(subTask.getId(), subTask);
                //Добавляем в эпик связь с подзадачей
                super.getEpicById(Integer.parseInt(taskDetails[5])).addLink(Integer.parseInt(taskDetails[0]));
                return subTask;
            default:
                throw new IOException("Не удалось прочитать строку, неизвестный тип задачи");

        }

    }
}


