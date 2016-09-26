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
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import hr.tosapinaetfos.quizmaker.R;
import hr.tosapinaetfos.quizmaker.adapters.QuizAdapter;
import hr.tosapinaetfos.quizmaker.authenticator.SessionManager;
import hr.tosapinaetfos.quizmaker.data.DBHelperQuizCreator;
import hr.tosapinaetfos.quizmaker.helper.InternetHelper;
import hr.tosapinaetfos.quizmaker.helper.SendQuizDataRequest;
import hr.tosapinaetfos.quizmaker.models.QuizModel;

public class ListOfQuizzesActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    SessionManager session;
    String user_id;
    HashMap<String, String> user;
    ListView quizListView;
    DBHelperQuizCreator dbHelperQuizCreator = new DBHelperQuizCreator(this);
    ArrayList<QuizModel> quizList = new ArrayList<QuizModel>();
    QuizAdapter myAdapter;
    Dialog dialog;
    QuizModel clicked;
    int positionHelper;
    InternetHelper isConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_quizzes);

        TextView tvThereAreNoQuizzess = (TextView) findViewById(R.id.tvThereAreNoQuizzes);

        Log.d("test", "4");
        //Get session data
        session = new SessionManager(getApplicationContext());
       // session.getUserDetails();
        user = session.getUserDetails();
        user_id = user.get(SessionManager.KEY_USER_ID);
        //Fetch listview vListOfQuizzes
        quizListView = (ListView) findViewById(R.id.lvListOfQuizzes);
        Log.d("test","5");
        //Get all quizmodel objects in ArrayList from database
        quizList = dbHelperQuizCreator.getQuizzes(user_id);

        //If list list is empty then show textview
        if(quizList.size() == 0){
            tvThereAreNoQuizzess.setVisibility(View.VISIBLE);
        }
        Log.d("test","6");
        //Creating new adapter
        myAdapter = new QuizAdapter(this,quizList);

        //Set adapter to listview
        quizListView.setAdapter(myAdapter);

        quizListView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //Get clicked Quiz object
        clicked = (QuizModel) myAdapter.getItem(position);

        //positionHelper for passing position value to other methods
        this.positionHelper = position;

        //Creating new dialog
        dialog = new Dialog(this); // Context, this, etc.

        //set content view layout, list_of_quizzes_dialog contain buttons for edit,delete, upload to server, cancel
        dialog.setContentView(R.layout.list_of_quizzes_dialog);

        dialog.setTitle(getResources().getString(R.string.select_action));
        //show dialog
        dialog.show();
    }

    //android:onClick="cancelDialog"
    public void cancelDialog(View v){
        dialog.cancel();
    }

    //android:onClick="deleteQuiz"
    public void deleteQuiz(View v){

        //oba dialoga gasit!!!
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
                dbHelperQuizCreator.deleteQuizById(clicked.getQuizId());
                //remove from listview
                quizList.remove(positionHelper);
                //adapter update listview
                myAdapter.notifyDataSetChanged();
                //kill dialogs
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

    //android:onClick="editQuiz"
    public void editQuiz(View v){
        //new intent for ListOfQuestionsActivity
        Intent intent = new Intent(ListOfQuizzesActivity.this, ListOfQuestionsActivity.class);
        //passing clicked quiz id to new activity
        intent.putExtra("QuizId", clicked.getQuizId());
        //passing current activity name
        intent.putExtra("ActivityName", "ListOfUserQuizzesActivity");
        //start new activity
        ListOfQuizzesActivity.this.startActivity(intent);
        //kill dialog
        dialog.cancel();
    }

    //android:onClick="uploadToServer"
    public void uploadToServer(View v){

        isConnected = new InternetHelper();
        if(isConnected.isNetworkAvailable(getApplicationContext())){
            String jsonArray;

            session = new SessionManager(getApplicationContext());
            session.getUserDetails();

            //Get session data
            user = session.getUserDetails();
            user_id = user.get(SessionManager.KEY_USER_ID);
            Log.d("testiranje", clicked.getQuizDuration());
            Log.d("testiranje1", clicked.getQuizName());
            JSONArray array = dbHelperQuizCreator.dataByQuizID(clicked.getQuizId(),  clicked.getQuizName(), clicked.getQuizDateOfCreation(), clicked.getQuizKey(), user_id);
            jsonArray = array.toString();

            Log.d("zbroj", jsonArray);


            //Response Listener, import com.android.volley.
            Response.Listener<String> responseListener  = new Response.Listener<String>(){

                //Response Listener, import com.android.volley.
                @Override
                public void onResponse(String response) {
                    //Try to fetch JSONObject
                    try {
                        Log.d("tag", response);
                        //Creating a new JSONObject with name/value mappings from the JSON string
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean success = jsonResponse.getBoolean("success");
                        boolean key_exist = jsonResponse.getBoolean("key_exist");
                        if(!key_exist){
                            if(success){
                                Toast toast = Toast.makeText(ListOfQuizzesActivity.this, getResources().getString(R.string.list_of_quizzess_uplad_sucessfull), Toast.LENGTH_LONG);
                                toast.show();
                            }else{
                                Toast toast = Toast.makeText(ListOfQuizzesActivity.this, getResources().getString(R.string.server_response_error), Toast.LENGTH_LONG);
                                toast.show();
                            }
                        }else{
                            Toast toast = Toast.makeText(ListOfQuizzesActivity.this,getResources().getString(R.string.list_of_quizzess_uplad_exist), Toast.LENGTH_LONG);
                            toast.show();
                        }


                        //JSONException if the parse fails or doesn't yield a
                    } catch (JSONException e) {
                        Toast toast = Toast.makeText(ListOfQuizzesActivity.this, "Server response error!", Toast.LENGTH_LONG);
                        toast.show();
                        e.printStackTrace();
                        Log.d("test", "ovo je catch");
                    }
                } //End of public void onResponse(String response)
            };//End of Response.Listener<String> responseListener

            //Print error when server is inactive
            Response.ErrorListener errorListener = new Response.ErrorListener(){
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast toast = Toast.makeText(ListOfQuizzesActivity.this, "Server response error!", Toast.LENGTH_LONG);
                    toast.show();
                }
            };


            //Creating KeyExistRequest and passing parameters for constructor
            SendQuizDataRequest SendQuizDataRequest = new SendQuizDataRequest(jsonArray, responseListener,  errorListener);

            //Send a Request
            RequestQueue queue = Volley.newRequestQueue(ListOfQuizzesActivity.this);
            queue.add(SendQuizDataRequest);
        }
        else
        {
            Toast toast = Toast.makeText(ListOfQuizzesActivity.this, getResources().getString(R.string.internet_connection) , Toast.LENGTH_LONG);
            toast.show();
        }


    }

    //android:onClick="previusActivty"
    public void previusActivty(View v){
        Intent intent = new Intent(ListOfQuizzesActivity.this, QuizCreatorActivity.class);
        ListOfQuizzesActivity.this.startActivity(intent);
    }

}
