package hr.tosapinaetfos.quizmaker.activities.activitiesQuizCreator;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import hr.tosapinaetfos.quizmaker.R;
import hr.tosapinaetfos.quizmaker.data.DBHelperQuizCreator;

public class MCQEditActivity extends AppCompatActivity {

    String QuestionID;
    DBHelperQuizCreator dbHelperQuizCreator = new DBHelperQuizCreator(this);
    JSONArray qData;
    String strQuestionName;
    String strQuestionID;
    String strAnswerName;
    String strAnswerType;
    String strQuizID;
    TableLayout answers;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mcquestion);

        Intent intent = getIntent();
        QuestionID = intent.getStringExtra("QuestionId");

        //get question data
        qData = dbHelperQuizCreator.getMCQuestionById(QuestionID);

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
                strAnswerName = jsonobject.optString("mcq_answer_name").toString();
                strAnswerType = jsonobject.optString("mcq_answer_type").toString();

                //Set params for new row
                TableLayout.LayoutParams trParams = new TableLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
                trParams.setMargins(0, 0, 0, 7);

                //Creating new row
                TableRow tr = new TableRow(this);

                //Set params for new edittext
                TableRow.LayoutParams etParams = new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 6);

                //Set params for new deleteimage
                TableRow.LayoutParams imgParams = new TableRow.LayoutParams(0, 50, 1);
                imgParams.gravity = Gravity.CENTER_VERTICAL;


                //Creating new img
                ImageView deleteAns = new ImageView(this);

                deleteAns.setImageResource(R.drawable.delete);
                deleteAns.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteRow(v);
                    }

                });


                //Creating new ET
                EditText cAns = new EditText(this);
                cAns.setText(strAnswerName);
                cAns.setPadding(5, 5, 5, 5);
                if(strAnswerType.equals("correct")){
                    cAns.setTag("correct");
                    cAns.setBackgroundColor(Color.parseColor("#a5f687"));
                }else{
                    cAns.setTag("wrong");
                    cAns.setBackgroundColor(Color.parseColor("#fa7f7f"));
                }

                //Add new ET cAnd to new row view
                tr.addView(cAns, etParams);

                //Add new IW deleteAns to new row view
                tr.addView(deleteAns, imgParams);
                //Add new row to answersTableLayout
                answers.addView(tr, trParams);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    public void deleteRow(View v) {
        // get row that is parent of clicked img
        View row = (View) v.getParent();
        // get rows container
        ViewGroup container = ((ViewGroup) row.getParent());
        // delete the row and invalidate view so it gets redrawn
        container.removeView(row);
        container.invalidate();
    }

    public void saveQuestion(View v) {
        String ansName;
        String ansType;
        String questionId;
        String ansId;

        //Check is there  minimum one Correct and minimum on Wrong answer
        if(hasCorrectandWrongAns()){

            //Check is there emtyfields
            if (!hasEmptyField()) {

                //Get question et
                EditText etEnterQuestion = (EditText) findViewById(R.id.etEnterQuestion);
                //Get text from etEnterQuestion
                String strEnterQuestion = etEnterQuestion.getText().toString();
                String strQuestionID = etEnterQuestion.getTag().toString();

                //Update question
                dbHelperQuizCreator.updateQuestion(strQuestionID, strEnterQuestion);

                //delete all answers , so can be new inserted, update is complicated in this case
                for(int i=1; i<qData.length(); i++){
                    try {
                        JSONObject jsonobject= (JSONObject) qData.get(i);
                        ansId = jsonobject.optString("msq_answer_id").toString();
                        dbHelperQuizCreator.deleteMCAnswerById(ansId);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

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

                            //Get name and tag for each fields
                            EditText et = (EditText) rchild;
                            ansName = et.getText().toString();
                            ansType = et.getTag().toString();

                            //Insert name , type, question id, method returns true or false
                            if(dbHelperQuizCreator.insertNewMCQAnswer(ansName, ansType, strQuestionID)){
                                //start new activity
                                Intent intent = new Intent(MCQEditActivity.this, ListOfQuestionsActivity.class);
                                intent.putExtra("QuizId", strQuizID);
                                MCQEditActivity.this.startActivity(intent);
                            }else{
                                Toast toast = Toast.makeText(this, getResources().getString(R.string.try_again), Toast.LENGTH_LONG);
                                toast.show();
                            }

                        }
                    }
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

    public void cancel(View v){
        Intent intent = new Intent(MCQEditActivity.this, ListOfQuestionsActivity.class);
        intent.putExtra("QuizId", strQuizID);
        MCQEditActivity.this.startActivity(intent);
    }

    public Boolean hasEmptyField() {

        //first check question field

        //Get question et
        EditText etEnterQuestion = (EditText) findViewById(R.id.etEnterQuestion);

        //Get text from etEnterQuestion
        String strEnterQuestion = etEnterQuestion.getText().toString();

        //Check if is empty
        if (strEnterQuestion.isEmpty()) {
            etEnterQuestion.setError(getResources().getString(R.string.mc_empty_field_validation));
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
                        et.setError(getResources().getString(R.string.mc_empty_field_validation));
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

    public Boolean hasCorrectandWrongAns() {

        int correctCounter = 0;
        int wrongCounter = 0;
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
                    if (et.getTag().toString().equals("correct")) {
                        correctCounter++;
                    } else {
                        wrongCounter++;
                    }
                }//End of rchild instanceof EditText

            }// End of if (child instanceof TableRow)
        }//end of loop

        if(correctCounter < 1 || wrongCounter <1){
            return false;
        }
        return true;
    }//End of method

    public void addNewCorrectAns(View v) {

        //Get answers table layout
        TableLayout answers = (TableLayout) findViewById(R.id.answersTableLayout);

        if (answers.getChildCount() < 6) {
            //Set params for new row
            TableLayout.LayoutParams trParams = new TableLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
            trParams.setMargins(0, 0, 0, 7);

            //Creating new row
            TableRow tr = new TableRow(this);

            //Set params for new edittext
            TableRow.LayoutParams etParams = new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 6);

            //Set params for new deleteimage
            TableRow.LayoutParams imgParams = new TableRow.LayoutParams(0, 50, 1);
            imgParams.gravity = Gravity.CENTER_VERTICAL;


            //Creating new img
            ImageView deleteAns = new ImageView(this);

            deleteAns.setImageResource(R.drawable.delete);
            deleteAns.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteRow(v);
                }

            });


            //Creating new ET
            EditText cAns = new EditText(this);
            cAns.setBackgroundColor(Color.parseColor("#a5f687"));
            cAns.setHint(getResources().getString(R.string.mc_question_ans_correct_edit_text));
            cAns.setPadding(5, 5, 5, 5);
            cAns.setTag("correct");
            //Add new ET cAnd to new row view
            tr.addView(cAns, etParams);

            //Add new IW deleteAns to new row view
            tr.addView(deleteAns, imgParams);
            //Add new row to answersTableLayout
            answers.addView(tr, trParams);
        } else {
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

    public void addNewWrongAns(View v) {
        //Get answers table layout
        TableLayout answers = (TableLayout) findViewById(R.id.answersTableLayout);

        if (answers.getChildCount() < 6) {
            //Set params for new row
            TableLayout.LayoutParams trParams = new TableLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
            trParams.setMargins(0, 0, 0, 7);

            //Creating new row
            TableRow tr = new TableRow(this);

            //Set params for new edittext
            TableRow.LayoutParams etParams = new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 6);

            //Set params for new deleteimage
            TableRow.LayoutParams imgParams = new TableRow.LayoutParams(0, 50, 1);
            imgParams.gravity = Gravity.CENTER_VERTICAL;


            //Creating new img
            ImageView deleteAns = new ImageView(this);

            deleteAns.setImageResource(R.drawable.delete);
            deleteAns.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteRow(v);
                }

            });

            //Creating new ET
            EditText cAns = new EditText(this);
            cAns.setBackgroundColor(Color.parseColor("#fa7f7f"));
            cAns.setHint(getResources().getString(R.string.mc_question_ans_wrong_edit_text));
            cAns.setPadding(5, 5, 5, 5);
            cAns.setTag("wrong");
            //Add new ET cAnd to new row view
            tr.addView(cAns, etParams);
            //Add new IW deleteAns to new row view
            tr.addView(deleteAns, imgParams);
            //Add new row to answersTableLayout
            answers.addView(tr, trParams);
        } else {
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

    public void warningDialogCancel(View v) {
        dialog.cancel();
    }



}
