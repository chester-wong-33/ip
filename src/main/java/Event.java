public class Event extends Task {
    private final String startDate;
    private final String endDate;

    public Event(String description, String startDate, String endDate) {
        super(description);
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Event(String description, boolean isDone, String startDate, String endDate) {
        super(description, isDone);
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return "[E]" + super.toString() + String.format(" (from: %s to: %s)", startDate, endDate);
    }

    @Override
    public String toDataString() {
        return String.format("%s | %s | %s | %s", "E", super.toDataString(), startDate, endDate);
    }
}
