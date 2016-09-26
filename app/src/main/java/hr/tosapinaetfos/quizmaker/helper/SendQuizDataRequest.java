package hr.tosapinaetfos.quizmaker.helper;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by tosap on 11.05.2016..
 */
public class SendQuizDataRequest extends StringRequest{

    //Request URL , Genimotion localhost adress and port
    private static final String REGISTER_REQUEST_URL = "http://www.etfos.unios.hr/~tsapina/InsertQuizData.php"; // adresa preko koje genimotion moze pristupiti localhostu

    //Map for saving data and sending them as a params; <key,value>
    private Map<String, String> params;

    public SendQuizDataRequest(String quizDataJsonString, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Request.Method.POST, REGISTER_REQUEST_URL, listener, errorListener);

        params = new HashMap<>();

        //Fill maps with params
        params.put("quizDataJsonString", quizDataJsonString);

    }

    //Returns a Map of parameters to be used for POST request , class volley.Request
    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
