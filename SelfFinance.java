import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class SelfFinance {
    private JFrame frame;
    private JLabel balanceLabel, expenseLabel;
    private JTextArea transactionLog, budgetLog;
    private double balance = 0.0, totalExpenses = 0.0;
    private Map<String, Double> categoryBudget = new HashMap<>();
    private Map<String, Double> categoryExpenses = new HashMap<>();
    private Map<String, String> userDatabase = new HashMap<>(); // Stores usernames and passwords

    public SelfFinance() {
        // Load user data from file
        loadUserDatabase();
        
        // Show login or registration dialog
        showLoginOrRegistration();
    }

    private void loadUserDatabase() {
        try {
            File file = new File("user_data.txt");
            if (file.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(":");
                    if (parts.length == 2) {
                        userDatabase.put(parts[0], parts[1]); // username : password
                    }
                }
                reader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveUserDatabase() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("user_data.txt"));
            for (Map.Entry<String, String> entry : userDatabase.entrySet()) {
                writer.write(entry.getKey() + ":" + entry.getValue() + "\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showLoginOrRegistration() {
        while (true) {
            String[] options = {"Login", "Register", "Exit"};
            int choice = JOptionPane.showOptionDialog(null,
                    "Welcome to Self Finance Management\nChoose an option:",
                    "Login/Register",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    options,
                    options[0]);

            if (choice == 0) { // Login
                if (performLogin()) break;
            } else if (choice == 1) { // Register
                performRegistration();
            } else { // Exit
                System.exit(0);
            }
        }
        // Initialize main financial management system after successful login
        initializeMainSystem();
    }

    private boolean performLogin() {
        int attempts = 3;

        while (attempts > 0) {
            String username = JOptionPane.showInputDialog(null, "Enter your username:");
            if (username == null) return false; // User canceled

            String password = JOptionPane.showInputDialog(null, "Enter your password:");
            if (password == null) return false; // User canceled

            if (userDatabase.containsKey(username) && userDatabase.get(username).equals(password)) {
                JOptionPane.showMessageDialog(null, "Login successful! Welcome, " + username + ".");
                return true;
            } else {
                attempts--;
                JOptionPane.showMessageDialog(null, "Incorrect username or password. Attempts left: " + attempts);
            }
        }

        JOptionPane.showMessageDialog(null, "Too many failed attempts. Try again later.");
        System.exit(0);
        return false;
    }

    private void performRegistration() {
        while (true) {
            String username = JOptionPane.showInputDialog(null, "Enter a username for registration:");
            if (username == null) return; // User canceled

            if (userDatabase.containsKey(username)) {
                JOptionPane.showMessageDialog(null, "Username already exists. Try a different username.");
            } else {
                String password = JOptionPane.showInputDialog(null, "Enter a password for registration:");
                if (password == null) return; // User canceled

                userDatabase.put(username, password);
                saveUserDatabase(); // Save the user data after registration
                JOptionPane.showMessageDialog(null, "Registration successful! You can now log in.");
                return;
            }
        }
    }

    private void initializeMainSystem() {
        // Frame setup
        frame = new JFrame("Self Finance Management");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 500);
        frame.setLayout(null);

        // Labels
        balanceLabel = new JLabel("Balance: ₹0.00", SwingConstants.LEFT);
        expenseLabel = new JLabel("Total Expenses: ₹0.00", SwingConstants.LEFT);
        balanceLabel.setBounds(20, 20, 300, 30);
        expenseLabel.setBounds(20, 50, 300, 30);

        balanceLabel.setFont(new Font("Arial", Font.BOLD, 14));
        expenseLabel.setFont(new Font("Arial", Font.BOLD, 14));

        frame.add(balanceLabel);
        frame.add(expenseLabel);

        // Buttons
        JButton addIncomeButton = createButton("Add Income", 20, 100);
        JButton setCategoryBudgetButton = createButton("Set Category Budget", 20, 160);
        JButton addExpenseButton = createButton("Add Expense", 20, 220);

        frame.add(addIncomeButton);
        frame.add(setCategoryBudgetButton);
        frame.add(addExpenseButton);

        // Transaction Log Area
        transactionLog = new JTextArea();
        transactionLog.setEditable(false);
        transactionLog.setFont(new Font("Monospaced", Font.PLAIN, 12));
        transactionLog.setForeground(Color.BLACK);
        transactionLog.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        transactionLog.setBounds(250, 20, 400, 200);

        JScrollPane transactionScrollPane = new JScrollPane(transactionLog);
        transactionScrollPane.setBounds(250, 20, 400, 200);
        frame.add(transactionScrollPane);

        // Budget Log Area
        budgetLog = new JTextArea("Category Budgets:\n");
        budgetLog.setEditable(false);
        budgetLog.setFont(new Font("Monospaced", Font.PLAIN, 12));
        budgetLog.setForeground(Color.BLACK);
        budgetLog.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        budgetLog.setBounds(250, 240, 400, 200);

        JScrollPane budgetScrollPane = new JScrollPane(budgetLog);
        budgetScrollPane.setBounds(250, 240, 400, 200);
        frame.add(budgetScrollPane);

        // Button Actions
        addIncomeButton.addActionListener(e -> addIncome());
        setCategoryBudgetButton.addActionListener(e -> setCategoryBudget());
        addExpenseButton.addActionListener(e -> addExpense());

        frame.setVisible(true);
    }

    private JButton createButton(String text, int x, int y) {
        JButton button = new JButton(text);
        button.setBounds(x, y, 200, 40);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setBackground(new Color(220, 240, 255));
        button.setForeground(Color.BLACK);
        return button;
    }

    private void addIncome() {
        String description = JOptionPane.showInputDialog(frame, "Enter description for income:");
        String amountStr = JOptionPane.showInputDialog(frame, "Enter income amount (₹):");

        if (description != null && amountStr != null) {
            try {
                double amount = Double.parseDouble(amountStr);
                balance += amount;
                transactionLog.append("Income | " + description + " | ₹" + String.format("%.2f", amount) + "\n");
                updateLabels();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Invalid amount. Please try again.");
            }
        }
    }

    private void setCategoryBudget() {
        String category = JOptionPane.showInputDialog(frame, "Enter category name (e.g., Shopping, Groceries, etc.):");
        String budgetStr = JOptionPane.showInputDialog(frame, "Enter budget amount for " + category + " (₹):");

        if (category != null && budgetStr != null) {
            try {
                double budget = Double.parseDouble(budgetStr);
                categoryBudget.put(category, budget);
                budgetLog.append(category + ": ₹" + String.format("%.2f", budget) + "\n");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Invalid amount. Please try again.");
            }
        }
    }

    private void addExpense() {
        String category = JOptionPane.showInputDialog(frame, "Enter category for expense (e.g., Shopping, Groceries, etc.):");
        String description = JOptionPane.showInputDialog(frame, "Enter description for expense:");
        String amountStr = JOptionPane.showInputDialog(frame, "Enter expense amount (₹):");

        if (category != null && description != null && amountStr != null) {
            try {
                double amount = Double.parseDouble(amountStr);
                totalExpenses += amount;

                // Track category-wise expenses
                categoryExpenses.put(category, categoryExpenses.getOrDefault(category, 0.0) + amount);

                // Check if the expense exceeds the budget
                if (categoryBudget.containsKey(category) && categoryExpenses.get(category) > categoryBudget.get(category)) {
                    JOptionPane.showMessageDialog(frame, "Warning: Expense exceeds the budget for " + category);
                }

                transactionLog.append("Expense | " + description + " | ₹" + String.format("%.2f", amount) + "\n");
                updateLabels();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Invalid amount. Please try again.");
            }
        }
    }

    private void updateLabels() {
        balanceLabel.setText("Balance: ₹" + String.format("%.2f", balance));
        expenseLabel.setText("Total Expenses: ₹" + String.format("%.2f", totalExpenses));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SelfFinance::new);
    }
}
