package api;

import api.handlers.*;
import com.sun.net.httpserver.HttpServer;
import manager.Managers;
import manager.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private final TaskManager tm;
    private HttpServer httpServer;

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
        TaskManager tm = Managers.getDefault();
        HttpTaskServer httpTaskServer = new HttpTaskServer(tm);
        httpTaskServer.start();


    }
}
