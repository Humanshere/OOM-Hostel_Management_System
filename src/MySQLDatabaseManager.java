import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

class MySQLDatabaseManager {
    private Connection connection;
    
    public MySQLDatabaseManager() {
        try {
            Class.forName(DatabaseConfig.DB_DRIVER);
            connection = DriverManager.getConnection(
                DatabaseConfig.DB_URL,
                DatabaseConfig.DB_USER,
                DatabaseConfig.DB_PASSWORD
            );
            System.out.println("Database connected successfully!");
            createTables();
        } catch (Exception e) {
            System.err.println("Database connection error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void createTables() {
        try (Statement stmt = connection.createStatement()) {
            String studentsTable = "CREATE TABLE IF NOT EXISTS students (" +
                    "student_id VARCHAR(20) PRIMARY KEY," +
                    "name VARCHAR(100) NOT NULL," +
                    "email VARCHAR(100)," +
                    "phone VARCHAR(15)," +
                    "department VARCHAR(50)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";
            stmt.execute(studentsTable);
            
            String roomsTable = "CREATE TABLE IF NOT EXISTS rooms (" +
                    "room_number VARCHAR(10) PRIMARY KEY," +
                    "room_type VARCHAR(20) NOT NULL," +
                    "capacity INT NOT NULL," +
                    "occupied INT DEFAULT 0," +
                    "rent_per_bed DECIMAL(10,2) NOT NULL," +
                    "floor VARCHAR(20)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";
            stmt.execute(roomsTable);
            
            String allocationsTable = "CREATE TABLE IF NOT EXISTS allocations (" +
                    "allocation_id VARCHAR(20) PRIMARY KEY," +
                    "student_id VARCHAR(20) NOT NULL," +
                    "room_number VARCHAR(10) NOT NULL," +
                    "allocation_date DATE NOT NULL," +
                    "checkout_date DATE," +
                    "status VARCHAR(20) DEFAULT 'Active'," +
                    "FOREIGN KEY (student_id) REFERENCES students(student_id)," +
                    "FOREIGN KEY (room_number) REFERENCES rooms(room_number)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";
            stmt.execute(allocationsTable);
            
            System.out.println("Tables created successfully!");
        } catch (SQLException e) {
            System.err.println("Error creating tables: " + e.getMessage());
        }
    }
    

    public boolean addStudent(Student student) {
        String sql = "INSERT INTO students (student_id, name, email, phone, department) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, student.getStudentId());
            pstmt.setString(2, student.getName());
            pstmt.setString(3, student.getEmail());
            pstmt.setString(4, student.getPhone());
            pstmt.setString(5, student.getDepartment());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error adding student: " + e.getMessage());
            return false;
        }
    }
    
    public boolean updateStudent(Student student) {
        String sql = "UPDATE students SET name = ?, email = ?, phone = ?, department = ? WHERE student_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, student.getName());
            pstmt.setString(2, student.getEmail());
            pstmt.setString(3, student.getPhone());
            pstmt.setString(4, student.getDepartment());
            pstmt.setString(5, student.getStudentId());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error updating student: " + e.getMessage());
            return false;
        }
    }
    
