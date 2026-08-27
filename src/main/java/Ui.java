import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

/** Handles all text-based interaction with the user. */
public class Ui {
    private static final String DASHES = "\t_*_*_*______________________________________________________\n";
    private static final String INTRO = "Hello! I'm Cooper.\n\tWhat can I do for you?";
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd uuuu HH:mm", Locale.ENGLISH);

    private final Scanner scanner = new Scanner(System.in);

    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    public String readCommand() {
        return scanner.nextLine();
    }

    public void showWelcome() {
        showMessage(INTRO);
    }

    public void showGoodbye() {
        showMessage("Bye. Hope to see you again soon!");
    }

    public void showError(String message) {
        showMessage(message);
    }

    public void showLoadingError() {
        showMessage("Cooper couldn't load the saved tasks. Starting with an empty task list.");
    }

    public void showAddedTask(Task task, int taskCount) {
        showMessage("Got it. I've added this task:\n\t  " + task + taskCountMessage(taskCount));
    }

    public void showDeletedTask(Task task, int taskCount) {
        showMessage("Noted. I've removed this task:\n\t  " + task + taskCountMessage(taskCount));
    }

    public void showMarkedTask(Task task) {
        showMessage("Nice! I've marked this task as done:\n\t  " + task
                + "\n\tCooper would have loved that :)");
    }

    public void showUnmarkedTask(Task task) {
        showMessage("OK, I've marked this task as not done yet:\n\t  " + task + "\n\tKeep going! :)");
    }

    /** Displays all tasks with one-based numbering. */
    public void showTaskList(List<Task> tasks) {
        if (tasks.isEmpty()) {
            showMessage("No current tasks!");
            return;
        }

        StringBuilder message = new StringBuilder("Here are the tasks in your list:\n\t");
        for (int i = 0; i < tasks.size(); i++) {
            if (i > 0) {
                message.append('\t');
            }
            message.append(String.format("%d.%s", i + 1, tasks.get(i)));
            if (i < tasks.size() - 1) {
                message.append('\n');
            }
        }
        showMessage(message.toString());
    }

    public static String formatDate(LocalDateTime time) {
        return time.format(DISPLAY_FORMAT);
    }

    private String taskCountMessage(int taskCount) {
        String noun = taskCount == 1 ? "task" : "tasks";
        return String.format("\n\tNow you have %d %s in the list.", taskCount, noun);
    }

    private void showMessage(String message) {
        System.out.println(DASHES + '\t' + message + '\n' + DASHES);
    }
}
