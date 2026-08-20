import java.util.Scanner;

public class Cooper {

    private static final String DASHES = "\t_*_*_*______________________________________________________\n";
    private static final String INTRO = "Hello! I'm Cooper.\n\tWhat can I do for you?";

    private String echo(String s) {
        return DASHES + '\t' + s + '\n' + DASHES;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        Cooper bot = new Cooper();

        String banner = bot.echo(INTRO);
        System.out.println(banner);

        String input = scanner.nextLine();

        while (!input.toLowerCase().equals("bye")) {
            String reply = bot.echo(input);
            System.out.println(reply);

            input = scanner.nextLine();
        }

        String last = bot.echo("Bye. Hope to see you again soon!");
        System.out.println(last);
    }
}
