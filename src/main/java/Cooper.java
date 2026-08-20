import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Cooper {

    private static final String DASHES = "\t_*_*_*______________________________________________________\n";
    private static final String INTRO = "Hello! I'm Cooper.\n\tWhat can I do for you?";

    private List<Task> checklist;

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

        StringBuilder s = new StringBuilder(String.format("%d. %s\n", 1, checklist.getFirst().getDescription()));

        for (int i = 1; i < checklist.size(); i++) {
            s.append(String.format("\t%d. %s\n", i + 1, checklist.get(i).getDescription()));
        }

        echo(s.delete(s.capacity() - 1, s.capacity()).toString());
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
                bot.addTask(input);

                bot.echo("added: " + input);
            }
                input = scanner.nextLine();
        }

        bot.echo("Bye. Hope to see you again soon!");
    }
}
