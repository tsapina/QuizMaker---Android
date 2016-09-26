package hr.tosapinaetfos.quizmaker.activities.activitiesLoginRegister;

import android.app.Activity;
import android.app.AlertDialog;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import hr.tosapinaetfos.quizmaker.activities.activitiesQuizCreator.QuizCreatorActivity;
import hr.tosapinaetfos.quizmaker.activities.activitiesQuizUser.QuizUserActivity;
import hr.tosapinaetfos.quizmaker.authenticator.LoginRequest;
import hr.tosapinaetfos.quizmaker.R;
import hr.tosapinaetfos.quizmaker.authenticator.SessionManager;
import hr.tosapinaetfos.quizmaker.helper.InternetHelper;

public class LoginActivity extends AppCompatActivity {

    SessionManager session;
    EditText etUsername;
    EditText etPassword;
    Button btnLogin;
    TextView tvRegisterLink;
    InternetHelper isConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new SessionManager(getApplicationContext());

        setContentView(R.layout.activity_register);
        //check is session on ,redirect
        if (session.isLoggedIn()) {
            HashMap < String, String > user = session.getUserDetails();
            session.redirect(user.get(SessionManager.KEY_USER_TYPE));
        }
        setContentView(R.layout.activity_login);

        //Fetch username, password, registerLink, button  by id
        etUsername = (EditText) findViewById(R.id.etLoginUsername);
        etPassword = (EditText) findViewById(R.id.etLoginPassword);
        tvRegisterLink = (TextView) findViewById(R.id.tvRegisterLink);
        btnLogin = (Button) findViewById(R.id.btnLogin);

    }

    public void loginOnClick(View v) {
        isConnected = new InternetHelper();
        if(isConnected.isNetworkAvailable(getApplicationContext())){
            //Get text from objects
            String strUsername = etUsername.getText().toString();
            String strPassword = etPassword.getText().toString();

            if (!isEmpty(strUsername, strPassword)) {

                //Response Listener, import com.android.volley.
                Response.Listener < String > responseListener = new Response.Listener < String > () {

                    //Response Listener, import com.android.volley.
                    @Override
                    public void onResponse(String response) {
                        //Try to fetch JSONObject
                        try {
                            //Print response
                            Log.i("tagconvertstr", "[" + response + "]");

                            //Creating a new JSONObject with name/value mappings from the JSON string
                            JSONObject jsonResponse = new JSONObject(response);
                            //Fetching $response["success"]
                            boolean success = jsonResponse.getBoolean("success");

                            //IF success start new activity , else print message
                            if (success) {
                                // Create Session Manager object
                                session = new SessionManager(getApplicationContext());

                                //From jsonObject to strings
                                String user_id = jsonResponse.getString("user_id");
                                String user_name = jsonResponse.getString("user_name");
                                String user_type = jsonResponse.getString("user_type");

                                //Create new Session
                                session.createLoginSession(user_id, user_name, user_type);

                                //Check user type
                                if (user_type.equals("Kviz kreator")) {
                                    Intent intent = new Intent(LoginActivity.this, QuizCreatorActivity.class);
                                    LoginActivity.this.startActivity(intent);

                                } else {
                                    Intent intent = new Intent(LoginActivity.this, QuizUserActivity.class);
                                    LoginActivity.this.startActivity(intent);
                                }
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                builder.setMessage(getResources().getString(R.string.login_user_pass_validation))
                                        .setNegativeButton(getResources().getString(R.string.alert), null)
                                        .create()
                                        .show();
                            }
                            //JSONException if the parse fails or doesn't yield a
                        } catch (JSONException e) {
                            Toast toast = Toast.makeText(LoginActivity.this, getResources().getString(R.string.server_response_error) , Toast.LENGTH_LONG);
                            toast.show();
                            e.printStackTrace();
                        }
                    } //End of public void onResponse(String response)
                }; //End of Response.Listener<String> responseListener

                //Print error when server is inactive
                Response.ErrorListener errorListener = new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast toast = Toast.makeText(LoginActivity.this, getResources().getString(R.string.server_response_error), Toast.LENGTH_SHORT);
                        toast.show();
                        Log.d("error", "response error");
                    }
                };

                //Creating loginiRequest and passing parameters for constructor
                LoginRequest loginRequest = new LoginRequest(strUsername, strPassword, responseListener, errorListener);

                //Send a Request
                RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                queue.add(loginRequest);

            } // End of validation process
        }//End of isConnected.isInternetAvailable()
        else
        {
            Toast toast = Toast.makeText(LoginActivity.this, getResources().getString(R.string.internet_connection) , Toast.LENGTH_SHORT);
            toast.show();
        }

    } //End of  loginOnClick

    //On register link click
    public void registerLinkOnClick(View v) {
        Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
        LoginActivity.this.startActivity(registerIntent);
    }


    public Boolean isEmpty(String strUsername, String strPassword) {
        Boolean isThereEmptyFields = false;

        if (strUsername.length() == 0) {
            etUsername.setError(getResources().getString(R.string.login_username_validation));
            isThereEmptyFields = true;
        }
        if (strPassword.length() == 0) {
            etPassword.setError(getResources().getString(R.string.login_password_validation));
            isThereEmptyFields = true;
        }
        return isThereEmptyFields;
    }
}