package hr.tosapinaetfos.quizmaker.activities.activitiesQuizUser;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import hr.tosapinaetfos.quizmaker.R;
import hr.tosapinaetfos.quizmaker.activities.activitiesLoginRegister.LoginActivity;
import hr.tosapinaetfos.quizmaker.authenticator.RegisterRequest;
import hr.tosapinaetfos.quizmaker.authenticator.SessionManager;
import hr.tosapinaetfos.quizmaker.helper.InternetHelper;
import hr.tosapinaetfos.quizmaker.helper.ResultUploadHelper;

public class QuizResultActivity extends AppCompatActivity {

    String user_name;
    HashMap<String, String> user;
    SessionManager session;
    String quizResult;
    String currentDateandTime;
    String quizKey;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-dd-MM hh:mm");
    InternetHelper isConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_result);

        String quizCorrect;
        String quizLength;

        Intent intent = getIntent();

        quizCorrect = intent.getStringExtra("quizCorrect");
        quizLength = intent.getStringExtra("quizLength");


        TextView tvResult = (TextView) findViewById(R.id.tvResultTimeExpired);
        tvResult.setVisibility(View.VISIBLE);
        tvResult.setText(getResources().getString(R.string.quiz_result_score) + "     " + quizCorrect + "/" + quizLength);

        quizResult = quizCorrect + "/" + quizLength;
        Log.d("result: ", quizResult);
        sdf.setTimeZone(TimeZone.getTimeZone("Europe/Zagreb"));
        currentDateandTime = sdf.format(new Date());
        Log.d("time: " , currentDateandTime);
        quizKey = intent.getStringExtra("quizKey");
        Log.d("key: " , quizKey);
        //Get session data
        session = new SessionManager(getApplicationContext());
        session.getUserDetails();
        user = session.getUserDetails();
        user_name = user.get(SessionManager.KEY_USER_NAME);
        Log.d("User: " , user_name);

        isConnected = new InternetHelper();
        if(isConnected.isNetworkAvailable(getApplicationContext())) {
            //Response Listener, import com.android.volley.
            Response.Listener<String> responseListener = new Response.Listener<String>() {
                //Response from Register.php
                @Override
                public void onResponse(String response) {
                    //Try to fetch JSONObject
                    try {
                        //Print response
                        Log.i("tagconvertstr", "[" + response + "]");

                        //Creating a new JSONObject with name/value mappings from the JSON string
                        JSONObject jsonObject = new JSONObject(response);

                        //Fetching $response["success"]
                        boolean success = jsonObject.getBoolean("success");

                        //If success == true then start LoginActivity , else print message
                        if (success)
                        {
                            Toast toast = Toast.makeText(QuizResultActivity.this, "Rezultat uploadan na server!", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                        else
                        {
                            Toast toast = Toast.makeText(QuizResultActivity.this, "Rezultat nije uploadan na server!", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                        //JSONException if the parse fails or doesn't yield a
                    } catch (JSONException e) {
                        Toast toast = Toast.makeText(QuizResultActivity.this, getResources().getString(R.string.server_response_error), Toast.LENGTH_SHORT);
                        toast.show();
                        e.printStackTrace();
                    }
                }//End of public void onResponse(String response)
            };//End of Response.Listener<String> responseListener

            //Print error when server is inactive
            Response.ErrorListener errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast toast = Toast.makeText(QuizResultActivity.this, getResources().getString(R.string.server_response_error), Toast.LENGTH_SHORT);
                    toast.show();
                }
            };

            //Creating registerRequest and passing parameters for constructor
            ResultUploadHelper resultUploadHelper = new ResultUploadHelper(user_name, quizKey, quizResult, currentDateandTime, responseListener,  errorListener);

            //Send a Request
            RequestQueue queue = Volley.newRequestQueue(QuizResultActivity.this);
            queue.add(resultUploadHelper);
        }// End of validation process
        else
        {
            Toast toast = Toast.makeText(QuizResultActivity.this, getResources().getString(R.string.internet_connection) , Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void goToHomeActivity(View v){
        Intent intent = new Intent(QuizResultActivity.this , QuizUserActivity.class);
        QuizResultActivity.this.startActivity(intent);
    }
}
