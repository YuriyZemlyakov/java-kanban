import java.util.ArrayList;
public class Epic extends Task {
    ArrayList<Integer> subTasksLinks = new ArrayList<>();

    public Epic(String name, String description, Status status) {
        super(name, description, status);
    }

    public Epic(String name, String description, int id, Status status, ArrayList<Integer> subTasksLinks) {
        super(name, description, id, status);
        this.subTasksLinks = subTasksLinks;
    }

    public void addLink(Integer link) {
        subTasksLinks.add(link);
    }
}
