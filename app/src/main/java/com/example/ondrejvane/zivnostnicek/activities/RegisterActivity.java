package com.example.ondrejvane.zivnostnicek.activities;

import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.ondrejvane.zivnostnicek.R;
import com.example.ondrejvane.zivnostnicek.helper.HashPassword;
import com.example.ondrejvane.zivnostnicek.session.MySingleton;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    private EditText nameET;
    private EditText userAddressET;
    private EditText password1ET;
    private EditText password2ET;

    private static final String KEY_STATUS = "status";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_FULL_NAME = "full_name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";

    private HashPassword hashPassword;
    private String email;
    private String password;
    private String fullName;
    private ProgressDialog pDialog;
    private static final String register_url = "http://zivnostnicek.000webhostapp.com/register.php";


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        //skryje horní panel
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();

        //zákaz orientace na šířku
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_register);

        initActivity();
    }

    /**
     * Metoda, která inicializuje jednotlivé prvky aktivity.
     */
    private void initActivity() {
        nameET = findViewById(R.id.name);
        userAddressET = findViewById(R.id.userAddress);
        password1ET = findViewById(R.id.userPassword);
        password2ET = findViewById(R.id.userConfirmPassword);
        hashPassword = new HashPassword();
    }

    /**
     * Display Progress bar while registering
     */
    private void displayLoader() {
        pDialog = new ProgressDialog(RegisterActivity.this);
        pDialog.setMessage(getString(R.string.registration_in_progress));
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();

    }


    /**
     * Metoda, která po kliknutí na textView ukončím aktivitu
     * RegisterActivity a vrátí se do LoginActivity.
     *
     * @param view
     */
    public void goToLoginActivity(View view) {
        finish();
    }

    /**
     *
     * @param view
     */
    public void makeRegistration(View view){

        if(checkIfIsAllFilled()){
            displayLoader();
            JSONObject request = new JSONObject();
            try {
                String hashedPassword = hashPassword.hashPassword(password);
                //Populate the request parameters
                request.put(KEY_EMAIL, email);
                request.put(KEY_PASSWORD, hashedPassword);
                request.put(KEY_FULL_NAME, fullName);



            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsArrayRequest = new JsonObjectRequest
                    (Request.Method.POST, register_url, request, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            pDialog.dismiss();
                            try {
                                Log.d(TAG, "KEY_STATUS = "+ response.getString(KEY_STATUS));
                                Log.d(TAG, "MESSAGE = "+ response.getString(KEY_MESSAGE));
                                //Check if user got registered successfully
                                if (response.getInt(KEY_STATUS) == 0) {
                                    //Set the user session
                                    Log.d(TAG, "User is successfully register");
                                    Toast.makeText(getApplicationContext(),
                                            getString(R.string.user_is_created), Toast.LENGTH_SHORT).show();
                                    finish();

                                }else if(response.getInt(KEY_STATUS) == 1){
                                    //Display error message if username is already existsing
                                    userAddressET.setError(getString(R.string.user_already_exists));
                                    userAddressET.requestFocus();

                                }else{
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
                                    getString(R.string.can_not_connect_to_the_server), Toast.LENGTH_SHORT).show();
                            Log.d(TAG, error.getMessage());

                        }
                    });

            // Access the RequestQueue through your singleton class.
            MySingleton.getInstance(this).addToRequestQueue(jsArrayRequest);
        }

    }

    /**
     * Metoda, která zkontroluje zda jsou všechna pole registračního
     * formuláře korektně vyplněna.
     *
     * @return boolean
     */
    private boolean checkIfIsAllFilled() {

        String name = nameET.getText().toString().trim();
        String userAddress = userAddressET.getText().toString().trim();
        String password1 = password1ET.getText().toString().trim();
        String password2 = password2ET.getText().toString().trim();
        String message;

        if (name.isEmpty()){
            message = getString(R.string.empty_name);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            return false;
        }

        if (userAddress.isEmpty()){
            message = getString(R.string.empty_user_name);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(userAddress).matches()) {
            message = getString(R.string.email_is_not_valid);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            return false;
        }

        if (password1.isEmpty()){
            message = getString(R.string.empty_password1);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            return false;
        }

        if (password2.isEmpty()){
            message = getString(R.string.empty_password2);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            return false;
        }

        if (password1.compareTo(password2) != 0){
            message = getString(R.string.passwords_are_not_equal);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            return false;
        }

        if (password1.length() <=7){
            message = getString(R.string.passwords_is_too_short);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            return false;
        }

        //načtení do globálních proměnných
        fullName = nameET.getText().toString().trim();
        email = userAddressET.getText().toString().trim();
        password = password1ET.getText().toString().trim();

        return true;

    }

}
