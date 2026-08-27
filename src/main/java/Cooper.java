import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class Cooper {

    private static final String DASHES = "\t_*_*_*______________________________________________________\n";
    private static final String INTRO = "Hello! I'm Cooper.\n\tWhat can I do for you?";
    private static final String FILE_PATH = "data/cooper.txt";
    private static final List<DateTimeFormatter> DATE_TIME_FORMATS = List.of(
            DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm").withResolverStyle(ResolverStyle.STRICT),
            DateTimeFormatter.ofPattern("dd-MM-uuuu HH:mm").withResolverStyle(ResolverStyle.STRICT)
    );
    private static final List<DateTimeFormatter> DATE_FORMATS = List.of(
            DateTimeFormatter.ofPattern("uuuu-MM-dd").withResolverStyle(ResolverStyle.STRICT),
            DateTimeFormatter.ofPattern("dd-MM-uuuu").withResolverStyle(ResolverStyle.STRICT)
    );
    private static final DateTimeFormatter DISPLAY_FORMAT
            = DateTimeFormatter.ofPattern("MMM dd uuuu HH:mm", Locale.ENGLISH);

    private final List<Task> checklist;
    private final Storage storage;

    public Cooper() {
        storage = new Storage(FILE_PATH);
        checklist = storage.loadTasks();
    }

    private static Action parseAction(String input) throws CooperException {
        String trimmedInput = input.trim();

        if (trimmedInput.isEmpty()) {
            throw new CooperException("Please enter a command!");
        }

        String cmd = trimmedInput.split("\\s+", 2)[0];

        try {
            return Action.valueOf(cmd.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new CooperException("Cooper doesn't understand this command: " + cmd);
        }
    }

    public static LocalDateTime parseDate(String time) {
        String normalizedTime = time.trim().replace('/', '-');

        for (DateTimeFormatter formatter : DATE_TIME_FORMATS) {
            try {
                return LocalDateTime.parse(normalizedTime, formatter);
            } catch (DateTimeParseException ignored) {
                // Nothing, try next date-time format
            }
        }

        for (DateTimeFormatter formatter : DATE_FORMATS) {
            try {
                return LocalDate.parse(normalizedTime, formatter).atStartOfDay();
            } catch (DateTimeParseException ignored) {
                // Nothing, try next date-time format
            }
        }

        throw new CooperException("Invalid date. Use yyyy-MM-dd or dd-MM-yyyy, and HH:mm optionally.");
    }

    public static String parseDateString(LocalDateTime time) {

        return time.format(DISPLAY_FORMAT);
    }

    private static int wordToNum(String numString) {
        int n = numString.length();
        int num = 0;

        for (int i = 0; i < n; i++) {
            char currChar = numString.charAt(i);
            if (currChar - '0' < 0 || currChar - '0' > 9) {
                return -1;
            } else {
                num += (currChar - '0') * (int) Math.pow(10, n - 1 - i);
            }
        }

        return num;
    }

    private void echo(String s) {
        System.out.println(DASHES + '\t' + s + '\n' + DASHES);
    }

    private void addTask(Task task) {
        checklist.add(task);

        storage.saveTasks(checklist);

        String status = (checklist.size() == 1) ? "task" : "tasks";

        echo("Got it. I've added this task:\n\t  " + task +
                String.format("\n\tNow you have %d %s in the list.", checklist.size(), status));
    }

    private void handleList() {
        if (checklist.isEmpty()) {
            echo("No current tasks!");
            return;
        }

        String preamble = "Here are the tasks in your list:\n\t";

        StringBuilder s = new StringBuilder(String.format("%d.%s\n", 1, checklist.getFirst()));

        for (int i = 1; i < checklist.size(); i++) {
            s.append(String.format("\t%d.%s\n", i + 1, checklist.get(i)));
        }

        echo(preamble + s.delete(s.length() - 1, s.length()).toString());
    }

    private void handleDelete(String input) throws CooperException {
        String[] params = input.split(" ");
        if (params.length != 2) {
            throw new CooperException("Deleting is serious! Cooper wishes you provided a proper index only.");
        }

        int idx = wordToNum(params[1]);
        if (idx <= 0 || idx > checklist.size()) {
            throw new CooperException("The index isn't valid!");
        }

        Task toRemove = checklist.get(idx - 1);
        checklist.remove(idx - 1);

        storage.saveTasks(checklist);

        String status = (checklist.size() == 1) ? "task" : "tasks";

        echo("Noted. I've removed this task:\n\t  " + toRemove
                + String.format("\n\tNow you have %d %s in the list.", checklist.size(), status));
    }

    private void handleMark(String input) throws CooperException {
        String[] params = input.trim().split(" ");

        if (params.length != 2) {
            throw new CooperException("Invalid syntax :( "
                    + "Cooper would like you to follow the format: mark <task-number>");
        }

        int listNum = wordToNum(params[1]);
        if (listNum > 0 && listNum <= checklist.size()) {
            Task currTask = checklist.get(listNum - 1);
            currTask.markAsDone();

            storage.saveTasks(checklist);

            echo("Nice! I've marked this task as done:\n\t  " + currTask +
                    "\n\tCooper would have loved that :)");
        } else {
            throw new CooperException("Cooper couldn't find a task with that index :(");
        }
    }

    private void handleUnmark(String input) throws CooperException {
        String[] params = input.trim().split(" ");

        if (params.length != 2) {
            throw new CooperException("Invalid syntax :( "
                    + "Cooper would like you to follow the format: unmark <task-number>");
        }

        int listNum = wordToNum(params[1]);
        if (listNum > 0 && listNum <= checklist.size()) {
            Task currTask = checklist.get(listNum - 1);
            currTask.markAsUndone();

            storage.saveTasks(checklist);

            echo("OK, I've marked this task as not done yet:\n\t  " + currTask
                    + "\n\tKeep going! :)");
        } else {
            throw new CooperException("Cooper couldn't find the task with that index :(");
        }
    }

    private void handleTodo(String input) throws CooperException {
        String[] params = input.split(" ");

        if (params.length == 1) {
            throw new CooperException("Cooper notices that your todo is empty. That's impossible!");
        }

        ToDo newTodo = new ToDo(input.split(" ", 2)[1]);
        addTask(newTodo);
    }

    private void handleDeadline(String input) throws CooperException {
        String[] params = input.split(" /by ");

        if (params.length != 2) {
            throw new CooperException("Cooper feels a task can only have exactly 1 deadline!");
        }

        if (params[0].trim().equals("deadline")) {
            throw new CooperException("Cooper can't keep track of tasks with no name!");
        }

        String taskName = params[0].split("deadline ")[1];
        String dueDate = params[1];

        Deadline newDeadline = new Deadline(taskName, parseDate(dueDate));
        addTask(newDeadline);
    }

    private void handleEvent(String input) throws CooperException {
        String[] params = input.split(" /from ");

        if (params.length != 2) {
            throw new CooperException("Cooper feels an event must have a title and start date!");
        }

        if (params[0].trim().equals("event")) {
            throw new CooperException("Cooper thinks we need a title!");
        }
        String taskName = params[0].split("event ")[1];

        String startDate = params[1].split(" /to ")[0];
        String endDate = params[1].split(" /to ")[1];

        Event newEvent = new Event(taskName, parseDate(startDate), parseDate(endDate));
        addTask(newEvent);
    }

    private boolean executeCommand(String input) {
        Action action = parseAction(input);

        switch (action) {
            case Action.LIST: {
                handleList();
                break;
            }
            case Action.DELETE: {
                handleDelete(input);
                break;
            }
            case Action.MARK: {
                handleMark(input);
                break;
            }
            case Action.UNMARK: {
                handleUnmark(input);
                break;
            }
            case Action.TODO: {
                handleTodo(input);
                break;
            }
            case Action.DEADLINE: {
                handleDeadline(input);
                break;
            }
            case Action.EVENT: {
                handleEvent(input);
                break;
            }
            case Action.BYE: {
                return true;
            }
            // did not break, since body returns directly
            default: {
                throw new CooperException("Cooper doesn't understand this command :(");
            }
        }
        return false;
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        echo(INTRO);

        while (scanner.hasNextLine()) {
            String input = scanner.nextLine();

            try {
                if (executeCommand(input)) {
                    break;
                }
            } catch (CooperException e) {
                echo(e.getMessage());
            }
        }

        echo("Bye. Hope to see you again soon!");
    }

    public static void main(String[] args) {
        new Cooper().run();
    }
}
