package model;

import java.time.Duration;
import java.time.LocalDateTime;

public class SubTask extends Task {
    private int epicLink;

    public SubTask(String name, String description, Status status, Duration duration, LocalDateTime startTime, int epicLink) {
        super(name, description, status, duration, startTime);
        this.epicLink = epicLink;
    }

    public SubTask(String name, String description, int id, Status status, int epicLink) {
        super(name, description, id, status);
        this.epicLink = epicLink;
    }

    public int getEpicLink() {
        return epicLink;
    }

    @Override
    public String toFileString() {
        return id + "," + TaskType.SUBTASK + "," + name + "," + status + "," + description + "," + epicLink;
    }
}
