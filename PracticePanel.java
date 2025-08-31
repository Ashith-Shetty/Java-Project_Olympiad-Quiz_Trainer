package project;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class PracticePanel extends JPanel implements ActionListener {

    private List<Question> questions;
    private int currentQuestionIndex = 0;
    private JLabel questionLabel;
    private JRadioButton[] optionButtons;
    private ButtonGroup optionsGroup;
    private JButton nextButton;
    private JButton prevButton;
    private JButton submitButton;
    private JLabel feedbackLabel;
    private JPanel questionArea;
    private JPanel navigationArea;
    private boolean answerChecked = false;

    public PracticePanel(List<Question> questions) {
        this.questions = questions;
        setLayout(new BorderLayout());

        questionArea = new JPanel(new BorderLayout());
        questionLabel = new JLabel();
        questionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        JPanel optionsPanel = new JPanel(new GridLayout(0, 1));
        optionButtons = new JRadioButton[4];
        optionsGroup = new ButtonGroup();
        for (int i = 0; i < 4; i++) {
            optionButtons[i] = new JRadioButton();
            optionsGroup.add(optionButtons[i]);
            optionsPanel.add(optionButtons[i]);
        }
        questionArea.add(questionLabel, BorderLayout.NORTH);
        questionArea.add(optionsPanel, BorderLayout.CENTER);
        feedbackLabel = new JLabel("");
        feedbackLabel.setHorizontalAlignment(SwingConstants.CENTER);
        questionArea.add(feedbackLabel, BorderLayout.SOUTH);
        add(questionArea, BorderLayout.CENTER);

        navigationArea = new JPanel(new FlowLayout(FlowLayout.CENTER));
        prevButton = new JButton("Previous");
        nextButton = new JButton("Next");
        submitButton = new JButton("Submit");
        prevButton.addActionListener(this);
        nextButton.addActionListener(this);
        submitButton.addActionListener(this);
        prevButton.setEnabled(false);
        navigationArea.add(prevButton);
        navigationArea.add(nextButton);
        navigationArea.add(submitButton);
        add(navigationArea, BorderLayout.SOUTH);

        loadQuestion();
    }

    private void loadQuestion() {
        if (currentQuestionIndex >= 0 && currentQuestionIndex < questions.size()) {
            Question currentQuestion = questions.get(currentQuestionIndex);
            questionLabel.setText("(" + (currentQuestionIndex + 1) + "/" + questions.size() + ") " + currentQuestion.getText());
            String[] optionsTemp = currentQuestion.getOptions().split("[a-z]\\)");
            
            String[] options = new String[4];
          
            for(int i = 0; i < 4 ; i++)
            {
            	options[i] = optionsTemp[i+1];
            }
            optionsGroup.clearSelection(); // Ensure no option is pre-selected
            for (int i = 0; i < optionButtons.length; i++) {
                optionButtons[i].setText(options[i].trim());
                optionButtons[i].setSelected(false);
                optionButtons[i].setVisible(i < options.length);
                optionButtons[i].setEnabled(!answerChecked); // Enable options if not checked
            }
            feedbackLabel.setText("");
            prevButton.setEnabled(currentQuestionIndex > 0);
            nextButton.setEnabled(currentQuestionIndex < questions.size() - 1);
            submitButton.setEnabled(!answerChecked);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == nextButton) {
            currentQuestionIndex++;
            loadQuestion();
            answerChecked = false; // Reset flag on navigation
            // Re-enable buttons in case they were disabled after a previous submit
            for (JRadioButton button : optionButtons) {
                button.setEnabled(true);
            }
            submitButton.setEnabled(true);
        } else if (e.getSource() == prevButton) {
            currentQuestionIndex--;
            loadQuestion();
            answerChecked = false; // Reset flag on navigation
            // Re-enable buttons
            for (JRadioButton button : optionButtons) {
                button.setEnabled(true);
            }
            submitButton.setEnabled(true);
        } else if (e.getSource() == submitButton) {
            checkAnswer();
            answerChecked = true;
            prevButton.setEnabled(currentQuestionIndex > 0);
            nextButton.setEnabled(currentQuestionIndex < questions.size() - 1);
            submitButton.setEnabled(false); // Disable submit after checking
            // Disable options after submitting
            for (JRadioButton button : optionButtons) {
                button.setEnabled(false);
            }
        } else if (e.getSource() instanceof JRadioButton) {
            // Enable submit button when an option is selected
            submitButton.setEnabled(!answerChecked);
        }
    }

    private void checkAnswer() {
        String selectedAnswer = null;
        for (int i = 0; i < optionButtons.length; i++) {
            if (optionButtons[i].isSelected()) {
                selectedAnswer = optionButtons[i].getText();
                break;
            }
        }
        String correctAnswer = questions.get(currentQuestionIndex).getAnswer().trim().substring(3);
        if (selectedAnswer != null && selectedAnswer.trim().equals(correctAnswer)) {
            feedbackLabel.setText("Correct!");
            feedbackLabel.setForeground(Color.GREEN);
        } else {
            feedbackLabel.setText("Incorrect. The correct answer is: " + correctAnswer);
            feedbackLabel.setForeground(Color.RED);
        }
    }
}