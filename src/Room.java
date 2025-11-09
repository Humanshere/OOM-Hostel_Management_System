
class Room {
    private String roomNumber;
    private String roomType;
    private int capacity;
    private int occupied;
    private double rentPerBed;
    private String floor;
    
    public Room(String roomNumber, String roomType, int capacity, double rentPerBed, String floor) {
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.capacity = capacity;
        this.occupied = 0;
        this.rentPerBed = rentPerBed;
        this.floor = floor;
    }
    
    public boolean isAvailable() { return occupied < capacity; }
    public int getAvailableBeds() { return capacity - occupied; }
    public void allocateBed() { if (isAvailable()) occupied++; }
    public void deallocateBed() { if (occupied > 0) occupied--; }
    
    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }
    public String getRoomType() { return roomType; }
    public void setRoomType(String roomType) { this.roomType = roomType; }
    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public int getOccupied() { return occupied; }
    public void setOccupied(int occupied) { this.occupied = occupied; }
    public double getRentPerBed() { return rentPerBed; }
    public void setRentPerBed(double rentPerBed) { this.rentPerBed = rentPerBed; }
    public String getFloor() { return floor; }
    public void setFloor(String floor) { this.floor = floor; }
    
    @Override
    public String toString() {
        return "Room: " + roomNumber + " | Type: " + roomType + " | Capacity: " + capacity +
               " | Occupied: " + occupied + " | Rent: ₹" + rentPerBed + " | Floor: " + floor;
    }
}

