package Tree;

import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;


public class Main {
    public static void main(String[] args)  {

        Set<String[]> tokens; // Contains the csv file's data.

        // Read data from csv file.

        try {
            System.out.println("Reading data from file 'familytree.csv'...");
            tokens = Functions.readData("familytree.csv");
        } catch (Exception ex) {
            // Displays error message and terminates the app.
            System.out.println("* Error: " + ex.getMessage() + "!");
            System.out.println("* * * * Goodbye * * * *");
            return;
        }


        // Map that contains the persons.
        HashMap<String, Person> personsMap = Functions.dataToPersonsSet(tokens);

        System.out.println("Writing data to file 'persons.txt'...");
        // Write sorted person list to disk.
        try {
            Functions.writeSortedListFile(personsMap, "persons.txt");
        } catch (Exception ex) {
            // Displays error message and terminates the app.
            System.out.println("* Error: " + ex.getMessage() + "!");
            System.out.println("* * * * Goodbye * * * *");
            return;
        }

        // Load all the relations to personsMap.
        Functions.loadRelations(tokens, personsMap);

        // Export dot file
        try {
            System.out.println("Writing dot file 'familytree.dot'...");
            Functions.writeDotFile(personsMap, "familytree.dot");
        } catch (Exception ex) {
            // Displays error message and terminates the app.
            System.out.println("* Error: " + ex.getMessage() + "!");
            System.out.println("* * * * Goodbye * * * *");
            return;
        }

        /* ***
        User Interface
         *** */

        System.out.println("\n--------------------------------------------------------");
        System.out.println("* Info: Enter the names of people you would like to know\n" +
                "        if they are relatives.");
        System.out.println("--------------------------------------------------------\n");

        Scanner inputScanner = new Scanner(System.in);
        boolean continueRunning = false;

        // Repeat until the user decides to quit.
        do {
            String person1 = "";
            String person2 = "";

            System.out.println("Please enter the name of the first person: ");
            person1 = inputScanner.nextLine();

            System.out.println("Please enter the name of the second person: ");
            person2 = inputScanner.nextLine();

            String output = Functions.findRelationBetween(personsMap, person1, person2);
            System.out.println("--------------------------------------------------------");
            System.out.println(output);
            System.out.println("--------------------------------------------------------\n");

            System.out.println("Do you want to continue (y/n)?");
            String input = inputScanner.nextLine();
            continueRunning = input.equalsIgnoreCase("y") || input.equalsIgnoreCase("yes");
        } while (continueRunning);

        System.out.println("* * * * Goodbye * * * *");
    }


}

