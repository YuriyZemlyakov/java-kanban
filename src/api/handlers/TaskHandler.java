package api.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;
import model.Task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager tm;

    public TaskHandler(TaskManager tm) {
        this.tm = tm;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String[] url = exchange.getRequestURI().getPath().split("/");
        switch (method) {
            case "GET":
                if (url.length == 3) {
                    int taskId = Integer.parseInt(exchange.getRequestURI().getPath().split("/")[2]);
                    if (tm.getTaskById(taskId) == null) {
                        sendNotFound(exchange);
                        break;
                    }
                    String response = gson.toJson(tm.getTaskById(taskId));
                    sendText(exchange, response);
                    break;

                } else if (url.length == 2) {
                    String response = gson.toJson(tm.getAllTasks());
                    sendText(exchange, response);
                    break;
                }
            case "POST":
                //update
                if (url.length == 3) {
                    int taskId = Integer.parseInt(exchange.getRequestURI().getPath().split("/")[2]);
                    if (tm.getTaskById(taskId) == null) {
                        sendNotFound(exchange);
                        break;
                    }
                    String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    Task task = gson.fromJson(requestBody, Task.class);
                    tm.updateTask(task);
                    sendText(exchange, "Задача успешно обновлена");
                    break;
                    //add new task
                } else if (url.length == 2) {
                    String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    Task task = gson.fromJson(requestBody, Task.class);
                    if (tm.addTask(task) == null) {
                        sendHasInteractions(exchange);
                        break;
                    }
                    sendText(exchange, "Задача добавлена");
                    System.out.println(task.getId());
                    System.out.println(task.getName());
                    System.out.println(tm.getAllTasks().size());
                    break;
                }
            case "DELETE":
                int taskId = Integer.parseInt(exchange.getRequestURI().getPath().split("/")[2]);
                if (tm.getTaskById(taskId) == null) {
                    sendNotFound(exchange);
                    break;
                }
                tm.deleteTaskById(taskId);
                sendText(exchange, "Задача " + taskId + " удалена");
                System.out.println(tm.getAllTasks());
                break;

        }
    }
}


