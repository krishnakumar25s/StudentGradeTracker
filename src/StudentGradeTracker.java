package src;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.text.DecimalFormat;
import java.io.*;
import java.util.Comparator;
import javax.swing.filechooser.FileNameExtensionFilter;

public class StudentGradeTracker extends JFrame {
    private ArrayList<Student> students = new ArrayList<>();
    private DefaultListModel<String> listModel = new DefaultListModel<>();
    private JList<String> studentList;
    
    // Form components
    private JTextField nameField;
    private JTextField gradeField;
    private JButton addButton;
    private JButton calculateButton;
    private JButton clearButton;
    private JTextArea reportArea;
    private JComboBox<String> sortCombo;
    private JComboBox<String> filterCombo;
    private GradeChartPanel chartPanel;
    private JTabbedPane tabbedPane;

    public StudentGradeTracker() {
        setTitle("Enhanced Student Grade Tracker");
        setSize(700, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        
        // Create components
        createInputPanel();
        createListPanel();
        createReportPanel();
        
        setVisible(true);
    }
    
    private void createInputPanel() {
        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Add Student"));
        
        inputPanel.add(new JLabel("Student Name:"));
        nameField = new JTextField();
        inputPanel.add(nameField);
        
        inputPanel.add(new JLabel("Grade (0-100):"));
        gradeField = new JTextField();
        inputPanel.add(gradeField);
        
        addButton = new JButton("Add Student");
        addButton.addActionListener(e -> addStudent());
        inputPanel.add(addButton);
        
        calculateButton = new JButton("Calculate Statistics");
        calculateButton.addActionListener(e -> calculateStatistics());
        inputPanel.add(calculateButton);
        
        clearButton = new JButton("Clear All");
        clearButton.addActionListener(e -> clearAll());
        clearButton.setBackground(new Color(255, 100, 100));
        clearButton.setForeground(Color.WHITE);
        inputPanel.add(clearButton);
        
        // Empty cell for layout balance
        inputPanel.add(new JLabel());
        
        add(inputPanel, BorderLayout.NORTH);
    }
    
    private void createListPanel() {
        JPanel listPanel = new JPanel(new BorderLayout());
        listPanel.setBorder(BorderFactory.createTitledBorder("Students"));
        
        // Sorting and filtering controls
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlPanel.add(new JLabel("Sort:"));
        
        sortCombo = new JComboBox<>(new String[]{"Default", "Name (A-Z)", "Name (Z-A)", "Grade (High-Low)", "Grade (Low-High)"});
        sortCombo.addActionListener(e -> sortStudents());
        controlPanel.add(sortCombo);
        
        controlPanel.add(new JLabel("Filter:"));
        filterCombo = new JComboBox<>(new String[]{"All Grades", "A (90-100)", "B (80-89)", "C (70-79)", "D (60-69)", "F (<60)"});
        filterCombo.addActionListener(e -> filterStudents());
        controlPanel.add(filterCombo);
        
        listPanel.add(controlPanel, BorderLayout.NORTH);
        
        studentList = new JList<>(listModel);
        studentList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        studentList.setVisibleRowCount(10);
        
        JScrollPane scrollPane = new JScrollPane(studentList);
        listPanel.add(scrollPane, BorderLayout.CENTER);
        
        JButton removeButton = new JButton("Remove Selected");
        removeButton.addActionListener(e -> removeStudent());
        listPanel.add(removeButton, BorderLayout.SOUTH);
        
        add(listPanel, BorderLayout.CENTER);
    }
    
    private void createReportPanel() {
        JPanel reportPanel = new JPanel(new BorderLayout());
        reportPanel.setBorder(BorderFactory.createTitledBorder("Grade Report"));
        
        // Create tabbed pane for text report and chart
        tabbedPane = new JTabbedPane();
        
        // Text report tab
        reportArea = new JTextArea();
        reportArea.setEditable(false);
        reportArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        tabbedPane.addTab("Text Report", new JScrollPane(reportArea));
        
        // Chart tab
        chartPanel = new GradeChartPanel();
        tabbedPane.addTab("Grade Distribution", chartPanel);
        
        // Export button
        JButton exportButton = new JButton("Export Report");
        exportButton.addActionListener(e -> exportReport());
        
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(tabbedPane, BorderLayout.CENTER);
        bottomPanel.add(exportButton, BorderLayout.SOUTH);
        
        reportPanel.add(bottomPanel, BorderLayout.CENTER);
        add(reportPanel, BorderLayout.SOUTH);
    }
    
    private void addStudent() {
        String name = nameField.getText().trim();
        String gradeText = gradeField.getText().trim();
        
        if (name.isEmpty() || gradeText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both name and grade", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            double grade = Double.parseDouble(gradeText);
            if (grade < 0 || grade > 100) {
                throw new NumberFormatException();
            }
            
            Student student = new Student(name, grade);
            students.add(student);
            updateListModel();
            
            nameField.setText("");
            gradeField.setText("");
            nameField.requestFocus();
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid grade (0-100)", 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void removeStudent() {
        int selectedIndex = studentList.getSelectedIndex();
        if (selectedIndex != -1) {
            students.remove(selectedIndex);
            updateListModel();
        } else {
            JOptionPane.showMessageDialog(this, "Please select a student to remove", 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateListModel() {
        listModel.clear();
        String filter = (String) filterCombo.getSelectedItem();
        
        for (Student student : students) {
            if (filter.equals("All Grades") || 
                (filter.startsWith("A") && student.getGrade() >= 90) ||
                (filter.startsWith("B") && student.getGrade() >= 80 && student.getGrade() < 90) ||
                (filter.startsWith("C") && student.getGrade() >= 70 && student.getGrade() < 80) ||
                (filter.startsWith("D") && student.getGrade() >= 60 && student.getGrade() < 70) ||
                (filter.startsWith("F") && student.getGrade() < 60)) {
                listModel.addElement(student.toString());
            }
        }
    }
    
    private void sortStudents() {
        String sortOption = (String) sortCombo.getSelectedItem();
        
        switch (sortOption) {
            case "Name (A-Z)":
                students.sort(Comparator.comparing(Student::getName));
                break;
            case "Name (Z-A)":
                students.sort(Comparator.comparing(Student::getName).reversed());
                break;
            case "Grade (High-Low)":
                students.sort(Comparator.comparingDouble(Student::getGrade).reversed());
                break;
            case "Grade (Low-High)":
                students.sort(Comparator.comparingDouble(Student::getGrade));
                break;
            // Default case does nothing (maintains insertion order)
        }
        
        updateListModel();
    }
    
    private void filterStudents() {
        updateListModel();
    }
    
    private void clearAll() {
        int confirm = JOptionPane.showConfirmDialog(
            this, 
            "Are you sure you want to clear all student data?\nThis cannot be undone.", 
            "Confirm Clear", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            students.clear();
            listModel.clear();
            reportArea.setText("");
            chartPanel.updateDistribution(new int[5]); // Reset chart
            nameField.setText("");
            gradeField.setText("");
            sortCombo.setSelectedIndex(0);
            filterCombo.setSelectedIndex(0);
        }
    }
    
    private void calculateStatistics() {
        if (students.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No students to calculate statistics", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        DecimalFormat df = new DecimalFormat("0.00");
        double total = 0;
        double highest = Double.MIN_VALUE;
        double lowest = Double.MAX_VALUE;
        int[] gradeDistribution = new int[5]; // A, B, C, D, F
        
        StringBuilder report = new StringBuilder();
        report.append("STUDENT GRADE REPORT\n");
        report.append("====================\n\n");
        
        for (Student student : students) {
            double grade = student.getGrade();
            total += grade;
            
            if (grade > highest) highest = grade;
            if (grade < lowest) lowest = grade;
            
            // Grade distribution
            if (grade >= 90) gradeDistribution[0]++;
            else if (grade >= 80) gradeDistribution[1]++;
            else if (grade >= 70) gradeDistribution[2]++;
            else if (grade >= 60) gradeDistribution[3]++;
            else gradeDistribution[4]++;
            
            report.append(String.format("%-20s: %6.2f %s\n", 
                student.getName(), grade, getLetterGrade(grade)));
        }
        
        double average = total / students.size();
        
        report.append("\nSTATISTICS:\n");
        report.append(String.format("Average grade: %10.2f\n", average));
        report.append(String.format("Highest grade: %10.2f (%s)\n", highest, getLetterGrade(highest)));
        report.append(String.format("Lowest grade:  %10.2f (%s)\n", lowest, getLetterGrade(lowest)));
        
        report.append("\nGRADE DISTRIBUTION:\n");
        report.append(String.format("A (90-100): %2d students\n", gradeDistribution[0]));
        report.append(String.format("B (80-89):  %2d students\n", gradeDistribution[1]));
        report.append(String.format("C (70-79):  %2d students\n", gradeDistribution[2]));
        report.append(String.format("D (60-69):  %2d students\n", gradeDistribution[3]));
        report.append(String.format("F (<60):    %2d students\n", gradeDistribution[4]));
        
        reportArea.setText(report.toString());
        chartPanel.updateDistribution(gradeDistribution);
        tabbedPane.setSelectedIndex(0); // Show text report by default
    }
    
    private String getLetterGrade(double grade) {
        if (grade >= 90) return "A";
        if (grade >= 80) return "B";
        if (grade >= 70) return "C";
        if (grade >= 60) return "D";
        return "F";
    }
    
    private void exportReport() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Report");
        
        FileNameExtensionFilter htmlFilter = new FileNameExtensionFilter("HTML Files", "html");
        FileNameExtensionFilter txtFilter = new FileNameExtensionFilter("Text Files", "txt");
        fileChooser.addChoosableFileFilter(htmlFilter);
        fileChooser.addChoosableFileFilter(txtFilter);
        fileChooser.setFileFilter(txtFilter);
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            String extension = "";
            
            if (fileChooser.getFileFilter() == htmlFilter) {
                extension = ".html";
            } else {
                extension = ".txt";
            }
            
            // Ensure the file has the correct extension
            if (!file.getName().toLowerCase().endsWith(extension)) {
                file = new File(file.getParentFile(), file.getName() + extension);
            }
            
            try (PrintWriter writer = new PrintWriter(file)) {
                if (extension.equals(".html")) {
                    writer.println("<html><head><title>Student Grade Report</title></head><body>");
                    writer.println("<h1>Student Grade Report</h1>");
                    writer.println("<pre>" + reportArea.getText().replace("\n", "<br>") + "</pre>");
                    writer.println("</body></html>");
                } else {
                    writer.println(reportArea.getText());
                }
                JOptionPane.showMessageDialog(this, "Report exported successfully to:\n" + file.getPath());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error exporting report: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StudentGradeTracker());
    }
    
    // Inner class for Student
    private class Student {
        private String name;
        private double grade;
        
        public Student(String name, double grade) {
            this.name = name;
            this.grade = grade;
        }
        
        public String getName() { return name; }
        public double getGrade() { return grade; }
        
        @Override
        public String toString() {
            return String.format("%s (%.2f)", name, grade);
        }
    }
    
    // Inner class for Grade Chart Panel
    private class GradeChartPanel extends JPanel {
        private int[] gradeDistribution;
        
        public GradeChartPanel() {
            setPreferredSize(new Dimension(400, 250));
            setBackground(Color.WHITE);
            gradeDistribution = new int[5]; // A, B, C, D, F
        }
        
        public void updateDistribution(int[] distribution) {
            this.gradeDistribution = distribution;
            repaint();
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            
            if (gradeDistribution == null) return;
            
            int width = getWidth();
            int height = getHeight();
            int padding = 25;
            int chartWidth = width - 2 * padding;
            int chartHeight = height - 2 * padding;
            
            // Find max value for scaling
            int max = 1;
            for (int value : gradeDistribution) {
                if (value > max) max = value;
            }
            
            // Draw axes
            g.setColor(Color.BLACK);
            g.drawLine(padding, height - padding, width - padding, height - padding); // X-axis
            g.drawLine(padding, height - padding, padding, padding); // Y-axis
            
            // Draw labels
            g.drawString("Grade Distribution", width/2 - 50, padding/2);
            g.drawString("Number of Students", padding - 15, height/2);
            
            // Draw grade labels and grid lines
            String[] grades = {"A", "B", "C", "D", "F"};
            Color[] colors = {new Color(75, 181, 67),   // Green for A
                             new Color(54, 162, 235),   // Blue for B
                             new Color(255, 206, 86),   // Yellow for C
                             new Color(255, 159, 64),   // Orange for D
                             new Color(255, 99, 132)};  // Red for F
            
            int barWidth = chartWidth / (grades.length + 1);
            
            for (int i = 0; i < grades.length; i++) {
                // Draw grade label
                g.drawString(grades[i], padding + i * barWidth + barWidth/2 - 5, height - padding/2);
                
                // Draw value label at top of bar
                if (gradeDistribution[i] > 0) {
                    g.drawString(String.valueOf(gradeDistribution[i]), 
                        padding + i * barWidth + barWidth/2 - 5, 
                        height - padding - (int)((gradeDistribution[i]/(double)max) * chartHeight) - 5);
                }
                
                // Draw bar
                g.setColor(colors[i]);
                int barHeight = (int)((gradeDistribution[i]/(double)max) * chartHeight);
                g.fillRect(padding + i * barWidth + 10, 
                          height - padding - barHeight, 
                          barWidth - 20, 
                          barHeight);
                
                // Draw bar outline
                g.setColor(Color.BLACK);
                g.drawRect(padding + i * barWidth + 10, 
                           height - padding - barHeight, 
                           barWidth - 20, 
                           barHeight);
            }
        }
    }
}