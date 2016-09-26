package hr.tosapinaetfos.quizmaker.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import hr.tosapinaetfos.quizmaker.models.QuestionModel;
import hr.tosapinaetfos.quizmaker.models.QuizModel;

/**
 * Created by tosap on 04.05.2016..
 */
public class DBHelperQuizCreator extends SQLiteOpenHelper{

    //Database version , important to database upgrade
    private static final int DATABASE_VERSION = 15;

    //Database name and name of all tables
    static final String DATABASE_NAME = "QuizCreator";
    static final String TABLE_QUIZ = "Quiz";
    static final String TABLE_QUESTION = "Question";
    static final String TABLE_MCQ_ANSWER = "Mcq_answer";
    static final String TABLE_S_ANSWER = "S_answer";


    //Atributes of table Quiz
    static final String KEY_QUIZ_ID = "quiz_id";
    static final String KEY_QUIZ_NAME = "quiz_name";
    static final String KEY_QUIZ_DATE_OF_CREATION = "quiz_dateofcreation";
    static final String KEY_QUIZ_KEY = "quiz_key";
    static final String KEY_USER_ID_FK = "user_id_fk";


    //Atributes of table Question
    static final String KEY_QUESTION_ID = "question_id";
    static final String KEY_QUESTION_NAME = "question_name";
    static final String KEY_QUESTION_TYPE = "question_type";
    static final String KEY_QUIZ_ID_FK = "quiz_id_fk";

    //Atributes of table TABLE_MCQ_ANSWER
    static final String KEY_MCQ_ANSWER_ID = "msq_answer_id";
    static final String KEY_MCQ_ANSWER_NAME = "mcq_answer_name";
    static final String KEY_MCQ_ANSWER_TYPE = "mcq_answer_type";
    static final String KEY_QUESTION_ID_FK = "question_id_fk";

    //Atributes of table TABLE_S_ANSWER
    static final String KEY_S_ANSWER_ID = "s_answer_id";
    static final String KEY_S_ANSWER_NAME = "s_answer_name";



    // Constructor
    public DBHelperQuizCreator(Context context){ super(context, DATABASE_NAME, null, DATABASE_VERSION); }

    // First initialization
    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_QUIZ_TABLE = "CREATE TABLE " + TABLE_QUIZ
                + "(" + KEY_QUIZ_ID  + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_QUIZ_NAME + " TEXT,"
                + KEY_QUIZ_DATE_OF_CREATION + "  DATE, "
                + KEY_QUIZ_KEY + " TEXT, "
                + KEY_USER_ID_FK + " INTEGER "
                +")";

        String CREATE_QUESTION_TABLE = "CREATE TABLE " + TABLE_QUESTION
                + "(" + KEY_QUESTION_ID  + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_QUESTION_NAME + " TEXT,"
                +  KEY_QUESTION_TYPE + "  TEXT, "
                + KEY_QUIZ_ID_FK + " INTEGER, "
                + " FOREIGN KEY ("+KEY_QUIZ_ID_FK+") REFERENCES "+TABLE_QUIZ+"("+KEY_QUIZ_ID+"));";

        String CREATE_MCQ_ANSWER_TABLE = "CREATE TABLE " + TABLE_MCQ_ANSWER
                + "(" + KEY_MCQ_ANSWER_ID  + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_MCQ_ANSWER_NAME + " TEXT,"
                +  KEY_MCQ_ANSWER_TYPE + "  TEXT, "
                + KEY_QUESTION_ID_FK + " INTEGER, "
                + " FOREIGN KEY ("+KEY_QUESTION_ID_FK +") REFERENCES "+TABLE_QUESTION+"("+KEY_QUESTION_ID+"));";

        String CREATE_S_ANSWER_TABLE = "CREATE TABLE " + TABLE_S_ANSWER
                + "(" + KEY_S_ANSWER_ID  + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_S_ANSWER_NAME + " TEXT,"
                + KEY_QUESTION_ID_FK + " INTEGER, "
                + " FOREIGN KEY ("+KEY_QUESTION_ID_FK +") REFERENCES "+TABLE_QUESTION+"("+KEY_QUESTION_ID+"));";

