package com.example.tm470_careapp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Button b_loginButton;
    private EditText et_usernameField;
    private EditText et_passwordField;
    private String usernameText;
    private String passwordText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        startService(new Intent(getBaseContext(), AppClosed.class));
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createLayoutItems();
        onClickers();
    }

    // Create layout items.
    private void createLayoutItems() {
        b_loginButton = (Button) findViewById(R.id.vb_login);
        et_usernameField = findViewById(R.id.et_username);
        et_passwordField = findViewById(R.id.et_password);
    }

    // Handle interaction with the button.
    private void onClickers() {
        b_loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((et_usernameField.getText().toString().length() > 1) && (et_passwordField.getText().toString().length() > 1)) {
                    usernameText = et_usernameField.getText().toString();
                    passwordText = et_passwordField.getText().toString();
                } else {
                    Toast.makeText(getApplicationContext(), "Please enter a username and password!", Toast.LENGTH_SHORT).show();
                }
                Database dbConnection = null;
                dbConnection = Database.getInstance(usernameText, passwordText);
                // Connect to DB and login in possible, or give user an error if not.
                if (dbConnection != null) {
                    if (dbConnection.getStatus()) {
                        System.out.println("Logged In!");
                        openStaffDetails();
                    } else {
                        Toast.makeText(getApplicationContext(), "Login Failed", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Error reaching the database, please try again", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Handle transition to staff details page.
    public void openStaffDetails() {
        Intent intent = new Intent(this, StaffDetails.class);
        startActivity(intent);
    }
}