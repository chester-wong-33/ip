package cooper;

import cooper.exception.CooperException;
import cooper.parser.Action;
import cooper.parser.Parser;
import cooper.storage.Storage;
import cooper.task.Task;
import cooper.task.TaskList;
import cooper.ui.Ui;

/**
 * Coordinates Cooper's user interface, task list, parser, and storage.
 */
public class Cooper {
    private static final String FILE_PATH = "data/cooper.txt";

    /** Records whether startup recovered from an unreadable saved-task file. */
    private boolean loadingFailed;
    private final TaskList tasks;
    private final Storage storage;
    private final Ui ui;

    /** Creates Cooper using the default task data file. */
    public Cooper() {
        this(FILE_PATH);
    }

    /**
     * Creates Cooper using the specified task data file.
     * If loading fails, Cooper reports the error and starts with an empty task list.
     *
     * @param filePath Path of the task data file.
     */
    public Cooper(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);

        TaskList loadedTasks;
        try {
            loadedTasks = new TaskList(storage.loadTasks());
        } catch (CooperException e) {
            loadingFailed = true;
            loadedTasks = new TaskList();
        }
        tasks = loadedTasks;
    }

    /** Persists a snapshot of the current task list. */
    private void saveTasks() {
        storage.saveTasks(tasks.asList());
    }

    /** Adds, saves, and displays a newly parsed task. */
    private String addTask(Task task) {
        tasks.add(task);
        saveTasks();
        return ui.getAddedTaskMessage(task, tasks.size());
    }

    /** Parses and executes a delete command, then persists the updated list. */
    private String handleDelete(String input) {
        int taskNumber = Parser.parseTaskNumber(input,
                "Deleting is serious! Cooper wishes you provided a proper index only.");
        Task removedTask = tasks.delete(taskNumber);
        saveTasks();
        return ui.getDeletedTaskMessage(removedTask, tasks.size());
    }

    /** Parses and executes a mark command, then persists the updated task. */
    private String handleMark(String input) {
        int taskNumber = Parser.parseTaskNumber(input,
                "Invalid syntax :( Cooper would like you to follow the format: mark <task-number>");
        Task task = tasks.get(taskNumber);
        task.markAsDone();
        saveTasks();
        return ui.getMarkedTaskMessage(task);
    }

    /** Parses and executes an unmark command, then persists the updated task. */
    private String handleUnmark(String input) {
        int taskNumber = Parser.parseTaskNumber(input,
                "Invalid syntax :( Cooper would like you to follow the format: unmark <task-number>");
        Task task = tasks.get(taskNumber);
        task.markAsUndone();
        saveTasks();
        return ui.getUnmarkedTaskMessage(task);
    }

    /**
     * Executes one user command and returns its response.
     *
     * @param input User command to execute.
     * @return Response produced by the command.
     */
    private String executeCommand(String input) {
        Action action = Parser.parseAction(input);

        switch (action) {
            case Action.LIST:
                return ui.getTaskListMessage(tasks.asList());
            case Action.DELETE:
                return handleDelete(input);
            case Action.MARK:
                return handleMark(input);
            case Action.UNMARK:
                return handleUnmark(input);
            case Action.TODO:
                return addTask(Parser.parseTodo(input));
            case Action.DEADLINE:
                return addTask(Parser.parseDeadline(input));
            case Action.EVENT:
                return addTask(Parser.parseEvent(input));
            case Action.FIND:
                return ui.getMatchingTasksMessage(tasks.find(Parser.parseFindKeyword(input)));
            case Action.BYE:
                return ui.getByeMessage();
            default:
                throw new CooperException("Cooper doesn't understand this command :(");
        }
    }

    /**
     * Executes one command and returns Cooper's response and exit status.
     * The action is intentionally parsed here to determine the exit status and
     * parsed again by {@link #executeCommand(String)} when the command is run.
     *
     * @param input User command to process.
     * @return Cooper's response to the command and whether Cooper should exit.
     */
    public CommandResult getResponse(String input) {
        try {
            Action action = Parser.parseAction(input);
            String response = executeCommand(input);
            return new CommandResult(response, action == Action.BYE);
        } catch (CooperException e) {
            return new CommandResult(e.getMessage(), false);
        }
    }

    /**
     * Returns welcome message that should be displayed when Cooper starts.
     *
     * @return Welcome text, including a loading warning when loading failed.
     */
    public String getStartupMessage() {
        if (loadingFailed) {
            return ui.getLoadingErrorMessage() + "\n\n" + ui.getWelcomeMessage();
        }
        return ui.getWelcomeMessage();
    }

    /** Runs the command-reading loop until the user exits or input ends. */
    public void run() {
        System.out.println(getStartupMessage());

        while (ui.hasNextCommand()) {
            CommandResult result = getResponse(ui.readCommand());
            System.out.println(result.message());
            if (result.shouldExit()) {
                break;
            }
        }
    }

    /** Starts Cooper using the default task data file. */
    public static void main(String[] args) {
        new Cooper().run();
    }
}
