#  Hostel Management System - Quickstart Guide

## Prerequisites
- **Java 11+** installed
- **MySQL/MariaDB** running on your system
- **MySQL Connector JAR** (included in `lib/` folder)

## Initial Setup

### 1. Start MySQL/MariaDB
```bash
# On Linux
sudo systemctl start mysql

# On macOS
brew services start mysql

# On Windows
# Use MySQL Workbench or command line
```

### 2. Set MySQL Root Password (if not already set), login and create db
```bash
sudo mariadb -u root -e "ALTER USER 'root'@'localhost' IDENTIFIED BY '';"

# Login to MariaDB
sudo mysql -u root -p

# Create database
CREATE DATABASE hostel_db;

```


### 3. Compile the Application
```bash
cd /home/himansh/hostel1
javac -cp lib/mysql-connector-j-9.5.0.jar -d bin src/*.java
```

### 4. Run the Application
```bash
java -cp "bin:lib/mysql-connector-j-9.5.0.jar" HostelManagementGUI
```

## Features Overview

###  Students Tab
- **Add Student**: Fill in Student ID, Name, Email, Phone, Department and click "Add Student"
- **Search Student**: Use the search field to find students by ID or name
- **Update Student**: Select a student from search results and click "Update Student" to modify their information
- **Delete Student**: Select a student and click "Delete Selected" to remove them (also removes their allocations)
- **View All**: Click "Refresh" to see all students

###  Rooms Tab
- **Add Room**: Enter Room Number, select Type (Single/Double/Triple), set Rent, select Floor and click "Add Room"
  - Capacity is auto-determined by room type
- **Toggle Filter**: Click "Show All" / "Available Only" button to filter room display
- **View Details**: Table shows Room Number, Type, Capacity, Occupied Beds, Available Beds, Rent, and Floor

###  Allocations Tab
- **Allocate Room**: 
  1. Select Student from searchable dropdown (type to search)
  2. Select Room from searchable dropdown (type to search)
  3. Click "Allocate Room"
- **Deallocate Room**: Select student and click "Deallocate"
- **Toggle Filter**: Click "Show All" / "Active Only" button to filter allocations by status
- **View History**: See all allocation records with dates and status

###  Reports Tab
- **Generate Report**: Click "Generate Report" to view:
  - Total Students, Rooms, Available Rooms
  - Active Allocations and Monthly Revenue
  - Room Occupancy Details with status indicators:
    -  AVAILABLE (0-50% occupied)
    -  HALF FULL (50-75% occupied)
    -  NEARLY FULL (75-100% occupied)
    -  FULL (100% occupied)

## Common Tasks

### Add a New Student
1. Go to ** Students** tab
2. Fill in all student details
3. Click **" Add Student"**
4. You'll see a success message

### Allocate a Room to Student
1. Go to ** Allocations** tab
2. Type student name/ID in the "Select Student" dropdown
3. Type room number in the "Select Room" dropdown
4. Click **" Allocate Room"**

### Find and Update Student Information
1. Go to ** Students** tab
2. Type student ID or name in the search field
3. Click on the student in results or press Enter
4. Modify the information in the form fields
5. Click **" Update Student"**

### View Only Available Rooms
1. Go to ** Rooms** tab
2. Click the **" Show All"** button (turns green and changes to " Available Only")
3. Click **" Available Only"** again to show all rooms

### Generate System Report
1. Go to ** Reports** tab
2. Click **" Generate Report"**
3. View complete system statistics and occupancy details
