package hr.tosapinaetfos.quizmaker.activities.activitiesQuizUser;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import hr.tosapinaetfos.quizmaker.R;
import hr.tosapinaetfos.quizmaker.activities.activitiesQuizCreator.QuizCreatorActivity;
import hr.tosapinaetfos.quizmaker.activities.activitiesQuizCreator.StandardQuestionActivity;
import hr.tosapinaetfos.quizmaker.adapters.QuizAdapter;
import hr.tosapinaetfos.quizmaker.authenticator.SessionManager;
import hr.tosapinaetfos.quizmaker.data.DBHelperQuizCreator;
import hr.tosapinaetfos.quizmaker.data.DBHelperQuizUser;
import hr.tosapinaetfos.quizmaker.models.QuizModel;

public class ListOfUserQuizzesActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{

    ListView quizListView;
    ArrayList<QuizModel> quizList = new ArrayList<QuizModel>();
    QuizAdapter myAdapter;
    DBHelperQuizUser dbHelperQuizUser = new DBHelperQuizUser(this);
    QuizModel clicked;
    SessionManager session;
    String user_id;
    HashMap<String, String> user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_quizzes2);
        TextView tvThereAreNoQuizzess = (TextView) findViewById(R.id.tvThereAreNoQuizzes2);
        //Fetch listview vListOfQuizzes
        quizListView = (ListView) findViewById(R.id.lvListOfQuizzes);
        session = new SessionManager(getApplicationContext());
        session.getUserDetails();

        //Get session data
        user = session.getUserDetails();
        user_id = user.get(SessionManager.KEY_USER_ID);
        quizList = dbHelperQuizUser.getQuizzes(user_id);
        //If list list is empty then show textview
        if(quizList.size() == 0){
            tvThereAreNoQuizzess.setVisibility(View.VISIBLE);
        }

        //Creating new adapter
        myAdapter = new QuizAdapter(this,quizList);

        //Set adapter to listview
        quizListView.setAdapter(myAdapter);

        quizListView.setOnItemClickListener(this);

        quizListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub

                clicked = (QuizModel) myAdapter.getItem(position);
                clicked.getQuizKey();

                return true;
            }

        });
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //Get clicked Quiz object
        clicked = (QuizModel) myAdapter.getItem(position);
        clicked.getQuizKey();

        Log.d("duration", clicked.getQuizDuration());

        Intent intent = new Intent(ListOfUserQuizzesActivity.this , QuizActivity.class);
        intent.putExtra("QuizKey", clicked.getQuizKey());
        ListOfUserQuizzesActivity.this.startActivity(intent);
    }

    //android:onClick="previusActivty"
    public void previusActivty(View v){
        Intent intent = new Intent(ListOfUserQuizzesActivity.this, QuizUserActivity.class);
        ListOfUserQuizzesActivity.this.startActivity(intent);
    }
}
