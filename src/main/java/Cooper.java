import java.util.Scanner;

public class Cooper {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        String dashes = "\t____________________________________________________________\n";
        String banner = dashes + "\tHello! I'm Cooper.\n\tWhat can I do for you?\n" + dashes;

        System.out.println(banner);

        String input = scanner.nextLine();

        while (!input.toLowerCase().equals("bye")) {
            String reply = dashes + '\t' + input + '\n' + dashes;

            System.out.println(reply);

            input = scanner.nextLine();
        }

        String last = dashes + "\tBye. Hope to see you again soon!\n" + dashes;

        System.out.println(last);
    }
}
