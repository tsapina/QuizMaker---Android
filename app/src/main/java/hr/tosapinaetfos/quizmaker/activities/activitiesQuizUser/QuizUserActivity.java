package hr.tosapinaetfos.quizmaker.activities.activitiesQuizUser;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

import hr.tosapinaetfos.quizmaker.R;
import hr.tosapinaetfos.quizmaker.activities.activitiesLoginRegister.LoginActivity;
import hr.tosapinaetfos.quizmaker.activities.activitiesQuizCreator.ListOfQuestionsActivity;
import hr.tosapinaetfos.quizmaker.activities.activitiesQuizCreator.ListOfQuizzesActivity;
import hr.tosapinaetfos.quizmaker.authenticator.SessionManager;
import hr.tosapinaetfos.quizmaker.data.DBHelperQuizUser;
import hr.tosapinaetfos.quizmaker.helper.GetQuizDataRequest;
import hr.tosapinaetfos.quizmaker.helper.InternetHelper;
import hr.tosapinaetfos.quizmaker.helper.KeyExistRequest;

public class QuizUserActivity extends AppCompatActivity {
    SessionManager session;
    DBHelperQuizUser dbHelperQuizUser = new DBHelperQuizUser(this);
    String user_id;
    String user_name;
    String user_type;
    HashMap<String, String> user;
    TextView tvWelcomeText;
    Dialog dialog;
    EditText etQuizKey;
    String strQuizKey;
    InternetHelper isConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_user);

        tvWelcomeText = (TextView) findViewById(R.id.tvWelcomeText);
        session = new SessionManager(getApplicationContext());
        session.getUserDetails();

        //Get session data
        user = session.getUserDetails();
        user_id = user.get(SessionManager.KEY_USER_ID);
        user_name = user.get(SessionManager.KEY_USER_NAME);
        user_type = user.get(SessionManager.KEY_USER_TYPE);

        //Set welcome message
        tvWelcomeText.setText("Welcome " + user_name + " !");

    }

    //LOGOUT USER
    public void logoutOnClick(View v){
        session.logoutUser();
        Intent intent = new Intent(QuizUserActivity.this, LoginActivity.class);
        QuizUserActivity.this.startActivity(intent);
    }

    public void retrieveQuizOnclick(View v){
        //Creating new dialog
        dialog = new Dialog(this); // Context, this, etc.

        //set content view layout, list_of_quizzes_dialog contain buttons for edit,delete, upload to server, cancel
        dialog.setContentView(R.layout.retrieve_quiz_dialog);

        //set dialog title
        dialog.setTitle(getResources().getString(R.string.quiz_user_pop_up_text));

        //show dialog
        dialog.show();

        //fetch dialog buttons
        Button btnConfirmationRetrieve = (Button) dialog.findViewById(R.id.btnRetrieve);
        Button btnConfirmationCancel = (Button) dialog.findViewById(R.id.btnConfirmationCancel);

        //On confirmation delete quesstion by id
        btnConfirmationRetrieve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isConnected = new InternetHelper();
                if(isConnected.isNetworkAvailable(getApplicationContext())) {
                    //fetch objects QuizKey
                    etQuizKey = (EditText) dialog.findViewById(R.id.etQuizKey);

                    //get strings from fetched objects
                    strQuizKey = etQuizKey.getText().toString();

                    //isEmpty validation
                    if (!isEmpty(strQuizKey)) {

                        //Response Listener, import com.android.volley.
                        Response.Listener<String> responseListener = new Response.Listener<String>() {

                            //Response Listener, import com.android.volley.
                            @Override
                            public void onResponse(String response) {
                                //Try to fetch JSONObject
                                try {
                                    //Print response
                                    Log.i("response", "[" + response + "]");

                                    //Creating a new JSONObject with name/value mappings from the JSON string
                                    JSONObject jsonResponse = new JSONObject(response);

                                    //Fetching $response["key_exist"]
                                    boolean key_exist = jsonResponse.getBoolean("key_exist");

                                    if (key_exist) {
                                        try {

                                            JSONArray questionDataArr = jsonResponse.getJSONArray("questionData");
                                            JSONObject quizData = jsonResponse.getJSONObject("quizData");

                                            String quizID = quizData.getString("quizID");
                                            String quizName = quizData.getString("quizName");
                                            String quizDateofCreation = quizData.getString("quizDateofCreation");
                                            String quizKeyName = quizData.getString("quizKeyName");


                                            Log.d("llll", "llll");
                                            Boolean quizAlredyExist = dbHelperQuizUser.quizAlredyExist(quizKeyName);

                                            if (!quizAlredyExist) {
                                                dbHelperQuizUser.insertQuiz(quizID, quizName, quizKeyName, quizDateofCreation, user_id);

                                                for (int i = 0; i < questionDataArr.length(); i++) {
                                                    JSONObject questionDataObj = questionDataArr.getJSONObject(i);
                                                    String questionIDStr = questionDataObj.getString("questionID");
                                                    String questionNameStr = questionDataObj.getString("question_name");
                                                    String questionTypeStr = questionDataObj.getString("question_type");

                                                    dbHelperQuizUser.insertQuestion(questionIDStr, questionNameStr, questionTypeStr, quizID);

                                                    JSONArray answArr = questionDataObj.getJSONArray("answ");
                                                    if (questionTypeStr.equals("SQ")) {
                                                        for (int j = 0; j < answArr.length(); j++) {
                                                            JSONObject answObj = answArr.getJSONObject(j);
                                                            String ansIdStr = answObj.getString("ans_id");
                                                            String ansNameStr = answObj.getString("ans_name");
                                                            dbHelperQuizUser.insertSAns(ansIdStr, ansNameStr, questionIDStr);
                                                        }
                                                    } else {
                                                        for (int j = 0; j < answArr.length(); j++) {
                                                            JSONObject answObj = answArr.getJSONObject(j);
                                                            String ansIdStr = answObj.getString("ans_id");
                                                            String ansNameStr = answObj.getString("ans_name");
                                                            String ansNameType = answObj.getString("ans_type");
                                                            dbHelperQuizUser.insertMCAns(ansIdStr, ansNameStr, ansNameType, questionIDStr);
                                                        }
                                                    }
                                                }
                                                dialog.cancel();
                                                Toast toast = Toast.makeText(QuizUserActivity.this, getResources().getString(R.string.quiz_user_retrieve_success), Toast.LENGTH_SHORT);
                                                toast.show();
                                            } else {
                                                Toast toast = Toast.makeText(QuizUserActivity.this, getResources().getString(R.string.quiz_user_key_exist), Toast.LENGTH_SHORT);
                                                toast.show();
                                            }
                                        } catch (JSONException e) {
                                            // TODO Auto-generated catch block
                                            e.printStackTrace();
                                        }
                                    } else {
                                        Toast toast = Toast.makeText(QuizUserActivity.this, getResources().getString(R.string.quiz_user_key_not_exist), Toast.LENGTH_LONG);
                                        toast.show();
                                    }

                                    //JSONException if the parse fails or doesn't yield a
                                } catch (JSONException e) {
                                    Toast toast = Toast.makeText(QuizUserActivity.this, getResources().getString(R.string.server_response_error), Toast.LENGTH_LONG);
                                    toast.show();
                                    e.printStackTrace();
                                }
                            } //End of public void onResponse(String response)
                        };//End of Response.Listener<String> responseListener


                        //Print error when server is inactive
                        Response.ErrorListener errorListener = new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast toast = Toast.makeText(QuizUserActivity.this, getResources().getString(R.string.server_response_error), Toast.LENGTH_LONG);
                                toast.show();
                            }
                        };

                        //Creating KeyExistRequest and passing parameters for constructor
                        GetQuizDataRequest GetQuizDataRequest = new GetQuizDataRequest(strQuizKey, responseListener, errorListener);

                        //Send a Request
                        RequestQueue queue = Volley.newRequestQueue(QuizUserActivity.this);
                        queue.add(GetQuizDataRequest);

                    } // end of if statement
                }
                else
                {
                    Toast toast = Toast.makeText(QuizUserActivity.this, getResources().getString(R.string.internet_connection) , Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });//End of  btnConfirmationCreate



        //On button Cancel click ..
        btnConfirmationCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

    } // End of createQuizOnclick(View v)

    //Check each field is empty
    public Boolean isEmpty(String strQuizKey){
        Boolean isThereEmptyFields = false;
        if (strQuizKey.length() ==  0) {
            etQuizKey.setError(getResources().getString(R.string.quiz_user_pop_up_validation));
            isThereEmptyFields =true;
        }
        return isThereEmptyFields;
    }

    public void listOfQuizzesOnclick(View v){
        Intent intent = new Intent(QuizUserActivity.this, ListOfUserQuizzesActivity.class);
        QuizUserActivity.this.startActivity(intent);
    }

}
