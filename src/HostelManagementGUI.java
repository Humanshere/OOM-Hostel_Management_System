import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.stream.Collectors;

public class HostelManagementGUI extends JFrame {
    private HostelManager hostelManager;
    private JTabbedPane tabbedPane;
    
    private JTextField studentIdField, studentNameField, studentEmailField;
    private JTextField studentPhoneField, studentDeptField;
    private JTable studentTable;
    private DefaultTableModel studentTableModel;
    
    private JTextField roomNumberField, roomFloorField;
    private JComboBox<String> roomTypeCombo;
    private JSpinner rentSpinner;
    private JTable roomTable;
    private DefaultTableModel roomTableModel;
    private JButton filterAvailableButton;
    private boolean showOnlyAvailable = false;
    
    private JComboBox<String> allocStudentCombo, allocRoomCombo;
    private JTable allocationTable;
    private DefaultTableModel allocationTableModel;
    private JButton filterActiveButton;
    private boolean showOnlyActive = false;
    
    private static final Color PRIMARY_COLOR = new Color(70, 130, 180);
    private static final Color SECONDARY_COLOR = new Color(100, 149, 237);
    private static final Color SUCCESS_COLOR = new Color(34, 139, 34);
    private static final Color DANGER_COLOR = new Color(220, 20, 60);
    private static final Color HEADER_COLOR = new Color(47, 79, 79);
    private static final Color BG_COLOR = new Color(245, 245, 245);
    
