package cooper.task;

import cooper.ui.Ui;

import java.time.LocalDateTime;

public class Deadline extends Task {
    private final LocalDateTime dueDate;

    public Deadline(String description, LocalDateTime dueDate) {
        super(description);
        this.dueDate = dueDate;
    }

    public Deadline(String description, boolean isDone, LocalDateTime dueDate) {
        super(description, isDone);
        this.dueDate = dueDate;
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + String.format(" (by: %s)", Ui.formatDate(dueDate));
    }

    @Override
    public String toDataString() {
        return String.format("%s | %s | %s", "D", super.toDataString(), dueDate);
    }
}