    public Student getStudentById(String studentId) {
        String sql = "SELECT * FROM students WHERE student_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, studentId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Student(
                    rs.getString("student_id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("phone"),
                    rs.getString("department")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error getting student: " + e.getMessage());
        }
        return null;
    }
    
    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM students ORDER BY student_id";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                students.add(new Student(
                    rs.getString("student_id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("phone"),
                    rs.getString("department")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error getting students: " + e.getMessage());
        }
        return students;
    }
    
    public boolean deleteStudent(String studentId) {
        try {
            connection.setAutoCommit(false);
            

            String allocSql = "SELECT * FROM allocations WHERE student_id = ? AND status = 'Active'";
            try (PreparedStatement pstmt = connection.prepareStatement(allocSql)) {
                pstmt.setString(1, studentId);
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    String roomNumber = rs.getString("room_number");
                    Room room = getRoomByNumber(roomNumber);
                    if (room != null) {
                        room.deallocateBed();
                        updateRoomOccupancy(roomNumber, room.getOccupied());
                    }
                }
            }
            

            String deleteAllocSql = "DELETE FROM allocations WHERE student_id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(deleteAllocSql)) {
                pstmt.setString(1, studentId);
                pstmt.executeUpdate();
            }
            

            String deleteStudentSql = "DELETE FROM students WHERE student_id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(deleteStudentSql)) {
                pstmt.setString(1, studentId);
                int result = pstmt.executeUpdate();
                connection.commit();
                return result > 0;
            }
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                System.err.println("Error rolling back transaction: " + ex.getMessage());
            }
            System.err.println("Error deleting student: " + e.getMessage());
            return false;
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                System.err.println("Error setting autocommit: " + e.getMessage());
            }
        }
    }
    

    public boolean addRoom(Room room) {
        String sql = "INSERT INTO rooms (room_number, room_type, capacity, occupied, rent_per_bed, floor) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, room.getRoomNumber());
            pstmt.setString(2, room.getRoomType());
            pstmt.setInt(3, room.getCapacity());
            pstmt.setInt(4, room.getOccupied());
            pstmt.setDouble(5, room.getRentPerBed());
            pstmt.setString(6, room.getFloor());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error adding room: " + e.getMessage());
            return false;
        }
    }
    
    public Room getRoomByNumber(String roomNumber) {
        String sql = "SELECT * FROM rooms WHERE room_number = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, roomNumber);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Room room = new Room(
                    rs.getString("room_number"),
                    rs.getString("room_type"),
                    rs.getInt("capacity"),
                    rs.getDouble("rent_per_bed"),
                    rs.getString("floor")
                );
                room.setOccupied(rs.getInt("occupied"));
                return room;
            }
        } catch (SQLException e) {
            System.err.println("Error getting room: " + e.getMessage());
        }
        return null;
    }
    
    public List<Room> getAllRooms() {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM rooms ORDER BY room_number";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Room room = new Room(
                    rs.getString("room_number"),
                    rs.getString("room_type"),
                    rs.getInt("capacity"),
                    rs.getDouble("rent_per_bed"),
                    rs.getString("floor")
                );
                room.setOccupied(rs.getInt("occupied"));
                rooms.add(room);
            }
        } catch (SQLException e) {
            System.err.println("Error getting rooms: " + e.getMessage());
        }
        return rooms;
    }
    
    public List<Room> getAvailableRooms() {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM rooms WHERE occupied < capacity ORDER BY room_number";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Room room = new Room(
                    rs.getString("room_number"),
                    rs.getString("room_type"),
                    rs.getInt("capacity"),
                    rs.getDouble("rent_per_bed"),
                    rs.getString("floor")
                );
                room.setOccupied(rs.getInt("occupied"));
                rooms.add(room);
            }
        } catch (SQLException e) {
            System.err.println("Error getting available rooms: " + e.getMessage());
        }
        return rooms;
    }
    
    public boolean updateRoomOccupancy(String roomNumber, int occupied) {
        String sql = "UPDATE rooms SET occupied = ? WHERE room_number = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, occupied);
            pstmt.setString(2, roomNumber);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error updating room occupancy: " + e.getMessage());
            return false;
        }
    }
    

    public boolean addAllocation(Allocation allocation) {
        String sql = "INSERT INTO allocations (allocation_id, student_id, room_number, allocation_date, checkout_date, status) VALUES (?, ?, ?, ?, ?, ?)";
        try {
            connection.setAutoCommit(false);
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, allocation.getAllocationId());
                pstmt.setString(2, allocation.getStudent().getStudentId());
                pstmt.setString(3, allocation.getRoom().getRoomNumber());
                pstmt.setDate(4, Date.valueOf(allocation.getAllocationDate()));
                pstmt.setDate(5, allocation.getCheckoutDate() != null ? Date.valueOf(allocation.getCheckoutDate()) : null);
                pstmt.setString(6, allocation.getStatus());
                pstmt.executeUpdate();
                
                Room room = allocation.getRoom();
                updateRoomOccupancy(room.getRoomNumber(), room.getOccupied());
                
                connection.commit();
                return true;
            }
        } catch (SQLException e) {
            try { connection.rollback(); } catch (SQLException ex) { }
            System.err.println("Error adding allocation: " + e.getMessage());
            return false;
        } finally {
            try { connection.setAutoCommit(true); } catch (SQLException e) { }
        }
    }
    
    public List<Allocation> getAllAllocations() {
        List<Allocation> allocations = new ArrayList<>();
        String sql = "SELECT * FROM allocations ORDER BY allocation_date DESC";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Student student = getStudentById(rs.getString("student_id"));
                Room room = getRoomByNumber(rs.getString("room_number"));
                if (student != null && room != null) {
                    Allocation allocation = new Allocation(
                        rs.getString("allocation_id"),
                        student, room,
                        rs.getDate("allocation_date").toLocalDate()
                    );
                    Date checkoutDate = rs.getDate("checkout_date");
                    if (checkoutDate != null) {
                        allocation.setCheckoutDate(checkoutDate.toLocalDate());
                    }
                    allocation.setStatus(rs.getString("status"));
                    allocations.add(allocation);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting allocations: " + e.getMessage());
        }
        return allocations;
    }
    
    public Allocation getActiveAllocationByStudent(String studentId) {
        String sql = "SELECT * FROM allocations WHERE student_id = ? AND status = 'Active'";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, studentId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Student student = getStudentById(rs.getString("student_id"));
                Room room = getRoomByNumber(rs.getString("room_number"));
                if (student != null && room != null) {
                    Allocation allocation = new Allocation(
                        rs.getString("allocation_id"),
                        student, room,
                        rs.getDate("allocation_date").toLocalDate()
                    );
                    allocation.setStatus(rs.getString("status"));
                    return allocation;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting active allocation: " + e.getMessage());
        }
        return null;
    }
    
    public boolean updateAllocationCheckout(String allocationId, LocalDate checkoutDate) {
        String sql = "UPDATE allocations SET checkout_date = ?, status = 'Checkout' WHERE allocation_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setDate(1, Date.valueOf(checkoutDate));
            pstmt.setString(2, allocationId);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error updating allocation checkout: " + e.getMessage());
            return false;
        }
    }
    
    public int getNextAllocationNumber() {
        String sql = "SELECT COUNT(*) as count FROM allocations";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("count") + 1;
            }
        } catch (SQLException e) {
            System.err.println("Error getting allocation count: " + e.getMessage());
        }
        return 1;
    }
    
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }
}
