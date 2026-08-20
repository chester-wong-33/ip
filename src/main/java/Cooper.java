import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Cooper {

    private static final String DASHES = "\t_*_*_*______________________________________________________\n";
    private static final String INTRO = "Hello! I'm Cooper.\n\tWhat can I do for you?";

    private List<Task> checklist;

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

    private void addTask(String description) {
        Task toDo = new Task(description);
        checklist.add(toDo);
    }

    private void printChecklist() {
        if (checklist.isEmpty()) {
            echo("No current tasks!");
            return;
        }

        StringBuilder s = new StringBuilder(String.format("%d.%s\n", 1, checklist.getFirst()));

        for (int i = 1; i < checklist.size(); i++) {
            s.append(String.format("\t%d.%s\n", i + 1, checklist.get(i)));
        }

        echo(s.delete(s.length() - 1, s.length()).toString());
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        Cooper bot = new Cooper();

        bot.echo(INTRO);

        String input = scanner.nextLine();

        while (!input.toLowerCase().equals("bye")) {

            if (input.toLowerCase().equals("list")) {
                bot.printChecklist();
            } else {
                String[] words = input.split(" ");

                if (words.length == 2 && words[0].equals("mark")) {
                    int listNum = wordToNum(words[1]);
                    if (listNum != -1) {
                        if (listNum <= bot.checklist.size()) {
                            Task currTask = bot.checklist.get(listNum - 1);
                            currTask.markAsDone();
                            bot.echo("Nice! I've marked this task as done:\n\t  " + currTask +
                                    "\n\tCooper would have loved that :)");
                        } else {
                            bot.echo("I coulnd't find the task with that index :(");
                        }
                        input = scanner.nextLine();
                        continue;
                    }
                }

                if (words.length == 2 && words[0].equals("unmark")) {
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
                        input = scanner.nextLine();
                        continue;
                    }
                }
                bot.addTask(input);

                bot.echo("added: " + input);
            }
            input = scanner.nextLine();
        }

        bot.echo("Bye. Hope to see you again soon!");
    }
}
