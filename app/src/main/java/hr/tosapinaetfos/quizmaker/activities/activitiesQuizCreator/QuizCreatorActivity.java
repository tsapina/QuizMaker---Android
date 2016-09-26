package hr.tosapinaetfos.quizmaker.activities.activitiesQuizCreator;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import hr.tosapinaetfos.quizmaker.R;
import hr.tosapinaetfos.quizmaker.activities.activitiesLoginRegister.LoginActivity;
import hr.tosapinaetfos.quizmaker.authenticator.SessionManager;
import hr.tosapinaetfos.quizmaker.data.DBHelperQuizCreator;
import hr.tosapinaetfos.quizmaker.helper.InternetHelper;
import hr.tosapinaetfos.quizmaker.helper.KeyExistRequest;

public class QuizCreatorActivity extends AppCompatActivity {

    SessionManager session;
    DBHelperQuizCreator dbHelperQuizCreator = new DBHelperQuizCreator(this);
    String user_id;
    String user_name;
    String user_type;
    HashMap<String, String> user;
    TextView tvWelcomeText;
    Dialog dialog;
    String strQuizName;
    String strQuizKey;
    EditText  etQuizName;
    EditText etQuizKey;
    InternetHelper isConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_creator);
        tvWelcomeText = (TextView) findViewById(R.id.tvWelcomeText);
        session = new SessionManager(getApplicationContext());
        session.getUserDetails();

        //Get session data
        user = session.getUserDetails();
        user_id = user.get(SessionManager.KEY_USER_ID);
        user_name = user.get(SessionManager.KEY_USER_NAME);
        user_type = user.get(SessionManager.KEY_USER_TYPE);

        //Set welcome message
        tvWelcomeText.setText("Dobrodošao " + user_name + " !");

    }

    public void logoutOnClick(View v){
        session.logoutUser();
        Intent intent = new Intent(QuizCreatorActivity.this, LoginActivity.class);
        QuizCreatorActivity.this.startActivity(intent);
    }

    public void listOfQuizzesOnclick(View v){
        Intent intent = new Intent(QuizCreatorActivity.this, ListOfQuizzesActivity.class);
        QuizCreatorActivity.this.startActivity(intent);
    }

    //Quiz creation
    public void createQuizOnclick(View v){
        //Creating new dialog
        dialog = new Dialog(this); // Context, this, etc.

        //set content view layout, list_of_quizzes_dialog contain buttons for edit,delete, upload to server, cancel
        dialog.setContentView(R.layout.create_quiz_dialog);

        //set dialog title
        dialog.setTitle(getResources().getString(R.string.quiz_name_text));

        //show dialog
        dialog.show();

        //fetch dialog buttons
        Button btnConfirmationCreate = (Button) dialog.findViewById(R.id.btnConfirmationCreate);
        Button btnConfirmationCancel = (Button) dialog.findViewById(R.id.btnConfirmationCancel);


        //On confirmation delete quesstion by id
        btnConfirmationCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isConnected = new InternetHelper();
                if(isConnected.isNetworkAvailable(getApplicationContext())) {

                    //fetch objects quizName and QuizKey
                    etQuizName = (EditText) dialog.findViewById(R.id.etQuizName);
                    etQuizKey = (EditText) dialog.findViewById(R.id.etQuizKey);

                    //get strings from fetched objects
                    strQuizKey = etQuizKey.getText().toString();
                    strQuizName = etQuizName.getText().toString();

                    //isEmpty validation
                    if (!isEmpty(strQuizKey, strQuizName)) {
                        //Response Listener, import com.android.volley.
                        Response.Listener<String> responseListener = new Response.Listener<String>() {

                            //Response Listener, import com.android.volley.
                            @Override
                            public void onResponse(String response) {
                                //Try to fetch JSONObject
                                try {
                                    //Print response
                                    Log.i("tagconvertstr", "[" + response + "]");

                                    //Creating a new JSONObject with name/value mappings from the JSON string
                                    JSONObject jsonResponse = new JSONObject(response);

                                    //Fetching $response["key_exist"]
                                    boolean key_exist = jsonResponse.getBoolean("key_exist");

                                    //If key not exist then insert in db and redirect to ListOfQuestionsActivity
                                    if (!key_exist) {
                                        //if inserted successful then redirect
                                        String quizId = dbHelperQuizCreator.insertNewQuiz(strQuizName, strQuizKey, user_id, getApplicationContext());
                                        //check if quiz id has value
                                        if (quizId != null && !quizId.isEmpty()) {
                                            Intent intent = new Intent(QuizCreatorActivity.this, ListOfQuestionsActivity.class);
                                            intent.putExtra("QuizId", quizId);
                                            QuizCreatorActivity.this.startActivity(intent);
                                        } else {
                                            Toast toast = Toast.makeText(QuizCreatorActivity.this, getResources().getString(R.string.try_again), Toast.LENGTH_SHORT);
                                            toast.show();
                                        }
                                    } else {
                                        Toast toast = Toast.makeText(QuizCreatorActivity.this, getResources().getString(R.string.key_exist), Toast.LENGTH_LONG);
                                        toast.show();
                                    }


                                    //JSONException if the parse fails or doesn't yield a
                                } catch (JSONException e) {
                                    Toast toast = Toast.makeText(QuizCreatorActivity.this, getResources().getString(R.string.server_response_error), Toast.LENGTH_LONG);
                                    toast.show();
                                    e.printStackTrace();
                                }
                            } //End of public void onResponse(String response)
                        };//End of Response.Listener<String> responseListener

                        //Print error when server is inactive
                        Response.ErrorListener errorListener = new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast toast = Toast.makeText(QuizCreatorActivity.this, getResources().getString(R.string.server_response_error), Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        };


                        //Creating KeyExistRequest and passing parameters for constructor
                        KeyExistRequest KeyExistRequest = new KeyExistRequest(strQuizKey, user_id, responseListener, errorListener);

                        //Send a Request
                        RequestQueue queue = Volley.newRequestQueue(QuizCreatorActivity.this);
                        queue.add(KeyExistRequest);

                    } // end of if statement
                }
                else
                {
                    Toast toast = Toast.makeText(QuizCreatorActivity.this, getResources().getString(R.string.internet_connection) , Toast.LENGTH_SHORT);
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
    public Boolean isEmpty(String strQuizKey, String strQuizName){

        Boolean isThereEmptyFields = false;

        if (strQuizKey.length() ==  0) {
            etQuizName.setError(getResources().getString(R.string.quiz_name_validation));
            isThereEmptyFields =true;
        }

        if (strQuizName.length() ==  0) {
            etQuizKey.setError(getResources().getString(R.string.key_name_validation));
            isThereEmptyFields =true;
        }

        return isThereEmptyFields;
    }

    //Check each field is empty
    public Boolean isEmpty2(String strQuizKey){

        Boolean isThereEmptyFields = false;

        if (strQuizKey.length() ==  0) {
            etQuizName.setError(getResources().getString(R.string.quiz_name_validation));
            isThereEmptyFields =true;
        }
        return isThereEmptyFields;
    }

    //Quiz creation
    public void openResults(View v){
        //Creating new dialog
        dialog = new Dialog(this); // Context, this, etc.

        //set content view layout, list_of_quizzes_dialog contain buttons for edit,delete, upload to server, cancel
        dialog.setContentView(R.layout.open_results_dialog);

        //set dialog title
        dialog.setTitle("Rezultati");

        //show dialog
        dialog.show();

        //fetch dialog buttons
        Button btnConfirmationView = (Button) dialog.findViewById(R.id.btnConfirmationView);
        Button btnConfirmationCancel = (Button) dialog.findViewById(R.id.btnConfirmationCancel);


        //On confirmation delete quesstion by id
        btnConfirmationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isConnected = new InternetHelper();
                if(isConnected.isNetworkAvailable(getApplicationContext())) {
                    //fetch objects quizName and QuizKey
                    etQuizKey = (EditText) dialog.findViewById(R.id.etQuizKey);
                    //get strings from fetched objects
                    strQuizKey = etQuizKey.getText().toString();

                    //isEmpty validation
                    if (!isEmpty2(strQuizKey))
                    {
                        String url = "http://www.etfos.unios.hr/~tsapina/Results.php?key=" + strQuizKey;
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                    }
                }
                else
                {
                    Toast toast = Toast.makeText(QuizCreatorActivity.this, getResources().getString(R.string.internet_connection) , Toast.LENGTH_SHORT);
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
}
