package org.example.ui;

import org.example.model.Patient;
import org.example.service.CareHomeService;
import org.example.exception.NotRosteredException;
import org.example.exception.UnauthorizedActionException;
import org.example.model.Bed;
import org.example.model.Gender;
import org.example.service.AuthenticationService;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
/**
 * Patient management menu for the Care Home System
 */
public class PatientMenu implements Menu {
    private final MenuManager menuManager;
    private final CareHomeService careHomeService;
    private final AuthenticationService authService;

    /**
     * Constructor
     */
    public PatientMenu() {
        menuManager = MenuManager.getInstance();
        careHomeService = CareHomeService.getInstance();
        authService = AuthenticationService.getInstance();
    }

    @Override
    public void display() {
        System.out.println("\n===================================");
        System.out.println("       PATIENT MANAGEMENT         ");
        System.out.println("===================================");
        System.out.println("1. View All Patients");
        System.out.println("2. View Discharged Patients");
        System.out.println("3. Add Patient");
        System.out.println("4. Move Patient");
        System.out.println("5. Discharge Patient");
        System.out.println("0. Back to Main Menu");
        System.out.println("===================================");
    }

    @Override
    public void handleInput(String input) {
        switch (input) {
            case "1":
                viewAllPatients();
                break;
            case "2":
                viewDischargedPatients();
                break;
            case "3":
                addPatient();
                break;
            case "4":
                movePatient();
                break;
            case "5":
                dischargePatient();
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
        return "Patient Management";
    }

    /**
     * View all patients
     */
    private void viewAllPatients() {
        System.out.println("\n===================================");
        System.out.println("          ALL PATIENTS            ");
        System.out.println("===================================");

        for (Patient patient : careHomeService.getAllPatients()) {
            System.out.println("ID: " + patient.getId());
            System.out.println("Name: " + patient.getFullName());
            System.out.println("Medical Condition: " + patient.getMedicalCondition());
            System.out.println("Needs Isolation: " + (patient.isNeedsIsolation() ? "Yes" : "No"));
            System.out.println("Admission Date: " + patient.getAdmissionDate());
            System.out.println("-----------------------------------");
        }

        menuManager.displayMessage("Press Enter to continue...");
    }

    /**
     * View discharged patients
     */
    private void viewDischargedPatients() {
        System.out.println("\n===================================");
        System.out.println("      DISCHARGED PATIENTS         ");
        System.out.println("===================================");

        for (Patient patient : careHomeService.getDischargedPatients()) {
            System.out.println("ID: " + patient.getId());
            System.out.println("Name: " + patient.getFullName());
            System.out.println("Medical Condition: " + patient.getMedicalCondition());
            System.out.println("Admission Date: " + patient.getAdmissionDate());
            System.out.println("Discharge Date: " + patient.getDischargeDate());
            System.out.println("-----------------------------------");
        }

        menuManager.displayMessage("Press Enter to continue...");
    }

    /**
     * Add a new patient to the system
     */
    private void addPatient() {
        try {
            // Check if user is authorized
            if (!authService.isAuthorized("ADD_PATIENT")) {
                menuManager.displayMessage("You are not authorized to add patients.");
                return;
            }

            System.out.println("\n===================================");
            System.out.println("           ADD PATIENT            ");
            System.out.println("===================================");

            // Get patient details
            String firstName = menuManager.getInput("First Name");
            String lastName = menuManager.getInput("Last Name");

            // Get date of birth
            LocalDate dateOfBirth = null;
            while (dateOfBirth == null) {
                try {
                    String dobString = menuManager.getInput("Date of Birth (YYYY-MM-DD)");
                    dateOfBirth = LocalDate.parse(dobString, DateTimeFormatter.ISO_LOCAL_DATE);
                } catch (DateTimeParseException e) {
                    System.out.println("Invalid date format. Please use YYYY-MM-DD.");
                }
            }

            // Get gender
            System.out.println("Select gender:");
            System.out.println("1. Male");
            System.out.println("2. Female");

            int genderChoice = menuManager.getIntInput("Enter choice");

            if (genderChoice != 1 && genderChoice != 2) {
                menuManager.displayMessage("Invalid choice");
                return;
            }

            Gender gender = (genderChoice == 1) ? Gender.MALE : Gender.FEMALE;

            // Get medical condition
            String medicalCondition = menuManager.getInput("Medical Condition");

            // Check if patient needs isolation
            System.out.println("Does the patient need isolation?");
            System.out.println("1. Yes");
            System.out.println("2. No");

            int isolationChoice = menuManager.getIntInput("Enter choice");

            if (isolationChoice != 1 && isolationChoice != 2) {
                menuManager.displayMessage("Invalid choice");
                return;
            }

            boolean needsIsolation = (isolationChoice == 1);

            // Find suitable beds for the patient
            List<Bed> suitableBeds = careHomeService.getSuitableVacantBeds(gender, needsIsolation);

            if (suitableBeds.isEmpty()) {
                menuManager.displayMessage("No suitable beds available for this patient.");
                return;
            }

            // Display available beds
            System.out.println("\n===================================");
            System.out.println("        AVAILABLE BEDS            ");
            System.out.println("===================================");

            for (int i = 0; i < suitableBeds.size(); i++) {
                Bed bed = suitableBeds.get(i);
                System.out.println((i + 1) + ". Ward: " + bed.getRoom().getWard().getName() +
                        ", Room: " + bed.getRoom().getNumber() +
                        ", Bed: " + bed.getName());
            }

            System.out.println("0. Cancel");

            int bedChoice = menuManager.getIntInput("Select bed");

            if (bedChoice == 0) {
                return;
            }

            if (bedChoice < 1 || bedChoice > suitableBeds.size()) {
                menuManager.displayMessage("Invalid choice");
                return;
            }

            Bed selectedBed = suitableBeds.get(bedChoice - 1);

            // Create and add the patient
            Patient newPatient = careHomeService.addPatient(
                    firstName,
                    lastName,
                    dateOfBirth,
                    gender,
                    medicalCondition,
                    needsIsolation,
                    selectedBed
            );

            menuManager.displayMessage("Patient " + newPatient.getFullName() + " added successfully and assigned to " +
                    selectedBed.getRoom().getWard().getName() + ", Room " +
                    selectedBed.getRoom().getNumber() + ", " + selectedBed.getName());

        } catch (UnauthorizedActionException | NotRosteredException | IllegalArgumentException e) {
            menuManager.displayMessage("Error: " + e.getMessage());
        } catch (Exception e) {
            menuManager.displayMessage("An unexpected error occurred: " + e.getMessage());
        }
    }

    private void movePatient() {
        try {
            // Check if user is authorized
            if (!authService.isAuthorized("MOVE_PATIENT")) {
                menuManager.displayMessage("You are not authorized to move patients.");
                return;
            }

            System.out.println("\n===================================");
            System.out.println("           MOVE PATIENT           ");
            System.out.println("===================================");

            // Get all patients
            List<Patient> allPatients = careHomeService.getAllPatients();

            if (allPatients.isEmpty()) {
                menuManager.displayMessage("No patients found in the system.");
                return;
            }

            // Display all patients
            System.out.println("Select patient to move:");

            for (int i = 0; i < allPatients.size(); i++) {
                Patient patient = allPatients.get(i);
                System.out.println((i + 1) + ". " + patient.getFullName() + " (" + patient.getMedicalCondition() + ")");
            }

            System.out.println("0. Cancel");

            int patientChoice = menuManager.getIntInput("Enter choice");

            if (patientChoice == 0) {
                return;
            }

            if (patientChoice < 1 || patientChoice > allPatients.size()) {
                menuManager.displayMessage("Invalid choice");
                return;
            }

            Patient selectedPatient = allPatients.get(patientChoice - 1);

            // Find suitable beds for the patient
            List<Bed> suitableBeds = careHomeService.getSuitableVacantBeds(
                    selectedPatient.getGender(),
                    selectedPatient.isNeedsIsolation()
            );

            if (suitableBeds.isEmpty()) {
                menuManager.displayMessage("No suitable beds available for this patient.");
                return;
            }

            // Display available beds
            System.out.println("\n===================================");
            System.out.println("        AVAILABLE BEDS            ");
            System.out.println("===================================");

            for (int i = 0; i < suitableBeds.size(); i++) {
                Bed bed = suitableBeds.get(i);
                System.out.println((i + 1) + ". Ward: " + bed.getRoom().getWard().getName() +
                        ", Room: " + bed.getRoom().getNumber() +
                        ", Bed: " + bed.getName());
            }

            System.out.println("0. Cancel");

            int bedChoice = menuManager.getIntInput("Select new bed");

            if (bedChoice == 0) {
                return;
            }

            if (bedChoice < 1 || bedChoice > suitableBeds.size()) {
                menuManager.displayMessage("Invalid choice");
                return;
            }

            Bed selectedBed = suitableBeds.get(bedChoice - 1);

            // Move the patient
            careHomeService.movePatient(selectedPatient, selectedBed);

            menuManager.displayMessage("Patient " + selectedPatient.getFullName() + " moved successfully to " +
                    selectedBed.getRoom().getWard().getName() + ", Room " +
                    selectedBed.getRoom().getNumber() + ", " + selectedBed.getName());

        } catch (UnauthorizedActionException | NotRosteredException | IllegalArgumentException e) {
            menuManager.displayMessage("Error: " + e.getMessage());
        } catch (Exception e) {
            menuManager.displayMessage("An unexpected error occurred: " + e.getMessage());
        }
    }

    private void dischargePatient() {
        try {
            // Check if user is authorized
            if (!authService.isAuthorized("DISCHARGE_PATIENT")) {
                menuManager.displayMessage("You are not authorized to discharge patients.");
                return;
            }

            System.out.println("\n===================================");
            System.out.println("        DISCHARGE PATIENT         ");
            System.out.println("===================================");

            // Get all patients
            List<Patient> allPatients = careHomeService.getAllPatients();

            if (allPatients.isEmpty()) {
                menuManager.displayMessage("No patients found in the system.");
                return;
            }

            // Display all patients
            System.out.println("Select patient to discharge:");

            for (int i = 0; i < allPatients.size(); i++) {
                Patient patient = allPatients.get(i);
                System.out.println((i + 1) + ". " + patient.getFullName() + " (" + patient.getMedicalCondition() + ")");
            }

            System.out.println("0. Cancel");

            int patientChoice = menuManager.getIntInput("Enter choice");

            if (patientChoice == 0) {
                return;
            }

            if (patientChoice < 1 || patientChoice > allPatients.size()) {
                menuManager.displayMessage("Invalid choice");
                return;
            }

            Patient selectedPatient = allPatients.get(patientChoice - 1);

            // Confirm discharge
            System.out.println("\nAre you sure you want to discharge " + selectedPatient.getFullName() + "?");
            System.out.println("1. Yes");
            System.out.println("2. No");

            int confirmChoice = menuManager.getIntInput("Enter choice");

            if (confirmChoice != 1) {
                menuManager.displayMessage("Discharge cancelled.");
                return;
            }

            // Discharge the patient
            if (careHomeService.dischargePatient(selectedPatient)) {
                menuManager.displayMessage("Patient " + selectedPatient.getFullName() + " has been discharged successfully.");
            } else {
                menuManager.displayMessage("Failed to discharge patient.");
            }

        } catch (UnauthorizedActionException | NotRosteredException | IllegalArgumentException e) {
            menuManager.displayMessage("Error: " + e.getMessage());
        } catch (Exception e) {
            menuManager.displayMessage("An unexpected error occurred: " + e.getMessage());
        }
    }
}
