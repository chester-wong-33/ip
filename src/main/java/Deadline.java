public class Deadline extends Task {
    private final String dueDate;

    public Deadline(String description, String dueDate) {
        super(description);
        this.dueDate = dueDate;
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + String.format(" (by: %s)", dueDate);
    }

    @Override
    public String toDataString() {
        return String.format("%s | %s | %s", "E", super.toDataString(), dueDate);
    }
}
