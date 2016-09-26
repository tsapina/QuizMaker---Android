package hr.tosapinaetfos.quizmaker.activities.activitiesQuizCreator;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import hr.tosapinaetfos.quizmaker.R;
import hr.tosapinaetfos.quizmaker.adapters.QuestionAdapter;
import hr.tosapinaetfos.quizmaker.data.DBHelperQuizCreator;
import hr.tosapinaetfos.quizmaker.models.QuestionModel;

public class ListOfQuestionsActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    ListView questionListView;
    DBHelperQuizCreator dbHelperQuizCreator = new DBHelperQuizCreator(this);
    ArrayList<QuestionModel> questionList = new ArrayList<>();
    QuestionAdapter myAdapter;
    Dialog dialog;
    String QuizId;
    QuestionModel clicked;
    int positionHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_questions);

        TextView tvThereAreNoQuestions = (TextView) findViewById(R.id.tvThereAreNoQuestions);

        Intent intent = getIntent();

        QuizId = intent.getStringExtra("QuizId");

        //Fetch listview lvListOfQuestions
        questionListView = (ListView) findViewById(R.id.lvListOfQuestions);

        //Get all questionmodel objects by quizID in ArrayList from database
        questionList = dbHelperQuizCreator.getQuestions(QuizId);

        //If list list is empty then show textview
        if(questionList.size() == 0){
            tvThereAreNoQuestions.setVisibility(View.VISIBLE);
        }

        //Creating new adapter
        myAdapter = new QuestionAdapter(this,questionList);

        //Set adapter to listview
        questionListView .setAdapter(myAdapter);

        questionListView .setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //Get clicked Quiz object
        clicked = (QuestionModel) myAdapter.getItem(position);

        //positionHelper for passing position value to other methods
        this.positionHelper = position;

        //Creating new dialoge
        dialog = new Dialog(this); // Context, this, etc.

        //set content view layout, list_of_quizzes_dialog contain buttons for edit,delete, upload to server, cancel
        dialog.setContentView(R.layout.list_of_questions_dialog);

        //set dialog title
        dialog.setTitle(getResources().getString(R.string.select_action));

        //show dialog
        dialog.show();
    }

    //android:onClick="cancelDialog"
    public void cancelDialog(View v){
        dialog.cancel();
    }

    //android:onClick="deleteQuestion"
    public void deleteQuestion(View v){

        final Dialog confirmationDialog = new Dialog(this);
        confirmationDialog.setContentView(R.layout.confirmation_dialog);
        confirmationDialog.setTitle(getResources().getString(R.string.select_action));
        //show dialog
        confirmationDialog.show();

        //fetch dialog buttons
        Button yesButton = (Button) confirmationDialog.findViewById(R.id.btnConfirmationYes);
        Button noButton = (Button) confirmationDialog.findViewById(R.id.btnConfirmationNo);

        //On confirmation delete quesstion by id
        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //remove from db by passing clicked quiz quizid
                dbHelperQuizCreator.deleteQuestionById(clicked.getQuestionId());
                //remove from listview
                questionList.remove(positionHelper);
                //adapter update listview
                myAdapter.notifyDataSetChanged();
                //dialogs cancel
                confirmationDialog.cancel();
                dialog.cancel();
            }
        });

        //cancel when is clicked noButton
        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmationDialog.cancel();
            }
        });
    }

    //android:onClick="editQuestion"
    public void editQuestion(View v){

        if(clicked.getQuestionType().equals("SQ")){
            Intent intent = new Intent(ListOfQuestionsActivity.this, SQEditActivity.class);
            intent.putExtra("QuestionId", clicked.getQuestionId());
            ListOfQuestionsActivity.this.startActivity(intent);

        }else{
            Intent intent = new Intent(ListOfQuestionsActivity.this, MCQEditActivity.class);
            intent.putExtra("QuestionId", clicked.getQuestionId());
            ListOfQuestionsActivity.this.startActivity(intent);
        }

    }

    public void addQuestion(View v){
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.add_question_dialog);
        dialog.setTitle(getResources().getString(R.string.select_action));
        //show dialog
        dialog.show();
    }

    public void goToStandardQuestion(View v){
        Intent intent = new Intent(ListOfQuestionsActivity.this , StandardQuestionActivity.class);
        intent.putExtra("ActivityName", "ListOfQuestionsActivity");
        intent.putExtra("QuizId", QuizId);
        ListOfQuestionsActivity.this.startActivity(intent);
    }

    public void goToMCQuestion(View v){
        Intent intent = new Intent(ListOfQuestionsActivity.this, MCQuestionActivity.class);
        intent.putExtra("ActivityName", "ListOfQuestionsActivity");
        intent.putExtra("QuizId", QuizId);
        ListOfQuestionsActivity.this.startActivity(intent);
        }

    //android:onClick="previusActivty"
    public void previusActivty(View v){
        Intent intent = new Intent(ListOfQuestionsActivity.this, ListOfQuizzesActivity.class);
        ListOfQuestionsActivity.this.startActivity(intent);
    }


}
