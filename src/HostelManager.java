import java.util.List;
import java.time.LocalDate;

class HostelManager {
    private MySQLDatabaseManager dbManager;
    
    public HostelManager() {
        this.dbManager = new MySQLDatabaseManager();
    }
    
    public boolean addStudent(Student student) {
        return dbManager.addStudent(student);
    }
    
    public boolean updateStudent(Student student) {
        return dbManager.updateStudent(student);
    }
    
    public Student findStudentById(String studentId) {
        return dbManager.getStudentById(studentId);
    }
    
    public boolean removeStudent(String studentId) {
        return dbManager.deleteStudent(studentId);
    }
    
    public boolean addRoom(Room room) {
        return dbManager.addRoom(room);
    }
    
    public Room findRoomByNumber(String roomNumber) {
        return dbManager.getRoomByNumber(roomNumber);
    }
    
    public List<Room> getAllRooms() {
        return dbManager.getAllRooms();
    }
    
    public List<Room> getAvailableRooms() {
        return dbManager.getAvailableRooms();
    }
    
    public List<Student> getAllStudents() {
        return dbManager.getAllStudents();
    }
    
    public List<Allocation> getAllAllocations() {
        return dbManager.getAllAllocations();
    }
    
    public boolean allocateRoom(String studentId, String roomNumber) {
        Student student = dbManager.getStudentById(studentId);
        Room room = dbManager.getRoomByNumber(roomNumber);
        
        if (student == null || room == null || !room.isAvailable()) {
            return false;
        }
        
        if (dbManager.getActiveAllocationByStudent(studentId) != null) {
            return false;
        }
        
        int allocationNum = dbManager.getNextAllocationNumber();
        String allocationId = "ALLOC" + String.format("%04d", allocationNum);
        
        room.allocateBed();
        Allocation allocation = new Allocation(allocationId, student, room, LocalDate.now());
        
        return dbManager.addAllocation(allocation);
    }
    
    public boolean deallocateRoom(String studentId) {
        Allocation allocation = dbManager.getActiveAllocationByStudent(studentId);
        if (allocation == null) {
            return false;
        }
        
        Room room = allocation.getRoom();
        room.deallocateBed();
        
        return dbManager.updateRoomOccupancy(room.getRoomNumber(), room.getOccupied()) &&
               dbManager.updateAllocationCheckout(allocation.getAllocationId(), LocalDate.now());
    }
    
    public void close() {
        dbManager.closeConnection();
    }
}
