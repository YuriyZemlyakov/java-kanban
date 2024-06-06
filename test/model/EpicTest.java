package model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

class EpicTest {

    @Test
    void epicAndTaskWithSameIdEquals() {
        Epic epic1 = new Epic("Epic1", "dfwsdfs", 2, Status.NEW, Duration.ofMinutes(20), LocalDateTime.of(2024, 11, 23, 2, 34, 56));
        Epic epic2 = new Epic("Epic2", "abcd", 2, Status.NEW, Duration.ofMinutes(50), LocalDateTime.of(2025, 1, 23, 12, 34, 56));
        Assertions.assertTrue(epic1.equals(epic2));
    }

}