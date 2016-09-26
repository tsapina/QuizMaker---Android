package hr.tosapinaetfos.quizmaker.authenticator;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.HashMap;

import hr.tosapinaetfos.quizmaker.activities.activitiesLoginRegister.LoginActivity;
import hr.tosapinaetfos.quizmaker.activities.activitiesQuizCreator.QuizCreatorActivity;
import hr.tosapinaetfos.quizmaker.activities.activitiesQuizUser.QuizUserActivity;

/**
 * Created by tosap on 30.04.2016..
 * http://www.androidhive.info/2012/08/android-session-management-using-shared-preferences/
 */
public class SessionManager {

    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    SharedPreferences.Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "QuizMaker";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";

    // User id
    public static final String KEY_USER_ID = "user_id";

    // User name
    public static final String KEY_USER_NAME = "user_name";

    // User type
    public static final String KEY_USER_TYPE = "user_type";

    // Constructor
    public SessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Create login session
     * */
    public void createLoginSession(String user_id, String user_name, String user_type){

        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        // Storing id in pref
        editor.putString(KEY_USER_ID, user_id);

        // Storing name in pref
        editor.putString(KEY_USER_NAME, user_name);

        // Storing type in pref
        editor.putString(KEY_USER_TYPE, user_type);

        // commit changes
        editor.commit();
    }

    /**
     * Get stored session data
     * */
    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        // user id
        user.put(KEY_USER_ID, pref.getString(KEY_USER_ID, null));

        // user name
        user.put(KEY_USER_NAME, pref.getString(KEY_USER_NAME, null));

        // user type
        user.put(KEY_USER_TYPE, pref.getString(KEY_USER_TYPE, null));

        // return user
        return user;
    }

    /**
     * Check login method wil check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     * */
    public void checkLogin(){
        // Check login status
        if(!this.isLoggedIn()){
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, LoginActivity.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);
        }

    }

    /**
     * Clear session details
     * */
    public void logoutUser(){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

        // After logout redirect user to Loing Activity
        Intent i = new Intent(_context, LoginActivity.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        _context.startActivity(i);
    }

    /**
     * Quick check for login
     * **/
    // Get Login State
    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }

    /**
     * Redirect if session is on , parameter String user_type
     * **/
    public void redirect(String user_type){

        if(user_type.equals("Kviz kreator") ){
            Intent intent = new Intent(_context, QuizCreatorActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            _context.startActivity(intent);
        }else{
            Intent intent = new Intent(_context, QuizUserActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            _context.startActivity(intent);
        }
    }
}