        db.execSQL(CREATE_QUIZ_TABLE);
        db.execSQL(CREATE_QUESTION_TABLE);
        db.execSQL(CREATE_MCQ_ANSWER_TABLE);
        db.execSQL(CREATE_S_ANSWER_TABLE);

    }

    //  Upgrade
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,int newVersion){
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUIZ);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUESTION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MCQ_ANSWER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_S_ANSWER);
        onCreate(db);
    }

    // Method returns ArrayList of QuizModel objects
    public ArrayList<QuizModel> getQuizzes(String user_id){
        //Create arraylist QuizModel type
        ArrayList<QuizModel> quizList = new ArrayList<QuizModel>();

        SQLiteDatabase db = this.getWritableDatabase();
        Log.d("test", user_id);

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_QUIZ + " WHERE " + KEY_USER_ID_FK + " = '" + user_id + "'", null);
        Log.d("test", user_id);
        //Loop through table Quiz
        if(cursor.moveToFirst()){
            do{
                QuizModel quiz = new QuizModel(
                        cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(3),
                        cursor.getString(2),
                        cursor.getString(4));
                quizList.add(quiz);
            }while(cursor.moveToNext());
        }
        Log.d("test", user_id);
        db.close();
        Log.d("test", user_id);
        return quizList;
    }

    // Brisanje po ID-u
    public void deleteQuizById(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_QUIZ, KEY_QUIZ_ID + "=?", new String[]{id});
        db.delete(TABLE_QUESTION, KEY_QUIZ_ID_FK + "=?", new String[]{id});
        db.close();
    }

    // Method returns ArrayList of QuizModel objects
    public ArrayList<QuestionModel> getQuestions(String QuizID){

        //Create arraylist QuizModel type
        ArrayList<QuestionModel> questionList = new ArrayList<QuestionModel>();

        SQLiteDatabase db = this.getWritableDatabase();


        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_QUESTION + " WHERE " + KEY_QUIZ_ID_FK + "=?", new String[]{QuizID});

        //Loop through table Quiz
        if(cursor.moveToFirst()){
            do{
                QuestionModel question = new QuestionModel(
                        cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3));
                questionList.add(question);
            }while(cursor.moveToNext());
        }
        db.close();
        return questionList;
    }

    // Brisanje po ID-u
    public void deleteQuestionById(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_QUESTION, KEY_QUESTION_ID + "=?", new String[]{id});
        db.delete(TABLE_MCQ_ANSWER, KEY_QUESTION_ID_FK + "=?", new String[]{id});
        db.close();
    }

    //Insert new Quiz
    public String insertNewQuiz(String quizName, String quizKey, String UserID, Context c) {
        Context ctx = c;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(new Date());
        values.put(KEY_QUIZ_NAME, quizName);
        values.put(KEY_QUIZ_DATE_OF_CREATION, date);
        values.put(KEY_QUIZ_KEY, quizKey);
        values.put(KEY_USER_ID_FK, UserID);
        long result = db.insert(TABLE_QUIZ, null, values); //AKO NIJE USPJEŠNO DODAN NOVI RED , VRATIT ĆE -1

        //if inserted successful get inserted QUIZID
        if (result != -1) {
            String selectQuery= "SELECT * FROM " + TABLE_QUIZ +" ORDER BY " + KEY_QUIZ_ID +  " DESC LIMIT 1";
            Cursor cursor = db.rawQuery(selectQuery, null);
            String lastQuizId = "";
            if(cursor.moveToFirst())
                lastQuizId  =  cursor.getString( cursor.getColumnIndex(KEY_QUIZ_ID));
            cursor.close();
            return lastQuizId;
        }
        return " ";
    }

    //Insert new Quiz
    public String insertNewQuestion(String questionName, String questionType, String quizID) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_QUESTION_NAME, questionName);
        values.put(KEY_QUESTION_TYPE, questionType);
        values.put(KEY_QUIZ_ID_FK, quizID);

        long result = db.insert(TABLE_QUESTION, null, values); //AKO NIJE USPJEŠNO DODAN NOVI RED , VRATIT ĆE -1

        //if inserted successful get inserted questionID
        if (result != -1) {
            String selectQuery= "SELECT * FROM " + TABLE_QUESTION +" ORDER BY " + KEY_QUESTION_ID +  " DESC LIMIT 1";
            Cursor cursor = db.rawQuery(selectQuery, null);
            String lastQuestionId = "";
            if(cursor.moveToFirst())
                lastQuestionId  =  cursor.getString( cursor.getColumnIndex(KEY_QUESTION_ID));
            cursor.close();
            return lastQuestionId;
        }
        return " ";
    }

    public Boolean insertNewMCQAnswer(String ansName, String ansType, String questionID){


        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_MCQ_ANSWER_NAME, ansName);
        values.put(KEY_MCQ_ANSWER_TYPE, ansType);
        values.put(KEY_QUESTION_ID_FK, questionID);

        long result = db.insert(TABLE_MCQ_ANSWER, null, values);

        //if inserted successful get inserted questionID
        if (result != -1) {
            return true;
        }
        return false;
    }

    public Boolean insertNewStandardAnswer(String ansName, String questionID){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_S_ANSWER_NAME, ansName);
        values.put(KEY_QUESTION_ID_FK, questionID);

        long result = db.insert(TABLE_S_ANSWER, null, values);

        //if inserted successful get inserted questionID
        if (result != -1) {
            return true;
        }
        return false;
    }

    public JSONArray dataByQuizID(String QuizID, String QuizName, String QuizDate, String QuizKey, String UserID){
        SQLiteDatabase db = this.getWritableDatabase();
        String insertedID  = " ";
        int a = 0;
        String searchQuery = "SELECT Question.question_id, Question.question_name, Question.question_type, " +
                            "S_answer.s_answer_id, S_answer.s_answer_name, " +
                            "Mcq_answer.msq_answer_id, Mcq_answer.mcq_answer_type, Mcq_answer.mcq_answer_name " +
                            "FROM Quiz " +
                            "left join Question on Question.quiz_id_fk = Quiz.quiz_id " +
                            "left join S_answer on S_answer.question_id_fk = Question.question_id " +
                            "left join Mcq_answer on Mcq_answer.question_id_fk = Question.question_id " +
                            "WHERE quiz_id = '" + QuizID + "'";

        Cursor cursor = db.rawQuery(searchQuery, null );
        JSONArray resultSet = new JSONArray();

        cursor.moveToFirst();

        resultSet.put(UserID);
        resultSet.put(QuizID);
        resultSet.put(QuizName);
        resultSet.put(QuizDate);
        resultSet.put(QuizKey);

        cursor.moveToFirst();

        String testId = "";
        int counter = 0;
        JSONObject questionObject = new JSONObject();
        JSONArray answersArray = new JSONArray();
        int totalColumn = cursor.getColumnCount();

        while (cursor.isAfterLast() == false) {
            JSONObject answerObject = new JSONObject();
            if(!testId.equals(cursor.getString(0))){
                try {
                    questionObject.put(cursor.getColumnName(0), cursor.getString(0));
                    questionObject.put(cursor.getColumnName(1), cursor.getString(1));
                    questionObject.put(cursor.getColumnName(2), cursor.getString(2));
                    testId = cursor.getString(0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            try {

                if(cursor.getColumnName(3) != null) answerObject.put(cursor.getColumnName(3), cursor.getString(3));
                if(cursor.getColumnName(4) != null) answerObject.put(cursor.getColumnName(4), cursor.getString(4));
                if(cursor.getColumnName(5) != null) answerObject.put(cursor.getColumnName(5), cursor.getString(5));
                if(cursor.getColumnName(6) != null) answerObject.put(cursor.getColumnName(6), cursor.getString(6));
                if(cursor.getColumnName(7) != null) answerObject.put(cursor.getColumnName(7), cursor.getString(7));
                answersArray.put(answerObject);

                if(cursor.getPosition()+1<cursor.getCount()){
                    cursor.moveToNext();
                    if(!testId.equals(cursor.getString(0))){
                        questionObject.put("answers", answersArray);
                        resultSet.put(questionObject);

                        questionObject = new JSONObject();
                        answersArray = new JSONArray();
                    }
                    cursor.moveToPrevious();
                }

                if(cursor.getPosition()+1 == cursor.getCount()){
                    questionObject.put("answers", answersArray);
                    resultSet.put(questionObject);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            cursor.moveToNext();
        }

        cursor.close();
        Log.d("TAG_NAME", resultSet.toString());
        return resultSet;
    }

    public JSONArray getStandardQuestionById(String QuestionId){

        SQLiteDatabase db = this.getWritableDatabase();
        JSONArray resultSet = new JSONArray();
        JSONObject questionSet = new JSONObject();

        String searchQuery = "SELECT Question.question_id, Question.question_name, Quiz.quiz_id,  S_answer.s_answer_id, S_answer.s_answer_name " +
                "FROM Question " +
                "LEFT JOIN Quiz ON Question.quiz_id_fk = Quiz.quiz_id " +
                "LEFT JOIN S_answer ON Question.question_id = S_answer.question_id_fk " +
                "WHERE Question.question_id = " + QuestionId;

        Cursor cursor = db.rawQuery(searchQuery, null );

        cursor.moveToFirst();
        try {
            questionSet.put(cursor.getColumnName(0),cursor.getString(0));
            questionSet.put(cursor.getColumnName(1),cursor.getString(1));
            questionSet.put(cursor.getColumnName(2),cursor.getString(2));
        } catch (Exception e) {
            Log.d("TAG", e.getMessage());
        }

        Log.d("cursor results", cursor.getColumnName(2) + " " + cursor.getString(2));
        resultSet.put(questionSet);
        int totalColumn = cursor.getColumnCount();
        while (cursor.isAfterLast() == false) {
            JSONObject answersSet = new JSONObject();

            for (int i = 3; i < totalColumn; i++) {
                if (cursor.getColumnName(i) != null) {
                    try {
                        answersSet.put(cursor.getColumnName(i),cursor.getString(i));
                    } catch (Exception e) {
                        Log.d("tag", e.getMessage());
                    }
             }
            }
            Log.d("answ", answersSet.toString());

            resultSet.put(answersSet);
            cursor.moveToNext();

        }
            db.close();
            cursor.close();
            return resultSet;
    }

    public void updateQuestion(String QuestionID, String QuestionName){
        Log.d("update", "test");
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues newValues = new ContentValues();
        newValues.put(KEY_QUESTION_NAME, QuestionName);
        String[] args = new String[]{QuestionID};
        db.update(TABLE_QUESTION, newValues, "question_id=?", args);
        Log.d("update", "test1");
    }

    public void deleteSAnswerById(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_S_ANSWER, KEY_S_ANSWER_ID + "=?", new String[]{id});
        db.close();
    }

    public JSONArray getMCQuestionById(String QuestionId){

        SQLiteDatabase db = this.getWritableDatabase();
        JSONArray resultSet = new JSONArray();
        JSONObject questionSet = new JSONObject();

        String searchQuery = "SELECT Question.question_id, Question.question_name, Quiz.quiz_id,  Mcq_answer.msq_answer_id, Mcq_answer.mcq_answer_name, Mcq_answer.mcq_answer_type " +
                "FROM Question " +
                "LEFT JOIN Quiz ON Question.quiz_id_fk = Quiz.quiz_id " +
                "LEFT JOIN Mcq_answer ON Question.question_id = Mcq_answer.question_id_fk " +
                "WHERE Question.question_id = " + QuestionId;

        Cursor cursor = db.rawQuery(searchQuery, null );

        cursor.moveToFirst();
        try {
            questionSet.put(cursor.getColumnName(0),cursor.getString(0));
            questionSet.put(cursor.getColumnName(1),cursor.getString(1));
            questionSet.put(cursor.getColumnName(2),cursor.getString(2));
        } catch (Exception e) {
            Log.d("TAG", e.getMessage());
        }

        Log.d("cursor results", cursor.getColumnName(4) + " " + cursor.getString(4));
        Log.d("cursor results", cursor.getColumnName(5) + " " + cursor.getString(5));
        resultSet.put(questionSet);
        int totalColumn = cursor.getColumnCount();
        while (cursor.isAfterLast() == false) {
            JSONObject answersSet = new JSONObject();

            for (int i = 3; i < totalColumn; i++) {
                if (cursor.getColumnName(i) != null) {
                    try {
                        answersSet.put(cursor.getColumnName(i),cursor.getString(i));
                    } catch (Exception e) {
                        Log.d("tag", e.getMessage());
                    }
                }
            }
            Log.d("answ", answersSet.toString());

            resultSet.put(answersSet);
            cursor.moveToNext();

        }
        db.close();
        cursor.close();
        return resultSet;
    }

    public void deleteMCAnswerById(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_MCQ_ANSWER, KEY_MCQ_ANSWER_ID + "=?", new String[]{id});
        db.close();
    }

}
