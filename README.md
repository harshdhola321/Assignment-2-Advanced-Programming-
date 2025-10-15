# RMIT Care Home System

A comprehensive care home management system designed to handle staff scheduling, patient management, medication administration, and ward/bed allocation while ensuring compliance with healthcare regulations.

## Overview

The RMIT Care Home System is a Java-based application that provides healthcare facilities with tools to manage:

- Staff scheduling and shift allocation
- Patient admission, transfer, and discharge
- Medication prescriptions and administration
- Ward and bed management
- Compliance with healthcare staffing regulations

The system supports two interfaces:
1. A text-based console interface for basic operations
2. A JavaFX-based graphical interface for advanced features

## Features

- **Staff Management**
  - Registration of doctors, nurses, and managers
  - Shift scheduling with compliance checking
  - Role-based access control

- **Patient Management**
  - Patient admission with appropriate bed allocation
  - Gender-based room assignment
  - Support for isolation requirements
  - Patient transfer between beds/wards
  - Patient discharge process

- **Medication Management**
  - Prescription creation by authorized doctors
  - Medication administration tracking
  - Medication history for patients

- **Ward Management**
  - Multiple ward support
  - Room and bed allocation
  -Occupancy tracking

- **Compliance Enforcement**
  - Nurse shift coverage (8am-4pm and 2pm-10pm)
  - Doctor availability (minimum 1 hour per day)
  - Maximum 8-hour nurse workday
  - Automated compliance checking

## System Requirements

- Java 11 or higher
- Maven 3.6 or higher
- JavaFX 17.0.2 (included as Maven dependency)

## Installation

1. Clone the repository:
   ```
   git clone https://github.com/yourusername/rmit-care-home.git
   cd rmit-care-home
   ```

2. Build the project using Maven:
   ```
   mvn clean package
   ```

## Running the Application

### Text-based Interface

Run the application with the following command:

```
mvn exec:java
```

Or directly using Java:

```
java -cp target/rmit-care-home-1.0-SNAPSHOT.jar org.example.Main
```

### JavaFX Interface (if implemented)

To run the JavaFX interface:

```
mvn javafx:run
```

## Default Login Credentials

The system comes with pre-configured users for testing:

- **Administrator**
  - Username: admin
  - Password: admin123

- **Doctor**
  - Username: doctor
  - Password: doctor123

- **Nurse**
  - Username: nurse
  - Password: nurse123

## Data Persistence

The system uses Java serialization to persist data across sessions. Data files include:
- `staff_data.ser` - Staff information
- `patients_data.ser` - Current patients
- `discharged_patients.ser` - Discharged patients
- `wards_data.ser` - Ward, room, and bed information
- `action_logs.ser` - System activity logs

## Testing

Run the test suite with:

```
mvn test
```

## Compliance Check Utility

To run the compliance check utility separately:

```
mvn exec:java@compliance-check
```

## License

This project is licensed under the Apache License 2.0 - see the LICENSE file for details.




