package Tree;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

//import java.io.*;
//import java.util.*;

public class Functions {

    /*
     * Reads data from file.
     * Returns the set with the data.
     */
    protected static Set<String[]> readData(String filePath) throws Exception {

        Set<String[]> resultSet = new HashSet<>();

        try (Scanner in = new Scanner(new File(filePath));) {

            while (in.hasNextLine()) {
                List<String> list = new ArrayList<>();
                for (String s : in.nextLine().split(",")) {
                    String trim = s.trim();
                    list.add(trim);
                }
                String data[] = list.toArray(new String[0]);

                switch(data.length) {
                    case 2: // Fact
                    case 3: // Relation
                        resultSet.add(data);
                        break;

                    default: // Wrong csv format.
                        throw new Exception("CSV file bad format");
                }
            }
        } catch (FileNotFoundException ex) {
            // File not found exception if the app is unable to find the csv file
            throw new Exception("Unable to find file '" + filePath + "'");
        }

        return resultSet;
    }

    /*
     * Reads data from tokens set.
     * Returns a set of Persons.
     */
    protected static HashMap<String, Person> dataToPersonsSet(Set<String[]> tokens) {

        HashMap<String, Person> resultSet = new HashMap<>();

        for (String[] token : tokens) {
            int tokenTypeLenth = token.length;

            if (tokenTypeLenth == 2) {
                if (resultSet.containsKey(token[0])) {
                    resultSet.get(token[0]).setGender(token[1]);
                } else {
                    resultSet.put(token[0], new Person(token[0], token[1]));
                }
            } else if (tokenTypeLenth == 3) {
                if (!resultSet.containsKey(token[0])) {
                    resultSet.put(token[0], new Person(token[0]));
                }

                if (!resultSet.containsKey(token[2])) {
                    resultSet.put(token[2], new Person(token[2]));
                }
            }
        }
        return resultSet;
    }

    /*
     * Saves the sorted list to disk.
     */
    protected static void writeSortedListFile(HashMap<String, Person> personsMap, String filePath) throws Exception {

        // Load person's data to a list.
        List<String> personList = new ArrayList<>(personsMap.keySet());
        // Sort personList.
        Collections.sort(personList, (p1, p2) -> {
            return p1.compareToIgnoreCase(p2);
        });

        // Write sorted list to file.
        try {
            FileWriter writer = new FileWriter(filePath);
            try (PrintWriter out = new PrintWriter(writer)) {
                for (String p : personList) {
                    out.write(p);
                    out.println();
                }
            }
        } catch (IOException e) {
            throw new Exception("Unable to write file '" + filePath + "'");
        }
    }

    /*
     * Takes the input and creates HashMap with Person objects.
     */
    protected static void loadRelations(Set<String[]> tokens, HashMap<String, Person> personsMap) {

        // Load direct relations.
        for (String[] token : tokens) {
            if (token.length == 3) {
                Person person = personsMap.get(token[0]);

                if (person != null) {
                    switch (token[1].toLowerCase()) {
                        case "father":
                            if (person.getGender().equals("")) {
                                person.setGender("man");
                            }

                            Person c1 = personsMap.get(token[2]);
                            if (c1 != null) {
                                person.getChildren().add(c1);
                                c1.getParents().add(person);
                            }
                            break;

                        case "mother":
                            if (person.getGender().equals("")) {
                                person.setGender("woman");
                            }

                            Person c2 = personsMap.get(token[2]);
                            if (c2 != null) {
                                person.getChildren().add(c2);
                                c2.getParents().add(person);
                            }
                            break;

                        case "partner":
                            Person p1 = personsMap.get(token[2]);
                            person.setPartner(p1);
                            p1.setPartner(person);
                            break;

                        case "sibling":
                            Person s1 = personsMap.get(token[2]);
                            person.getSiblings().add(s1);
                            s1.getSiblings().add(person);
                            break;
                    }
                }
            }
        }

        // Load indirect relations.
        for (Person p : personsMap.values()) {

            // If two persons have the same children they are partners.
            if (!p.getChildren().isEmpty()) {
                for (Person p2 : personsMap.values()) {
                    if (!p.equals(p2) && p.getChildren().equals(p2.getChildren())) {
                        p.setPartner(p2);
                        p2.setPartner(p);
                        break;
                    }
                }
            }

            // If two persons have the same parents they are siblings
            if (!p.getParents().isEmpty()) {
                for (Person p2 : personsMap.values()) {
                    if (!p.equals(p2) && p.getParents().equals(p2.getParents())) {
                        p.getSiblings().add(p2);
                        p2.getSiblings().add(p);
                    }
                }
            }
        }
    }

