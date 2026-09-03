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
    private static final String INTRO = "Hello! I'm Cooper. What can I do for you?";
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd uuuu HH:mm", Locale.ENGLISH);

    private final Scanner scanner = new Scanner(System.in);

    /**
     * Returns whether another command is available from standard input.
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads and returns the next command from standard input.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Returns Cooper's welcome message string
     * */
    public String getWelcomeMessage() {
        return INTRO;
    }

    /**
     * Returns Cooper's bye message string.
     * */
    public String getByeMessage() {
        return "Bye. Hope to see you again soon!";
    }

    /**
     * Displays an error message to the user.
     */
    public String getErrorMessage(String message) {
        return message;
    }

    /**
     * Displays a warning that saved tasks could not be loaded.
     */
    public String getLoadingErrorMessage() {
        return "Cooper couldn't load the saved tasks. Starting with an empty task list.";
    }

    /**
     * Returns message string of adding task
     *
     * @param task Task to be added
     * @param taskCount Number of tasks (after addition)
     * @return String of adding new task
     */
    public String getAddedTaskMessage(Task task, int taskCount) {
        return String.format("Got it. I've added this task:\n%s\n%s", task.toString(), taskCountMessage(taskCount));
    }

    /**
     * Returns message string of deleting task
     *
     * @param task Task to be deleted
     * @param taskCount Number of tasks (after deletion)
     * @return String of deleting task
     */
    public String getDeletedTaskMessage(Task task, int taskCount) {
        return String.format("Noted. I've removed this task:\n%s\n%s", task.toString(), taskCountMessage(taskCount));
    }

    /**
     * Returns message string of marking task
     *
     * @param task Task to be marked
     * @return String of marking task
     */
    public String getMarkedTaskMessage(Task task) {
        return "Nice! I've marked this task as done:\n" + task.toString()
                + "\nCooper would have loved that :)";
    }

    /**
     * Returns message string of unmarking task
     *
     * @param task Task to be unmarked
     * @return String of unmarking task
     */
    public String getUnmarkedTaskMessage(Task task) {
        return "OK, I've marked this task as not done yet:\n" + task + "\nKeep going! :)";
    }

    /**
     * Returns message string of listing tasks with one-based numbering, or an empty-list message if necessary.
     *
     * @param tasks List of tasks to display
     * @return String of all tasks in their order
     */
    public String getTaskListMessage(List<Task> tasks) {
        if (tasks.isEmpty()) {
            return "No current tasks!";
        }

        StringBuilder message = new StringBuilder("Here are the tasks in your list:\n");
        for (int i = 0; i < tasks.size(); i++) {
            message.append(String.format("%d.%s", i + 1, tasks.get(i)));
            if (i < tasks.size() - 1) {
                message.append('\n');
            }
        }
        return message.toString();
    }

    /**
     * Returns message string of matching tasks with one-based numbering, or an empty-list message if necessary.
     *
     * @param tasks List of matching tasks to display
     * @return String of all tasks in their order
     */
    public String getMatchingTasksMessage(List<Task> tasks) {
        if (tasks.isEmpty()) {
            return "No matching tasks found!";
        }

        StringBuilder message = new StringBuilder("Here are the matching tasks in your list:\n");
        for (int i = 0; i < tasks.size(); i++) {
            message.append(String.format("%d.%s", i + 1, tasks.get(i)));
            if (i < tasks.size() - 1) {
                message.append('\n');
            }
        }
        return message.toString();
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
}
