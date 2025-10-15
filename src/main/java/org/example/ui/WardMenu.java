package org.example.ui;

import org.example.model.Bed;
import org.example.model.Gender;
import org.example.model.Room;
import org.example.model.Ward;
import org.example.service.CareHomeService;

/**
 * Ward management menu for the Care Home System
 */
public class WardMenu implements Menu {
    private final MenuManager menuManager;
    private final CareHomeService careHomeService;

    /**
     * Constructor
     */
    public WardMenu() {
        menuManager = MenuManager.getInstance();
        careHomeService = CareHomeService.getInstance();
    }

    @Override
    public void display() {
        System.out.println("\n===================================");
        System.out.println("        WARD MANAGEMENT           ");
        System.out.println("===================================");
        System.out.println("1. View All Wards");
        System.out.println("2. View Vacant Beds");
        System.out.println("3. View Suitable Beds for Patient");
        System.out.println("0. Back to Main Menu");
        System.out.println("===================================");
    }

    @Override
    public void handleInput(String input) {
        switch (input) {
            case "1":
                viewAllWards();
                break;
            case "2":
                viewVacantBeds();
                break;
            case "3":
                viewSuitableBeds();
                break;
            case "0":
                menuManager.goBack();
                break;
            default:
                System.out.println("Invalid option. Please try again.");
                break;
        }
    }

    @Override
    public String getTitle() {
        return "Ward Management";
    }

    /**
     * View all wards
     */
    private void viewAllWards() {
        System.out.println("\n===================================");
        System.out.println("            ALL WARDS             ");
        System.out.println("===================================");

        for (Ward ward : careHomeService.getAllWards()) {
            System.out.println("Ward: " + ward.getName());
            System.out.println("Capacity: " + ward.getTotalBeds());
            System.out.println("Rooms:");

            for (Room room : ward.getRooms()) {
                System.out.println("  Room " + room.getNumber() + " (" + room.getNumberOfBeds() + " beds)");

                for (Bed bed : room.getBeds()) {
                    String status = bed.isOccupied() ? "Occupied by " + bed.getPatient().getFullName() : "Vacant";
                    System.out.println("    Bed " + bed.getName() + ": " + status);
                }
            }

            System.out.println("-----------------------------------");
        }

        menuManager.displayMessage("Press Enter to continue...");
    }

    /**
     * View vacant beds
     */
    private void viewVacantBeds() {
        System.out.println("\n===================================");
        System.out.println("          VACANT BEDS             ");
        System.out.println("===================================");

        for (Bed bed : careHomeService.getVacantBeds()) {
            System.out.println("Ward: " + bed.getRoom().getWard().getName());
            System.out.println("Room: " + bed.getRoom().getNumber());
            System.out.println("Bed: " + bed.getName());
            System.out.println("-----------------------------------");
        }

        menuManager.displayMessage("Press Enter to continue...");
    }

    /**
     * View suitable beds for a patient
     */
    private void viewSuitableBeds() {
        System.out.println("\n===================================");
        System.out.println("      SUITABLE BEDS SEARCH        ");
        System.out.println("===================================");

        System.out.println("Select gender:");
        System.out.println("1. Male");
        System.out.println("2. Female");
        System.out.println("0. Cancel");

        int genderChoice = menuManager.getIntInput("Enter choice");

        if (genderChoice == 0) {
            return;
        }

        if (genderChoice != 1 && genderChoice != 2) {
            menuManager.displayMessage("Invalid choice");
            return;
        }

        Gender gender = (genderChoice == 1) ? Gender.MALE : Gender.FEMALE;

        System.out.println("\nNeeds isolation?");
        System.out.println("1. Yes");
        System.out.println("2. No");
        System.out.println("0. Cancel");

        int isolationChoice = menuManager.getIntInput("Enter choice");

        if (isolationChoice == 0) {
            return;
        }

        if (isolationChoice != 1 && isolationChoice != 2) {
            menuManager.displayMessage("Invalid choice");
            return;
        }

        boolean needsIsolation = (isolationChoice == 1);

        System.out.println("\n===================================");
        System.out.println("        SUITABLE BEDS             ");
        System.out.println("===================================");

        for (Bed bed : careHomeService.getSuitableVacantBeds(gender, needsIsolation)) {
            System.out.println("Ward: " + bed.getRoom().getWard().getName());
            System.out.println("Room: " + bed.getRoom().getNumber());
            System.out.println("Bed: " + bed.getName());
            System.out.println("-----------------------------------");
        }

        menuManager.displayMessage("Press Enter to continue...");
    }
}
