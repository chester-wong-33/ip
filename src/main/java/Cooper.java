import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Cooper {

    private static final String DASHES = "\t_*_*_*______________________________________________________\n";
    private static final String INTRO = "Hello! I'm Cooper.\n\tWhat can I do for you?";

    private final List<Task> checklist;

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

    public Cooper() {
        checklist = new ArrayList<>();
    }

    private void echo(String s) {
        System.out.println(DASHES + '\t' + s + '\n' + DASHES);
    }

    private void addTask(Task task) {
        checklist.add(task);
        String status = (checklist.size() == 1) ? "task" : "tasks";

        echo("Got it. I've added this task:\n\t  " + task +
                String.format("\n\tNow you have %d %s in the list.", checklist.size(), status));
    }

    private void printChecklist() {
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

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        Cooper bot = new Cooper();

        bot.echo(INTRO);

        String input;
        Action cmd = Action.LIST;

        do {

            try {

                input = scanner.nextLine();
                String[] words = input.split(" ");
                cmd = Action.valueOf(words[0].toUpperCase());

                switch (cmd) {
                    case Action.LIST: {
                        bot.printChecklist();
                        break;
                    }
                    case Action.DELETE: {
                        String[] params = input.split(" ");
                        if (params.length != 2) {
                            throw new Exception("Deleting is serious! Cooper wishes you provided a proper index only.");
                        }

                        int idx = wordToNum(params[1]);
                        if (idx == -1 || idx > bot.checklist.size()) {
                            throw new Exception("The index isn't valid!");
                        }

                        Task toRemove = bot.checklist.get(idx - 1);
                        bot.checklist.remove(idx - 1);
                        String status = (bot.checklist.size() == 1) ? "task" : "tasks";

                        bot.echo("Noted. I've removed this task:\n\t  " + toRemove +
                                String.format("\n\tNow you have %d %s in the list.", bot.checklist.size(), status));
                        break;
                    }
                    case Action.MARK: {
                        if (words.length == 2) {
                            int listNum = wordToNum(words[1]);
                            if (listNum != -1) {
                                if (listNum <= bot.checklist.size()) {
                                    Task currTask = bot.checklist.get(listNum - 1);
                                    currTask.markAsDone();
                                    bot.echo("Nice! I've marked this task as done:\n\t  " + currTask +
                                            "\n\tCooper would have loved that :)");
                                } else {
                                    bot.echo("I couldn't find the task with that index :(");
                                }
                                break;
                            }
                        }
                        bot.echo("Invalid syntax :(");
                        break;
                    }
                    case Action.UNMARK: {
                        if (words.length == 2) {
                            int listNum = wordToNum(words[1]);
                            if (listNum != -1) {
                                if (listNum <= bot.checklist.size()) {
                                    Task currTask = bot.checklist.get(listNum - 1);
                                    currTask.markAsUndone();
                                    bot.echo("OK, I've marked this task as not done yet:\n\t  " + currTask +
                                            "\n\tKeep going! :)");
                                } else {
                                    bot.echo("I coulnd't find the task with that index :(");
                                }
                                break;
                            }
                        }
                        bot.echo("Invalid syntax :(");
                        break;
                    }
                    case Action.TODO: {
                        String[] params = input.split(" ");
                        if (params.length == 1) {
                            throw new Exception("Cooper notices that your todo is empty. That's impossible!");
                        }

                        ToDo newTodo = new ToDo(input.split(" ", 2)[1]);
                        bot.addTask(newTodo);
                        break;
                    }
                    case Action.DEADLINE: {
                        String[] params = input.split(" /by ");
                        if (params.length != 2) {
                            throw new Exception("Cooper feels a task can only have exactly 1 deadline!");
                        }

                        if (params[0].trim().equals("deadline")) {
                            throw new Exception("Cooper can't keep track of tasks with no name!");
                        }
                        String taskName = params[0].split("deadline ")[1];
                        String dueDate = params[1];

                        Deadline newDeadline = new Deadline(taskName, dueDate);
                        bot.addTask(newDeadline);
                        break;
                    }
                    case Action.EVENT: {
                        String[] params = input.split(" /from ");
                        if (params.length != 2) {
                            throw new Exception("Cooper feels an event must have a title and start date!");
                        }

                        if (params[0].trim().equals("event")) {
                            throw new Exception("Cooper thinks we need a title!");
                        }
                        String taskName = params[0].split("event ")[1];

                        String startDate = params[1].split(" /to ")[0];
                        String endDate = params[1].split(" /to ")[1];

                        Event newEvent = new Event(taskName, startDate, endDate);
                        bot.addTask(newEvent);
                        break;
                    }
                    case Action.BYE: {
                        cmd = Action.BYE;
                        break;
                    }
                    default:
                        throw new Exception("Cooper doesn't understand this command :(");
                }

            } catch (Exception e) {
                bot.echo(e.getMessage());
            }
        } while (!cmd.equals(Action.BYE));

        bot.echo("Bye. Hope to see you again soon!");
    }
}