    public HostelManagementGUI() {
        hostelManager = new HostelManager();
        
        setTitle("Hostel Management System");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                hostelManager.close();
            }
        });
        
        initComponents();
        loadInitialData();
        setVisible(true);
    }
    
    private void initComponents() {
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));
        
        tabbedPane.setBackground(PRIMARY_COLOR);
        tabbedPane.setForeground(Color.WHITE);
        
        tabbedPane.addTab("Students", createStudentPanel());
        tabbedPane.addTab("Rooms", createRoomPanel());
        tabbedPane.addTab("Allocations", createAllocationPanel());
        tabbedPane.addTab("Reports", createReportPanel());
        
        add(tabbedPane);
    }
    

    private JPanel createStudentPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BG_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR, 1),
            "Find Student",
            javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 11),
            PRIMARY_COLOR
        ));
        JLabel searchLabel = new JLabel("Search by ID or Name:");
        searchLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        searchLabel.setForeground(HEADER_COLOR);
        JTextField searchField = new JTextField(20);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        JButton searchButton = createStyledButton("Search", PRIMARY_COLOR);
        JButton clearSearchButton = createStyledButton("Clear", SECONDARY_COLOR);
        
        searchButton.addActionListener(e -> {
            String query = searchField.getText().trim();
            if (query.isEmpty()) {
                refreshStudentTable();
            } else {
                searchStudents(query);
            }
        });
        clearSearchButton.addActionListener(e -> {
            searchField.setText("");
            refreshStudentTable();
        });
        
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(clearSearchButton);
        

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
            " Add New Student /  Update Student",
            javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 12),
            PRIMARY_COLOR
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        

        gbc.gridx = 0; gbc.gridy = 0;
        JLabel idLabel = new JLabel("Student ID:");
        idLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        idLabel.setForeground(HEADER_COLOR);
        formPanel.add(idLabel, gbc);
        gbc.gridx = 1;
        studentIdField = new JTextField(15);
        studentIdField.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        studentIdField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        formPanel.add(studentIdField, gbc);
        

        gbc.gridx = 0; gbc.gridy = 1;
        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        nameLabel.setForeground(HEADER_COLOR);
        formPanel.add(nameLabel, gbc);
        gbc.gridx = 1;
        studentNameField = new JTextField(15);
        studentNameField.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        studentNameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        formPanel.add(studentNameField, gbc);
        

        gbc.gridx = 0; gbc.gridy = 2;
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        emailLabel.setForeground(HEADER_COLOR);
        formPanel.add(emailLabel, gbc);
        gbc.gridx = 1;
        studentEmailField = new JTextField(15);
        studentEmailField.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        studentEmailField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        formPanel.add(studentEmailField, gbc);
        

        gbc.gridx = 0; gbc.gridy = 3;
        JLabel phoneLabel = new JLabel("Phone:");
        phoneLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        phoneLabel.setForeground(HEADER_COLOR);
        formPanel.add(phoneLabel, gbc);
        gbc.gridx = 1;
        studentPhoneField = new JTextField(15);
        studentPhoneField.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        studentPhoneField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        formPanel.add(studentPhoneField, gbc);
        

        gbc.gridx = 0; gbc.gridy = 4;
        JLabel deptLabel = new JLabel("Department:");
        deptLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        deptLabel.setForeground(HEADER_COLOR);
        formPanel.add(deptLabel, gbc);
        gbc.gridx = 1;
        studentDeptField = new JTextField(15);
        studentDeptField.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        studentDeptField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        formPanel.add(studentDeptField, gbc);
        

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton addButton = createStyledButton(" Add Student", SUCCESS_COLOR);
        JButton updateButton = createStyledButton(" Update Student", PRIMARY_COLOR);
        JButton deleteButton = createStyledButton(" Delete Selected", DANGER_COLOR);
        JButton refreshButton = createStyledButton(" Refresh", SECONDARY_COLOR);
        
        addButton.addActionListener(e -> addStudent());
        updateButton.addActionListener(e -> updateStudent());
        deleteButton.addActionListener(e -> deleteStudent());
        refreshButton.addActionListener(e -> refreshStudentTable());
        
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);
        

        String[] columnNames = {"Student ID", "Name", "Email", "Phone", "Department"};
        studentTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        studentTable = new JTable(studentTableModel);
        studentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        studentTable.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        studentTable.setRowHeight(25);
        

        JTableHeader studentHeader = studentTable.getTableHeader();
        studentHeader.setBackground(PRIMARY_COLOR);
        studentHeader.setForeground(Color.WHITE);
        studentHeader.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        studentTable.setGridColor(new Color(220, 220, 220));
        studentTable.setSelectionBackground(SECONDARY_COLOR);
        studentTable.setSelectionForeground(Color.WHITE);
        

        studentTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = studentTable.getSelectedRow();
                if (selectedRow != -1) {
                    populateStudentForm(selectedRow);
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(studentTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR, 1),
            " All Students",
            javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 12),
            PRIMARY_COLOR
        ));
        

        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBackground(BG_COLOR);
        topPanel.add(searchPanel, BorderLayout.NORTH);
        topPanel.add(formPanel, BorderLayout.CENTER);
        
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void searchStudents(String query) {
        studentTableModel.setRowCount(0);
        List<Student> students = hostelManager.getAllStudents();
        String lowerQuery = query.toLowerCase();
        
        for (Student s : students) {
            if (s.getStudentId().toLowerCase().contains(lowerQuery) ||
                s.getName().toLowerCase().contains(lowerQuery)) {
                studentTableModel.addRow(new Object[]{
                    s.getStudentId(), s.getName(), s.getEmail(), s.getPhone(), s.getDepartment()
                });
            }
        }
    }
    
    private void populateStudentForm(int row) {
        String studentId = (String) studentTableModel.getValueAt(row, 0);
        String name = (String) studentTableModel.getValueAt(row, 1);
        String email = (String) studentTableModel.getValueAt(row, 2);
        String phone = (String) studentTableModel.getValueAt(row, 3);
        String dept = (String) studentTableModel.getValueAt(row, 4);
        
        studentIdField.setText(studentId);
        studentIdField.setEditable(false);
        studentNameField.setText(name);
        studentEmailField.setText(email);
        studentPhoneField.setText(phone);
        studentDeptField.setText(dept);
    }
    
    private void updateStudent() {
        String id = studentIdField.getText().trim();
        String name = studentNameField.getText().trim();
        String email = studentEmailField.getText().trim();
        String phone = studentPhoneField.getText().trim();
        String dept = studentDeptField.getText().trim();
        
        if (id.isEmpty() || name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a student and fill in required fields!", 
                "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Student student = new Student(id, name, email, phone, dept);
        if (hostelManager.updateStudent(student)) {
            JOptionPane.showMessageDialog(this, "Student updated successfully!", 
                "Success", JOptionPane.INFORMATION_MESSAGE);
            clearStudentFields();
            refreshStudentTable();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update student!", 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void clearStudentFields() {
        studentIdField.setText("");
        studentIdField.setEditable(true);
        studentNameField.setText("");
        studentEmailField.setText("");
        studentPhoneField.setText("");
        studentDeptField.setText("");
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
                clearStudentFields();
                refreshStudentTable();
                refreshAllocationTable();
                refreshRoomTable();
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
    
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 11));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createRaisedBevelBorder());
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(150, 35));
        

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(darkenColor(bgColor, 20));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }
    
    private Color darkenColor(Color color, int amount) {
        return new Color(
            Math.max(0, color.getRed() - amount),
            Math.max(0, color.getGreen() - amount),
            Math.max(0, color.getBlue() - amount)
        );
    }
    
    
    private void setupSearchableCombo(JComboBox<String> comboBox) {
        JTextField textField = (JTextField) comboBox.getEditor().getEditorComponent();
        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                try {
                    String searchText = textField.getText().toLowerCase();
                    if (searchText.isEmpty()) {
                        comboBox.showPopup();
                        return;
                    }
                    

                    comboBox.showPopup();
                } catch (Exception ex) {

                    ex.printStackTrace();
                }
            }
        });
    }
    
    
    private void refreshAllocationDropdowns() {
        allocStudentCombo.removeAllItems();
        allocRoomCombo.removeAllItems();
        
        List<Student> students = hostelManager.getAllStudents();
        for (Student s : students) {
            allocStudentCombo.addItem(s.getStudentId() + " - " + s.getName());
        }
        
        List<Room> availableRooms = hostelManager.getAvailableRooms();
        for (Room r : availableRooms) {
            allocRoomCombo.addItem(r.getRoomNumber() + " (" + r.getAvailableBeds() + " available)");
        }
    }
    

    private JPanel createRoomPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BG_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
            " Add New Room",
            javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 12),
            PRIMARY_COLOR
        ));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        

        gbc.gridx = 0; gbc.gridy = 0;
        JLabel roomNumLabel = new JLabel("Room Number:");
        roomNumLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        roomNumLabel.setForeground(HEADER_COLOR);
        formPanel.add(roomNumLabel, gbc);
        gbc.gridx = 1;
        roomNumberField = new JTextField(15);
        roomNumberField.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        roomNumberField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        formPanel.add(roomNumberField, gbc);
        

        gbc.gridx = 0; gbc.gridy = 1;
        JLabel typeLabel = new JLabel("Room Type:");
        typeLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        typeLabel.setForeground(HEADER_COLOR);
        formPanel.add(typeLabel, gbc);
        gbc.gridx = 1;
        roomTypeCombo = new JComboBox<>(new String[]{"Single", "Double", "Triple"});
        roomTypeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        formPanel.add(roomTypeCombo, gbc);
        

        gbc.gridx = 0; gbc.gridy = 2;
        JLabel rentLabel = new JLabel("Rent per Bed (Rs Rs ):");
        rentLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        rentLabel.setForeground(HEADER_COLOR);
        formPanel.add(rentLabel, gbc);
        gbc.gridx = 1;
        rentSpinner = new JSpinner(new SpinnerNumberModel(2000.0, 1000.0, 10000.0, 100.0));
        formPanel.add(rentSpinner, gbc);
        

        gbc.gridx = 0; gbc.gridy = 3;
        JLabel floorLabel = new JLabel("Floor:");
        floorLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        floorLabel.setForeground(HEADER_COLOR);
        formPanel.add(floorLabel, gbc);
        gbc.gridx = 1;
        roomFloorField = new JTextField(15);
        roomFloorField.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        roomFloorField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        formPanel.add(roomFloorField, gbc);
        

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(Color.WHITE);
        JButton addButton = createStyledButton(" Add Room", SUCCESS_COLOR);
        JButton refreshButton = createStyledButton(" Refresh", SECONDARY_COLOR);
        filterAvailableButton = createStyledButton(" Show All", PRIMARY_COLOR);
        
        addButton.addActionListener(e -> addRoom());
        refreshButton.addActionListener(e -> refreshRoomTable());
        filterAvailableButton.addActionListener(e -> toggleAvailableRoomFilter());
        
        buttonPanel.add(addButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(filterAvailableButton);
        
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);
        

        String[] columnNames = {"Room Number", "Type", "Capacity", "Occupied", "Available", "Rent (Rs Rs )", "Floor"};
        roomTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        roomTable = new JTable(roomTableModel);
        roomTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        roomTable.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        roomTable.setRowHeight(25);
        

        JTableHeader roomHeader = roomTable.getTableHeader();
        roomHeader.setBackground(PRIMARY_COLOR);
        roomHeader.setForeground(Color.WHITE);
        roomHeader.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        roomTable.setGridColor(new Color(220, 220, 220));
        roomTable.setSelectionBackground(SECONDARY_COLOR);
        roomTable.setSelectionForeground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(roomTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR, 1),
            " All Rooms",
            javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 12),
            PRIMARY_COLOR
        ));
        
        panel.add(formPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void addRoom() {
        String roomNumber = roomNumberField.getText().trim();
        String roomType = (String) roomTypeCombo.getSelectedItem();
        double rent = (Double) rentSpinner.getValue();
        String floor = roomFloorField.getText().trim();
        

        int capacity = 2;
        if ("Single".equals(roomType)) {
            capacity = 1;
        } else if ("Double".equals(roomType)) {
            capacity = 2;
        } else if ("Triple".equals(roomType)) {
            capacity = 3;
        }
        
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
            refreshRoomTable();
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
    
    private void refreshRoomTable() {
        roomTableModel.setRowCount(0);
        List<Room> rooms = showOnlyAvailable ? hostelManager.getAvailableRooms() : hostelManager.getAllRooms();
        for (Room r : rooms) {
            roomTableModel.addRow(new Object[]{
                r.getRoomNumber(), r.getRoomType(), r.getCapacity(), 
                r.getOccupied(), r.getAvailableBeds(), r.getRentPerBed(), r.getFloor()
            });
        }
    }
    
    private void toggleAvailableRoomFilter() {
        showOnlyAvailable = !showOnlyAvailable;
        if (showOnlyAvailable) {
            filterAvailableButton.setText(" Available Only");
            filterAvailableButton.setBackground(SUCCESS_COLOR);
        } else {
            filterAvailableButton.setText(" Show All");
            filterAvailableButton.setBackground(PRIMARY_COLOR);
        }
        refreshRoomTable();
    }
    
    private void clearRoomFields() {
        roomNumberField.setText("");
        roomTypeCombo.setSelectedIndex(0);
        rentSpinner.setValue(2000.0);
        roomFloorField.setText("");
    }
    

    private JPanel createAllocationPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BG_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
            " Room Allocation",
            javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 12),
            PRIMARY_COLOR
        ));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        

        gbc.gridx = 0; gbc.gridy = 0;
        JLabel studentLabel = new JLabel("Select Student:");
        studentLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        studentLabel.setForeground(HEADER_COLOR);
        formPanel.add(studentLabel, gbc);
        gbc.gridx = 1;
        allocStudentCombo = new JComboBox<>();
        allocStudentCombo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        allocStudentCombo.setEditable(true);
        setupSearchableCombo(allocStudentCombo);
        formPanel.add(allocStudentCombo, gbc);
        

        gbc.gridx = 0; gbc.gridy = 1;
        JLabel roomLabel = new JLabel("Select Room:");
        roomLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        roomLabel.setForeground(HEADER_COLOR);
        formPanel.add(roomLabel, gbc);
        gbc.gridx = 1;
        allocRoomCombo = new JComboBox<>();
        allocRoomCombo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        allocRoomCombo.setEditable(true);
        setupSearchableCombo(allocRoomCombo);
        formPanel.add(allocRoomCombo, gbc);
        

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(Color.WHITE);
        JButton allocateButton = createStyledButton(" Allocate Room", SUCCESS_COLOR);
        JButton deallocateButton = createStyledButton(" Deallocate", DANGER_COLOR);
        JButton refreshButton = createStyledButton(" Refresh", SECONDARY_COLOR);
        filterActiveButton = createStyledButton(" Show All", PRIMARY_COLOR);
        
        allocateButton.addActionListener(e -> allocateRoom());
        deallocateButton.addActionListener(e -> deallocateRoom());
        refreshButton.addActionListener(e -> {
            refreshAllocationTable();
            refreshAllocationDropdowns();
        });
        filterActiveButton.addActionListener(e -> toggleAllocationFilter());
        
        buttonPanel.add(allocateButton);
        buttonPanel.add(deallocateButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(filterActiveButton);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);
        

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
        allocationTable.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        allocationTable.setRowHeight(25);
        

        JTableHeader allocHeader = allocationTable.getTableHeader();
        allocHeader.setBackground(PRIMARY_COLOR);
        allocHeader.setForeground(Color.WHITE);
        allocHeader.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        allocationTable.setGridColor(new Color(220, 220, 220));
        allocationTable.setSelectionBackground(SECONDARY_COLOR);
        allocationTable.setSelectionForeground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(allocationTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR, 1),
            " All Allocations",
            javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 12),
            PRIMARY_COLOR
        ));
        
        panel.add(formPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void allocateRoom() {
        String studentId = "";
        String roomNumber = "";
        

        String studentComboValue = (String) allocStudentCombo.getSelectedItem();
        String roomComboValue = (String) allocRoomCombo.getSelectedItem();
        
        if (studentComboValue != null && !studentComboValue.isEmpty()) {
            studentId = studentComboValue.split(" - ")[0].trim();
        }
        if (roomComboValue != null && !roomComboValue.isEmpty()) {
            roomNumber = roomComboValue.split(" ")[0].trim();
        }
        
        if (studentId.isEmpty() || roomNumber.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select both Student and Room from the dropdowns!", 
                "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (hostelManager.allocateRoom(studentId, roomNumber)) {
            JOptionPane.showMessageDialog(this, " Room allocated successfully!", 
                "Success", JOptionPane.INFORMATION_MESSAGE);
            allocStudentCombo.setSelectedIndex(-1);
            allocRoomCombo.setSelectedIndex(-1);
            refreshAllocationTable();
            refreshAllocationDropdowns();
            refreshRoomTable();
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
        String studentComboValue = (String) allocStudentCombo.getSelectedItem();
        String studentId = "";
        
        if (studentComboValue != null && !studentComboValue.isEmpty()) {
            studentId = studentComboValue.split(" - ")[0].trim();
        }
        
        if (studentId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a Student from the dropdown!", 
                "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to deallocate this student's room?",
            "Confirm Deallocate", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (hostelManager.deallocateRoom(studentId)) {
                JOptionPane.showMessageDialog(this, " Room deallocated successfully!", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                allocStudentCombo.setSelectedIndex(-1);
                refreshAllocationTable();
                refreshAllocationDropdowns();
                refreshRoomTable();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Failed to deallocate room!\nNo active allocation found for this student.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void refreshAllocationTable() {
        allocationTableModel.setRowCount(0);
        List<Allocation> allocations = hostelManager.getAllAllocations();
        

        if (showOnlyActive) {
            allocations = allocations.stream()
                .filter(a -> a.getStatus().equals("Active"))
                .collect(Collectors.toList());
        }
        
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
    
    private void toggleAllocationFilter() {
        showOnlyActive = !showOnlyActive;
        if (showOnlyActive) {
            filterActiveButton.setText(" Active Only");
            filterActiveButton.setBackground(SUCCESS_COLOR);
        } else {
            filterActiveButton.setText(" Show All");
            filterActiveButton.setBackground(PRIMARY_COLOR);
        }
        refreshAllocationTable();
    }
    

    private JPanel createReportPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BG_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JTextArea reportArea = new JTextArea();
        reportArea.setEditable(false);
        reportArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        reportArea.setBackground(Color.WHITE);
        reportArea.setForeground(HEADER_COLOR);
        reportArea.setLineWrap(true);
        reportArea.setWrapStyleWord(true);
        reportArea.setMargin(new Insets(10, 10, 10, 10));
        
        JScrollPane scrollPane = new JScrollPane(reportArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR, 1),
            " System Report",
            javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 12),
            PRIMARY_COLOR
        ));
        scrollPane.setBackground(BG_COLOR);
        
        JButton generateButton = createStyledButton("Generate Report", PRIMARY_COLOR);
        generateButton.addActionListener(e -> {
            StringBuilder report = new StringBuilder();
            report.append("====================================================================\n");
            report.append("                  HOSTEL MANAGEMENT SYSTEM REPORT                    \n");
            report.append("====================================================================\n\n");
            
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
            
            report.append(" SUMMARY STATISTICS\n");
            report.append("====================================================================\n");
            report.append(String.format("   Total Students:               %d\n", students.size()));
            report.append(String.format("    Total Rooms:                %d\n", rooms.size()));
            report.append(String.format("   Available Rooms:             %d\n", availableRooms.size()));
            report.append(String.format("   Active Allocations:          %d\n", activeAllocations));
            report.append(String.format("   Total Monthly Revenue:        Rs %.2f\n", totalRevenue));
            
            report.append("\n ROOM OCCUPANCY DETAILS\n");
            report.append("====================================================================\n");
            int i = 1;
            for (Room r : rooms) {
                double occupancyPercent = (r.getOccupied() * 100.0 / r.getCapacity());
                String status = occupancyPercent >= 100 ? " FULL" : 
                               occupancyPercent >= 75 ? " NEARLY FULL" : 
                               occupancyPercent >= 50 ? " HALF FULL" : " AVAILABLE";
                
                report.append(String.format("  %2d. Room %s: %d/%d occupied (%.0f%%) [%s]\n",
                    i++, r.getRoomNumber(), r.getOccupied(), r.getCapacity(), 
                    occupancyPercent, status));
            }
            
            report.append("\n====================================================================\n");
            report.append("Report generated on: ").append(java.time.LocalDateTime.now()).append("\n");
            
            reportArea.setText(report.toString());
        });
        
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        topPanel.setBackground(Color.WHITE);
        topPanel.add(generateButton);
        
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    

    private void loadInitialData() {
        refreshStudentTable();
        refreshRoomTable();
        refreshAllocationTable();
        refreshAllocationDropdowns();
    }
    

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        
        SwingUtilities.invokeLater(() -> {
            HostelManagementGUI gui = new HostelManagementGUI();
        });
    }
}
