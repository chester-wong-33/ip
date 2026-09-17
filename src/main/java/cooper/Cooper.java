package cooper;

import java.util.List;

import cooper.exception.CooperException;
import cooper.note.Note;
import cooper.note.NoteList;
import cooper.parser.Action;
import cooper.parser.NoteCommand;
import cooper.parser.NoteParser;
import cooper.parser.Parser;
import cooper.storage.NoteStorage;
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
    private final NoteStorage noteStorage;
    private NoteList notes;
    private boolean notesLoadingFailed;

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
        this(filePath, new NoteStorage(filePath));
    }

    /** Allows storage failure tests to supply a note storage implementation. */
    Cooper(String filePath, NoteStorage noteStorage) {
        ui = new Ui();
        storage = new Storage(filePath);
        this.noteStorage = noteStorage;

        TaskList loadedTasks;
        try {
            loadedTasks = new TaskList(storage.loadTasks());
        } catch (CooperException e) {
            loadingFailed = true;
            loadedTasks = new TaskList();
        }
        tasks = loadedTasks;
        try {
            notes = new NoteList(noteStorage.load());
        } catch (CooperException e) {
            notesLoadingFailed = true;
            notes = new NoteList(List.of());
        }
    }

    /** Validates note syntax before checking availability and dispatching the operation. */
    private String handleNote(String input) {
        NoteCommand command = NoteParser.parse(input);
        if (notesLoadingFailed) {
            throw new CooperException(NoteStorage.UNAVAILABLE);
        }
        return switch (command.operation()) {
            case LIST -> ui.getNotesMessage(notes, false, "");
            case FIND -> ui.getNotesMessage(notes, true, command.text());
            case ADD, EDIT, DELETE -> changeNotes(command);
        };
    }

    /** Persists a candidate before publishing it or reporting success. */
    private String changeNotes(NoteCommand command) {
        NoteList candidate;
        Note affected;
        int number = command.number();
        switch (command.operation()) {
            case ADD:
                affected = new Note(command.text());
                candidate = notes.add(affected);
                number = candidate.size();
                break;
            case EDIT:
                affected = new Note(command.text());
                candidate = notes.edit(number, affected);
                break;
            case DELETE:
                affected = notes.get(number);
                candidate = notes.delete(number);
                break;
            default:
                throw new AssertionError("Expected a note mutation");
        }
        noteStorage.save(candidate.asList());
        notes = candidate;
        return ui.getNoteChangedMessage(command.operation(), number, affected, notes.size());
    }

    /** Returns a message listing the current tasks. */
    private String handleList() {
        return ui.getTaskListMessage(tasks.asList());
    }

    /** Persists a snapshot of the current task list. */
    private void saveTasks() {
        storage.saveTasks(tasks.asList());
    }

    /** Adds, saves, and displays a newly parsed task. */
    private String addTask(Task task) {
        // Successful task parsers must produce a task before it is stored or displayed.
        assert task != null : "A successful task parser must return a task";
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

    /** Parses a find command and returns a message containing matching tasks */
    private String handleFind(String input) {
        String keyword = Parser.parseFindKeyword(input);
        List<Task> matchingTasks = tasks.find(keyword);
        return ui.getMatchingTasksMessage(matchingTasks);
    }

    /** Returns the farewell message. */
    private String handleBye() {
        return ui.getByeMessage();
    }

    /**
     * Executes one user command and returns its response.
     *
     * @param action Parsed action to execute.
     * @param input User command to execute.
     * @return Response produced by the command.
     */
    private String executeCommand(Action action, String input) {
        switch (action) {
            case Action.LIST:
                return handleList();
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
                return handleFind(input);
            case Action.BYE:
                return handleBye();
            case Action.NOTE:
                return handleNote(input);
            default:
                // Unknown commands are rejected by the parser, so every Action must be handled above.
                assert false : "Missing command handler for action: " + action;
                throw new CooperException("Cooper doesn't understand this command :(");
        }
    }

    /**
     * Executes one command and returns Cooper's response and exit status.
     *
     * @param input User command to process.
     * @return Cooper's response to the command and whether Cooper should exit.
     */
    public CommandResult getResponse(String input) {
        try {
            Action action = Parser.parseAction(input);
            String response = executeCommand(action, input);
            return new CommandResult(response, action == Action.BYE);
        } catch (CooperException e) {
            return new CommandResult(e.getMessage(), false, true);
        }
    }

    /**
     * Returns welcome message that should be displayed when Cooper starts.
     *
     * @return Welcome text, including a loading warning when loading failed.
     */
    public String getStartupMessage() {
        String message = ui.getWelcomeMessage();
        if (loadingFailed) {
            message = ui.getLoadingErrorMessage() + "\n\n" + message;
        }
        if (notesLoadingFailed) {
            message += "\n\n" + ui.getNotesLoadingErrorMessage();
        }
        return message;
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
