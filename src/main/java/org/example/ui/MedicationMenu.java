package org.example.ui;

import org.example.exception.NotRosteredException;
import org.example.exception.UnauthorizedActionException;
import org.example.model.*;
import org.example.service.AuthenticationService;
import org.example.service.CareHomeService;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Medication management menu for the Care Home System
 */
public class MedicationMenu implements Menu {
    private final MenuManager menuManager;
    private final CareHomeService careHomeService;
    private final AuthenticationService authService;

    /**
     * Constructor
     */
    public MedicationMenu() {
        menuManager = MenuManager.getInstance();
        careHomeService = CareHomeService.getInstance();
        authService = AuthenticationService.getInstance();
    }

    @Override
    public void display() {
        System.out.println("\n===================================");
        System.out.println("     MEDICATION MANAGEMENT        ");
        System.out.println("===================================");
        System.out.println("1. View Patient Prescriptions");
        System.out.println("2. Add Prescription");
        System.out.println("3. Add Medication to Prescription");
        System.out.println("4. Administer Medication");
        System.out.println("0. Back to Main Menu");
        System.out.println("===================================");
    }

    @Override
    public void handleInput(String input) {
        switch (input) {
            case "1":
                viewPatientPrescriptions();
                break;
            case "2":
                addPrescription();
                break;
            case "3":
                addMedicationToPrescription();
                break;
            case "4":
                // Placeholder for administer medication functionality
                menuManager.displayMessage("Administer Medication functionality will be implemented soon.");
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
        return "Medication Management";
    }

    private void viewPatientPrescriptions() {
        System.out.println("\n===================================");
        System.out.println("     SELECT PATIENT               ");
        System.out.println("===================================");

        // List all patients
        int index = 1;
        for (Patient patient : careHomeService.getAllPatients()) {
            System.out.println(index + ". " + patient.getFullName() + " (" + patient.getId() + ")");
            index++;
        }

        System.out.println("0. Cancel");

        int choice = menuManager.getIntInput("Enter patient number");

        if (choice == 0) {
            return;
        }

        if (choice < 1 || choice > careHomeService.getAllPatients().size()) {
            menuManager.displayMessage("Invalid choice");
            return;
        }

        Patient selectedPatient = careHomeService.getAllPatients().get(choice - 1);

        System.out.println("\n===================================");
        System.out.println("PRESCRIPTIONS FOR " + selectedPatient.getFullName());
        System.out.println("===================================");

        if (selectedPatient.getPrescriptions().isEmpty()) {
            System.out.println("No prescriptions found for this patient.");
        } else {
            for (Prescription prescription : selectedPatient.getPrescriptions()) {
                System.out.println("ID: " + prescription.getId());
//                System.out.println("Date: " + prescription.get());
                System.out.println("Doctor: " + prescription.getDoctor().getFullName());
                System.out.println("Notes: " + prescription.getNotes());
                System.out.println("Medications:");

                prescription.getMedications().forEach(medication -> {
                    System.out.println("  - " + medication.getName() + " (" + medication.getDosage() + ")");
                    System.out.println("    Instructions: " + medication.getInstructions());
                    System.out.println("    Administration Times: " + medication.getAdministrationTimes());
                });

                System.out.println("-----------------------------------");
            }
        }

        menuManager.displayMessage("Press Enter to continue...");
    }

    private void addPrescription() {
        try {
            // Check if the current user is a doctor
            if (!(authService.getCurrentUser() instanceof Doctor)) {
                menuManager.displayMessage("Only doctors can add prescriptions.");
                return;
            }

            System.out.println("\n===================================");
            System.out.println("        ADD PRESCRIPTION          ");
            System.out.println("===================================");

            // Get the current doctor
            Doctor currentDoctor = (Doctor) authService.getCurrentUser();

            // Select a patient
            System.out.println("Select patient:");

            List<Patient> patients = careHomeService.getAllPatients();

            if (patients.isEmpty()) {
                menuManager.displayMessage("No patients found in the system.");
                return;
            }

            for (int i = 0; i < patients.size(); i++) {
                Patient patient = patients.get(i);
                System.out.println((i + 1) + ". " + patient.getFullName() + " (" + patient.getMedicalCondition() + ")");
            }

            System.out.println("0. Cancel");

            int patientChoice = menuManager.getIntInput("Enter choice");

            if (patientChoice == 0) {
                return;
            }

            if (patientChoice < 1 || patientChoice > patients.size()) {
                menuManager.displayMessage("Invalid choice");
                return;
            }

            Patient selectedPatient = patients.get(patientChoice - 1);

            // Get prescription notes
            String notes = menuManager.getInput("Prescription Notes");

            // Create the prescription
            Prescription prescription = careHomeService.addPrescription(selectedPatient, notes);

            // Ask if the user wants to add medications now
            System.out.println("\nDo you want to add medications to this prescription now?");
            System.out.println("1. Yes");
            System.out.println("2. No");

            int addMedsChoice = menuManager.getIntInput("Enter choice");

            if (addMedsChoice == 1) {
                boolean addingMedications = true;

                while (addingMedications) {
                    // Get medication details
                    String name = menuManager.getInput("Medication Name");
                    String dosage = menuManager.getInput("Dosage (e.g., 10mg)");
                    String instructions = menuManager.getInput("Instructions (e.g., Take with food)");

                    // Add the medication to the prescription
                    Medication medication = careHomeService.addMedicationToPrescription(
                            prescription, name, dosage, instructions);

                    // Add administration times
                    boolean addingTimes = true;

                    System.out.println("\nAdd administration times for " + name);

                    while (addingTimes) {
                        try {
                            System.out.println("Enter time (HH:MM) or leave blank to finish");
                            String timeStr = menuManager.getInput("Time (HH:MM)");

                            if (timeStr.isEmpty()) {
                                addingTimes = false;
                            } else {
                                String[] parts = timeStr.split(":");
                                if (parts.length != 2) {
                                    System.out.println("Invalid time format. Please use HH:MM.");
                                    continue;
                                }

                                int hour = Integer.parseInt(parts[0]);
                                int minute = Integer.parseInt(parts[1]);

                                if (hour < 0 || hour > 23 || minute < 0 || minute > 59) {
                                    System.out.println("Invalid time. Hours must be 0-23, minutes must be 0-59.");
                                    continue;
                                }

                                LocalTime time = LocalTime.of(hour, minute);
                                careHomeService.addAdministrationTimeToMedication(medication, time);

                                System.out.println("Time " + time + " added.");
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid time format. Please use HH:MM.");
                        }
                    }

                    System.out.println("\nDo you want to add another medication?");
                    System.out.println("1. Yes");
                    System.out.println("2. No");

                    int anotherMedChoice = menuManager.getIntInput("Enter choice");

                    if (anotherMedChoice != 1) {
                        addingMedications = false;
                    }
                }
            }

            menuManager.displayMessage("Prescription added successfully for " + selectedPatient.getFullName());

        } catch (UnauthorizedActionException | NotRosteredException e) {
            menuManager.displayMessage("Error: " + e.getMessage());
        } catch (Exception e) {
            menuManager.displayMessage("An unexpected error occurred: " + e.getMessage());
        }
    }

    private void addMedicationToPrescription() {
        try {
            // Check if the current user is a doctor
            if (!(authService.getCurrentUser() instanceof Doctor)) {
                menuManager.displayMessage("Only doctors can add medications to prescriptions.");
                return;
            }

            System.out.println("\n===================================");
            System.out.println("   ADD MEDICATION TO PRESCRIPTION  ");
            System.out.println("===================================");

            // Get the current doctor
            Doctor currentDoctor = (Doctor) authService.getCurrentUser();

            // First select a patient
            System.out.println("Select patient:");

            List<Patient> patients = careHomeService.getAllPatients();

            if (patients.isEmpty()) {
                menuManager.displayMessage("No patients found in the system.");
                return;
            }

            for (int i = 0; i < patients.size(); i++) {
                Patient patient = patients.get(i);
                System.out.println((i + 1) + ". " + patient.getFullName() + " (" + patient.getMedicalCondition() + ")");
            }

            System.out.println("0. Cancel");

            int patientChoice = menuManager.getIntInput("Enter choice");

            if (patientChoice == 0) {
                return;
            }

            if (patientChoice < 1 || patientChoice > patients.size()) {
                menuManager.displayMessage("Invalid choice");
                return;
            }

            Patient selectedPatient = patients.get(patientChoice - 1);

            // Check if the patient has any prescriptions
            if (selectedPatient.getPrescriptions().isEmpty()) {
                menuManager.displayMessage("This patient has no prescriptions. Please create a prescription first.");
                return;
            }

            // Select a prescription
            System.out.println("\nSelect prescription:");

            List<Prescription> prescriptions = selectedPatient.getPrescriptions();

            for (int i = 0; i < prescriptions.size(); i++) {
                Prescription prescription = prescriptions.get(i);
                System.out.println((i + 1) + ". " + prescription.getId() + " - Dr. " +
                        prescription.getDoctor().getLastName() + " (" +
                        prescription.getPrescriptionDateTime().toLocalDate() + ")");
            }

            System.out.println("0. Cancel");

            int prescriptionChoice = menuManager.getIntInput("Enter choice");

            if (prescriptionChoice == 0) {
                return;
            }

            if (prescriptionChoice < 1 || prescriptionChoice > prescriptions.size()) {
                menuManager.displayMessage("Invalid choice");
                return;
            }

            Prescription selectedPrescription = prescriptions.get(prescriptionChoice - 1);

            // Check if the current doctor is the one who created the prescription
            if (!selectedPrescription.getDoctor().getId().equals(currentDoctor.getId())) {
                menuManager.displayMessage("You can only add medications to prescriptions that you created.");
                return;
            }

            // Get medication details
            String name = menuManager.getInput("Medication Name");
            String dosage = menuManager.getInput("Dosage (e.g., 10mg)");
            String instructions = menuManager.getInput("Instructions (e.g., Take with food)");

            // Add the medication to the prescription
            Medication medication = careHomeService.addMedicationToPrescription(
                    selectedPrescription, name, dosage, instructions);

            // Add administration times
            boolean addingTimes = true;

            System.out.println("\nAdd administration times for " + name);

            while (addingTimes) {
                try {
                    System.out.println("Enter time (HH:MM) or leave blank to finish");
                    String timeStr = menuManager.getInput("Time (HH:MM)");

                    if (timeStr.isEmpty()) {
                        addingTimes = false;
                    } else {
                        String[] parts = timeStr.split(":");
                        if (parts.length != 2) {
                            System.out.println("Invalid time format. Please use HH:MM.");
                            continue;
                        }

                        int hour = Integer.parseInt(parts[0]);
                        int minute = Integer.parseInt(parts[1]);

                        if (hour < 0 || hour > 23 || minute < 0 || minute > 59) {
                            System.out.println("Invalid time. Hours must be 0-23, minutes must be 0-59.");
                            continue;
                        }

                        LocalTime time = LocalTime.of(hour, minute);
                        careHomeService.addAdministrationTimeToMedication(medication, time);

                        System.out.println("Time " + time + " added.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid time format. Please use HH:MM.");
                }
            }

            menuManager.displayMessage("Medication " + name + " added successfully to the prescription.");

        } catch (UnauthorizedActionException | NotRosteredException e) {
            menuManager.displayMessage("Error: " + e.getMessage());
        } catch (Exception e) {
            menuManager.displayMessage("An unexpected error occurred: " + e.getMessage());
        }
    }

    private void administerMedication() {
        try {
            // Check if the current user is a nurse
            if (!(authService.getCurrentUser() instanceof Nurse)) {
                menuManager.displayMessage("Only nurses can administer medications.");
                return;
            }

            System.out.println("\n===================================");
            System.out.println("      ADMINISTER MEDICATION       ");
            System.out.println("===================================");

            // Get the current nurse
            Nurse currentNurse = (Nurse) authService.getCurrentUser();

            // First select a patient
            System.out.println("Select patient:");

            List<Patient> patients = careHomeService.getAllPatients();

            if (patients.isEmpty()) {
                menuManager.displayMessage("No patients found in the system.");
                return;
            }

            for (int i = 0; i < patients.size(); i++) {
                Patient patient = patients.get(i);
                System.out.println((i + 1) + ". " + patient.getFullName() + " (" + patient.getMedicalCondition() + ")");
            }

            System.out.println("0. Cancel");

            int patientChoice = menuManager.getIntInput("Enter choice");

            if (patientChoice == 0) {
                return;
            }

            if (patientChoice < 1 || patientChoice > patients.size()) {
                menuManager.displayMessage("Invalid choice");
                return;
            }

            Patient selectedPatient = patients.get(patientChoice - 1);

            // Check if the patient has any prescriptions
            if (selectedPatient.getPrescriptions().isEmpty()) {
                menuManager.displayMessage("This patient has no prescriptions.");
                return;
            }

            // Collect all medications from all prescriptions
            List<Medication> allMedications = new ArrayList<>();
            Map<Medication, Prescription> medicationToPrescription = new HashMap<>();

            for (Prescription prescription : selectedPatient.getPrescriptions()) {
                for (Medication medication : prescription.getMedications()) {
                    allMedications.add(medication);
                    medicationToPrescription.put(medication, prescription);
                }
            }

            if (allMedications.isEmpty()) {
                menuManager.displayMessage("This patient has no medications prescribed.");
                return;
            }

            // Select a medication to administer
            System.out.println("\nSelect medication to administer:");

            for (int i = 0; i < allMedications.size(); i++) {
                Medication medication = allMedications.get(i);
                Prescription prescription = medicationToPrescription.get(medication);
                System.out.println((i + 1) + ". " + medication.getName() + " (" + medication.getDosage() + ") - " +
                        "Prescribed by Dr. " + prescription.getDoctor().getLastName());
                System.out.println("   Instructions: " + medication.getInstructions());
                System.out.println("   Administration Times: " + formatAdministrationTimes(medication.getAdministrationTimes()));
            }

            System.out.println("0. Cancel");

            int medicationChoice = menuManager.getIntInput("Enter choice");

            if (medicationChoice == 0) {
                return;
            }

            if (medicationChoice < 1 || medicationChoice > allMedications.size()) {
                menuManager.displayMessage("Invalid choice");
                return;
            }

            Medication selectedMedication = allMedications.get(medicationChoice - 1);

            // Get administration notes
            String notes = menuManager.getInput("Administration Notes (optional)");

            // Administer the medication
            MedicationAdministration administration = careHomeService.administerMedication(
                    selectedMedication, selectedPatient, notes);

            menuManager.displayMessage("Medication " + selectedMedication.getName() +
                    " administered successfully to " + selectedPatient.getFullName() + ".");

        } catch (UnauthorizedActionException | NotRosteredException e) {
            menuManager.displayMessage("Error: " + e.getMessage());
        } catch (Exception e) {
            menuManager.displayMessage("An unexpected error occurred: " + e.getMessage());
        }
    }

    /**
     * Format a list of administration times for display
     */
    private String formatAdministrationTimes(List<LocalTime> times) {
        if (times.isEmpty()) {
            return "No times specified";
        }

        StringBuilder sb = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a");

        for (int i = 0; i < times.size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(times.get(i).format(formatter));
        }

        return sb.toString();
    }
}
