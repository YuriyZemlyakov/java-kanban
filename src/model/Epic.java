package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subTasksLinks = new ArrayList<>();

    public Epic(String name, String description, Status status, Duration duration, LocalDateTime startTime) {
        super(name, description, status, duration, startTime);
        this.duration = Duration.ofMinutes(0);
        this.startTime = null;
    }

    public Epic(String name, String description, int id, Status status, Duration duration, LocalDateTime startTime) {
        super(name, description, id, status, duration, startTime);
        this.duration = Duration.ofMinutes(0);
        this.startTime = null;
    }

    public void addLink(Integer link) {
        subTasksLinks.add(link);
    }


    public void deleteLinkById(Integer subTaskId) {
        subTasksLinks.remove((Integer) subTaskId);
    }

    public void deleteAllLinks() {
        subTasksLinks.clear();
    }

    public ArrayList<Integer> getSubTasksLinks() {
        ArrayList<Integer> links = new ArrayList<>(subTasksLinks);
        return links;
    }

    @Override
    public String toFileString() {
        return id + "," + TaskType.EPIC + "," + name + "," + status + "," + description + ",";
    }

    public void setEpicDuration(Duration duration) {
        this.duration = duration;
    }

    public void setEpicStartTime(LocalDateTime epicStartTime) {
        this.startTime = epicStartTime;
    }
}

