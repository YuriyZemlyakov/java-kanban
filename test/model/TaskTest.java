package model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

class TaskTest {

    @Test
    void testEquals() {
// Проверяем, что задачи с одинаковым id, но разным содержимым определяются как одинаковые
        Task task1 = new Task("task1", "To do something", 2, Status.NEW, Duration.ofMinutes(27), LocalDateTime.of(2024,1,1,1,11,1));
        Task task2 = new Task("task2", "To do something wrong", 2, Status.NEW, Duration.ofMinutes(27), LocalDateTime.of(2024,1,1,1,11,1));
        Assertions.assertTrue(task1.equals(task2));
    }
}