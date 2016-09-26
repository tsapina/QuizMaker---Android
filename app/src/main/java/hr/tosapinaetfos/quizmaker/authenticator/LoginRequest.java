package hr.tosapinaetfos.quizmaker.authenticator;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by tosap on 29.04.2016..
 */
public class LoginRequest extends StringRequest{

    //Request URL , Genimotion localhost adress and port
    private static final String REGISTER_REQUEST_URL = "http://www.etfos.unios.hr/~tsapina/Login1.php"; // adresa preko koje genimotion moze pristupiti localhostu

    //Map for saving data and sending them as a params; <key,value>
    private Map<String, String> params;

    //Constructor
    public LoginRequest(String username,String password,  Response.Listener<String> listener, Response.ErrorListener errorListener) {

        super(Request.Method.POST, REGISTER_REQUEST_URL, listener, errorListener);
        params = new HashMap<>();
        //Fill maps with params
        params.put("user_name", username);
        params.put("user_password", password);

    }

    //Returns a Map of parameters to be used for POST request , class volley.Request
    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
