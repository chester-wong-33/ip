import java.util.Scanner;

public class Cooper {

    private static final String DASHES = "\t____________________________________________________________\n";
    private static final String INTRO = "Hello! I'm Cooper.\n\tWhat can I do for you?";

    private static String echo(String s) {
        return DASHES + '\t' + s + '\n' + DASHES;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        String banner = echo(INTRO);
        System.out.println(banner);

        String input = scanner.nextLine();

        while (!input.toLowerCase().equals("bye")) {
            String reply = echo(input);
            System.out.println(reply);

            input = scanner.nextLine();
        }

        String last = echo("Bye. Hope to see you again soon!");
        System.out.println(last);
    }
}
