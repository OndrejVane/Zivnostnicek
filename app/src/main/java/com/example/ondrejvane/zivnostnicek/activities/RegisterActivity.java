package com.example.ondrejvane.zivnostnicek.activities;

import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ondrejvane.zivnostnicek.R;
import com.example.ondrejvane.zivnostnicek.database.DatabaseHelper;
import com.example.ondrejvane.zivnostnicek.model.User;

public class RegisterActivity extends AppCompatActivity {

    private EditText nameET;
    private EditText userAddressET;
    private EditText password1ET;
    private EditText password2ET;

    private DatabaseHelper databaseHelper;
    private User user;

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
        databaseHelper = new DatabaseHelper(RegisterActivity.this);
        user = new User();
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
     * Metoda, která pomocí ostatních metod zjistí, zda jsou vyplněny
     * vstupní pole a vloží uživatele do databáze.
     * @param view
     */
    public void makeRegistration(View view){

        if (checkIfIsAllFilled() && !isUserExists()){
            user.setFullName(nameET.getText().toString());
            user.setEmail(userAddressET.getText().toString());
            user.setPassword(password1ET.getText().toString());

            databaseHelper.addUser(user);

            String message = getString(R.string.user_is_created);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            finish();
        }

    }

    /**
     * Metoda, která se zjistí zda uživatel se zadanou e-mailovou
     * adresou již existuje nebo neexistuje.
     * @return  true/false
     */
    private boolean isUserExists() {
        String userAddress = userAddressET.getText().toString();

        if(databaseHelper.checkUser(userAddress)){
            String message = getString(R.string.user_already_exists);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    /**
     * Metoda, která zkontroluje zda jsou všechna pole registračního
     * formuláře korektně vyplněna.
     *
     * @return true/false
     */
    private boolean checkIfIsAllFilled() {

        String name = nameET.getText().toString();
        String userAdress = userAddressET.getText().toString();
        String password1 = password1ET.getText().toString();
        String password2 = password2ET.getText().toString();
        String message;

        if (name.isEmpty()){
            message = getString(R.string.empty_name);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            return false;
        }

        if (userAdress.isEmpty()){
            message = getString(R.string.empty_user_name);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(userAdress).matches()) {
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

        return true;

    }

}
