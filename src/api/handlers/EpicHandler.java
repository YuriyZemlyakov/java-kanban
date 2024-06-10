package api.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;
import model.Epic;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager tm;

    public EpicHandler(TaskManager tm) {
        this.tm = tm;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String[] url = exchange.getRequestURI().getPath().split("/");
        switch (method) {
            case "GET":
                if (url.length == 3) {
                    int epicId = Integer.parseInt(exchange.getRequestURI().getPath().split("/")[2]);
                    if (tm.getEpicById(epicId) == null) {
                        sendNotFound(exchange);
                    }
                    String response = gson.toJson(tm.getEpicById(epicId));
                    sendText(exchange, response);

                } else if (url.length == 2) {
                    String response = gson.toJson(tm.getAllEpics());
                    sendText(exchange, response);
                }
            case "POST":
                //update
                if (url.length == 3) {
                    int epicId = Integer.parseInt(exchange.getRequestURI().getPath().split("/")[2]);
                    if (tm.getEpicById(epicId) == null) {
                        sendNotFound(exchange);
                        break;
                    }
                    String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    Epic epic = gson.fromJson(requestBody, Epic.class);
                    try {
                        tm.updateEpic(epic);
                    } catch (RuntimeException e) {
                        e.printStackTrace();
                    }
                    sendText(exchange, "Подзадача успешно обновлена");
                    System.out.println(epic.getId());
                    System.out.println("name " + epic.getName());


                    //add new task
                } else if (url.length == 2) {
                    String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    Epic epic = gson.fromJson(requestBody, Epic.class);
                    if (tm.addEpic(epic) == null) {
                        sendHasInteractions(exchange);
                    }
                    sendText(exchange, "Задача добавлена");
                    System.out.println(epic.getId());
                    System.out.println(epic.getName());
                }
            case "DELETE":
                int epicId = Integer.parseInt(exchange.getRequestURI().getPath().split("/")[2]);
                if (tm.getEpicById(epicId) == null) {
                    sendNotFound(exchange);
                    break;
                }
                tm.deleteEpicById(epicId);
                sendText(exchange, "Задача " + epicId + " удалена");
        }
    }
}

