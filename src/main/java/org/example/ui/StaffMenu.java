package org.example.ui;

import org.example.exception.ComplianceException;
import org.example.service.CareHomeService;
import org.example.exception.NotRosteredException;
import org.example.exception.UnauthorizedActionException;
import org.example.model.*;
import org.example.service.AuthenticationService;
import org.example.service.ComplianceChecker;
import org.example.util.IdGenerator;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

// Add AuthenticationService as a field

/**
 * Staff management menu for the Care Home System
 */
public class StaffMenu implements Menu {
    private final MenuManager menuManager;
    private final CareHomeService careHomeService;
    private final AuthenticationService authService;

    /**
     * Constructor
     */
    public StaffMenu() {
        menuManager = MenuManager.getInstance();
        careHomeService = CareHomeService.getInstance();
        authService = AuthenticationService.getInstance();
    }

    @Override
    public void display() {
        System.out.println("\n===================================");
        System.out.println("        STAFF MANAGEMENT          ");
        System.out.println("===================================");
        System.out.println("1. View All Staff");
        System.out.println("2. Add Staff");
        System.out.println("3. Edit Staff");
        System.out.println("4. Manage Shifts");
        System.out.println("0. Back to Main Menu");
        System.out.println("===================================");
    }

    @Override
    public void handleInput(String input) {
        switch (input) {
            case "1":
                viewAllStaff();
                break;
            case "2":
                addStaff();
                break;
            case "3":
               editStaff();
                break;
            case "4":
                manageShifts();
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
        return "Staff Management";
    }

    /**
     * View all staff members
     */
    private void viewAllStaff() {
        System.out.println("\n===================================");
        System.out.println("           ALL STAFF              ");
        System.out.println("===================================");

        careHomeService.getAllStaff().forEach(staff -> {
            System.out.println("ID: " + staff.getId());
            System.out.println("Name: " + staff.getFullName());
            System.out.println("Role: " + staff.getClass().getSimpleName());
            System.out.println("-----------------------------------");
        });

        menuManager.displayMessage("Press Enter to continue...");
    }

    private void addStaff() {
        try {
            // Check if user is authorized
            if (!authService.isAuthorized("ADD_STAFF")) {
                menuManager.displayMessage("You are not authorized to add staff members.");
                return;
            }

            System.out.println("\n===================================");
            System.out.println("           ADD STAFF              ");
            System.out.println("===================================");

            // Select staff type
            System.out.println("Select staff type:");
            System.out.println("1. Doctor");
            System.out.println("2. Nurse");
            System.out.println("3. Manager");
            System.out.println("0. Cancel");

            int staffTypeChoice = menuManager.getIntInput("Enter choice");

            if (staffTypeChoice == 0) {
                return;
            }

            if (staffTypeChoice < 1 || staffTypeChoice > 3) {
                menuManager.displayMessage("Invalid choice");
                return;
            }

            // Get common staff details
            String firstName = menuManager.getInput("First Name");
            String lastName = menuManager.getInput("Last Name");

            LocalDate dateOfBirth = null;
            while (dateOfBirth == null) {
                try {
                    String dobString = menuManager.getInput("Date of Birth (YYYY-MM-DD)");
                    dateOfBirth = LocalDate.parse(dobString, DateTimeFormatter.ISO_LOCAL_DATE);
                } catch (DateTimeParseException e) {
                    System.out.println("Invalid date format. Please use YYYY-MM-DD.");
                }
            }

            System.out.println("Select gender:");
            System.out.println("1. Male");
            System.out.println("2. Female");

            int genderChoice = menuManager.getIntInput("Enter choice");

            if (genderChoice != 1 && genderChoice != 2) {
                menuManager.displayMessage("Invalid choice");
                return;
            }

            Gender gender = (genderChoice == 1) ? Gender.MALE : Gender.FEMALE;

            String username = menuManager.getInput("Username");
            String password = menuManager.getInput("Password");

            // Check if username already exists
            boolean usernameExists = careHomeService.getAllStaff().stream()
                    .anyMatch(s -> s.getUsername().equals(username));

            if (usernameExists) {
                menuManager.displayMessage("Username already exists. Please choose a different username.");
                return;
            }

            Staff newStaff = null;

            // Create specific staff type
            switch (staffTypeChoice) {
                case 1: // Doctor
                    String specialization = menuManager.getInput("Specialization");
                    newStaff = new Doctor(
                            IdGenerator.generateId("DOC"),
                            firstName,
                            lastName,
                            dateOfBirth,
                            gender,
                            username,
                            password,
                            specialization
                    );
                    break;
                case 2: // Nurse
                    String qualification = menuManager.getInput("Qualification");
                    newStaff = new Nurse(
                            IdGenerator.generateId("NUR"),
                            firstName,
                            lastName,
                            dateOfBirth,
                            gender,
                            username,
                            password,
                            qualification
                    );
                    break;
                case 3: // Manager
                    String department = menuManager.getInput("Department");
                    newStaff = new Manager(
                            IdGenerator.generateId("MGR"),
                            firstName,
                            lastName,
                            dateOfBirth,
                            gender,
                            username,
                            password,
                            department
                    );
                    break;
            }

            // Add the staff member
            if (newStaff != null && careHomeService.addStaff(newStaff)) {
                menuManager.displayMessage("Staff member added successfully.");

                // Register the user in the authentication service
                authService.registerUser(newStaff);
            } else {
                menuManager.displayMessage("Failed to add staff member.");
            }

        } catch (UnauthorizedActionException | NotRosteredException e) {
            menuManager.displayMessage("Error: " + e.getMessage());
        }
    }

    private void editStaff() {
        try {
            // Check if user is authorized
            if (!authService.isAuthorized("EDIT_STAFF")) {
                menuManager.displayMessage("You are not authorized to edit staff members.");
                return;
            }

            System.out.println("\n===================================");
            System.out.println("          EDIT STAFF              ");
            System.out.println("===================================");

            // List all staff members
            List<Staff> allStaff = careHomeService.getAllStaff();

            if (allStaff.isEmpty()) {
                menuManager.displayMessage("No staff members found.");
                return;
            }

            System.out.println("Select staff member to edit:");

            for (int i = 0; i < allStaff.size(); i++) {
                Staff staff = allStaff.get(i);
                System.out.println((i + 1) + ". " + staff.getFullName() + " (" + staff.getClass().getSimpleName() + ")");
            }

            System.out.println("0. Cancel");

            int staffChoice = menuManager.getIntInput("Enter choice");

            if (staffChoice == 0) {
                return;
            }

            if (staffChoice < 1 || staffChoice > allStaff.size()) {
                menuManager.displayMessage("Invalid choice");
                return;
            }

            Staff selectedStaff = allStaff.get(staffChoice - 1);

            // Display current details
            System.out.println("\nCurrent Details:");
            System.out.println("ID: " + selectedStaff.getId());
            System.out.println("Name: " + selectedStaff.getFullName());
            System.out.println("Date of Birth: " + selectedStaff.getDateOfBirth());
            System.out.println("Gender: " + selectedStaff.getGender());
            System.out.println("Username: " + selectedStaff.getUsername());

            if (selectedStaff instanceof Doctor) {
                System.out.println("Specialization: " + ((Doctor) selectedStaff).getSpecialization());
            } else if (selectedStaff instanceof Nurse) {
                System.out.println("Qualification: " + ((Nurse) selectedStaff).getQualification());
            } else if (selectedStaff instanceof Manager) {
                System.out.println("Department: " + ((Manager) selectedStaff).getDepartment());
            }

            System.out.println("\nEnter new details (leave blank to keep current value):");

            // Get updated details
            String firstName = menuManager.getInput("First Name (" + selectedStaff.getFirstName() + ")");
            if (!firstName.isEmpty()) {
                selectedStaff.setFirstName(firstName);
            }

            String lastName = menuManager.getInput("Last Name (" + selectedStaff.getLastName() + ")");
            if (!lastName.isEmpty()) {
                selectedStaff.setLastName(lastName);
            }

            String dobString = menuManager.getInput("Date of Birth (" + selectedStaff.getDateOfBirth() + ") (YYYY-MM-DD)");
            if (!dobString.isEmpty()) {
                try {
                    LocalDate dateOfBirth = LocalDate.parse(dobString, DateTimeFormatter.ISO_LOCAL_DATE);
                    selectedStaff.setDateOfBirth(dateOfBirth);
                } catch (DateTimeParseException e) {
                    System.out.println("Invalid date format. Date of birth not updated.");
                }
            }

            String password = menuManager.getInput("New Password (leave blank to keep current)");
            if (!password.isEmpty()) {
                selectedStaff.setPassword(password);
            }

            // Update specific fields based on staff type
            if (selectedStaff instanceof Doctor) {
                Doctor doctor = (Doctor) selectedStaff;
                String specialization = menuManager.getInput("Specialization (" + doctor.getSpecialization() + ")");
                if (!specialization.isEmpty()) {
                    doctor.setSpecialization(specialization);
                }
            } else if (selectedStaff instanceof Nurse) {
                Nurse nurse = (Nurse) selectedStaff;
                String qualification = menuManager.getInput("Qualification (" + nurse.getQualification() + ")");
                if (!qualification.isEmpty()) {
                    nurse.setQualification(qualification);
                }
            } else if (selectedStaff instanceof Manager) {
                Manager manager = (Manager) selectedStaff;
                String department = menuManager.getInput("Department (" + manager.getDepartment() + ")");
                if (!department.isEmpty()) {
                    manager.setDepartment(department);
                }
            }

            // Update the staff member
            if (careHomeService.updateStaff(selectedStaff)) {
                menuManager.displayMessage("Staff member updated successfully.");
            } else {
                menuManager.displayMessage("Failed to update staff member.");
            }

        } catch (UnauthorizedActionException | NotRosteredException e) {
            menuManager.displayMessage("Error: " + e.getMessage());
        }
    }

    private void manageShifts() {
        try {
            // Check if user is authorized
            if (!authService.isAuthorized("EDIT_STAFF")) {
                menuManager.displayMessage("You are not authorized to manage shifts.");
                return;
            }

            System.out.println("\n===================================");
            System.out.println("         MANAGE SHIFTS            ");
            System.out.println("===================================");
            System.out.println("1. View All Shifts");
            System.out.println("2. Assign Nurse Shift");
            System.out.println("3. Assign Doctor Shift");
            System.out.println("4. Remove Shift");
            System.out.println("5. Check Compliance");
            System.out.println("0. Back");

            int choice = menuManager.getIntInput("Enter choice");

            switch (choice) {
                case 1:
                    viewAllShifts();
                    break;
                case 2:
                    assignNurseShift();
                    break;
                case 3:
                    assignDoctorShift();
                    break;
                case 4:
                    removeShift();
                    break;
                case 5:
                    checkCompliance();
                    break;
                case 0:
                    return;
                default:
                    menuManager.displayMessage("Invalid choice");
                    break;
            }
        } catch (Exception e) {
            menuManager.displayMessage("Error: " + e.getMessage());
        }
    }

    /**
     * View all staff shifts
     */
    private void viewAllShifts() {
        System.out.println("\n===================================");
        System.out.println("           ALL SHIFTS             ");
        System.out.println("===================================");

        List<Staff> allStaff = careHomeService.getAllStaff();

        for (Staff staff : allStaff) {
            System.out.println(staff.getFullName() + " (" + staff.getClass().getSimpleName() + "):");

            if (staff.getShifts().isEmpty()) {
                System.out.println("  No shifts assigned");
            } else {
                for (Shift shift : staff.getShifts()) {
                    System.out.println("  " + shift.getDayOfWeek() + ": " +
                            formatTime(shift.getStartTime()) + " - " +
                            formatTime(shift.getEndTime()));
                }
            }

            System.out.println("-----------------------------------");
        }

        menuManager.displayMessage("Press Enter to continue...");
    }

    private String formatTime(LocalTime time) {
        return time.format(DateTimeFormatter.ofPattern("h:mm a"));
    }

    private void assignDoctorShift() {
        try {
            // Get all doctors
            List<Doctor> doctors = careHomeService.getAllStaff().stream()
                    .filter(s -> s instanceof Doctor)
                    .map(s -> (Doctor) s)
                    .collect(Collectors.toList());

            if (doctors.isEmpty()) {
                menuManager.displayMessage("No doctors found in the system.");
                return;
            }

            System.out.println("\nSelect doctor:");

            for (int i = 0; i < doctors.size(); i++) {
                System.out.println((i + 1) + ". " + doctors.get(i).getFullName());
            }

            System.out.println("0. Cancel");

            int doctorChoice = menuManager.getIntInput("Enter choice");

            if (doctorChoice == 0) {
                return;
            }

            if (doctorChoice < 1 || doctorChoice > doctors.size()) {
                menuManager.displayMessage("Invalid choice");
                return;
            }

            Doctor selectedDoctor = doctors.get(doctorChoice - 1);

            // Select day of week
            System.out.println("\nSelect day of week:");
            DayOfWeek[] days = DayOfWeek.values();

            for (int i = 0; i < days.length; i++) {
                System.out.println((i + 1) + ". " + days[i]);
            }

            int dayChoice = menuManager.getIntInput("Enter choice");

            if (dayChoice < 1 || dayChoice > days.length) {
                menuManager.displayMessage("Invalid choice");
                return;
            }

            DayOfWeek selectedDay = days[dayChoice - 1];

            // Check if the doctor already has a shift on this day
            for (Shift existingShift : selectedDoctor.getShifts()) {
                if (existingShift.getDayOfWeek() == selectedDay) {
                    menuManager.displayMessage("This doctor already has a shift on " + selectedDay + ".");
                    return;
                }
            }

            // Get shift time
            System.out.println("\nEnter shift start hour (0-23):");
            int startHour = menuManager.getIntInput("Start hour");

            if (startHour < 0 || startHour > 23) {
                menuManager.displayMessage("Invalid hour");
                return;
            }

            // Doctor shifts are 1 hour
            LocalTime startTime = LocalTime.of(startHour, 0);
            LocalTime endTime = LocalTime.of((startHour + 1) % 24, 0);

            // Create and add the shift
            Shift newShift = new Shift(selectedDay, startTime, endTime);
            selectedDoctor.addShift(newShift);

            // Update the staff member
            if (careHomeService.updateStaff(selectedDoctor)) {
                menuManager.displayMessage("Shift assigned successfully.");
            } else {
                menuManager.displayMessage("Failed to assign shift.");
            }

        } catch (UnauthorizedActionException | NotRosteredException e) {
            menuManager.displayMessage("Error: " + e.getMessage());
        }
    }

    /**
     * Remove a shift from a staff member
     */
    private void removeShift() {
        try {
            // List all staff members
            List<Staff> allStaff = careHomeService.getAllStaff();

            if (allStaff.isEmpty()) {
                menuManager.displayMessage("No staff members found.");
                return;
            }

            System.out.println("\nSelect staff member:");

            for (int i = 0; i < allStaff.size(); i++) {
                Staff staff = allStaff.get(i);
                System.out.println((i + 1) + ". " + staff.getFullName() + " (" + staff.getClass().getSimpleName() + ")");
            }

            System.out.println("0. Cancel");

            int staffChoice = menuManager.getIntInput("Enter choice");

            if (staffChoice == 0) {
                return;
            }

            if (staffChoice < 1 || staffChoice > allStaff.size()) {
                menuManager.displayMessage("Invalid choice");
                return;
            }

            Staff selectedStaff = allStaff.get(staffChoice - 1);

            if (selectedStaff.getShifts().isEmpty()) {
                menuManager.displayMessage("This staff member has no shifts assigned.");
                return;
            }

            // List all shifts for the selected staff member
            System.out.println("\nSelect shift to remove:");

            Shift[] shifts = selectedStaff.getShifts().toArray(new Shift[0]);

            for (int i = 0; i < shifts.length; i++) {
                Shift shift = shifts[i];
                System.out.println((i + 1) + ". " + shift.getDayOfWeek() + ": " +
                        formatTime(shift.getStartTime()) + " - " +
                        formatTime(shift.getEndTime()));
            }

            System.out.println("0. Cancel");

            int shiftChoice = menuManager.getIntInput("Enter choice");

            if (shiftChoice == 0) {
                return;
            }

            if (shiftChoice < 1 || shiftChoice > shifts.length) {
                menuManager.displayMessage("Invalid choice");
                return;
            }

            Shift selectedShift = shifts[shiftChoice - 1];

            // Remove the shift
            selectedStaff.removeShift(selectedShift);

            // Update the staff member
            if (careHomeService.updateStaff(selectedStaff)) {
                menuManager.displayMessage("Shift removed successfully.");
            } else {
                menuManager.displayMessage("Failed to remove shift.");
            }

        } catch (UnauthorizedActionException | NotRosteredException e) {
            menuManager.displayMessage("Error: " + e.getMessage());
        }
    }

    private void assignNurseShift() {
        try {
            // Get all nurses
            List<Nurse> nurses = careHomeService.getAllStaff().stream()
                    .filter(s -> s instanceof Nurse)
                    .map(s -> (Nurse) s)
                    .collect(Collectors.toList());

            if (nurses.isEmpty()) {
                menuManager.displayMessage("No nurses found in the system.");
                return;
            }

            System.out.println("\nSelect nurse:");

            for (int i = 0; i < nurses.size(); i++) {
                System.out.println((i + 1) + ". " + nurses.get(i).getFullName());
            }

            System.out.println("0. Cancel");

            int nurseChoice = menuManager.getIntInput("Enter choice");

            if (nurseChoice == 0) {
                return;
            }

            if (nurseChoice < 1 || nurseChoice > nurses.size()) {
                menuManager.displayMessage("Invalid choice");
                return;
            }

            Nurse selectedNurse = nurses.get(nurseChoice - 1);

            // Select day of week
            System.out.println("\nSelect day of week:");
            DayOfWeek[] days = DayOfWeek.values();

            for (int i = 0; i < days.length; i++) {
                System.out.println((i + 1) + ". " + days[i]);
            }

            int dayChoice = menuManager.getIntInput("Enter choice");

            if (dayChoice < 1 || dayChoice > days.length) {
                menuManager.displayMessage("Invalid choice");
                return;
            }

            DayOfWeek selectedDay = days[dayChoice - 1];

            // Select shift type
            System.out.println("\nSelect shift:");
            System.out.println("1. Morning Shift (8:00 AM - 4:00 PM)");
            System.out.println("2. Afternoon Shift (2:00 PM - 10:00 PM)");

            int shiftChoice = menuManager.getIntInput("Enter choice");

            if (shiftChoice != 1 && shiftChoice != 2) {
                menuManager.displayMessage("Invalid choice");
                return;
            }

            LocalTime startTime, endTime;

            if (shiftChoice == 1) {
                startTime = LocalTime.of(8, 0);
                endTime = LocalTime.of(16, 0);
            } else {
                startTime = LocalTime.of(14, 0);
                endTime = LocalTime.of(22, 0);
            }

            // Check if the nurse already has a shift on this day
            int totalHoursForDay = 0;

            for (Shift existingShift : selectedNurse.getShifts()) {
                if (existingShift.getDayOfWeek() == selectedDay) {
                    // Calculate existing shift hours
                    int existingHours = existingShift.getEndTime().getHour() - existingShift.getStartTime().getHour();
                    totalHoursForDay += existingHours;

                    // Check if this is the same shift
                    if (existingShift.getStartTime().equals(startTime) && existingShift.getEndTime().equals(endTime)) {
                        menuManager.displayMessage("This nurse is already assigned to this shift.");
                        return;
                    }
                }
            }

            // Check if adding this shift would exceed 8 hours
            if (totalHoursForDay + 8 > 8) {
                menuManager.displayMessage("Cannot assign this shift. It would exceed the 8-hour limit for this day.");
                return;
            }

            // Create and add the shift
            Shift newShift = new Shift(selectedDay, startTime, endTime);
            selectedNurse.addShift(newShift);

            // Update the staff member
            if (careHomeService.updateStaff(selectedNurse)) {
                menuManager.displayMessage("Shift assigned successfully.");
            } else {
                menuManager.displayMessage("Failed to assign shift.");
            }

        } catch (UnauthorizedActionException | NotRosteredException e) {
            menuManager.displayMessage("Error: " + e.getMessage());
        }
    }

    /**
     * Check compliance with staffing regulations
     */
    private void checkCompliance() {
        try {
            System.out.println("\n===================================");
            System.out.println("       COMPLIANCE CHECK           ");
            System.out.println("===================================");

            try {
                careHomeService.checkCompliance();
                menuManager.displayMessage("All compliance checks passed successfully!");
            } catch (ComplianceException e) {
                menuManager.displayMessage("Compliance check failed: " + e.getMessage());
            }

        } catch (Exception e) {
            menuManager.displayMessage("Error: " + e.getMessage());
        }
    }
}
