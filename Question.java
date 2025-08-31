package project;

public class Question {
    private String text;
    private String options;
    private String answer;
    private String difficulty;
    private String topic;
    private String subject;

    public Question(String text, String options, String answer, String difficulty, String topic, String subject) {
        this.text = text;
        this.options = options;
        this.answer = answer;
        this.difficulty = difficulty;
        this.topic = topic;
        this.subject = subject;
    }

    public String getText() {
        return text;
    }

    public String getOptions() {
        return options;
    }

    public String getAnswer() {
        return answer;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public String getTopic() {
        return topic;
    }

    public String getSubject() {
        return subject;
    }

    @Override
    public String toString() {
        return "Question: " + text + "\nOptions: " + String.join(", ", options) + "\nAnswer: " + answer + "\nDifficulty: " + difficulty + "\nTopic: " + topic + "\nSubject: " + subject;
    }
}