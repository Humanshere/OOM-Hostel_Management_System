// HostelManagementGUI.java - Swing GUI Application with MySQL Integration
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class HostelManagementGUI extends JFrame {
    private HostelManager hostelManager;
    private JTabbedPane tabbedPane;
    
    // Student panel components
    private JTextField studentIdField, studentNameField, studentEmailField;
    private JTextField studentPhoneField, studentDeptField;
    private JTable studentTable;
    private DefaultTableModel studentTableModel;
    
    // Room panel components
    private JTextField roomNumberField, roomFloorField;
    private JComboBox<String> roomTypeCombo;
    private JSpinner capacitySpinner, rentSpinner;
    private JTable roomTable;
    private DefaultTableModel roomTableModel;
    
    // Allocation panel components
    private JTextField allocStudentIdField, allocRoomNumberField;
    private JTable allocationTable;
    private DefaultTableModel allocationTableModel;
    
    public HostelManagementGUI() {
        hostelManager = new HostelManager();
        
        setTitle("Hostel Management System - MySQL Edition");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Add window listener to close database connection
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                hostelManager.close();
            }
        });
        
        initComponents();
        loadInitialData();
    }
    
    private void initComponents() {
        tabbedPane = new JTabbedPane();
        
        // Add tabs
        tabbedPane.addTab("Students", createStudentPanel());
        tabbedPane.addTab("Rooms", createRoomPanel());
        tabbedPane.addTab("Allocations", createAllocationPanel());
        tabbedPane.addTab("Reports", createReportPanel());
        
        add(tabbedPane);
    }
    
    // ==================== STUDENT PANEL ====================
    private JPanel createStudentPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Input form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Add New Student"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Student ID
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Student ID:"), gbc);
        gbc.gridx = 1;
        studentIdField = new JTextField(15);
        formPanel.add(studentIdField, gbc);
        
        // Name
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        studentNameField = new JTextField(15);
        formPanel.add(studentNameField, gbc);
        
        // Email
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        studentEmailField = new JTextField(15);
        formPanel.add(studentEmailField, gbc);
        
        // Phone
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1;
        studentPhoneField = new JTextField(15);
        formPanel.add(studentPhoneField, gbc);
        
        // Department
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Department:"), gbc);
        gbc.gridx = 1;
        studentDeptField = new JTextField(15);
        formPanel.add(studentDeptField, gbc);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton addButton = new JButton("Add Student");
        JButton deleteButton = new JButton("Delete Selected");
        JButton refreshButton = new JButton("Refresh");
        
        addButton.addActionListener(e -> addStudent());
        deleteButton.addActionListener(e -> deleteStudent());
        refreshButton.addActionListener(e -> refreshStudentTable());
        
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);
        
        // Table
        String[] columnNames = {"Student ID", "Name", "Email", "Phone", "Department"};
        studentTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        studentTable = new JTable(studentTableModel);
        studentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(studentTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("All Students"));
        
        panel.add(formPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void addStudent() {
        String id = studentIdField.getText().trim();
        String name = studentNameField.getText().trim();
        String email = studentEmailField.getText().trim();
        String phone = studentPhoneField.getText().trim();
        String dept = studentDeptField.getText().trim();
        
        if (id.isEmpty() || name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Student ID and Name are required!", 
                "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Student student = new Student(id, name, email, phone, dept);
        if (hostelManager.addStudent(student)) {
            JOptionPane.showMessageDialog(this, "Student added successfully!", 
                "Success", JOptionPane.INFORMATION_MESSAGE);
            clearStudentFields();
            refreshStudentTable();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add student. ID may already exist!", 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteStudent() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a student to delete!", 
                "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String studentId = (String) studentTableModel.getValueAt(selectedRow, 0);
        String studentName = (String) studentTableModel.getValueAt(selectedRow, 1);
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete student: " + studentName + "?\n" +
            "This will also remove all their room allocations.", 
            "Confirm Delete", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (hostelManager.removeStudent(studentId)) {
                JOptionPane.showMessageDialog(this, "Student deleted successfully!\n" +
                    "All related allocations have been removed.", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshStudentTable();
                refreshAllocationTable();
                refreshRoomTable(false);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete student!", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void refreshStudentTable() {
        studentTableModel.setRowCount(0);
        List<Student> students = hostelManager.getAllStudents();
        for (Student s : students) {
            studentTableModel.addRow(new Object[]{
                s.getStudentId(), s.getName(), s.getEmail(), s.getPhone(), s.getDepartment()
            });
        }
    }
    
    private void clearStudentFields() {
        studentIdField.setText("");
        studentNameField.setText("");
        studentEmailField.setText("");
        studentPhoneField.setText("");
        studentDeptField.setText("");
    }
    
    // ==================== ROOM PANEL ====================
    private JPanel createRoomPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Input form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Add New Room"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Room Number
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Room Number:"), gbc);
        gbc.gridx = 1;
        roomNumberField = new JTextField(15);
        formPanel.add(roomNumberField, gbc);
        
        // Room Type
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Room Type:"), gbc);
        gbc.gridx = 1;
        roomTypeCombo = new JComboBox<>(new String[]{"Single", "Double", "Triple"});
        formPanel.add(roomTypeCombo, gbc);
        
        // Capacity
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Capacity:"), gbc);
        gbc.gridx = 1;
        capacitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 4, 1));
        formPanel.add(capacitySpinner, gbc);
        
        // Rent per Bed
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Rent per Bed (₹):"), gbc);
        gbc.gridx = 1;
        rentSpinner = new JSpinner(new SpinnerNumberModel(2000.0, 1000.0, 10000.0, 100.0));
        formPanel.add(rentSpinner, gbc);
        
        // Floor
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Floor:"), gbc);
        gbc.gridx = 1;
        roomFloorField = new JTextField(15);
        formPanel.add(roomFloorField, gbc);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton addButton = new JButton("Add Room");
        JButton refreshButton = new JButton("Refresh");
        JButton viewAvailableButton = new JButton("View Available");
        
        addButton.addActionListener(e -> addRoom());
        refreshButton.addActionListener(e -> refreshRoomTable(false));
        viewAvailableButton.addActionListener(e -> refreshRoomTable(true));
        
        buttonPanel.add(addButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(viewAvailableButton);
        
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);
        
        // Table
        String[] columnNames = {"Room Number", "Type", "Capacity", "Occupied", "Available", "Rent (₹)", "Floor"};
        roomTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        roomTable = new JTable(roomTableModel);
        roomTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(roomTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("All Rooms"));
        
        panel.add(formPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void addRoom() {
        String roomNumber = roomNumberField.getText().trim();
        String roomType = (String) roomTypeCombo.getSelectedItem();
        int capacity = (Integer) capacitySpinner.getValue();
        double rent = (Double) rentSpinner.getValue();
        String floor = roomFloorField.getText().trim();
        
        if (roomNumber.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Room number is required!", 
                "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Room room = new Room(roomNumber, roomType, capacity, rent, floor);
        if (hostelManager.addRoom(room)) {
            JOptionPane.showMessageDialog(this, "Room added successfully!", 
                "Success", JOptionPane.INFORMATION_MESSAGE);
            clearRoomFields();
            refreshRoomTable(false);
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add room. Room number may already exist!", 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void refreshRoomTable(boolean availableOnly) {
        roomTableModel.setRowCount(0);
        List<Room> rooms = availableOnly ? hostelManager.getAvailableRooms() : hostelManager.getAllRooms();
        for (Room r : rooms) {
            roomTableModel.addRow(new Object[]{
                r.getRoomNumber(), r.getRoomType(), r.getCapacity(), 
                r.getOccupied(), r.getAvailableBeds(), r.getRentPerBed(), r.getFloor()
            });
        }
    }
    
    private void clearRoomFields() {
        roomNumberField.setText("");
        roomTypeCombo.setSelectedIndex(0);
        capacitySpinner.setValue(1);
        rentSpinner.setValue(2000.0);
        roomFloorField.setText("");
    }
    
    // ==================== ALLOCATION PANEL ====================
    private JPanel createAllocationPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Input form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Room Allocation"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Student ID
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Student ID:"), gbc);
        gbc.gridx = 1;
        allocStudentIdField = new JTextField(15);
        formPanel.add(allocStudentIdField, gbc);
        
        // Room Number
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Room Number:"), gbc);
        gbc.gridx = 1;
        allocRoomNumberField = new JTextField(15);
        formPanel.add(allocRoomNumberField, gbc);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton allocateButton = new JButton("Allocate Room");
        JButton deallocateButton = new JButton("Deallocate (Checkout)");
        JButton refreshButton = new JButton("Refresh");
        
        allocateButton.addActionListener(e -> allocateRoom());
        deallocateButton.addActionListener(e -> deallocateRoom());
        refreshButton.addActionListener(e -> refreshAllocationTable());
        
        buttonPanel.add(allocateButton);
        buttonPanel.add(deallocateButton);
        buttonPanel.add(refreshButton);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);
        
        // Table
        String[] columnNames = {"Allocation ID", "Student ID", "Student Name", "Room Number", 
                                "Allocation Date", "Checkout Date", "Status"};
        allocationTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        allocationTable = new JTable(allocationTableModel);
        allocationTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(allocationTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("All Allocations"));
        
        panel.add(formPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void allocateRoom() {
        String studentId = allocStudentIdField.getText().trim();
        String roomNumber = allocRoomNumberField.getText().trim();
        
        if (studentId.isEmpty() || roomNumber.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Student ID and Room Number are required!", 
                "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (hostelManager.allocateRoom(studentId, roomNumber)) {
            JOptionPane.showMessageDialog(this, "Room allocated successfully!", 
                "Success", JOptionPane.INFORMATION_MESSAGE);
            allocStudentIdField.setText("");
            allocRoomNumberField.setText("");
            refreshAllocationTable();
            refreshRoomTable(false);
        } else {
            JOptionPane.showMessageDialog(this, 
                "Failed to allocate room!\nReasons:\n" +
                "- Student or Room not found\n" +
                "- Room is full\n" +
                "- Student already has an active allocation", 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deallocateRoom() {
        String studentId = allocStudentIdField.getText().trim();
        
        if (studentId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter Student ID!", 
                "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (hostelManager.deallocateRoom(studentId)) {
            JOptionPane.showMessageDialog(this, "Room deallocated successfully!", 
                "Success", JOptionPane.INFORMATION_MESSAGE);
            allocStudentIdField.setText("");
            refreshAllocationTable();
            refreshRoomTable(false);
        } else {
            JOptionPane.showMessageDialog(this, 
                "Failed to deallocate room!\nNo active allocation found for this student.", 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void refreshAllocationTable() {
        allocationTableModel.setRowCount(0);
        List<Allocation> allocations = hostelManager.getAllAllocations();
        for (Allocation a : allocations) {
            allocationTableModel.addRow(new Object[]{
                a.getAllocationId(),
                a.getStudent().getStudentId(),
                a.getStudent().getName(),
                a.getRoom().getRoomNumber(),
                a.getAllocationDate(),
                a.getCheckoutDate() != null ? a.getCheckoutDate() : "N/A",
                a.getStatus()
            });
        }
    }
    
    // ==================== REPORT PANEL ====================
    private JPanel createReportPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JTextArea reportArea = new JTextArea();
        reportArea.setEditable(false);
        reportArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(reportArea);
        
        JButton generateButton = new JButton("Generate Report");
        generateButton.addActionListener(e -> {
            StringBuilder report = new StringBuilder();
            report.append("========== HOSTEL MANAGEMENT SYSTEM REPORT ==========\n\n");
            
            List<Student> students = hostelManager.getAllStudents();
            List<Room> rooms = hostelManager.getAllRooms();
            List<Room> availableRooms = hostelManager.getAvailableRooms();
            List<Allocation> allocations = hostelManager.getAllAllocations();
            
            long activeAllocations = allocations.stream()
                .filter(a -> a.getStatus().equals("Active"))
                .count();
            
            double totalRevenue = allocations.stream()
                .filter(a -> a.getStatus().equals("Active"))
                .mapToDouble(a -> a.getRoom().getRentPerBed())
                .sum();
            
            report.append(String.format("Total Students:         %d\n", students.size()));
            report.append(String.format("Total Rooms:            %d\n", rooms.size()));
            report.append(String.format("Available Rooms:        %d\n", availableRooms.size()));
            report.append(String.format("Active Allocations:     %d\n", activeAllocations));
            report.append(String.format("Total Monthly Revenue:  ₹%.2f\n", totalRevenue));
            
            report.append("\n========== ROOM OCCUPANCY ==========\n");
            for (Room r : rooms) {
                report.append(String.format("Room %s: %d/%d occupied (%.1f%% full)\n",
                    r.getRoomNumber(), r.getOccupied(), r.getCapacity(),
                    (r.getOccupied() * 100.0 / r.getCapacity())));
            }
            
            reportArea.setText(report.toString());
        });
        
        JPanel topPanel = new JPanel();
        topPanel.add(generateButton);
        
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    // ==================== LOAD INITIAL DATA ====================
    private void loadInitialData() {
        refreshStudentTable();
        refreshRoomTable(false);
        refreshAllocationTable();
    }
    
    // ==================== MAIN METHOD ====================
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            HostelManagementGUI gui = new HostelManagementGUI();
            gui.setVisible(true);
        });
    }
}
