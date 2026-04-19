package HMS;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        HospitalApp hms = new HospitalApp();
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("==================================");
            System.out.println("  HospiCare Launch Menu           ");
            System.out.println("==================================");
            System.out.println("  1. Command Line Interface (CLI) ");
            System.out.println("  2. Graphical User Interface (GUI)");
            System.out.println("  0. Exit                         ");
            System.out.println("==================================");
            System.out.print("  Select Launch Mode: ");
            String choice = sc.nextLine().trim();

            if (choice.equals("1")) {
                hms.runCLI();
                break;
            } else if (choice.equals("2")) {
                hms.runGUI();
                break;
            } else if (choice.equals("0")) {
                System.out.println("Exiting. Goodbye!");
                break;
            } else {
                System.out.println("Invalid choice, please try again.\n");
            }
        }
        sc.close();
    }
}
