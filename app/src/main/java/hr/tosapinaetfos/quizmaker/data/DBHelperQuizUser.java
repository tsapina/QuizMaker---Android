package hr.tosapinaetfos.quizmaker.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import hr.tosapinaetfos.quizmaker.models.QuizModel;

/**
 * Created by tosap on 07.06.2016..
 */
public class DBHelperQuizUser extends SQLiteOpenHelper {
    //Database version , important to database upgrade
    private static final int DATABASE_VERSION = 2;

    //Database name and name of all tables
    static final String DATABASE_NAME = "QuizCrUser";
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
    public DBHelperQuizUser(Context context){ super(context, DATABASE_NAME, null, DATABASE_VERSION); }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_QUIZ_TABLE = "CREATE TABLE " + TABLE_QUIZ
                + "(" + KEY_QUIZ_ID  + " INTEGER ,"
                + KEY_QUIZ_NAME + " TEXT,"
                +  KEY_QUIZ_DATE_OF_CREATION + "  DATE, "
                + KEY_QUIZ_KEY + " TEXT, "
                + KEY_USER_ID_FK + " INTEGER "
                +")";

        String CREATE_QUESTION_TABLE = "CREATE TABLE " + TABLE_QUESTION
                + "(" + KEY_QUESTION_ID  + " INTEGER ,"
                + KEY_QUESTION_NAME + " TEXT,"
                +  KEY_QUESTION_TYPE + "  TEXT, "
                + KEY_QUIZ_ID_FK + " INTEGER "
                + ")";

        String CREATE_MCQ_ANSWER_TABLE = "CREATE TABLE " + TABLE_MCQ_ANSWER
                + "(" + KEY_MCQ_ANSWER_ID  + " INTEGER , "
                + KEY_MCQ_ANSWER_NAME + " TEXT,"
                +  KEY_MCQ_ANSWER_TYPE + "  TEXT, "
                + KEY_QUESTION_ID_FK + " INTEGER "
                + ")";

        String CREATE_S_ANSWER_TABLE = "CREATE TABLE " + TABLE_S_ANSWER
                + "(" + KEY_S_ANSWER_ID  + " INTEGER , "
                + KEY_S_ANSWER_NAME + " TEXT,"
                + KEY_QUESTION_ID_FK + " INTEGER "
                + ")";

        db.execSQL(CREATE_QUIZ_TABLE);
        db.execSQL(CREATE_QUESTION_TABLE);
        db.execSQL(CREATE_MCQ_ANSWER_TABLE);
        db.execSQL(CREATE_S_ANSWER_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUIZ);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUESTION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MCQ_ANSWER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_S_ANSWER);
        onCreate(db);
    }

    public Boolean quizAlredyExist(String quizKey){
        SQLiteDatabase db = this.getWritableDatabase();
        String searchQuery = "SELECT * FROM " + TABLE_QUIZ + " WHERE " + KEY_QUIZ_KEY + " = '" + quizKey + "'" ;
        Cursor cursor = db.rawQuery(searchQuery, null);

        Log.d("str", String.valueOf(cursor.getCount()));
        if(cursor.getCount()>0){
            return true;
        }
        return false;
    }

    public void insertQuiz(String quizID, String quizName, String quizKey, String quizDate,  String userID){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_QUIZ_ID, quizID);
        values.put(KEY_QUIZ_NAME, quizName);
        values.put(KEY_QUIZ_DATE_OF_CREATION, quizDate);
        values.put(KEY_QUIZ_KEY, quizKey);
        values.put(KEY_USER_ID_FK, userID);
        db.insert(TABLE_QUIZ, null, values); //AKO NIJE USPJEŠNO DODAN NOVI RED , VRATIT ĆE -1

    }

    public void insertQuestion(String questionID, String questionName, String questionType, String quizIDFK){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_QUESTION_ID, questionID);
        values.put(KEY_QUESTION_NAME, questionName);
        values.put(KEY_QUESTION_TYPE, questionType);
        values.put(KEY_QUIZ_ID_FK, quizIDFK);

        db.insert(TABLE_QUESTION, null, values); //AKO NIJE USPJEŠNO DODAN NOVI RED , VRATIT ĆE -1

    }

    public void insertSAns(String sAnsID, String sAnsName, String questionIDFK){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_S_ANSWER_ID, sAnsID);
        values.put(KEY_S_ANSWER_NAME, sAnsName);
        values.put(KEY_QUESTION_ID_FK, questionIDFK);

        db.insert(TABLE_S_ANSWER, null, values); //AKO NIJE USPJEŠNO DODAN NOVI RED , VRATIT ĆE -1

    }

    public void insertMCAns(String mcAnsId, String mcAnsName, String mcAnsType, String questionIDFK){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_MCQ_ANSWER_ID, mcAnsId);
        values.put(KEY_MCQ_ANSWER_NAME, mcAnsName);
        values.put(KEY_MCQ_ANSWER_TYPE, mcAnsType);
        values.put(KEY_QUESTION_ID_FK, questionIDFK);

        db.insert(TABLE_MCQ_ANSWER, null, values); //AKO NIJE USPJEŠNO DODAN NOVI RED , VRATIT ĆE -1
    }

    // Method returns ArrayList of QuizModel objects
    public ArrayList<QuizModel> getQuizzes(String user_id){
        //Create arraylist QuizModel type
        ArrayList<QuizModel> quizList = new ArrayList<QuizModel>();

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_QUIZ + " WHERE " + KEY_USER_ID_FK + " = '" + user_id + "'", null);

        //Loop through table Quiz
        if(cursor.moveToFirst()){
            do{
                QuizModel quiz = new QuizModel(
                        cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(2));
                quizList.add(quiz);
            }while(cursor.moveToNext());
        }
        db.close();
        return quizList;
    }

    public JSONArray dataByQuizKey(String QuizKey){
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
                "WHERE quiz_key = '" + QuizKey + "'";

        Cursor cursor = db.rawQuery(searchQuery, null );
        JSONArray resultSet = new JSONArray();

        cursor.moveToFirst();

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



}
