package hr.tosapinaetfos.quizmaker.models;

/**
 * Created by tosap on 04.05.2016..
 */
public class QuizModel {

    String quizId, quizName, quizDateOfCreation, quizKey, quizDuration;

    //Quiz model constructor
    public QuizModel(String quiz_Id, String quiz_Name, String quiz_Key, String quiz_DateOfCreation, String quiz_Duration ){
        quizId = quiz_Id;
        quizName = quiz_Name;
        quizDateOfCreation = quiz_DateOfCreation;
        quizKey = quiz_Key;
        quizDuration = quiz_Duration;
    }

    //Quiz model getters and setters
    public String getQuizId() {
        return quizId;
    }

    public void setQuizId(String quizId) {
        this.quizId = quizId;
    }

    public String getQuizName() {
        return quizName;
    }

    public void setQuizName(String quizName) {
        this.quizName = quizName;
    }

    public String getQuizDateOfCreation() {
        return quizDateOfCreation;
    }

    public void setQuizDateOfCreation(String quizDateOfCreation) {
        this.quizDateOfCreation = quizDateOfCreation;
    }

    public String getQuizKey() {
        return quizKey;
    }

    public void setQuizKey(String quizKey) {
        this.quizKey = quizKey;
    }

    public String getQuizDuration() {
        return quizDuration;
    }

    public void setQuizDuration(String quizDuration) {
        this.quizDuration= quizDuration;
    }

    @Override
    public String toString() {
        return "QuizModel{" +
                "quizId='" + quizId + '\'' +
                ", quizName='" + quizName + '\'' +
                ", quizDateOfCreation='" + quizDateOfCreation + '\'' +
                ", quizKey='" + quizKey + '\'' +
                ", quizDuration='" + quizDuration + '\'' +
                '}';
    }
}
