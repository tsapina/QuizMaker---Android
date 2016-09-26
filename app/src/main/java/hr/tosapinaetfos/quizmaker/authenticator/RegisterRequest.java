package hr.tosapinaetfos.quizmaker.authenticator;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by tosap on 28.04.2016..
 */
public class RegisterRequest extends StringRequest{

    //Request URL , Genimotion localhost adress and port
    private static final String REGISTER_REQUEST_URL = "http://www.etfos.unios.hr/~tsapina/Register1.php";

    //Map for saving data and sending them as a params; <key,value>
    private Map<String, String> params;

    //Constructor
    public RegisterRequest(String username,String password, String type,  Response.Listener<String> listener, Response.ErrorListener errorListener) {
        //Call String Request Constructor
        super(Method.POST, REGISTER_REQUEST_URL, listener, errorListener);
        //Creating instance
        params = new HashMap<>();
        //Fill maps with params
        params.put("user_name", username);
        params.put("user_password", password);
        params.put("user_type",type);
    }

    //Returns a Map of parameters to be used for POST request , class volley.Request
    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
