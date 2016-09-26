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
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import hr.tosapinaetfos.quizmaker.R;
import hr.tosapinaetfos.quizmaker.activities.activitiesQuizCreator.ListOfQuizzesActivity;
import hr.tosapinaetfos.quizmaker.authenticator.RegisterRequest;
import hr.tosapinaetfos.quizmaker.authenticator.SessionManager;
import hr.tosapinaetfos.quizmaker.helper.InternetHelper;

public class RegisterActivity extends AppCompatActivity {
    SessionManager session;
    EditText etUsername;
    EditText etPassword;
    EditText etPasswordAgain;
    Spinner accountTypeSpinner;
    Button btnRegister;
    InternetHelper isConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Fetch username, password, passwordAgain , accountTypeSpinner, registerButton  by id
        etUsername = (EditText) findViewById(R.id.etRegisterUsername);
        etPassword = (EditText) findViewById(R.id.etRegisterPassword);
        etPasswordAgain = (EditText) findViewById(R.id.etRegisterPasswordAgain);
        accountTypeSpinner = (Spinner) findViewById(R.id.sRegisterSpinner);
        btnRegister = (Button) findViewById(R.id.btnRegister);
    }

    public void registerOnclick(View v){
        isConnected = new InternetHelper();
        if(isConnected.isNetworkAvailable(getApplicationContext())) {
            //Get text from objects
            String strUsername = etUsername.getText().toString();
            String strPassword = etPassword.getText().toString();
            String strPassowrdAgain = etPasswordAgain.getText().toString();
            String strAccountType = accountTypeSpinner.getSelectedItem().toString();

            //validation process checks userlength, passwordlength, passwordmatch, isempty
            if (validationProcess(strUsername, strPassword, strPassowrdAgain)) {

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

                            //Fetching $response["success"] and  $response["user_exist"] Boolean type,
                            boolean user_exist = jsonObject.getBoolean("user_exist");
                            boolean success = jsonObject.getBoolean("success");

                            //If success == true then start LoginActivity , else print message
                            if (success && !user_exist) {
                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                RegisterActivity.this.startActivity(intent);
                                Toast toast = Toast.makeText(RegisterActivity.this, getResources().getString(R.string.register_success), Toast.LENGTH_SHORT);
                                toast.show();
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                builder.setMessage(getResources().getString(R.string.register_username_exist))
                                        .setNegativeButton(getResources().getString(R.string.alert), null)
                                        .create()
                                        .show();
                            }
                            //JSONException if the parse fails or doesn't yield a
                        } catch (JSONException e) {
                            Toast toast = Toast.makeText(RegisterActivity.this, getResources().getString(R.string.server_response_error), Toast.LENGTH_SHORT);
                            toast.show();
                            e.printStackTrace();
                        }
                    }//End of public void onResponse(String response)
                };//End of Response.Listener<String> responseListener

                //Print error when server is inactive
                Response.ErrorListener errorListener = new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast toast = Toast.makeText(RegisterActivity.this, getResources().getString(R.string.server_response_error), Toast.LENGTH_SHORT);
                        toast.show();
                    }
                };

                //Creating registerRequest and passing parameters for constructor
                RegisterRequest registerRequest = new RegisterRequest(strUsername, strPassword, strAccountType, responseListener, errorListener);

                //Send a Request
                RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
                queue.add(registerRequest);
            }// End of validation process
        }
        else
        {
            Toast toast = Toast.makeText(RegisterActivity.this, getResources().getString(R.string.internet_connection) , Toast.LENGTH_SHORT);
            toast.show();
        }
    }//End of registerOnclick


    //Check each field is empty
    public Boolean isEmpty(String strUsername, String strPassword, String strPassowrdAgain){
        Boolean isThereEmptyFields = false;

        if (strUsername.length() ==  0) {
            etUsername.setError(getResources().getString(R.string.register_username_validation));
            isThereEmptyFields =true;
        }
        if (strPassword.length() ==  0) {
            etPassword.setError(getResources().getString(R.string.register_password_validation));
            isThereEmptyFields =true;
        }
        if (strPassowrdAgain.length() ==  0) {
            etPasswordAgain.setError(getResources().getString(R.string.register_password_again_validation));
            isThereEmptyFields =true;
        }
        return isThereEmptyFields;
    }


    //Check password and passwordagain fields
    public Boolean passwordMatch(String strPassword, String strPassowrdAgain){
        if(!strPassword.equals(strPassowrdAgain)){
            etPasswordAgain.setError(getResources().getString(R.string.register_password_match_validation));
            return false;
        }
        return true;
    }

    //Check user field ,user length
    public Boolean userLength(String strUsername){
        if(strUsername.length() <= 3){
            etUsername.setError(getResources().getString(R.string.register_username_length_validation));
            return false;
        }
        return true;
    }

    //Check passwordfield, pass length
    public Boolean passwordLength(String strPassword){
        if(strPassword.length() <= 5){
            etPassword.setError(getResources().getString(R.string.register_password_length_validation));
            return false;
        }
        return true;
    }

    //Validationprocess checks userlength, passwordlength, passwordmatch, isempty
    public Boolean validationProcess(String strUsername, String strPassword, String strPassowrdAgain){
        if(!isEmpty(strUsername,strPassword,strPassowrdAgain)
                && userLength(strUsername)
                && passwordLength(strPassword)
                && passwordMatch(strPassword, strPassowrdAgain)){
            return true;
        }
        return false;
    }

    public void btnRegisterBack(View v){
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        RegisterActivity.this.startActivity(intent);
    }

}


