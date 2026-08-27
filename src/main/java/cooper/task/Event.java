package cooper.task;

import cooper.ui.Ui;

import java.time.LocalDateTime;

public class Event extends Task {
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;

    public Event(String description, LocalDateTime startDate, LocalDateTime endDate) {
        super(description);
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Event(String description, boolean isDone, LocalDateTime startDate, LocalDateTime endDate) {
        super(description, isDone);
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return "[E]" + super.toString()
                + String.format(" (from: %s to: %s)",
                Ui.formatDate(startDate), Ui.formatDate(endDate));
    }

    @Override
    public String toDataString() {
        return String.format("%s | %s | %s | %s", "E",
                super.toDataString(), startDate, endDate);
    }
}
