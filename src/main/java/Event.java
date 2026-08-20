public class Event extends Task {
    private final String startDate;
    private final String endDate;

    public Event(String description, String startDate, String endDate) {
        super(description);
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + String.format(" (from: %s to %s)", startDate, endDate);
    }
}
