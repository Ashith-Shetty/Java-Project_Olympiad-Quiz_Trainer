package project;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MainApp extends JFrame implements ActionListener {

    private JMenuBar menuBar;
    private JMenu practiceMenu;
    private JMenuItem mathPracticeItem;
    private JMenuItem physicsPracticeItem;
    private JMenu quizMenu;
    private JMenuItem mathQuizItem;
    private JMenuItem physicsQuizItem;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private static final String INITIAL_PANEL = "Initial";
    private static final String PRACTICE_PANEL = "Practice";
    private static final String QUIZ_PANEL = "Quiz";
    private JPanel initialPanel;

    public MainApp() {
        setTitle("Olympiad Prep Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600); // Increased size for better visibility
        setLocationRelativeTo(null); // Center the window
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        add(contentPanel, BorderLayout.CENTER);

        // Initial Panel
        createInitialPanel();
        contentPanel.add(initialPanel, INITIAL_PANEL);

        // Menu Bar
        createMenuBar();
        setJMenuBar(menuBar);

        cardLayout.show(contentPanel, INITIAL_PANEL);
        setVisible(true);
    }

    private void createInitialPanel() {
        initialPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Set a background color (light blue)
                g.setColor(new Color(220, 240, 255));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        initialPanel.setLayout(new GridBagLayout()); // Use GridBagLayout for better control

        JLabel welcomeLabel = new JLabel("Welcome to the Olympiad Prep!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));

        JButton practiceButton = new JButton("Practice");
        JButton quizButton = new JButton("Quiz");

        // Style the buttons
        styleButton(practiceButton);
        styleButton(quizButton);

        practiceButton.addActionListener(e -> showSubjectSelection("Practice"));
        quizButton.addActionListener(e -> showSubjectSelection("Quiz"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(20, 20, 20, 20); // Padding
        initialPanel.add(welcomeLabel, gbc);

        gbc.gridy = 1;
        initialPanel.add(practiceButton, gbc);

        gbc.gridy = 2;
        initialPanel.add(quizButton, gbc);
    }

    private void styleButton(JButton button) {
        button.setPreferredSize(new Dimension(200, 60)); // Larger size
        button.setFont(new Font("Verdana", Font.PLAIN, 18));
        button.setBackground(new Color(255, 230, 200)); // Light orange
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createRaisedBevelBorder());

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(240, 220, 190));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(255, 230, 200));
            }
        });
    }

    private void createMenuBar() {
        menuBar = new JMenuBar();

        practiceMenu = new JMenu("Practice");
        practiceMenu.setForeground(new Color(0, 100, 0)); // Dark green
        mathPracticeItem = new JMenuItem("Math Practice");
        physicsPracticeItem = new JMenuItem("Physics Practice");
        mathPracticeItem.addActionListener(this);
        physicsPracticeItem.addActionListener(this);
        practiceMenu.add(mathPracticeItem);
        practiceMenu.add(physicsPracticeItem);
        menuBar.add(practiceMenu);

        quizMenu = new JMenu("Quiz");
        quizMenu.setForeground(new Color(0, 100, 0));
        mathQuizItem = new JMenuItem("Math Quiz (10 Questions, 10 minutes)");
        physicsQuizItem = new JMenuItem("Physics Quiz (10 Questions, 10 minutes)");
        mathQuizItem.addActionListener(this);
        physicsQuizItem.addActionListener(this);
        quizMenu.add(mathQuizItem);
        quizMenu.add(physicsQuizItem);
        menuBar.add(quizMenu);
    }

    private void showSubjectSelection(String mode) {
        JPanel subjectPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Light yellow background
                g.setColor(new Color(255, 250, 205));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel selectLabel = new JLabel("Select Subject:");
        selectLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        subjectPanel.add(selectLabel, gbc);

        String[] subjects = {"Math", "Physics"};
        JComboBox<String> subjectComboBox = new JComboBox<>(subjects);
        subjectComboBox.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subjectComboBox.setBackground(new Color(240, 240, 240));

        gbc.gridy = 1;
        subjectPanel.add(subjectComboBox, gbc);

        JButton goButton = new JButton("Go");
        styleButton(goButton);
        goButton.setPreferredSize(new Dimension(120, 40));
        goButton.addActionListener(e -> {
            String selectedSubject = ((String) subjectComboBox.getSelectedItem()).toLowerCase();
            try {
                if (mode.equals("Practice")) {
                    loadAndShowPractice(selectedSubject);
                } else if (mode.equals("Quiz")) {
                    loadAndStartQuiz(selectedSubject, 10, 600);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error loading " + selectedSubject + " questions.", "Database Error", JOptionPane.ERROR_MESSAGE);
                cardLayout.show(contentPanel, INITIAL_PANEL); // Go back to initial if error
            }
            ((JDialog) SwingUtilities.getWindowAncestor(goButton)).dispose(); // Close the dialog
        });

        gbc.gridy = 2;
        subjectPanel.add(goButton, gbc);

        JDialog dialog = new JDialog(this, mode + " Selection", true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setContentPane(subjectPanel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }


    private void loadAndShowPractice(String subject) throws SQLException {
        List<Question> allQuestions = new ArrayList<>();
        if (subject.equals("math")) {
            List<Question> algebraQuestions = DataManager.loadQuestions("math", "Algebra");
            List<Question> geometryQuestions = DataManager.loadQuestions("math", "Geometry");
            if (algebraQuestions != null) allQuestions.addAll(algebraQuestions);
            if (geometryQuestions != null) allQuestions.addAll(geometryQuestions);
        } else if (subject.equals("physics")) {
            List<Question> mechanicsQuestions = DataManager.loadQuestions("physics", "Mechanics");
            List<Question> opticsQuestions = DataManager.loadQuestions("physics", "Optics");
            if (mechanicsQuestions != null) allQuestions.addAll(mechanicsQuestions);
            if (opticsQuestions != null) allQuestions.addAll(opticsQuestions);
        }

        if (!allQuestions.isEmpty()) {
            Component[] components = contentPanel.getComponents();
            for (Component comp : components) {
                if (comp instanceof PracticePanel) {
                    contentPanel.remove(comp);
                    break;
                }
            }
            contentPanel.add(new PracticePanel(allQuestions), PRACTICE_PANEL);
            cardLayout.show(contentPanel, PRACTICE_PANEL);
            contentPanel.revalidate();
            contentPanel.repaint();
        } else {
            JOptionPane.showMessageDialog(this, "No " + subject + " questions available for practice yet.");
            cardLayout.show(contentPanel, INITIAL_PANEL);
        }
    }

    private void loadAndStartQuiz(String subject, int numberOfQuestions, int durationSeconds) throws SQLException {
        List<Question> allQuestions = new ArrayList<>();
        if (subject.equals("math")) {
            List<Question> algebraQuestions = DataManager.loadQuestions("math", "Algebra");
            List<Question> calculusQuestions = DataManager.loadQuestions("math", "Calculus");
            if (algebraQuestions != null) allQuestions.addAll(algebraQuestions);
            if (calculusQuestions != null) allQuestions.addAll(calculusQuestions);

        } else if (subject.equals("physics")) {
            List<Question> mechanicsQuestions = DataManager.loadQuestions("physics", "Mechanics");
            List<Question> opticsQuestions = DataManager.loadQuestions("physics", "Optics");
            if (mechanicsQuestions != null) allQuestions.addAll(mechanicsQuestions);
            if (opticsQuestions != null) allQuestions.addAll(opticsQuestions);
        }

        if (!allQuestions.isEmpty()) {
            Component[] components = contentPanel.getComponents();
            for (Component comp : components) {
                if (comp instanceof QuizPanel) {
                    contentPanel.remove(comp);
                    break;
                }
            }
            QuizPanel quizPanel = new QuizPanel(this, allQuestions, numberOfQuestions, durationSeconds);
            contentPanel.add(quizPanel, QUIZ_PANEL);
            cardLayout.show(contentPanel, QUIZ_PANEL);
            contentPanel.revalidate();
            contentPanel.repaint();
        } else {
            JOptionPane.showMessageDialog(this, "No " + subject + " questions available for a quiz yet.");
            cardLayout.show(contentPanel, INITIAL_PANEL);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            if (e.getSource() == mathPracticeItem) {
                loadAndShowPractice("math");
            } else if (e.getSource() == physicsPracticeItem) {
                loadAndShowPractice("physics");
            } else if (e.getSource() == mathQuizItem) {
                loadAndStartQuiz("math", 10, 600);
            } else if (e.getSource() == physicsQuizItem) {
                loadAndStartQuiz("physics", 10, 600);
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }

    public void switchToInitialScreen() {
        cardLayout.show(contentPanel, INITIAL_PANEL);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainApp::new);
    }
}