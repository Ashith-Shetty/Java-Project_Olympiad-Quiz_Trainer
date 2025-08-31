package project;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public class QuizPanel extends JPanel implements ActionListener {

    private MainApp mainApp;
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
    private int score = 0;
    private int totalQuestions;
    private Timer timer;
    private int timeLeft;
    private JLabel timerLabel;
    private List<AnswerRecord> answerRecords = new ArrayList<>();
    private String[] userAnswers; // To store user's selection for each question

    private static class AnswerRecord {
        String questionText;
        String userAnswer;
        String correctAnswer;
    }

    public QuizPanel(MainApp mainApp, List<Question> questions, int numberOfQuestions, int durationSeconds) {
        this.mainApp = mainApp;
        this.questions = new ArrayList<>(questions);
        Collections.shuffle(this.questions);
        this.totalQuestions = Math.min(numberOfQuestions, this.questions.size());
        this.questions = this.questions.subList(0, this.totalQuestions);
        this.timeLeft = durationSeconds;
        this.userAnswers = new String[this.totalQuestions]; // Initialize userAnswers array

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

        timerLabel = new JLabel(formatTime(timeLeft));
        timerLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        add(timerLabel, BorderLayout.NORTH);

        startTimer();
        loadQuestion();
        updateSubmitButtonVisibility();
    }

    private void startTimer() {
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timeLeft--;
                timerLabel.setText(formatTime(timeLeft));
                if (timeLeft <= 0) {
                    timer.stop();
                    endQuiz();
                }
            }
        });
        timer.start();
    }

    private String formatTime(int seconds) {
        int minutes = seconds / 60;
        int remainingSeconds = seconds % 60;
        return String.format("%02d:%02d", minutes, remainingSeconds);
    }

    private void loadQuestion() {
        if (currentQuestionIndex >= 0 && currentQuestionIndex < totalQuestions) {
            Question currentQuestion = questions.get(currentQuestionIndex);
            questionLabel.setText("(" + (currentQuestionIndex + 1) + "/" + totalQuestions + ") " + currentQuestion.getText());
            String[] optionsTemp = currentQuestion.getOptions().split("[a-z]\\)");
            
            String[] options = new String[4];
          
            for(int i = 0; i < 4 ; i++)
            {
            	options[i] = optionsTemp[i+1];
            }

            // Adjust the number of option buttons if necessary
            if (optionButtons.length < options.length) {
                JPanel optionsPanel = (JPanel) questionArea.getComponent(1);
                optionsPanel.removeAll();
                optionButtons = new JRadioButton[options.length];
                optionsGroup = new ButtonGroup();
                for (int i = 0; i < options.length; i++) {
                    optionButtons[i] = new JRadioButton();
                    optionsGroup.add(optionButtons[i]);
                    optionsPanel.add(optionButtons[i]);
                }
                optionsPanel.setLayout(new GridLayout(0, 1));
                questionArea.revalidate();
                questionArea.repaint();
            } else {
                for (int i = 0; i < optionButtons.length; i++) {
                    optionButtons[i].setVisible(i < options.length);
                }
            }

            optionsGroup.clearSelection(); // Deselect all initially

            for (int i = 0; i < options.length; i++) {
                optionButtons[i].setText(options[i].trim());
                optionButtons[i].setSelected(options[i].trim().equals(userAnswers[currentQuestionIndex])); // Re-select if previously answered
            }
            feedbackLabel.setText("");
            prevButton.setEnabled(currentQuestionIndex > 0);
            nextButton.setEnabled(currentQuestionIndex < totalQuestions - 1);
            updateSubmitButtonVisibility();
        } else {
            endQuiz();
        }
    }

    private void updateSubmitButtonVisibility() {
        submitButton.setEnabled(currentQuestionIndex == totalQuestions - 1);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == nextButton) {
        	recordCurrentAnswer();
            currentQuestionIndex++;
            loadQuestion();
        } else if (e.getSource() == prevButton) {
        	recordCurrentAnswer();
            currentQuestionIndex--;
            loadQuestion();
        } else if (e.getSource() == submitButton) {
        	recordCurrentAnswer();
            timer.stop();
            showQuizResults();
        } else if (e.getSource() instanceof JRadioButton) {
            recordCurrentAnswer(); // Record immediately when an option is selected
        }
    }

    private void recordCurrentAnswer() {
        for (int i = 0; i < optionButtons.length; i++) {
            if (optionButtons[i].isSelected()) {
                userAnswers[currentQuestionIndex] = optionButtons[i].getText().trim();
                break;
            }
        }
    }

    private void recordAnswer() {
        String selectedAnswer = null;
        for (int i = 0; i < optionButtons.length; i++) {
            if (optionButtons[i].isSelected()) {
                selectedAnswer = optionButtons[i].getText().trim();
                break;
            }
        }
        Question currentQuestion = questions.get(currentQuestionIndex);
        answerRecords.add(new AnswerRecord());
        answerRecords.get(answerRecords.size() - 1).questionText = currentQuestion.getText();
        answerRecords.get(answerRecords.size() - 1).userAnswer = userAnswers[currentQuestionIndex] == null ? "Not Attempted" : userAnswers[currentQuestionIndex];
        answerRecords.get(answerRecords.size() - 1).correctAnswer = currentQuestion.getAnswer().trim();
        if (userAnswers[currentQuestionIndex] != null && userAnswers[currentQuestionIndex].equals(currentQuestion.getAnswer().trim())) {
            // Score is calculated only once at the end in showQuizResults
        }
    }

    private void showQuizResults() {
        score = 0; // Reset score before calculating
        StringBuilder results = new StringBuilder("Quiz Ended! Your score: 0/" + totalQuestions + "\n\nIncorrect/Not Attempted Questions:\n");
        for (int i = 0; i < totalQuestions; i++) {
            Question question = questions.get(i);
            String userAnswer = userAnswers[i] == null ? "Not Attempted" : userAnswers[i];
            String correctAnswer = question.getAnswer().trim().substring(3);
            if (!userAnswer.equals(correctAnswer)) {
                results.append("Question: ").append(question.getText()).append("\n");
                results.append("Your Answer: ").append(userAnswer).append("\n");
                results.append("Correct Answer: ").append(correctAnswer).append("\n\n");
            }
            if (userAnswers[i] != null && userAnswer.equals(correctAnswer)){
                score++;
            }
        }
        results.insert(results.indexOf("/"), score); // Update the score in the results string
        JOptionPane.showMessageDialog(this, results.toString());
        mainApp.switchToInitialScreen();
    }

    private void endQuiz() {
        recordAnswer(); // Record answer for the last question if time runs out
        timer.stop();
        showQuizResults();
    }
}