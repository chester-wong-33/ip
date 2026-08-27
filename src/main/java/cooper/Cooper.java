package cooper;

import cooper.exception.CooperException;
import cooper.parser.Action;
import cooper.parser.Parser;
import cooper.storage.Storage;
import cooper.task.Task;
import cooper.task.TaskList;
import cooper.ui.Ui;

/** Coordinates Cooper's user interface, task list, parser, and storage. */
public class Cooper {
    private static final String FILE_PATH = "data/cooper.txt";

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
     * @param filePath path of the task data file
     */
    public Cooper(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);

        TaskList loadedTasks;
        try {
            loadedTasks = new TaskList(storage.loadTasks());
        } catch (CooperException e) {
            ui.showLoadingError();
            loadedTasks = new TaskList();
        }
        tasks = loadedTasks;
    }

    /** Persists a snapshot of the current task list. */
    private void saveTasks() {
        storage.saveTasks(tasks.asList());
    }

    /** Adds, saves, and displays a newly parsed task. */
    private void addTask(Task task) {
        tasks.add(task);
        saveTasks();
        ui.showAddedTask(task, tasks.size());
    }

    /** Parses and executes a delete command, then persists the updated list. */
    private void handleDelete(String input) {
        int taskNumber = Parser.parseTaskNumber(input,
                "Deleting is serious! Cooper wishes you provided a proper index only.");
        Task removedTask = tasks.delete(taskNumber);
        saveTasks();
        ui.showDeletedTask(removedTask, tasks.size());
    }

    /** Parses and executes a mark command, then persists the updated task. */
    private void handleMark(String input) {
        int taskNumber = Parser.parseTaskNumber(input,
                "Invalid syntax :( Cooper would like you to follow the format: mark <task-number>");
        Task task = tasks.get(taskNumber);
        task.markAsDone();
        saveTasks();
        ui.showMarkedTask(task);
    }

    /** Parses and executes an unmark command, then persists the updated task. */
    private void handleUnmark(String input) {
        int taskNumber = Parser.parseTaskNumber(input,
                "Invalid syntax :( Cooper would like you to follow the format: unmark <task-number>");
        Task task = tasks.get(taskNumber);
        task.markAsUndone();
        saveTasks();
        ui.showUnmarkedTask(task);
    }

    /**
     * Executes one user command.
     *
     * @return true if Cooper should exit
     */
    private boolean executeCommand(String input) {
        Action action = Parser.parseAction(input);

        switch (action) {
        case Action.LIST:
            ui.showTaskList(tasks.asList());
            break;
        case Action.DELETE:
            handleDelete(input);
            break;
        case Action.MARK:
            handleMark(input);
            break;
        case Action.UNMARK:
            handleUnmark(input);
            break;
        case Action.TODO:
            addTask(Parser.parseTodo(input));
            break;
        case Action.DEADLINE:
            addTask(Parser.parseDeadline(input));
            break;
        case Action.EVENT:
            addTask(Parser.parseEvent(input));
            break;
        case Action.BYE:
            return true;
        default:
            throw new CooperException("Cooper doesn't understand this command :(");
        }
        return false;
    }

    /** Runs the command-reading loop until the user exits or input ends. */
    public void run() {
        ui.showWelcome();

        while (ui.hasNextCommand()) {
            String input = ui.readCommand();
            try {
                if (executeCommand(input)) {
                    break;
                }
            } catch (CooperException e) {
                ui.showError(e.getMessage());
            }
        }

        ui.showGoodbye();
    }

    /** Starts Cooper using the default task data file. */
    public static void main(String[] args) {
        new Cooper().run();
    }
}
