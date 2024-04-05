package Model;

import Model.Task;

public class SubTask extends Task {
 private int epicLink;

    public SubTask(String name, String description, Status status, int epicLink) {
        super(name, description, status);
        this.epicLink = epicLink;
    }

    public SubTask(String name, String description, int id, Status status, int epicLink) {
        super(name, description, id, status);
        this.epicLink = epicLink;
    }
    public int getEpicLink() {
        return epicLink;
    }

}
