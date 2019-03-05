package com.example.ondrejvane.zivnostnicek.activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.ondrejvane.zivnostnicek.R;
import com.example.ondrejvane.zivnostnicek.helper.HashPassword;
import com.example.ondrejvane.zivnostnicek.helper.SecureSending;
import com.example.ondrejvane.zivnostnicek.server.Server;
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
    private static final String KEY_FULL_NAME = "full_name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";

    private SecureSending secureSending;
    private HashPassword hashPassword;
    private String email;
    private String password;
    private String fullName;
    private ProgressDialog pDialog;


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
        secureSending = new SecureSending(null, null);
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
                //zahashování hesla
                String hashedPassword = hashPassword.hashPassword(password);
                //naplněné JSONu daty
                request.put(KEY_EMAIL, secureSending.encrypt(email));
                request.put(KEY_PASSWORD, secureSending.encrypt(hashedPassword));
                request.put(KEY_FULL_NAME, secureSending.encrypt(fullName));

            } catch (JSONException e) {
                e.printStackTrace();
            }

            String url = Server.getInstance().getRegisterUrl();
            JsonObjectRequest jsArrayRequest = new JsonObjectRequest
                    (Request.Method.POST, url, request, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            pDialog.dismiss();
                            try {
                                Log.d(TAG, "KEY_STATUS = "+ response.getString(KEY_STATUS));

                                int status = Integer.parseInt(secureSending.decrypt(response.getString(KEY_STATUS)));
                                if (status == 0) {
                                    //Set the user session
                                    Log.d(TAG, "User is successfully register");
                                    Toast.makeText(getApplicationContext(),
                                            getString(R.string.user_is_created), Toast.LENGTH_SHORT).show();
                                    finish();

                                }else if(status == 1){
                                    //Display error message if username is already existsing
                                    userAddressET.setError(getString(R.string.user_already_exists));
                                    userAddressET.requestFocus();

                                }else{
                                    Toast.makeText(getApplicationContext(),
                                            getResources().getString(R.string.fill_all_columns),
                                            Toast.LENGTH_SHORT).show();


                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            pDialog.dismiss();

                            //vypsání erroru do konzole a uživatele informovat o nedostupném serveru
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

    /**
     * Zobrazení dialogového okna pro nastavení
     * atributů serveru. Následné infomrace jsou uloženy do
     * třídy server a také do
     * @param view view aktivity
     */
    public void showServerDialog(View view) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(RegisterActivity.this);
        @SuppressLint("InflateParams")
        View mView = getLayoutInflater().inflate(R.layout.server_settings_dialog, null);

        //inicializace všech prvků v dialogovém okně
        final Spinner spinnerProtocol = mView.findViewById(R.id.spinnerServerProtocol);
        final EditText editTextServerName = mView.findViewById(R.id.editTextServerName);
        final TextInputLayout inputLayoutServerName = mView.findViewById(R.id.layoutServerName);
        final Button buttonSave = mView.findViewById(R.id.buttonSaveServerSettings);

        //inicializace singletonu server pro načtení předchozího nastavení
        final Server server = Server.getInstance();

        //zobrazení předchozího nastavení
        spinnerProtocol.setSelection(server.getServerProtocolId());
        editTextServerName.setText(server.getServerName());

        //zobrazení dialogovéhookna
        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int protocol = spinnerProtocol.getSelectedItemPosition();
                String serverName = editTextServerName.getText().toString();

                if(serverName.isEmpty()){
                    inputLayoutServerName.setError(getString(R.string.empty_name));
                    return;
                }
                Log.d(TAG, "id: "+ protocol);

                server.setServerNameAndProtocol(serverName, protocol);
                server.saveDataToSharedPref(RegisterActivity.this);

                dialog.dismiss();
            }
        });

    }

}
