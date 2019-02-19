package com.example.ondrejvane.zivnostnicek.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.example.ondrejvane.zivnostnicek.R;
import com.example.ondrejvane.zivnostnicek.activities.home.HomeActivity;
import com.example.ondrejvane.zivnostnicek.database.UserDatabaseHelper;
import com.example.ondrejvane.zivnostnicek.helper.HashPassword;
import com.example.ondrejvane.zivnostnicek.helper.Settings;
import com.example.ondrejvane.zivnostnicek.helper.UserInformation;
import com.example.ondrejvane.zivnostnicek.model.User;
import com.example.ondrejvane.zivnostnicek.session.MySingleton;
import com.example.ondrejvane.zivnostnicek.session.SessionHandler;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private final String TAG = "LoginActivity";

    private EditText userAddressET;
    private EditText passwordET;
    private CheckBox rememberMeBox;
    private ImageView imageViewLogo;

    private UserDatabaseHelper userDatabaseHelper;
    private HashPassword hashPassword;
    private Settings settings;
    private static final String KEY_STATUS = "status";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_FULL_NAME = "full_name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_ID = "id";
    private static final String KEY_EMPTY = "";
    private String email;
    private String password;
    private ProgressDialog pDialog;
    //private String login_url = "http://10.0.0.2:8089/Zivnostnicek/login.php";
    //private static final String login_url = "http://zivnostnicek.000webhostapp.com/login.php";
    private static String login_url = "/login.php";
    private SessionHandler session;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "Starting activity");

        //skryje horní panel
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();

        //zákaz orientace na šířku
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_login);

        initActivity();

        Log.d(TAG, "Activity successfully started");

    }

    /**
     * Metoda, která inicializuje prvky teto aktivity.
     */
    private void initActivity() {
        userAddressET = findViewById(R.id.userAddress);
        passwordET = findViewById(R.id.userPassword);
        rememberMeBox = findViewById(R.id.checkBox);
        imageViewLogo = findViewById(R.id.imageViewLoginLogo);
        userDatabaseHelper = new UserDatabaseHelper(LoginActivity.this);
        hashPassword = new HashPassword();
        settings = Settings.getInstance();
        session = new SessionHandler(getApplicationContext());

        //zobrazit logo do aktivity => musí se nastavit takto pomocí knihovny, jinak vytvoří memory leak
        Glide.with(this).load(R.drawable.logo1).into(imageViewLogo);

        //přidat název serveru
        login_url = getResources().getString(R.string.web_server) + login_url;

    }

    /**
     * Metoda, která po kliknutí na textView přepne aktivitu do RegisterActivity.
     * @param view
     */
    public void goToRegisterActivity(View view) {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }


    /**
     * Metoda, která zkotroluje, zda jsou všechna pole správně vyplněna.
     * @return  logická hodnota true/false
     */
    private boolean checkIfIsAllFilled() {
        String emailAddress = userAddressET.getText().toString().trim();
        String passwordLoad = passwordET.getText().toString().trim();
        String message;

        if(emailAddress.isEmpty()){
            message = getString(R.string.empty_user_name);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()) {
            message = getString(R.string.email_is_not_valid);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            return false;
        }

        if (passwordLoad.isEmpty()){
            message = getString(R.string.empty_password1);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            return false;
        }

        //načtení do globálních promměnných
        email = userAddressET.getText().toString().trim();
        password = passwordET.getText().toString().trim();

        return true;
    }

    /**
     * Display Progress bar while registering
     */
    private void displayLoader() {
        pDialog = new ProgressDialog(LoginActivity.this);
        pDialog.setMessage(getString(R.string.registration_in_progress));
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();

    }

    public void logIn(View view) {

        if(checkIfIsAllFilled()) {
            displayLoader();
            JSONObject request = new JSONObject();
            try {
                String hashedPassword = hashPassword.hashPassword(password);
                //Populate the request parameters
                request.put(KEY_EMAIL, email);
                request.put(KEY_PASSWORD, hashedPassword);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            JsonObjectRequest jsArrayRequest = new JsonObjectRequest
                    (Request.Method.POST, login_url, request, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            pDialog.dismiss();
                            try {
                                //Check if user got logged in successfully

                                Log.d(TAG, "KEY_STATUS = "+ response.getString(KEY_STATUS));
                                Log.d(TAG, "MESSAGE = "+ response.getString(KEY_MESSAGE));

                                if (response.getInt(KEY_STATUS) == 0) {
                                    //pokud je zaškrtnuto pole pro zustat přihlášen
                                    if (rememberMeBox.isChecked()) {
                                        session.loginUser(email, response.getString(KEY_FULL_NAME), response.getInt(KEY_ID));
                                    }
                                    //nastavení informací do pomocné třídy, aby byli přístupné všude v aplikaci
                                    UserInformation userInformation = UserInformation.getInstance();
                                    userInformation.setMail(email);
                                    userInformation.setFullName(response.getString(KEY_FULL_NAME));
                                    userInformation.setUserId(response.getInt(KEY_ID));

                                    User user = new User();
                                    user.setId(response.getInt(KEY_ID));
                                    user.setEmail(email);
                                    user.setFullName(response.getString(KEY_FULL_NAME));
                                    user.setPassword(hashPassword.hashPassword(password));

                                    //vložení záznamu uživatele do databáze, pokud ještě neexistuje
                                    userDatabaseHelper.addUser(user);

                                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                    startActivity(intent);
                                    finish();

                                } else {
                                    Toast.makeText(getApplicationContext(),
                                            response.getString(KEY_MESSAGE), Toast.LENGTH_SHORT).show();

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            pDialog.dismiss();

                            //Display error message whenever an error occurs
                            Toast.makeText(getApplicationContext(),
                                    error.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });

            // Access the RequestQueue through your singleton class.
            MySingleton.getInstance(this).addToRequestQueue(jsArrayRequest);
        }

    }

    /**
     * Pokud uživatel zvolí možnost "Zůstat přihlášen", tak se do shared preferences
     * uloží jeho emailová adresa a nastaví se hodnota IS_LOGEDIN na true. Pokud nezvolí
     * zústat přihlášen ,tak se nastaví hodnota IS_LOGEDIN na false.
     */
    private void stayLogIn(){
        String userAddress = userAddressET.getText().toString();
        SharedPreferences sp = getSharedPreferences("USER", MODE_PRIVATE);

        if(rememberMeBox.isChecked()){
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean("IS_LOGEDIN", true);
            editor.putString("USER", userAddress);
            editor.apply();

        }else {
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean("IS_LOGEDIN", false);
            editor.putString("USER", userAddress);
            editor.apply();
        }

    }

    private void loadAllInformation(){

        SharedPreferences sp = getSharedPreferences("USER", MODE_PRIVATE);
        String emailAddress = sp.getString("USER", "NULL");
        User user = userDatabaseHelper.getUserByEmailAddress(emailAddress);
        UserInformation userInformation = UserInformation.getInstance();
        userInformation.setMail(user.getEmail());
        userInformation.setFullName(user.getFullName());
        userInformation.setUserId(user.getId());

        //načtení dat o nstavení aplikace ze shared preferences
        settings.readSettingsFromSharedPreferences(this);

    }

    private void loadAllInformation(String emailAddress){
        User user = userDatabaseHelper.getUserByEmailAddress(emailAddress);
        UserInformation userInformation = UserInformation.getInstance();
        userInformation.setMail(user.getEmail());
        userInformation.setFullName(user.getFullName());
        userInformation.setUserId(user.getId());

        //načtení dat o nstavení aplikace ze shared preferences
        settings.readSettingsFromSharedPreferences(this);
    }
}
