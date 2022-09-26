package com.example.tm470_careapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import java.util.Calendar;

public class UserMaintenance extends AppCompatActivity {


    private NavDrawerHelper navDrawerHelper;
    private EditText et_newStaffMemberFirstName;
    private EditText et_newStaffMemberLastName;
    private EditText et_newStaffMemberUserName;
    private EditText et_newStaffMemberDigitalSignature;
    private EditText et_newStaffMemberDateOfBirth;
    private EditText et_newStaffMemberStartDate;
    private EditText et_newStaffMemberPassword;
    private Button b_newStaffMemberAdd;
    private Integer staffAccessLevel;
    private Integer adminAccessLevel = 1;
    private Database dbConnection;
    private DatePickerDialog datePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get User Details.
        getCurrentUser();
        setContentView(R.layout.activity_user_maintenance);
        getSupportActionBar().setTitle("Create New User");
        initNavigationDrawer();
        createLayoutItems();
        addNewUser();
        handleCalendars();
    }

    private void getCurrentUser() {
        staffAccessLevel = Utilities.getUserAccessLevel();
        dbConnection = Database.getInstance();
    }

    private void createLayoutItems() {
        //Buttons.
        b_newStaffMemberAdd = findViewById(R.id.b_newStaffMemberAdd);
        //Textboxes + EditTexts.
        et_newStaffMemberFirstName = (EditText) findViewById(R.id.et_newStaffMemberFirstName);
        et_newStaffMemberLastName = (EditText) findViewById(R.id.et_newStaffMemberLastName);
        et_newStaffMemberUserName  = (EditText) findViewById(R.id.et_newStaffMemberUserName);
        et_newStaffMemberDigitalSignature  = (EditText) findViewById(R.id.et_newStaffMemberDigitalSignature);
        et_newStaffMemberDateOfBirth  = (EditText) findViewById(R.id.et_newStaffMemberDateOfBirth);
        et_newStaffMemberStartDate  = (EditText) findViewById(R.id.et_newStaffMemberStartDate);
        et_newStaffMemberPassword  = (EditText) findViewById(R.id.et_newStaffMemberPassword);
        // Control access to some areas.
        if (staffAccessLevel == adminAccessLevel) {
            b_newStaffMemberAdd.setEnabled(false);
        }
    }

    private void handleCalendars() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        et_newStaffMemberDateOfBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog = new DatePickerDialog(UserMaintenance.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        et_newStaffMemberDateOfBirth.setText(String.format("%02d/%02d/%04d", day, month, year));
                    }
                }, year, month, dayOfMonth);
                datePickerDialog.show();
            }
        });

        et_newStaffMemberStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog = new DatePickerDialog(UserMaintenance.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        et_newStaffMemberStartDate.setText(String.format("%02d/%02d/%04d", day, month, year));
                    }
                }, year, month, dayOfMonth);
                datePickerDialog.show();
            }
        });
    }

    private void addNewUser() {
        b_newStaffMemberAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if ((et_newStaffMemberFirstName.getText().toString().length() > 1 ) &&
                   (et_newStaffMemberLastName.getText().toString().length() > 1 ) &&
                   (et_newStaffMemberUserName.getText().toString().length() > 1 ) &&
                   (et_newStaffMemberDigitalSignature.getText().toString().length() > 1 ) &&
                   (et_newStaffMemberDateOfBirth.getText().toString().length() > 1 ) &&
                   (et_newStaffMemberStartDate.getText().toString().length() > 1) &&
                   (et_newStaffMemberPassword.getText().toString().length() > 1)) {

                   dbConnection = Database.getInstance();
                   Long newUserID = dbConnection.insertNewStaffMember(et_newStaffMemberFirstName.getText().toString(), et_newStaffMemberLastName.getText().toString(), et_newStaffMemberUserName.getText().toString(),
                           et_newStaffMemberDigitalSignature.getText().toString(), et_newStaffMemberDateOfBirth.getText().toString(), et_newStaffMemberStartDate.getText().toString(), et_newStaffMemberPassword.getText().toString());
               } else {
                   Toast.makeText(getApplicationContext(), "Please enter all details!", Toast.LENGTH_SHORT).show();
               }
            }
        });
    }

    public void initNavigationDrawer() {
        NavigationView navigationView = findViewById(R.id.nvNewStaffUserNavView);
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout_UserMaintenance);
        navDrawerHelper = new NavDrawerHelper(this);
        navDrawerHelper.initNav(drawerLayout, navigationView, this, false);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        navDrawerHelper.handleOnItemSelected(item);
        return super.onOptionsItemSelected(item);
    }
};

