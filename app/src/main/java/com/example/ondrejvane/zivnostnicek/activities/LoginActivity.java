package com.example.ondrejvane.zivnostnicek.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.example.ondrejvane.zivnostnicek.R;
import com.example.ondrejvane.zivnostnicek.database.DatabaseHelper;
import com.example.ondrejvane.zivnostnicek.helper.HashPassword;
import com.example.ondrejvane.zivnostnicek.helper.UserInformation;
import com.example.ondrejvane.zivnostnicek.model.User;

public class LoginActivity extends AppCompatActivity {

    private EditText userAddressET;
    private EditText passwordET;
    private CheckBox rememberMeBox;

    private DatabaseHelper databaseHelper;
    private HashPassword hashPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        //skryje horní panel
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();

        //zákaz orientace na šířku
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_login);


        initActivity();

        //kontrola, zda uživatel zůstal přihlášen
        if (checkIfYouAreLogIn()){
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }


    }

    /**
     * Metoda, která inicializuje prvky teto aktivity.
     */
    private void initActivity() {
        userAddressET = findViewById(R.id.userAddress);
        passwordET = findViewById(R.id.userPassword);
        rememberMeBox = findViewById(R.id.checkBox);
        databaseHelper = new DatabaseHelper(LoginActivity.this);
        hashPassword = new HashPassword();
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
        String emailAddress = userAddressET.getText().toString();
        String password = passwordET.getText().toString();
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

        if (password.isEmpty()){
            message = getString(R.string.empty_password1);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void logIn(View view) {

        if (checkIfIsAllFilled() == true){

            String hashedPassword = hashPassword.hashPassword(passwordET.getText().toString());

            if(databaseHelper.checkUser(userAddressET.getText().toString(),hashedPassword)){
                String message = getString(R.string.you_are_succesful_login);
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                loadAllInformation();
                stayLogIn();
                startActivity(intent);
                finish();
            }else {
                String message = getString(R.string.wrong_password_or_address);
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
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
            editor.commit();

        }else {
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean("IS_LOGEDIN", false);
            editor.putString("USER", userAddress);
            editor.commit();
        }

    }

    private boolean checkIfYouAreLogIn(){
        SharedPreferences sp = getSharedPreferences("USER", MODE_PRIVATE);
        boolean logedIn = sp.getBoolean("IS_LOGEDIN", false);

        if (logedIn == true){
            loadAllInformation();
            return logedIn;
        }
        return logedIn;
    }

    private void loadAllInformation(){

        SharedPreferences sp = getSharedPreferences("USER", MODE_PRIVATE);
        String emailAddress = sp.getString("USER", "NULL");
        User user = databaseHelper.getUserByEmailAddress(emailAddress);
        UserInformation userInformation = UserInformation.getInstance();
        userInformation.setMail(user.getEmail());
        userInformation.setFullName(user.getFullName());
        userInformation.setUserId(user.getId());

    }
}
