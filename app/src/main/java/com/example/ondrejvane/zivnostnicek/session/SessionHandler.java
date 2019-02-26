package com.example.ondrejvane.zivnostnicek.session;
import android.content.Context;
import android.content.SharedPreferences;

import com.example.ondrejvane.zivnostnicek.model.User;

import java.util.Date;

/**
 * Created by Abhi on 20 Jan 2018 020.
 */

public class SessionHandler {


    private static final String PREF_NAME = "UserSession";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_FULL_NAME = "full_name";
    private static final String KEY_USER_ID = "id";
    private static final String KEY_EMPTY = "";
    private Context mContext;
    private SharedPreferences.Editor mEditor;
    private SharedPreferences mPreferences;

    public SessionHandler(Context mContext) {
        this.mContext = mContext;
        mPreferences = mContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.mEditor = mPreferences.edit();
    }

    /**
     * Logs in the user by saving user details and setting session
     *
     * @param user uživatel
     */
    public void loginUser(User user) {
        mEditor.putString(KEY_EMAIL, user.getEmail());
        mEditor.putString(KEY_FULL_NAME, user.getFullName());
        mEditor.putInt(KEY_USER_ID, user.getId());
        mEditor.commit();
    }


    /**
     * Checks whether user is logged in
     *
     * @return
     */
    public boolean isLoggedIn() {


        int userId = mPreferences.getInt(KEY_USER_ID, -1);

        if(userId == -1){
            return false;
        }else {
            return true;
        }
    }

    /**
     * Fetches and returns user details
     *
     * @return user details
     */
    public User getUserDetails() {
        //Check if user is logged in first
        if (!isLoggedIn()) {
            return null;
        }
        User user = new User();
        user.setEmail(mPreferences.getString(KEY_EMAIL, KEY_EMPTY));
        user.setFullName(mPreferences.getString(KEY_FULL_NAME, KEY_EMPTY));
        user.setId(mPreferences.getInt(KEY_USER_ID, 0));

        return user;
    }

    /**
     * Logs out user by clearing the session
     */
    public void logoutUser(){
        mEditor.clear();
        mEditor.commit();
    }

}
