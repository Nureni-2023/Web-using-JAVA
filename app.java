import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * Main class for the Console-Based Contact Management System.
 * Manages contacts and handles user interaction.
 */
public class ContactManager {

    // Inner class to represent a Contact
    // This keeps the Contact definition within the same file for simplicity.
    static class Contact {
        private String name;
        private String phone;
        private String email;

        /**
         * Constructor for a Contact object.
         * @param name The name of the contact.
         * @param phone The phone number of the contact.
         * @param email The email address of the contact.
         */
        public Contact(String name, String phone, String email) {
            this.name = name;
            this.phone = phone;
            this.email = email;
        }

        // Getters for contact properties
        public String getName() {
            return name;
        }

        public String getPhone() {
            return phone;
        }

        public String getEmail() {
            return email;
        }

        /**
         * Returns a string representation of the contact, suitable for display.
         * @return Formatted string of contact details.
         */
        @Override
        public String toString() {
            return "Name: " + name + ", Phone: " + phone + ", Email: " + email;
        }

        /**
         * Converts the Contact object to a CSV string format for file storage.
         * @return CSV string of contact data.
         */
        public String toCsvString() {
            // Escape commas in fields if they exist to prevent breaking CSV format
            // Simple escaping: replace comma with a placeholder or enclose in quotes.
            // For this demo, we'll assume no commas in name, phone, email to keep it simple.
            return String.format("%s,%s,%s", name, phone, email);
        }

        /**
         * Creates a Contact object from a CSV string.
         * @param csvString The CSV string representing a contact.
         * @return A new Contact object, or null if the string is invalid.
         */
        public static Contact fromCsvString(String csvString) {
            String[] parts = csvString.split(",", 3); // Split into 3 parts: name, phone, email
            if (parts.length == 3) {
                return new Contact(parts[0], parts[1], parts[2]);
            }
            return null; // Invalid CSV string
        }
    }

    private List<Contact> contacts; // List to store all contacts
    private Scanner scanner; // Scanner for reading user input
    private static final String FILENAME = "contacts.txt"; // File to save/load contacts

    /**
     * Constructor for the ContactManager.
     * Initializes the contact list and scanner.
     */
    public ContactManager() {
        contacts = new ArrayList<>();
        scanner = new Scanner(System.in);
        System.out.println("Contact Management System Initialized.");
        loadContactsFromFile(); // Attempt to load contacts on startup
    }

    /**
     * Displays the main menu options to the user.
     */
    private void displayMenu() {
        System.out.println("\n--- Contact Manager Menu ---");
        System.out.println("1. Add New Contact");
        System.out.println("2. View All Contacts");
        System.out.println("3. Search Contact by Name");
        System.out.println("4. Delete Contact by Name");
        System.out.println("5. Save Contacts to File");
        System.out.println("6. Load Contacts from File");
        System.out.println("7. Exit");
        System.out.print("Enter your choice: ");
    }

    /**
     * Adds a new contact based on user input.
     */
    private void addContact() {
        System.out.println("\n--- Add New Contact ---");
        System.out.print("Enter Name: ");
        String name = scanner.nextLine();
        System.out.print("Enter Phone: ");
        String phone = scanner.nextLine();
        System.out.print("Enter Email: ");
        String email = scanner.nextLine();

        if (name.isEmpty() || phone.isEmpty() || email.isEmpty()) {
            System.out.println("Error: All fields must be filled. Contact not added.");
            return;
        }

        contacts.add(new Contact(name, phone, email));
        System.out.println("Contact added successfully!");
    }

    /**
     * Displays all contacts currently in the list.
     */
    private void viewAllContacts() {
        System.out.println("\n--- All Contacts ---");
        if (contacts.isEmpty()) {
            System.out.println("No contacts available. Add some first!");
        } else {
            for (int i = 0; i < contacts.size(); i++) {
                System.out.println((i + 1) + ". " + contacts.get(i));
            }
        }
    }

    /**
     * Searches for contacts by name (case-insensitive, partial match).
     */
    private void searchContact() {
        System.out.println("\n--- Search Contact ---");
        System.out.print("Enter name or part of name to search: ");
        String searchTerm = scanner.nextLine().toLowerCase();

        List<Contact> foundContacts = contacts.stream()
                                            .filter(c -> c.getName().toLowerCase().contains(searchTerm))
                                            .collect(Collectors.toList());

        if (foundContacts.isEmpty()) {
            System.out.println("No contacts found matching '" + searchTerm + "'.");
        } else {
            System.out.println("--- Found Contacts ---");
            for (int i = 0; i < foundContacts.size(); i++) {
                System.out.println((i + 1) + ". " + foundContacts.get(i));
            }
        }
    }

    /**
     * Deletes a contact by name.
     */
    private void deleteContact() {
        System.out.println("\n--- Delete Contact ---");
        if (contacts.isEmpty()) {
            System.out.println("No contacts to delete.");
            return;
        }

        System.out.print("Enter the name of the contact to delete: ");
        String nameToDelete = scanner.nextLine().toLowerCase();

        boolean foundAndDeleted = contacts.removeIf(c -> c.getName().toLowerCase().equals(nameToDelete));

        if (foundAndDeleted) {
            System.out.println("Contact(s) named '" + nameToDelete + "' deleted successfully!");
        } else {
            System.out.println("No contact found with the exact name '" + nameToDelete + "'.");
        }
    }

    /**
     * Saves all current contacts to a file.
     */
    private void saveContactsToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILENAME))) {
            for (Contact contact : contacts) {
                writer.write(contact.toCsvString());
                writer.newLine();
            }
            System.out.println("Contacts saved to " + FILENAME + " successfully!");
        } catch (IOException e) {
            System.err.println("Error saving contacts to file: " + e.getMessage());
        }
    }

    /**
     * Loads contacts from a file, replacing current contacts.
     */
    private void loadContactsFromFile() {
        File file = new File(FILENAME);
        if (!file.exists()) {
            System.out.println("No existing contacts file found. Starting with an empty list.");
            return;
        }

        List<Contact> loadedContacts = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILENAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Contact contact = Contact.fromCsvString(line);
                if (contact != null) {
                    loadedContacts.add(contact);
                } else {
                    System.err.println("Skipping invalid line in file: " + line);
                }
            }
            contacts = loadedContacts; // Replace current contacts with loaded ones
            System.out.println("Contacts loaded from " + FILENAME + " successfully!");
        } catch (IOException e) {
            System.err.println("Error loading contacts from file: " + e.getMessage());
        }
    }

    /**
     * Runs the main application loop.
     */
    public void run() {
        int choice;
        do {
            displayMenu();
            try {
                choice = Integer.parseInt(scanner.nextLine());
                switch (choice) {
                    case 1:
                        addContact();
                        break;
                    case 2:
                        viewAllContacts();
                        break;
                    case 3:
                        searchContact();
                        break;
                    case 4:
                        deleteContact();
                        break;
                    case 5:
                        saveContactsToFile();
                        break;
                    case 6:
                        loadContactsFromFile();
                        break;
                    case 7:
                        System.out.println("Exiting Contact Management System. Goodbye!");
                        break;
                    default:
                        System.out.println("Invalid choice. Please enter a number between 1 and 7.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                choice = 0; // Set to 0 to loop again
            }
        } while (choice != 7);

        scanner.close(); // Close the scanner when exiting
    }

    /**
     * Main method to start the application.
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        ContactManager manager = new ContactManager();
        manager.run();
    }
}