    /*
     * Writes to disk the dot file.
     */
    protected static void writeDotFile(HashMap<String, Person> personsMap, String filePath) throws Exception {
        try {
            FileWriter writer = new FileWriter(filePath);
            try (PrintWriter out = new PrintWriter(writer)) {
                out.write("digraph FamilyTree {");
                out.println();

                out.write("rankdir=LR;");
                out.println();

                out.write("size=\"8.5\"");
                out.println();

                out.write("node [shape=rectangle] [color=black];");
                out.println();

                for (Person person : personsMap.values()) {
                    if (!person.getChildren().isEmpty()) {
                        for (Person child : person.getChildren()) {
                            out.write("\"" + person.getName() + "\"");
                            out.write(" -> ");
                            out.write("\"" + child.getName() + "\"");

                            switch(person.getGender().toLowerCase()) {
                                case "man":
                                    out.write(" [label=\"father\"];");
                                    break;
                                case "woman":
                                    out.write(" [label=\"mother\"];");
                                    break;
                                default:
                                    out.write(" [label=\"parent\"];");
                                    break;
                            }
                            out.println();
                        }
                    }
                }
                out.write("}");
            }
        } catch (IOException e) {
            throw new Exception("Unable to write file '" + filePath + "'");
        }
    }

    /*
     * Finds the relation between a pair of persons.
     * Returns a string that describes the relation.
     */
    protected static String findRelationBetween(HashMap<String, Person> personsMap, String person1, String person2) {

        if (person1.equalsIgnoreCase(person2)) {
            return "* Warning: You have entered the same name twice!\n           The names must be different.";
        } else if (!personsMap.containsKey(person1) &&
                !personsMap.containsKey(person2)) {
            return "* Warning: '" + person1 + "' and '" + person2 + "' do not exist!";
        } else if (!personsMap.containsKey(person1)) {
            return "* Warning: '" + person1 + "' does not exist!";
        } else if (!personsMap.containsKey(person2)) {
            return "* Warning: '" + person2 + "' does not exist!";
        } else {
            Person p1 = personsMap.get(person1);
            Person p2 = personsMap.get(person2);

            /* Check if first person is the parent of the second person. */
            if (p1.getChildren().contains(p2)) {
                switch (p1.getGender().toLowerCase()) {
                    case "man":
                        return "'" + person1 + "' is the father of '" + person2 + "'";
                    case "woman":
                        return "'" + person1 + "' is the mother of '" + person2 + "'";
                    default:
                        return "'" + person1 + "' is the parent of '" + person2 + "'";
                }
            }

            /* Check if first person is the child of the second person. */
            if (p1.getParents().contains(p2)) {
                switch (p1.getGender().toLowerCase()) {
                    case "man":
                        return "'" + person1 + "' is the son of '" + person2 + "'";
                    case "woman":
                        return "'" + person1 +
                                "' is the daughter of '" +
                                person2 + "'";
                    default:
                        return "'" + person1 +
                                "' is the child of '" +
                                person2 + "'";
                }
            }

            /* Check if first person is the sibling of the second person. */
            if (p1.getSiblings().contains(p2)) {
                switch (p1.getGender().toLowerCase()) {
                    case "man":
                        return "'" + person1 + "' is the brother of '" + person2 + "'";
                    case "woman":
                        return "'" + person1 + "' is the sister of '" + person2 + "'";
                    default:
                        return "'" + person1 + "' is the sibling of '" + person2 + "'";
                }
            }

            /* Check if they are cousins */
            for (Person personOneParent : p1.getParents()) {
                for (Person personTwoParent : p2.getParents()) {
                    if (personOneParent.getSiblings().contains(personTwoParent)) {
                        return "'" + p1.getName() + "' and '" + p2.getName() + "' are cousins";
                    }
                }
            }

            /* Check if they are married */
            if (p1.getPartner() != null && p1.getPartner().equals(p2)) {
                switch (p1.getGender().toLowerCase()) {
                    case "man":
                        return "'" + person1 + "' is the husband of '" + person2 + "'";
                    case "woman":
                        return "'" + person1 + "' is the wife of '" + person2 + "'";
                    default:
                        return "'" + person1 + "' is the partner of '" + person2 + "'";
                }
            }

            /* Check if first person is the grandparent of the second person. */
            for (Person parent : p2.getParents()) {
                if (parent.getParents().contains(p1)) {
                    switch (p1.getGender()) {
                        case "man":
                            return "'" + person1 + "' is the grandfather of '" + person2 + "'";

                        case "woman":
                            return "'" + person1 + "' is the grandmother of '" + person2 + "'";

                        default:
                            return "'" + person1 + "' is the grandparent of '" + person2 + "'";
                    }
                }
            }

            /* Check if first person is the grandchild of the second person. */
            for (Person parent : p1.getParents()) {
                if (parent.getParents().contains(p2)) {
                    switch (p1.getGender()) {
                        case "man":
                            return "'" + person1 + "' is the grandson of '" + person2 + "'";

                        case "woman":
                            return "'" + person1 + "' is the granddaughter of '" + person2 + "'";

                        default:
                            return "'" + person1 + "' is the grandchild of '" + person2 + "'";
                    }
                }
            }

            /* Check if first person is the nephew/niece of the second person. */
            for (Person parent : p1.getParents()) {
                if (parent.getSiblings().contains(p2)) {
                    switch (p1.getGender()) {
                        case "man":
                            return "'" + person1 + "' is the nephew of '" + person2 + "'";

                        case "woman":
                            return "'" + person1 + "' is the niece of '" + person2 + "'";

                        default:
                            return "'" + person1 + "' is the nephew/niece of '" + person2 + "'";
                    }
                } else {
                    for (Person p : parent.getSiblings()) {
                        if (p.getPartner() != null && p.getPartner().equals(p2)) {
                            switch (p1.getGender()) {
                                case "man":
                                    return "'" + person1 + "' is the nephew of '" + person2 + "'";

                                case "woman":
                                    return "'" + person1 + "' is the niece of '" + person2 + "'";

                                default:
                                    return "'" + person1 + "' is the nephew/niece of '" + person2 + "'";
                            }
                        }
                    }
                }
            }

            /* Check if first person is the uncle/aunt of the second person. */
            for (Person sibling : p1.getSiblings()) {
                if (sibling.getChildren().contains(p2)) {
                    switch (p1.getGender()) {
                        case "man":
                            return "'" + person1 + "' is the uncle of '" + person2 + "'";

                        case "woman":
                            return "'" + person1 + "' is the aunt of '" + person2 + "'";

                        default:
                            return "'" + person1 + "' is the uncle/aunt of '" + person2 + "'";
                    }
                }
            }
            if (p1.getPartner() != null) {
                for (Person sibling : p1.getPartner().getSiblings()) {
                    if (sibling.getChildren().contains(p2)) {
                        switch (p1.getGender()) {
                            case "man":
                                return "'" + person1 + "' is the uncle of '" + person2 + "'";

                            case "woman":
                                return "'" + person1 + "' is the aunt of '" + person2 + "'";

                            default:
                                return "'" + person1 + "' is the uncle/aunt of '" + person2 + "'";
                        }
                    }
                }
            }

            // No relatives.
            return "'" + person1 + "' and '" + person2 + "' are no relatives";
        }
    }
}


