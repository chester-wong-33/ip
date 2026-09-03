package cooper.ui;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import cooper.task.Task;

/**
 * Handles all text-based interaction with the user.
 */
public class Ui {
    private static final String DASHES = "\t_*_*_*______________________________________________________\n";
    private static final String INTRO = "Hello! I'm Cooper.\n\tWhat can I do for you?";
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd uuuu HH:mm", Locale.ENGLISH);

    private final Scanner scanner = new Scanner(System.in);

    /** Returns whether another command is available from standard input. */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /** Reads and returns the next command from standard input. */
    public String readCommand() {
        return scanner.nextLine();
    }

    /** Displays Cooper's welcome message. */
    public void showWelcome() {
        showMessage(INTRO);
    }

    /** Displays Cooper's goodbye message. */
    public void showGoodbye() {
        showMessage("Bye. Hope to see you again soon!");
    }

    /** Displays an error message to the user. */
    public void showError(String message) {
        showMessage(message);
    }

    /** Displays a warning that saved tasks could not be loaded. */
    public void showLoadingError() {
        showMessage("Cooper couldn't load the saved tasks. Starting with an empty task list.");
    }

    /** Displays the added task and the updated number of tasks. */
    public void showAddedTask(Task task, int taskCount) {
        showMessage("Got it. I've added this task:\n\t  " + task + taskCountMessage(taskCount));
    }

    /** Displays the deleted task and the updated number of tasks. */
    public void showDeletedTask(Task task, int taskCount) {
        showMessage("Noted. I've removed this task:\n\t  " + task + taskCountMessage(taskCount));
    }

    /** Displays confirmation that a task was marked complete. */
    public void showMarkedTask(Task task) {
        showMessage("Nice! I've marked this task as done:\n\t  " + task
                + "\n\tCooper would have loved that :)");
    }

    /** Displays confirmation that a task was marked incomplete. */
    public void showUnmarkedTask(Task task) {
        showMessage("OK, I've marked this task as not done yet:\n\t  " + task + "\n\tKeep going! :)");
    }

    /** Displays all tasks with one-based numbering, or an empty-list message if necessary. */
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

    /** Displays tasks matching a find command with one-based result numbering. */
    public void showMatchingTasks(List<Task> tasks) {
        if (tasks.isEmpty()) {
            showMessage("No matching tasks found!");
            return;
        }

        StringBuilder message = new StringBuilder("Here are the matching tasks in your list:\n\t");
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

    /**
     * Formats a date-time for display to the user.
     *
     * @param time Date-time to format.
     * @return Date-time in {@code MMM dd uuuu HH:mm} format.
     */
    public static String formatDate(LocalDateTime time) {
        return time.format(DISPLAY_FORMAT);
    }

    /** Builds a task-count message with the correct singular or plural noun. */
    private String taskCountMessage(int taskCount) {
        String noun = taskCount == 1 ? "task" : "tasks";
        return String.format("\n\tNow you have %d %s in the list.", taskCount, noun);
    }

    /** Displays a message between Cooper's standard separators. */
    private void showMessage(String message) {
        System.out.println(DASHES + '\t' + message + '\n' + DASHES);
    }
}
