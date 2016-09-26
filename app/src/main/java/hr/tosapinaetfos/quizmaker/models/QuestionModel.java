package hr.tosapinaetfos.quizmaker.models;

/**
 * Created by tosap on 05.05.2016..
 */
public class QuestionModel {

    String questionId, questionName, questionType, quizIdFk;

    //Constructor
    public QuestionModel(String questionId, String questionName, String questionType, String quizIdFk) {
        this.questionId = questionId;
        this.questionName = questionName;
        this.questionType = questionType;
        this.quizIdFk = quizIdFk;
    }

    //Getters and setters
    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getQuestionName() {
        return questionName;
    }

    public void setQuestionName(String questionName) {
        this.questionName = questionName;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public String getQuizIdFk() {
        return quizIdFk;
    }

    public void setQuizIdFk(String quizIdFk) {
        this.quizIdFk = quizIdFk;
    }

    //To string
    @Override
    public String toString() {
        return "QuestionModel{" +
                "questionId='" + questionId + '\'' +
                ", questionName='" + questionName + '\'' +
                ", questionType='" + questionType + '\'' +
                ", quizIdFk='" + quizIdFk + '\'' +
                '}';
    }
}
