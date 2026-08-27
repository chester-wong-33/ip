import java.util.ArrayList;
import java.util.List;

/** Owns the application's task collection and its list operations. */
public class TaskList {
    private final List<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        tasks = new ArrayList<>();
    }

    /** Creates a task list containing tasks previously loaded from storage. */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    public int size() {
        return tasks.size();
    }

    public Task get(int taskNumber) {
        validateTaskNumber(taskNumber);
        return tasks.get(taskNumber - 1);
    }

    public void add(Task task) {
        tasks.add(task);
    }

    public Task delete(int taskNumber) {
        validateTaskNumber(taskNumber);
        return tasks.remove(taskNumber - 1);
    }

    /** Returns a snapshot suitable for display or saving. */
    public List<Task> asList() {
        return List.copyOf(tasks);
    }

    private void validateTaskNumber(int taskNumber) {
        if (taskNumber <= 0 || taskNumber > tasks.size()) {
            throw new CooperException("Cooper couldn't find a task with that index :(");
        }
    }
}
