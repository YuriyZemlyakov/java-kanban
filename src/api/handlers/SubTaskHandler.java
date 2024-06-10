package api.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;
import model.SubTask;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class SubTaskHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager tm;

    public SubTaskHandler(TaskManager tm) {
        this.tm = tm;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String[] url = exchange.getRequestURI().getPath().split("/");
        switch (method) {
            case "GET":
                if (url.length == 3) {
                    int subTaskId = Integer.parseInt(exchange.getRequestURI().getPath().split("/")[2]);
                    if (tm.getSubTaskById(subTaskId) == null) {
                        sendNotFound(exchange);
                    }
                    String response = gson.toJson(tm.getSubTaskById(subTaskId));
                    sendText(exchange, response);

                } else if (url.length == 2) {
                    String response = gson.toJson(tm.getAllSubTasks());
                    sendText(exchange, response);
                }
            case "POST":
                //update
                if (url.length == 3) {
                    int subTaskId = Integer.parseInt(exchange.getRequestURI().getPath().split("/")[2]);
                    if (tm.getSubTaskById(subTaskId) == null) {
                        sendNotFound(exchange);
                        break;
                    }
                    String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    SubTask subTask = gson.fromJson(requestBody, SubTask.class);

                    tm.updateSubTask(subTask);
                    sendText(exchange, "Подзадача успешно обновлена");
                    //add new task
                } else if (url.length == 2) {
                    String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    SubTask subTask = gson.fromJson(requestBody, SubTask.class);
                    if (tm.addSubTask(subTask) == null) {
                        sendHasInteractions(exchange);
                    }
                    sendText(exchange, "Задача добавлена");
                    System.out.println(subTask.getId());
                    System.out.println(subTask.getName());
                }
            case "DELETE":
                int subTaskId = Integer.parseInt(exchange.getRequestURI().getPath().split("/")[2]);
                if (tm.getSubTaskById(subTaskId) == null) {
                    sendNotFound(exchange);
                    break;
                }
                tm.deleteSubTaskById(subTaskId);
                sendText(exchange, "Задача " + subTaskId + " удалена");
        }
    }
}

