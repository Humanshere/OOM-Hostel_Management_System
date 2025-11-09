import java.time.LocalDate;

class Allocation {
    private String allocationId;
    private Student student;
    private Room room;
    private LocalDate allocationDate;
    private LocalDate checkoutDate;
    private String status;
    
    public Allocation(String allocationId, Student student, Room room, LocalDate allocationDate) {
        this.allocationId = allocationId;
        this.student = student;
        this.room = room;
        this.allocationDate = allocationDate;
        this.checkoutDate = null;
        this.status = "Active";
    }
    
    public void checkout() {
        this.checkoutDate = LocalDate.now();
        this.status = "Checkout";
        room.deallocateBed();
    }
    
    public String getAllocationId() { return allocationId; }
    public void setAllocationId(String allocationId) { this.allocationId = allocationId; }
    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }
    public Room getRoom() { return room; }
    public void setRoom(Room room) { this.room = room; }
    public LocalDate getAllocationDate() { return allocationDate; }
    public void setAllocationDate(LocalDate allocationDate) { this.allocationDate = allocationDate; }
    public LocalDate getCheckoutDate() { return checkoutDate; }
    public void setCheckoutDate(LocalDate checkoutDate) { this.checkoutDate = checkoutDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    @Override
    public String toString() {
        return "ID: " + allocationId + " | Student: " + student.getName() +
               " | Room: " + room.getRoomNumber() + " | Date: " + allocationDate +
               " | Status: " + status + (checkoutDate != null ? " | Checkout: " + checkoutDate : "");
    }
}
