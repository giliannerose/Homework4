import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EmployeeInfoGUI {

    // List to store employee data
    private static ArrayList<String[]> employeeData = new ArrayList<>();

    public static void main(String[] args) {
        String tsvFile = "employees.tsv"; // Path to your TSV file
        String line;
        String tsvSplitBy = "\t"; // Use tab as the delimiter for TSV

        // Load employee data from TSV file
        try (BufferedReader br = new BufferedReader(new FileReader(tsvFile))) {
            // Skip the first row (header)
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] employee = line.split(tsvSplitBy);
                employeeData.add(employee);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Sort the employee data based on Employee Number
        employeeData.sort(Comparator.comparingInt(employee -> Integer.parseInt(employee[0].trim())));

        // Create the GUI
        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Employee Info");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Input Panel
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Input"));

        JLabel labelEmployeeNumber = new JLabel("Employee Number:");
        JTextField textEmployeeNumber = new JTextField();
        JButton buttonCalculate = new JButton("Calculate Salary");

        inputPanel.add(labelEmployeeNumber);
        inputPanel.add(textEmployeeNumber);
        inputPanel.add(new JLabel()); // Placeholder
        inputPanel.add(buttonCalculate);

        // Result Area
        JTextArea textAreaResult = new JTextArea();
        textAreaResult.setEditable(false);
        textAreaResult.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(textAreaResult);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Result"));

        // Employee Data Table
        String[] columnNames = {"Employee Number", "Last Name", "First Name", "SSS No.", "PhilHealth No.", "TIN", "Pagibig No."};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable employeeTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(employeeTable);
        tableScrollPane.setBorder(BorderFactory.createTitledBorder("Employee Data"));

        // Populate the table with employee data
        for (String[] employee : employeeData) {
            // Assuming the columns in the TSV file are in the following order:
            // 0 - Employee Number, 1 - Last Name, 2 - First Name, 3 - SSS No., 4 - PhilHealth No., 5 - TIN, 6 - Pagibig No.
            tableModel.addRow(new Object[]{employee[0], employee[1], employee[2], employee[6], employee[7], employee[8], employee[9]});
        }

        // Add input panel, result area, and employee table to main panel
        mainPanel.add(inputPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(tableScrollPane, BorderLayout.SOUTH);

        // Button to view employee details
        JButton buttonViewEmployee = new JButton("View Employee");
        mainPanel.add(buttonViewEmployee, BorderLayout.EAST);

        // Add main panel to frame
        frame.getContentPane().add(mainPanel);
        frame.setVisible(true);

        // Action listener for calculate button
        buttonCalculate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String employeeNumber = textEmployeeNumber.getText().trim();
                    if (employeeNumber.isEmpty()) {
                        throw new IllegalArgumentException("Employee number cannot be empty.");
                    }

                    // Select month
                    String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
                    String selectedMonth = (String) JOptionPane.showInputDialog(frame, "Select month:", "Month Selection", JOptionPane.QUESTION_MESSAGE, null, months, months[0]);
                    if (selectedMonth == null || selectedMonth.isEmpty()) {
                        throw new IllegalArgumentException("Month cannot be empty.");
                    }

                    // Prompt for number of hours worked
                    String hoursInput = JOptionPane.showInputDialog(frame, "Enter the number of hours worked in " + selectedMonth + ":", "Enter Hours Worked", JOptionPane.QUESTION_MESSAGE);
                    if (hoursInput == null || hoursInput.isEmpty()) {
                        throw new IllegalArgumentException("Number of hours worked cannot be empty.");
                    }

                    int hoursWorked = Integer.parseInt(hoursInput.trim());
                    if (hoursWorked <= 0) {
                        throw new IllegalArgumentException("Hours worked must be greater than zero.");
                    }

                    String result = calculateAndDisplaySalary(employeeNumber, hoursWorked);
                    textAreaResult.setText(result);

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Invalid input for hours worked. Please enter a valid number.", "Input Error", JOptionPane.ERROR_MESSAGE);
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(frame, ex.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Action listener for view employee button
        buttonViewEmployee.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = employeeTable.getSelectedRow();
                if (selectedRow != -1) {
                    String[] employee = new String[employeeTable.getColumnCount()];
                    for (int i = 0; i < employeeTable.getColumnCount(); i++) {
                        employee[i] = employeeTable.getValueAt(selectedRow, i).toString();
                    }
                    showEmployeeDetails(employee);
                } else {
                    JOptionPane.showMessageDialog(frame, "Please select an employee from the table.", "No Selection", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
    }

    private static String calculateAndDisplaySalary(String employeeNumber, int hoursWorked) {
        for (String[] employee : employeeData) {
            if (employee[0].trim().equals(employeeNumber)) {
                try {
                    double hourlyRate = Double.parseDouble(employee[18].trim()); // Assuming hourly rate is at index 18
                    double grossSalary = hoursWorked * hourlyRate;

                    // Calculate SSS contribution
                    double sssContributionRate = 0.045; // Employee's share of SSS contribution is 4.5%
                    double sssContribution = grossSalary * sssContributionRate;

                    // Calculate PhilHealth contribution
                    double philhealthContributionRate = 0.025; // Employee's share of PhilHealth contribution is 2.5%
                    double philhealthContribution = grossSalary * philhealthContributionRate;

                    // Calculate Pag-IBIG contribution
                    double pagibigContributionRate = 0.03; // Pag-IBIG contribution is 3%
                    double pagibigContribution = grossSalary * pagibigContributionRate;

                    // Calculate net salary after deductions
                    double netSalary = grossSalary - sssContribution - philhealthContribution - pagibigContribution;

                    String employeeName = employee[2] + " " + employee[1]; // Assuming 2 is First Name and 1 is Last Name

                    return "Employee Name : " + employeeName + "\n" +
                            "Gross Salary  : P" + String.format("%.2f", grossSalary) + "\n" +
                            "SSS           : P" + String.format("%.2f", sssContribution) + "\n" +
                            "PhilHealth    : P" + String.format("%.2f", philhealthContribution) + "\n" +
                            "Pag-IBIG      : P" + String.format("%.2f", pagibigContribution) + "\n" +
                            "Net Salary    : P" + String.format("%.2f", netSalary);

                } catch (NumberFormatException e) {
                    return "Invalid hourly rate format for employee.";
                }
            }
        }
        return "Employee not found.";
    }

    private static void showEmployeeDetails(String[] employee) {
        JFrame detailsFrame = new JFrame("Employee Details");
        detailsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        detailsFrame.setSize(400, 300);
        detailsFrame.setLocationRelativeTo(null); // Center the frame on the screen

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Employee Info Panel
        JPanel employeeInfoPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 10, 5, 10);

        String[] labels = {"Employee Number:", "Last Name:", "First Name:", "SSS No.:", "PhilHealth No.:", "TIN:", "Pagibig No."};

        for (int i = 0; i < labels.length; i++) {
            JLabel label = new JLabel(labels[i]);
            JTextField textField = new JTextField(employee[i]);
            textField.setEditable(false);

            employeeInfoPanel.add(label, gbc);
            gbc.gridx++;
            employeeInfoPanel.add(textField, gbc);
            gbc.gridx = 0;
            gbc.gridy++;
        }

        mainPanel.add(employeeInfoPanel, BorderLayout.CENTER);

        // Month selection
        JPanel monthPanel = new JPanel(new BorderLayout());
        JLabel labelMonth = new JLabel("Select Month:");
        String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        JComboBox<String> comboBoxMonth = new JComboBox<>(months);
        monthPanel.add(labelMonth, BorderLayout.WEST);
        monthPanel.add(comboBoxMonth, BorderLayout.CENTER);

        mainPanel.add(monthPanel, BorderLayout.NORTH);

        // Calculate button
        JButton buttonCalculate = new JButton("Calculate Salary");
        buttonCalculate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedMonth = (String) comboBoxMonth.getSelectedItem();

                // Prompt for number of hours worked
                String hoursInput = JOptionPane.showInputDialog(detailsFrame, "Enter the number of hours worked in " + selectedMonth + ":", "Enter Hours Worked", JOptionPane.QUESTION_MESSAGE);
                if (hoursInput == null || hoursInput.isEmpty()) {
                    JOptionPane.showMessageDialog(detailsFrame, "Number of hours worked cannot be empty.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try {
                    int hoursWorked = Integer.parseInt(hoursInput.trim());
                    if (hoursWorked <= 0) {
                        JOptionPane.showMessageDialog(detailsFrame, "Hours worked must be greater than zero.", "Input Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    String result = calculateAndDisplaySalary(employee[0], hoursWorked);
                    JOptionPane.showMessageDialog(detailsFrame, result, "Salary Calculation", JOptionPane.INFORMATION_MESSAGE);

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(detailsFrame, "Invalid input for hours worked. Please enter a valid number.", "Input Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        mainPanel.add(buttonCalculate, BorderLayout.SOUTH);

        detailsFrame.getContentPane().add(mainPanel);
        detailsFrame.setVisible(true);
    }
}
