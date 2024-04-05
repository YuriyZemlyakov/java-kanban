package Model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubTaskTest {
    static Epic epic;
    static SubTask subTask1;
    static SubTask subTask2;

    @BeforeAll
    static void prepareEnvironment() {
        epic = new Epic("Epic1", "kjlkjlkjl",1, Status.NEW);
        subTask1 = new SubTask("ST1", "dfwsdfs", 2, Status.NEW,1);
        subTask2 = new SubTask("ST2", "abcd", 2, Status.NEW, 1);
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