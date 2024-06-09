package api;

import api.handlers.*;
import com.sun.net.httpserver.HttpServer;
import manager.Managers;
import manager.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    final TaskManager tm;
    HttpServer httpServer;

    public HttpTaskServer(TaskManager tm) {
        this.tm = tm;
    }

    public void start() throws IOException {
        httpServer = HttpServer.create(new InetSocketAddress(8080), 0);
        httpServer.createContext("/tasks", new TaskHandler(tm));
        httpServer.createContext("/subTasks", new SubTaskHandler(tm));
        httpServer.createContext("/epics", new EpicHandler(tm));
        httpServer.createContext("/history", new HistoryHandler(tm));
        httpServer.createContext("/prioritized", new PrioritizedHandler(tm));
        httpServer.start();
        System.out.println("HTTP-сервер запущен");
    }

    public void stop() {
        httpServer.stop(2);
    }

    public static void main(String[] args) throws IOException {
//        Task task1 = new Task("Task1", "NNN", Status.NEW, null, null);
//        Task task2 = new Task("Task2", "NNN", Status.NEW, Duration.ofMinutes(30), LocalDateTime.of(2024, Month.JUNE, 24, 23, 23, 15));
//        Epic epic1 = new Epic("Epic1", "1111", Status.NEW, Duration.ofMinutes(30), LocalDateTime.of(2024, Month.JUNE, 24, 12, 30, 30));
//        Epic epic2 = new Epic("Epic2", "1111", Status.NEW, Duration.ofMinutes(30), LocalDateTime.of(2024, Month.JUNE, 24, 12, 30, 30));
//        SubTask subTask1 = new SubTask("SubTask1", "1111", Status.NEW, Duration.ofMinutes(200), LocalDateTime.of(2024, Month.AUGUST, 23, 9, 23, 43), 1);
//        SubTask subTask2 = new SubTask("SubTask2", "2222", Status.DONE, Duration.ofMinutes(25), LocalDateTime.of(2024, Month.JUNE, 15, 11, 34, 23), 1);
//        SubTask subTask3 = new SubTask("SubTask3", "3333", Status.IN_PROGRESS, Duration.ofMinutes(5), LocalDateTime.of(2024, Month.JUNE, 15, 13, 34, 23), 1);
//        tm.addEpic(epic1);
//        tm.addEpic(epic2);
//        tm.addTask(task1);
//        tm.addTask(task2);
//        tm.addSubTask(subTask1);
//        tm.addSubTask(subTask2);
//        tm.addSubTask(subTask3);

        TaskManager tm = Managers.getDefault();
        HttpTaskServer httpTaskServer = new HttpTaskServer(tm);
        httpTaskServer.start();


    }
}
