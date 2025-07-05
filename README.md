# Student Grade Tracker

![Java](https://img.shields.io/badge/Java-17%2B-blue)
![Swing](https://img.shields.io/badge/GUI-Swing-orange)
![License](https://img.shields.io/badge/License-MIT-green)

## Features

- **Student Management**
  - Add students with names and grades
  - Remove individual students
  - Clear all data with confirmation

- **Grade Analysis**
  - Calculate average, highest, and lowest grades
  - Letter grade conversion (A-F)
  - Grade distribution visualization

- **Data Organization**
  - Sort by name (A-Z/Z-A) or grade (High-Low/Low-High)
  - Filter by grade ranges (A, B, C, D, F)

- **Reporting**
  - Text-based summary reports
  - Visual grade distribution chart
  - Export reports as HTML or TXT

## Installation

1. **Requirements**:
   - Java JDK 17+
   - Maven (for building)

2. **Clone the repository**:
   ```bash
   git clone https://github.com/your-username/student-grade-tracker.git
   cd student-grade-tracker

3.**Build and Run**:
bash
mvn clean package
java -jar target/student-grade-tracker.jar

**Usage**
Add Students:

Enter student name and grade (0-100)

Click "Add Student"

Analyze Data:

Click "Calculate Statistics" to generate reports

Switch between "Text Report" and "Grade Distribution" tabs

Manage Data:

Use sort/filter comboboxes to organize students

Select a student and click "Remove Selected" to delete

Export Reports:

Click "Export Report" button

Choose HTML or TXT format

Select save location

student-grade-tracker/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── StudentGradeTracker.java  # Main application
│   │   └── resources/                   # (Optional resource files)
├── target/                              # Built artifacts
├── pom.xml                              # Maven configuration
└── README.md                            # This file
