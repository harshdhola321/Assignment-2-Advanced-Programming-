package org.example.util;

import org.example.exception.ComplianceException;
import org.example.model.*;
import org.example.service.CareHomeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Scanner;

/**
 * Utility class to demonstrate compliance checking functionality
 */
public class ComplianceCheckUtility {
    private static final Logger logger = LoggerFactory.getLogger(ComplianceCheckUtility.class);
    
    public static void main(String[] args) {
        System.out.println("=== RMIT Care Home Compliance Check Utility ===");
        
        // Get the CareHomeService instance
        CareHomeService careHomeService = CareHomeService.getInstance();
        
        // Display menu
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;
        
        while (!exit) {
            System.out.println("\nOptions:");
            System.out.println("1. Run compliance check");
            System.out.println("2. Add a nurse");
            System.out.println("3. Add a doctor");
            System.out.println("4. View all staff");
            System.out.println("5. Modify staff shifts");
            System.out.println("6. Exit");
            System.out.print("Enter your choice: ");
            
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline
            
            switch (choice) {
                case 1:
                    runComplianceCheck(careHomeService);
                    break;
                case 2:
                    addNurse(careHomeService, scanner);
                    break;
                case 3:
                    addDoctor(careHomeService, scanner);
                    break;
                case 4:
                    viewAllStaff(careHomeService);
                    break;
                case 5:
                    modifyShifts(careHomeService, scanner);
                    break;
                case 6:
                    exit = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
        
        System.out.println("Exiting. Thank you!");
        
        // Save data before exiting
        careHomeService.saveData();
    }
    
    private static void runComplianceCheck(CareHomeService careHomeService) {
        System.out.println("\n=== Running Compliance Check ===");
        
        try {
            careHomeService.checkCompliance();
            System.out.println("COMPLIANCE CHECK PASSED: All staffing regulations are met.");
        } catch (ComplianceException e) {
            System.out.println("COMPLIANCE CHECK FAILED: " + e.getMessage());
            logger.error("Compliance check failed", e);
        }
    }
    
    private static void addNurse(CareHomeService careHomeService, Scanner scanner) {
        System.out.println("\n=== Add a New Nurse ===");
        
        System.out.print("First Name: ");
        String firstName = scanner.nextLine();
        
        System.out.print("Last Name: ");
        String lastName = scanner.nextLine();
        
        System.out.print("Username: ");
        String username = scanner.nextLine();
        
        System.out.print("Password: ");
        String password = scanner.nextLine();
        
        System.out.print("Qualification: ");
        String qualification = scanner.nextLine();
        
        System.out.print("Gender (M/F): ");
        String genderInput = scanner.nextLine();
        Gender gender = genderInput.toUpperCase().startsWith("M") ? Gender.MALE : Gender.FEMALE;
        
        // Create the nurse
        Nurse nurse = new Nurse(
            IdGenerator.generateId("STAFF"),
            firstName,
            lastName,
            LocalDate.now().minusYears(30), // Default age
            gender,
            username,
            password,
            qualification
        );
        
        // Ask for shift type
        System.out.println("Assign shifts:");
        System.out.println("1. Morning shifts (8am-4pm)");
        System.out.println("2. Afternoon shifts (2pm-10pm)");
        System.out.print("Enter your choice: ");
        int shiftType = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        
        // Assign shifts
        ShiftManager.assignStandardShiftsToNurse(nurse, shiftType);
        
        // Add to staff list
        careHomeService.getAllStaff().add(nurse);
        
        System.out.println("Nurse added successfully: " + nurse.getFullName());
    }
    
    private static void addDoctor(CareHomeService careHomeService, Scanner scanner) {
        System.out.println("\n=== Add a New Doctor ===");
        
        System.out.print("First Name: ");
        String firstName = scanner.nextLine();
        
        System.out.print("Last Name: ");
        String lastName = scanner.nextLine();
        
        System.out.print("Username: ");
        String username = scanner.nextLine();
        
        System.out.print("Password: ");
        String password = scanner.nextLine();
        
        System.out.print("Specialization: ");
        String specialization = scanner.nextLine();
        
        System.out.print("Gender (M/F): ");
        String genderInput = scanner.nextLine();
        Gender gender = genderInput.toUpperCase().startsWith("M") ? Gender.MALE : Gender.FEMALE;
        
        // Create the doctor
        Doctor doctor = new Doctor(
            IdGenerator.generateId("STAFF"),
            firstName,
            lastName,
            LocalDate.now().minusYears(40), // Default age
            gender,
            username,
            password,
            specialization
        );
        
        // Assign standard shifts
        ShiftManager.assignStandardShiftsToDoctor(doctor);
        
        // Add to staff list
        careHomeService.getAllStaff().add(doctor);
        
        System.out.println("Doctor added successfully: " + doctor.getFullName());
    }
    
    private static void viewAllStaff(CareHomeService careHomeService) {
        System.out.println("\n=== All Staff Members ===");
        
        if (careHomeService.getAllStaff().isEmpty()) {
            System.out.println("No staff members found.");
            return;
        }
        
        int index = 1;
        for (Staff staff : careHomeService.getAllStaff()) {
            System.out.println(index + ". " + staff.getFullName() + " (" + staff.getClass().getSimpleName() + ")");
            
            // Display shifts
            if (staff.getShifts().isEmpty()) {
                System.out.println("   No shifts assigned");
            } else {
                System.out.println("   Shifts:");
                for (DayOfWeek day : DayOfWeek.values()) {
                    int hours = ShiftManager.getTotalHoursForDay(staff, day);
                    System.out.println("   - " + day + ": " + hours + " hours");
                }
            }
            
            index++;
        }
    }
    
    private static void modifyShifts(CareHomeService careHomeService, Scanner scanner) {
        System.out.println("\n=== Modify Staff Shifts ===");
        
        // Display all staff
        viewAllStaff(careHomeService);
        
        if (careHomeService.getAllStaff().isEmpty()) {
            return;
        }
        
        System.out.print("Enter staff number to modify: ");
        int staffIndex = scanner.nextInt() - 1;
        scanner.nextLine(); // Consume newline
        
        if (staffIndex < 0 || staffIndex >= careHomeService.getAllStaff().size()) {
            System.out.println("Invalid staff number.");
            return;
        }
        
        Staff staff = careHomeService.getAllStaff().get(staffIndex);
        
        System.out.println("Modifying shifts for: " + staff.getFullName());
        
        if (staff instanceof Nurse) {
            System.out.println("1. Assign morning shifts (8am-4pm)");
            System.out.println("2. Assign afternoon shifts (2pm-10pm)");
            System.out.println("3. Clear all shifts");
            System.out.print("Enter your choice: ");
            
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline
            
            switch (choice) {
                case 1:
                    ShiftManager.assignStandardShiftsToNurse((Nurse) staff, 1);
                    System.out.println("Morning shifts assigned.");
                    break;
                case 2:
                    ShiftManager.assignStandardShiftsToNurse((Nurse) staff, 2);
                    System.out.println("Afternoon shifts assigned.");
                    break;
                case 3:
                    staff.getShifts().clear();
                    System.out.println("All shifts cleared.");
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        } else if (staff instanceof Doctor) {
            System.out.println("1. Assign standard shifts (1 hour per day)");
            System.out.println("2. Clear all shifts");
            System.out.print("Enter your choice: ");
            
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline
            
            switch (choice) {
                case 1:
                    ShiftManager.assignStandardShiftsToDoctor((Doctor) staff);
                    System.out.println("Standard shifts assigned.");
                    break;
                case 2:
                    staff.getShifts().clear();
                    System.out.println("All shifts cleared.");
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        } else {
            System.out.println("Shift modification not supported for this staff type.");
        }
    }
}
