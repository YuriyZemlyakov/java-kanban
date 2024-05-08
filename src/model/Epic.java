package model;

import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subTasksLinks = new ArrayList<>();

    public Epic(String name, String description, Status status) {
        super(name, description, Status.NEW);
    }

    public Epic(String name, String description, int id, Status status) {
        super(name, description, id, status);
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
}

