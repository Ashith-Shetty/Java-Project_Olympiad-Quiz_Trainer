package project;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DataManager {

    public static List<Question> loadQuestions(String subject, String topic) throws SQLException {
        List<Question> questions = new ArrayList<>();
        Connection connection = MySQLDB.getConnection();
    	String query = "select * from " + subject;
    	Statement stmt = connection.createStatement();
    	ResultSet results = stmt.executeQuery(query);
 
    	while (results.next()) {
            String question = results.getString("question");
            String options = results.getString("options");
            String answer = results.getString("answer");
            String difficulty =  results.getString("level");
            topic = results.getString("topic");
            Question q = new Question(question, options, answer, difficulty, topic, subject);
            questions.add(q);
        }
        return questions;
    }

    // You might add methods here to get lists of subjects and topics from your data files
}