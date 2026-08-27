package cooper.task;

import cooper.exception.CooperException;

import java.util.ArrayList;
import java.util.List;

/**
 * Owns the application's task collection and its list operations.
 */
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

    /** Returns the number of tasks in the list. */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns the task at a one-based task number.
     *
     * @throws CooperException If the task number is outside the list.
     */
    public Task get(int taskNumber) {
        validateTaskNumber(taskNumber);
        return tasks.get(taskNumber - 1);
    }

    /** Adds a task to the end of the list. */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Removes and returns the task at a one-based task number.
     *
     * @throws CooperException If the task number is outside the list.
     */
    public Task delete(int taskNumber) {
        validateTaskNumber(taskNumber);
        return tasks.remove(taskNumber - 1);
    }

    /** Returns an immutable snapshot suitable for display or saving. */
    public List<Task> asList() {
        return List.copyOf(tasks);
    }

    /** Ensures a one-based task number refers to a task currently in the list. */
    private void validateTaskNumber(int taskNumber) {
        if (taskNumber <= 0 || taskNumber > tasks.size()) {
            throw new CooperException("Cooper couldn't find a task with that index :(");
        }
    }
}
