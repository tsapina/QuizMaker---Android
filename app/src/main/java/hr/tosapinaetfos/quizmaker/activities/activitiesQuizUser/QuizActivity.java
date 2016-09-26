package hr.tosapinaetfos.quizmaker.activities.activitiesQuizUser;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;

import hr.tosapinaetfos.quizmaker.R;
import hr.tosapinaetfos.quizmaker.data.DBHelperQuizUser;

public class QuizActivity extends AppCompatActivity {

    String QuizKey;
    String strQuestionType;
    JSONArray dataArray;
    ArrayList<String> correctAnswers = new ArrayList<String>();
    DBHelperQuizUser dbHelperQuizUser = new DBHelperQuizUser(this);
    int questionNumber = 0;
    int correctAnswer = 0;
    EditText ansEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        Intent intent = getIntent();
        QuizKey = intent.getStringExtra("QuizKey");
        dataArray = dbHelperQuizUser.dataByQuizKey(QuizKey);
        quizLoop();
    }

    public void quizLoop(){
        //Get answers table layout
        TableLayout answers = (TableLayout) findViewById(R.id.answersTableLayout);
        TextView questionName = (TextView) findViewById(R.id.questionName);
        TextView questionNum = (TextView) findViewById(R.id.questionNumber);
        answers.removeAllViews();

        if(questionNumber<dataArray.length()){
            try {
                JSONObject questionAnsObj = dataArray.getJSONObject(questionNumber);

                strQuestionType = questionAnsObj.getString("question_type");
                String strQuestionNum ="Pitanje " + String.valueOf(questionNumber + 1) + ". ";
                String strQuestionName = questionAnsObj.getString("question_name");

                questionNum.setText(strQuestionNum);
                questionName.setText(strQuestionName);

                JSONArray answersArray = questionAnsObj.getJSONArray("answers");

                if(strQuestionType.equals("SQ")){
                    //Set params for new row
                    TableLayout.LayoutParams trParams = new TableLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
                    trParams.setMargins(0, 0, 0, 7);
                    //Creating new row
                    TableRow tr = new TableRow(this);

                    //Set params for new edittext
                    TableRow.LayoutParams etParams = new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 6);

                    //Creating new EditText
                    ansEditText = new EditText(this);
                    ansEditText.setPadding(5, 5, 5, 5);
                    ansEditText.setHint(getResources().getString(R.string.standard_question_enter_answer_edit_text));

                    //Add new ET cAnd to new row view
                    tr.addView(ansEditText, etParams);

                    //Add new row to answersTableLayout
                    answers.addView(tr, trParams);
                    correctAnswers.clear();
                    for(int j=0; j<answersArray.length(); j++){
                        JSONObject ansObj = answersArray.getJSONObject(j);
                        String strAnsName = ansObj.getString("s_answer_name");
                        correctAnswers.add(strAnsName.toLowerCase());
                    }


                }else{
                    for(int j=0; j<answersArray.length(); j++){
                        JSONObject ansObj = answersArray.getJSONObject(j);
                        String strAnsName = ansObj.getString("mcq_answer_name");
                        String strAnsType = ansObj.getString("mcq_answer_type");

                        //Set params for new row
                        TableLayout.LayoutParams trParams = new TableLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
                        trParams.setMargins(0, 0, 0, 7);
                        //Creating new row
                        TableRow tr = new TableRow(this);

                        //Set params for new edittext
                        TableRow.LayoutParams etParams = new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 6);

                        //Set params for new deleteimage
                        TableRow.LayoutParams checkboxParams = new TableRow.LayoutParams(0, 50, 1);
                        checkboxParams.gravity = Gravity.CENTER_VERTICAL;

                        //Creating new img
                        CheckBox checkBox = new CheckBox(this);

                        //Creating new ET
                        TextView ans = new TextView(this);
                        ans.setText(strAnsName);
                        ans.setTag(strAnsType);
                        ans.setPadding(5, 5, 5, 5);

                        //Add new ET cAnd to new row view
                        tr.addView(ans, etParams);

                        //Add new IW deleteAns to new row view
                        tr.addView(checkBox, checkboxParams);
                        //Add new row to answersTableLayout
                        answers.addView(tr, trParams);

                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void newQuestion(View  v){

        if(strQuestionType.equals("SQ")){
            String strAns =  ansEditText.getText().toString().toLowerCase().trim();
            if(!isEmpty(strAns)) {
                if(correctAnswers.contains(strAns)){
                    correctAnswer++;
                }
                if(questionNumber != dataArray.length()-1){
                    questionNumber++;
                    quizLoop();
                }else{
                    Intent intent = new Intent(QuizActivity.this , QuizResultActivity.class);
                    intent.putExtra("quizCorrect", correctAnswer);
                    intent.putExtra("quizLength", dataArray.length());
                    intent.putExtra("quizKey", QuizKey);

                    QuizActivity.this.startActivity(intent);
                }

            }
        }
        else
        {
            if(checkBoxValidation())
            {
                //Get layout that contain answes
                TableLayout answers = (TableLayout) findViewById(R.id.answersTableLayout);
                Boolean isCorrect = true;
                //Loop through answers layout
                for (int i = 0; i < answers.getChildCount(); i++) {

                    // get answers child
                    Object child = answers.getChildAt(i);

                    //check if is child a tablerow
                    if (child instanceof TableRow) {

                        //create tablerow object
                        TableRow row = (TableRow) child;

                        //get child at 1 position, always checkBox
                        Object rchild = row.getChildAt(1);

                        //Check is child Edit text
                        if (rchild instanceof CheckBox) {
                            if(((CheckBox) rchild).isChecked()){

                                Object rchild1 = row.getChildAt(0);
                                TextView tV = (TextView) rchild1;
                                String status = tV.getTag().toString();
                                Log.d("status", status);
                                if(!status.equals("correct")){
                                    isCorrect = false;
                                    break;
                                }
                            }
                        }
                    }
                }

                if(isCorrect){correctAnswer++;}
                if(questionNumber != dataArray.length()-1){
                    questionNumber++;
                    quizLoop();
                }else{
                    Intent intent = new Intent(QuizActivity.this , QuizResultActivity.class);
                    intent.putExtra("quizCorrect",String.valueOf(correctAnswer));
                    intent.putExtra("quizLength",String.valueOf(dataArray.length()));
                    intent.putExtra("quizKey", QuizKey);
                    QuizActivity.this.startActivity(intent);
                }
            }
            else
            {
                Toast toast = Toast.makeText(this, getResources().getString(R.string.quiz_checkbox_validation) , Toast.LENGTH_SHORT);
                toast.show();
            }
        }

    }

    //Check each field is empty
    public Boolean isEmpty(String ans){

        Boolean isThereEmptyFields = false;
        if (ans.length() ==  0) {
            ansEditText.setError(getResources().getString(R.string.standard_question_empty_field_validation));
            isThereEmptyFields =true;
        }
        return isThereEmptyFields;
    }

    public Boolean checkBoxValidation(){
        //is empty helper
        Boolean isChecked = false;

        //Get layout that contain answes
        TableLayout answers = (TableLayout) findViewById(R.id.answersTableLayout);

        //Loop through answers layout
        for (int i = 0; i < answers.getChildCount(); i++) {

            // get answers child
            Object child = answers.getChildAt(i);

            //check if is child a tablerow
            if (child instanceof TableRow) {

                //create tablerow object
                TableRow row = (TableRow) child;

                //get child at 1 position, always checkBox
                Object rchild = row.getChildAt(1);

                //Check is child Edit text
                if (rchild instanceof CheckBox) {
                    Log.d("loop", "Checked");
                    if(((CheckBox) rchild).isChecked()){
                        isChecked = true;
                        Log.d("check", "isChecked");
                    }
                }
            }
        }

        // return isChecked
        return isChecked;
    }



}
