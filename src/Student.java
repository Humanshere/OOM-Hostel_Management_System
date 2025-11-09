class Student {
    private String studentId;
    private String name;
    private String email;
    private String phone;
    private String department;
    
    public Student(String studentId, String name, String email, String phone, String department) {
        this.studentId = studentId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.department = department;
    }
    
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    
    @Override
    public String toString() {
        return "ID: " + studentId + " | Name: " + name + " | Email: " + email + 
               " | Phone: " + phone + " | Dept: " + department;
    }
}
