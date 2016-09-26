package hr.tosapinaetfos.quizmaker.activities.activitiesQuizCreator;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import hr.tosapinaetfos.quizmaker.R;
import hr.tosapinaetfos.quizmaker.data.DBHelperQuizCreator;
import hr.tosapinaetfos.quizmaker.helper.DynamicLayoutHelper;

public class SQEditActivity extends AppCompatActivity {

    String QuestionID;
    DBHelperQuizCreator dbHelperQuizCreator = new DBHelperQuizCreator(this);
    JSONArray qData;
    String strQuestionName;
    String strQuestionID;
    String strAnswerName;
    String strAnswerId;
    String strQuizID;
    TableLayout answers;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_standard_question_activity);
        Intent intent = getIntent();

        //get questionID from intent
        QuestionID = intent.getStringExtra("QuestionId");

        //get question data
        qData = dbHelperQuizCreator.getStandardQuestionById(QuestionID);

        try {
            //Get first object from qData Array, Question data is always at 0 index
            JSONObject objectQuestion= (JSONObject) qData.get(0);

            strQuestionName = objectQuestion.optString("question_name").toString();
            strQuestionID = objectQuestion.optString("question_id").toString();
            strQuizID = objectQuestion.optString("quiz_id").toString();

            //Set strQuestionName to etEnterQuestion view
            EditText etEnterQuestion = (EditText)findViewById(R.id.etEnterQuestion);
            etEnterQuestion.setText(strQuestionName);
            etEnterQuestion.setTag(strQuestionID);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        //Get answers table layout
        answers = (TableLayout) findViewById(R.id.answersTableLayout);
        //Delete first answer
        answers.removeAllViews();

        //listing answers in Tablelayout answers
        for(int i=1; i<qData.length(); i++){
            try {

                JSONObject jsonobject= (JSONObject) qData.get(i);
                strAnswerName = jsonobject.optString("s_answer_name").toString();

                //add answer view
                //https://en.wikipedia.org/wiki/Builder_pattern
                DynamicLayoutHelper dlHelper = new DynamicLayoutHelper()
                                                .setCtx(this)
                                                .setPurpose("listing")
                                                .setAnsLayout(answers)
                                                .setStrAnswerName(strAnswerName)
                                                .addAnswer();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }//End of protected void onCreate(Bundle savedInstanceState)


    public void saveQuestion(View v) {
        String ansName;
        String questionId;
        //Check is there  minimum one Correct and minimum on Wrong answer
        if(hasAns()){
            //Check is there emtyfields
            if (!hasEmptyField()) {
                //Get question et
                EditText etEnterQuestion = (EditText) findViewById(R.id.etEnterQuestion);
                TableLayout answers = (TableLayout) findViewById(R.id.answersTableLayout);
                DynamicLayoutHelper dlHelper = new DynamicLayoutHelper();
                if(dlHelper.saveQuestion(this, etEnterQuestion, qData, answers)){
                    //start new activity
                    Intent intent = new Intent(this, ListOfQuestionsActivity.class);
                    intent.putExtra("QuizId", strQuizID);
                    this.startActivity(intent);
                }else{
                    Toast toast = Toast.makeText(this, getResources().getString(R.string.try_again), Toast.LENGTH_LONG);
                    toast.show();
                }

            }
        }else{
            //Creating new dialog
            dialog = new Dialog(this); // Context, this, etc.

            //set content view layout
            dialog.setContentView(R.layout.warning_dialog_fields);

            //set dialog title
            dialog.setTitle(getResources().getString(R.string.select_action));

            //show dialog
            dialog.show();
        }
    }
    public void addNewAns(View v){

        if (answers.getChildCount() < 6) {
            //add answer view
            DynamicLayoutHelper dlHelper = new DynamicLayoutHelper()
                    .setCtx(this)
                    .setPurpose("new")
                    .setAnsLayout(answers)
                    .addAnswer();
        }else{
            //Creating new dialog
            dialog = new Dialog(this); // Context, this, etc.
            //set content view layout
            dialog.setContentView(R.layout.warning_dialog_answers);
            //set dialog title
            dialog.setTitle(getResources().getString(R.string.select_action));
            //show dialog
            dialog.show();
        }
    }

    public Boolean hasEmptyField() {

        //first check question field

        //Get question et
        EditText etEnterQuestion = (EditText) findViewById(R.id.etEnterQuestion);

        //Get text from etEnterQuestion
        String strEnterQuestion = etEnterQuestion.getText().toString();

        //Check if is empty
        if (strEnterQuestion.isEmpty()) {
            etEnterQuestion.setError(getResources().getString(R.string.standard_question_empty_field_validation));
            return true;
        }

        //is empty helper
        Boolean isEmpty = false;

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

                //get child at 0 position, always EditText
                Object rchild = row.getChildAt(0);

                //Check is child Edit text
                if (rchild instanceof EditText) {

                    EditText et = (EditText) rchild;

                    //Get value from ET
                    String strEt = et.getText().toString();

                    //Check is empty
                    if (strEt.isEmpty()) {
                        et.setError(getResources().getString(R.string.standard_question_empty_field_validation));
                        isEmpty = true;
                    }
                }
            }
        }

        //check is isEmpty changed
        if (isEmpty == false) {
            return false;
        }

        // return true if isEmpty hasn't changed value
        return true;
    }

    public Boolean hasAns() {

        int ans = 0;
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

                //get child at 0 position, always EditText
                Object rchild = row.getChildAt(0);

                //Check is child Edit text
                if (rchild instanceof EditText) {
                    EditText et = (EditText) rchild;
                    //count correct and wrong ans
                    ans++;
                }//End of rchild instanceof EditText

            }// End of if (child instanceof TableRow)
        }//end of loop

        if(ans < 1){
            return false;
        }
        return true;
    }//End of method

    public void cancel(View v){
        Intent intent = new Intent(this, ListOfQuestionsActivity.class);
        intent.putExtra("QuizId", strQuizID);
        this.startActivity(intent);
    }
}
