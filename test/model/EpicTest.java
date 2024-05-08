package model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class EpicTest {

    @Test
    void epicAndTaskWithSameIdEquals() {
        Epic epic1 = new Epic("Epic1", "dfwsdfs", 2, Status.NEW);
        Epic epic2 = new Epic("Epic2", "abcd", 2, Status.NEW);
        Assertions.assertTrue(epic1.equals(epic2));
    }

}