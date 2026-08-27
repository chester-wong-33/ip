package cooper.task;

/**
 * Represents a task with a description and completion status.
 */
public class Task {
    protected final String description;
    protected boolean isDone;

    /**
     * Creates an incomplete task.
     *
     * @param description Description of the task.
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Creates a task with the specified completion status.
     *
     * @param description Description of the task.
     * @param isDone Whether the task is complete.
     */
    public Task(String description, boolean isDone) {
        this(description);
        this.isDone = isDone;
    }

    /** Marks this task as complete. */
    public void markAsDone() {
        isDone = true;
    }

    /** Marks this task as incomplete. */
    public void markAsUndone() {
        isDone = false;
    }

    private String getStatusIcon() {
        return (isDone ? "X" : " ");
    }

    /** Returns the display form of this task, including its completion status. */
    @Override
    public String toString() {
        return "[" + this.getStatusIcon() + "] " + this.description;
    }

    /**
     * Returns the task fields in the format used by persistent storage.
     *
     * @return Storage representation of this task.
     */
    public String toDataString() {
        return String.format("%d | %s", isDone ? 1 : 0, description);
    }
}
