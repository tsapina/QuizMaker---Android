package hr.tosapinaetfos.quizmaker.helper;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by tosap on 15.09.2016..
 */
public class ResultUploadHelper extends StringRequest {

    //Request URL , Genimotion localhost adress and port
    private static final String REGISTER_REQUEST_URL = "http://www.etfos.unios.hr/~tsapina/InsertResultsData.php";

    //Map for saving data and sending them as a params; <key,value>
    private Map<String, String> params;

    //Constructor
    public ResultUploadHelper(String username,String quizKey, String result, String currentDateandTime, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        //Call String Request Constructor
        super(Method.POST, REGISTER_REQUEST_URL, listener, errorListener);
        //Creating instance
        params = new HashMap<>();

        //Fill maps with params
        params.put("user_name", username);
        params.put("quiz_key", quizKey);
        params.put("result", result);
        params.put("date_time", currentDateandTime);

    }


    //Returns a Map of parameters to be used for POST request , class volley.Request
    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
