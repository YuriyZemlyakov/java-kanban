package api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import manager.Managers;
import manager.TaskManager;
import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskServerTest {
    TaskManager tm = Managers.getDefault();
    HttpTaskServer httpTaskServer = new HttpTaskServer(tm);
    Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
            .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
            .create();

    public HttpTaskServerTest() throws IOException {
    }

    @BeforeEach
    public void setUp() {
        tm.deleteAllTasks();
        tm.deleteAllEpics();
        tm.deleteAllTasks();
        try {
            httpTaskServer.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterEach
    public void shutDown() {
        httpTaskServer.stop();
    }

    @Test
    public void testAddTask() throws IOException, InterruptedException {
        // создаём задачу
        Task task = new Task("Test 2", "Testing task 2",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        // конвертируем её в JSON
        String taskJson = gson.toJson(task);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<Task> tasksFromManager = tm.getAllTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 2", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void testAddEpic() throws IOException, InterruptedException {
        // создаём задачу
        Epic epic = new Epic("Test 3", "Testing task 3",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        // конвертируем её в JSON
        String taskJson = gson.toJson(epic);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<Epic> tasksFromManager = tm.getAllEpics();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 3", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void testAddSubTask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Testing epic",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        tm.addEpic(epic);
        // создаём задачу
        SubTask subTask = new SubTask("Test 4", "Testing task 4",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now(), epic.getId());
        // конвертируем её в JSON
        String taskJson = gson.toJson(subTask);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subTasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<SubTask> tasksFromManager = tm.getAllSubTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 4", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void testGetAllTasks() throws IOException, InterruptedException {
        Task task1 = new Task("TestTask", "Testing task",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        Task task2 = new Task("TestTask2", "Testing task2",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now().plusHours(3));
        tm.addTask(task1);
        tm.addTask(task2);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        // проверяем, что вернулась созданная задача
        JsonElement jsonElement = JsonParser.parseString(response.body());
        String actualTaskName = jsonElement.getAsJsonArray().get(0).getAsJsonObject().get("name").getAsString();
        assertEquals("TestTask", actualTaskName);
        // проверяем, что число добавленных задач соответствуют числу возвращенных задач
        assertEquals(2, jsonElement.getAsJsonArray().size());

    }

    @Test
    public void testGetEpicById() throws IOException, InterruptedException {
        Epic epic = new Epic("Test epic", "Testing task 3",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        tm.addEpic(epic);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        // проверяем, что вернулась созданная задача
        JsonElement jsonElement = JsonParser.parseString(response.body());
        String actualTaskName = jsonElement.getAsJsonObject().get("name").getAsString();
        assertEquals("Test epic", actualTaskName);
    }

    @Test
    public void testGetHistory() throws IOException, InterruptedException {
        Task task1 = new Task("TestTask", "Testing task",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        Task task2 = new Task("TestTask2", "Testing task2",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now().plusHours(3));
        tm.addTask(task1);
        tm.addTask(task2);
        tm.getTaskById(task1.getId());
        tm.getTaskById(task2.getId());
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        JsonElement jsonElement = JsonParser.parseString(response.body());
        String actualTaskName = jsonElement.getAsJsonArray().get(0).getAsJsonObject().get("name").getAsString();
        assertEquals("TestTask", actualTaskName);
        // проверяем, что число добавленных задач соответствуют числу возвращенных задач
        assertEquals(2, jsonElement.getAsJsonArray().size());
    }

    @Test
    public void testGetPrioritized() throws IOException, InterruptedException {
        Task task1 = new Task("TestTask", "Testing task",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        Task task2 = new Task("TestTask2", "Testing task2",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now().plusHours(3));
        Task task3 = new Task("TestTask3", "Testing task3",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now().plusHours(2));
        tm.addTask(task1);
        tm.addTask(task2);
        tm.addTask(task3);
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());
        // проверяем, что задачи отсортированы по startTime
        JsonElement jsonElement = JsonParser.parseString(response.body());
        String[] expectedList = new String[]{"TestTask", "TestTask3", "TestTask2"};
        String[] actualList = new String[3];
        int i = 0;
        for (JsonElement element : jsonElement.getAsJsonArray()) {
            actualList[i] = element.getAsJsonObject().get("name").getAsString();
            i++;
        }
        assertArrayEquals(expectedList, actualList);
    }

}
