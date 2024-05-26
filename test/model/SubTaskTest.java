package model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

class SubTaskTest {
    static Epic epic;
    static SubTask subTask1;
    static SubTask subTask2;

    @BeforeAll
    static void prepareEnvironment() {
        epic = new Epic("Epic1", "kjlkjlkjl",1, Status.NEW, Duration.ofMinutes(20), LocalDateTime.of(2024,4,30,1,23,25));
        subTask1 = new SubTask("ST1", "eee", 2, Status.NEW, Duration.ofMinutes(30), LocalDateTime.of(2024,12,24,5,23,45), 1);
        subTask2 = new SubTask("ST2", "abcd", 2, Status.NEW, Duration.ofMinutes(10), LocalDateTime.of(2024,12,23,5,23,45), 1);
    }
    @Test
    void subTaskAndTaskWithSameIdEquals() {
        Assertions.assertTrue(subTask2.equals(subTask1));
}

    @Test
    void iSEpicLinkCorrect() {
        Assertions.assertEquals(1, subTask1.getEpicLink(), "epicLink записался с ошибкой");
    }
}