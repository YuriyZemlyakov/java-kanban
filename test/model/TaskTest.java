package model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TaskTest {

    @Test
    void testEquals() {
// Проверяем, что задачи с одинаковым id, но разным содержимым определяются как одинаковые
        Task task1 = new Task("task1", "To do something", 2, Status.NEW);
        Task task2 = new Task("task2", "To do something wrong", 2, Status.NEW);
        Assertions.assertTrue(task1.equals(task2));
    }
}