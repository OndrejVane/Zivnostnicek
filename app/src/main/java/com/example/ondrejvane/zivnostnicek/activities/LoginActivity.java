package com.example.ondrejvane.zivnostnicek.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
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
import com.example.ondrejvane.zivnostnicek.helper.UserInformation;
import com.example.ondrejvane.zivnostnicek.model.User;
import com.example.ondrejvane.zivnostnicek.session.MySingleton;
import com.example.ondrejvane.zivnostnicek.session.SessionHandler;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private final String TAG = "LoginActivity";

    //prvky aktivity
    private EditText userAddressET;
    private EditText passwordET;
    private CheckBox rememberMeBox;
    private ImageView imageViewLogo;

    //pomocné globální proměnné
    private UserDatabaseHelper userDatabaseHelper;
    private HashPassword hashPassword;
    private static final String KEY_STATUS = "status";
    private static final String KEY_FULL_NAME = "full_name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_ID = "id";
    private String email;
    private String password;
    private ProgressDialog pDialog;
    private static final String login_url = "http://zivnostnicek.000webhostapp.com/api2/login.php";
    private SessionHandler session;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "Starting activity");

        //skryje horní panel
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();

        //zákaz orientace na šířku
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_login);

        //inicializace aktivity
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
        session = new SessionHandler(getApplicationContext());

        //zobrazit logo do aktivity => musí se nastavit takto pomocí knihovny, jinak vytvoří memory leak
        Glide.with(this).load(R.drawable.logo1).into(imageViewLogo);

    }

    /**
     * Metoda, která po kliknutí na textView přepne aktivitu do RegisterActivity.
     *
     * @param view view
     */
    public void goToRegisterActivity(View view) {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }


    /**
     * Metoda, která zkotroluje, zda jsou všechna pole správně vyplněna.
     *
     * @return logická hodnota true/false
     */
    private boolean checkIfIsAllFilled() {
        String emailAddress = userAddressET.getText().toString().trim();
        String passwordLoad = passwordET.getText().toString().trim();
        String message;

        if (emailAddress.isEmpty()) {
            message = getString(R.string.empty_user_name);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()) {
            message = getString(R.string.email_is_not_valid);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            return false;
        }

        if (passwordLoad.isEmpty()) {
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
        pDialog.setMessage(getString(R.string.login_in_progress));
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();

    }

    public void logIn(View view) {

        if (checkIfIsAllFilled()) {
            displayLoader();
            JSONObject request = new JSONObject();
            try {
                String hashedPassword = hashPassword.hashPassword(password);
                //vložení uživatelských dat do JSONu
                request.put(KEY_EMAIL, email);
                request.put(KEY_PASSWORD, hashedPassword);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            //poslání JSONu na server a čekání na odpověd
            JsonObjectRequest jsArrayRequest = new JsonObjectRequest
                    (Request.Method.POST, login_url, request, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            pDialog.dismiss();
                            try {


                                Log.d(TAG, "KEY_STATUS = " + response.getString(KEY_STATUS));

                                //uživatel byl úspěšně autentizován serverem
                                if (response.getInt(KEY_STATUS) == 0) {

                                    //načtení informací o uživateli
                                    User user = new User();
                                    user.setId(response.getInt(KEY_ID));
                                    user.setEmail(email);
                                    user.setFullName(response.getString(KEY_FULL_NAME));

                                    //je lokálně uložen v databázi??
                                    if (userDatabaseHelper.getUserById(user.getId()) == null) {
                                        //není uložen, musím ho vložit do lokální databáze
                                        //jeho synchronizační číslo bude 0, protože nemá zatím žádná data synchronizována
                                        user.setSyncNumber(0);
                                        //vložení uživatele do databáze
                                        userDatabaseHelper.addUser(user);
                                    } else {
                                        //už je uložen v lokální databázi a jen si načtu aktuální synchronizační číslo z db
                                        int syncNumber = userDatabaseHelper.getSyncNumberByUserId(user.getId());
                                        user.setSyncNumber(syncNumber);
                                    }


                                    //pokud je zaškrtnuto pole pro zustat přihlášen
                                    if (rememberMeBox.isChecked()) {
                                        session.loginUser(user);
                                    }

                                    //nastavení informací o uživateli do singletonu, aby byla přístupná všude v aplikaci
                                    UserInformation userInformation = UserInformation.getInstance();
                                    userInformation.setDataFromUser(user);

                                    //nastartování hlavní aktivity aplikace
                                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                    startActivity(intent);
                                    finish();

                                    //přihlášení neproběhlo správně, heslo nebo mail není strávné
                                } else if (response.getInt(KEY_STATUS) == 1) {
                                    //vypsání uživateli
                                    Toast.makeText(getApplicationContext(),
                                            getResources().getString(R.string.wrong_password_or_address),
                                            Toast.LENGTH_SHORT).show();

                                    //nevyplněné údaje
                                } else {
                                    //vypsání informace uživateli
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

                            //zobrazení informace uživateli, pokud došlo k chybě
                            Toast.makeText(getApplicationContext(),
                                    getResources().getString(R.string.can_not_connect_to_the_server), Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "Error message: " + error.getMessage());

                        }
                    });

            MySingleton.getInstance(this).addToRequestQueue(jsArrayRequest);
        }

    }
}
