package com.example.tm470_careapp;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AddUpdateServiceUser extends AppCompatActivity {

    private NavDrawerHelper navDrawerHelper;
    private AutoCompleteTextView spinner_roomNumber;
    private AutoCompleteTextView spinner_sex;
    private AutoCompleteTextView spinner_dnacpr;
    private AutoCompleteTextView spinner_ethnicity;
    private Button b_takeNewPhoto;
    private Button b_uploadPicture;
    private Button b_saveRecord;
    private Button b_updateRoom;
    private CheckBox cb_addToCompanyGPs;
    private CheckBox cb_showOccupiedRooms;
    private EditText et_firstName;
    private EditText et_lastName;
    private EditText et_knownAs;
    private EditText et_dateAdmitted;
    private EditText et_dateOfBirth;
    private ImageView iv_serviceUserPhoto;
    private TextView tv_uploadPhotoWarning;
    private Bitmap newUserImage;
    private Boolean showOccupiedRooms = false;
    private Database dbConnection;
    private Integer newUserRoomNumber;
    private Integer newUserEthnicityID;
    private Integer newUserDNACPR;
    private String newUserSex;
    private String activityMode = "newUser";
    private Integer currentServiceUserID;
    private Integer currentRoomNumber;
    private Map<Integer, String> spinnerMapRoomNumbers;
    private Map<Integer, String> spinnerMapEthnicities;
    private DatePickerDialog datePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_service_user);
        initNavigationDrawer();
        Objects.requireNonNull(getSupportActionBar()).setTitle("Add New Service User");
        checkFormMode();
        createLayoutItems();
        setupButtons();
        populateSpinners();

        if (activityMode.equals("updateUser")) {
            getCurrentUserDetails();
            Objects.requireNonNull(getSupportActionBar()).setTitle("Edit Service User Details");
        }
    }


    private void createLayoutItems() {
        b_takeNewPhoto = findViewById(R.id.b_AddNewServiceUserTakePhoto);
        b_uploadPicture = findViewById(R.id.b_AddNewServiceUserSavePhoto);
        b_saveRecord = findViewById(R.id.b_AddNewServiceUserSaveUser);
        b_updateRoom = findViewById(R.id.b_AddNewServiceUserUpdateRoom);
        et_firstName = findViewById(R.id.et_addNewServiceUserFirstName);
        et_lastName = findViewById(R.id.et_addNewServiceUserLastName);
        et_knownAs = findViewById(R.id.et_addNewServiceUserKnownAs);
        et_dateAdmitted = findViewById(R.id.et_addNewServiceUserAdmittedDate);
        et_dateOfBirth = findViewById(R.id.et_addNewServiceUserDOB);
        iv_serviceUserPhoto = findViewById(R.id.addNewServiceUserImage);
        spinner_roomNumber = findViewById(R.id.actv_staffHomeSelectUser);
        spinner_sex = findViewById(R.id.act_addNewServiceUserSex);
        spinner_dnacpr = findViewById(R.id.act_addNewServiceUserDNO);
        spinner_ethnicity = findViewById(R.id.act_addNewServiceUserEthnicity);
        cb_addToCompanyGPs = findViewById(R.id.cb_addToHomesDefaultGP);
        cb_showOccupiedRooms = findViewById(R.id.cb_addNewServicUserOccupiedRooms);
        tv_uploadPhotoWarning = findViewById(R.id.tv_addNewServicUserUploadPhotoWarning);
        if (activityMode.equals("updateUser")) {
            b_saveRecord.setText(R.string.UpdateUser);
            b_updateRoom.setVisibility(View.VISIBLE);
        }
    }

    private void checkFormMode() {
        Bundle intentDetails = getIntent().getExtras();
        if (intentDetails != null) {
            currentServiceUserID = intentDetails.getInt("SERVICE_USER_ID");
            currentRoomNumber = intentDetails.getInt("SERVICE_USER_ROOM_ID");
        }
        if (currentServiceUserID != null) {
            activityMode = "updateUser";
        } else {
            activityMode = "newUser";
        }
    }

    private void populateSpinners() {
        // Get DB Instance, or open one if possible.
        dbConnection = Database.getInstance();
        //Local Vars.
        String[] spinnerStringsSex = getResources().getStringArray(R.array.SexChoices);
        String[] spinnerStringsDNACPR = getResources().getStringArray(R.array.DNACPRChoices);

        // Rooms Spinner.
        spinnerMapRoomNumbers = new HashMap<>();
        List<HashMap<String, Object>> roomNumbersList;
        roomNumbersList = dbConnection.getAvailableRooms(showOccupiedRooms);
        String[] spinnerStringsRooms = new String[roomNumbersList.size()];
        for (int i = 0; i < roomNumbersList.size(); i++) {
            spinnerMapRoomNumbers.put(i, roomNumbersList.get(i).get("room_number").toString());
            spinnerStringsRooms[i] = "Room: " + roomNumbersList.get(i).get("room_number").toString() + " (" + roomNumbersList.get(i).get("room_type_description").toString() +")" ;
        }
        //Available Rooms Spinner
        ArrayAdapter<String> adapterRoomsSpinner = new ArrayAdapter<String>(AddUpdateServiceUser.this, R.layout.spinner_item, spinnerStringsRooms);

        spinner_roomNumber.setAdapter(adapterRoomsSpinner);
        spinner_roomNumber.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                newUserRoomNumber = Integer.parseInt(Objects.requireNonNull(spinnerMapRoomNumbers.get(i)));
                spinner_roomNumber.setText("Room: " + newUserRoomNumber, false);

            }
        });

        cb_showOccupiedRooms.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if ( isChecked )
                {
                    showOccupiedRooms = true;
                } else {
                    showOccupiedRooms = false;
                }
                // Rooms Spinner.
                spinnerMapRoomNumbers = new HashMap<>();
                List<HashMap<String, Object>> roomNumbersList;
                roomNumbersList = dbConnection.getAvailableRooms(showOccupiedRooms);
                String[] spinnerStringsRooms = new String[roomNumbersList.size()];
                for (int i = 0; i < roomNumbersList.size(); i++) {
                    spinnerMapRoomNumbers.put(i, roomNumbersList.get(i).get("room_number").toString());
                    //spinnerStringsRooms[i] = "Room: " + roomNumbersList.get(i).get("room_number").toString() + ". Type: " + roomNumbersList.get(i).get("room_type_description").toString();
                    spinnerStringsRooms[i] = "Room: " + roomNumbersList.get(i).get("room_number").toString() + " (" + roomNumbersList.get(i).get("room_type_description").toString() + ")";
                    spinner_roomNumber.setText("Room: " + newUserRoomNumber, false);
                }
                ArrayAdapter<String> adapterRoomsSpinner = new ArrayAdapter<String>(AddUpdateServiceUser.this, R.layout.spinner_item, spinnerStringsRooms);
                spinner_roomNumber.setAdapter(adapterRoomsSpinner);
            }
        });

        // Sex Choice Spinner.
        ArrayAdapter<String> adapterSexSpinner = new ArrayAdapter<String>(AddUpdateServiceUser.this, R.layout.spinner_item, spinnerStringsSex);
        spinner_sex.setAdapter(adapterSexSpinner);
        spinner_sex.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (spinner_sex.getText().toString()) {
                    case "Male":
                        newUserSex = String.valueOf('M');
                        break;
                    case "Female":
                        newUserSex = String.valueOf('F');
                        break;
                }
            }
        });

        // DNACPR Choice Spinner.
        ArrayAdapter<String> adapterDNACPRSpinner = new ArrayAdapter<String>(AddUpdateServiceUser.this, R.layout.spinner_item, spinnerStringsDNACPR);
        spinner_dnacpr.setAdapter(adapterDNACPRSpinner);
        spinner_dnacpr.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                newUserDNACPR = i;

            }
        });

        // Ethnicities Spinner.
        spinnerMapEthnicities = new HashMap<>();
        List<HashMap<String, Object>> ethnicitiesList;
        ethnicitiesList = dbConnection.getEthnicities();
        String[] spinnerStringsEthnicities = new String[ethnicitiesList.size()];
        for (int i = 0; i < ethnicitiesList.size(); i++) {
            spinnerMapEthnicities.put(i, ethnicitiesList.get(i).get("ethnicity_id").toString());
            spinnerStringsEthnicities[i] = ethnicitiesList.get(i).get("ethnicity_description").toString();
        }
        ArrayAdapter<String> adapterRoomsEthnicities = new ArrayAdapter<String>(AddUpdateServiceUser.this, R.layout.spinner_item, spinnerStringsEthnicities);
        spinner_ethnicity.setAdapter(adapterRoomsEthnicities);
        spinner_ethnicity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                newUserEthnicityID = Integer.parseInt(Objects.requireNonNull(spinnerMapEthnicities.get(i)));
            }
        });
    }

    private void setupButtons() {
        // Handle Calendars for date fields.
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        et_dateAdmitted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog = new DatePickerDialog(AddUpdateServiceUser.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        et_dateAdmitted.setText(String.format("%02d/%02d/%04d", day, month, year));
                    }
                }, year, month, dayOfMonth);
                datePickerDialog.show();
            }
        });
        et_dateOfBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog = new DatePickerDialog(AddUpdateServiceUser.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        et_dateOfBirth.setText(String.format("%02d/%02d/%04d", day, month, year));
                    }
                }, year, month, dayOfMonth);
                datePickerDialog.show();
            }
        });

        b_saveRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Validate all basic fields.
                if ((spinner_sex.getText().toString().length() > 0) &&
                        (spinner_dnacpr.getText().toString().length() > 0) &&
                        (spinner_ethnicity.getText().toString().length() > 0) &&
                        (et_firstName.getText().toString().length() > 0) &&
                        (et_lastName.getText().toString().length() > 0) &&
                        (et_knownAs.getText().toString().length() > 0) &&
                        (et_dateAdmitted.getText().toString().length() > 0) &&
                        (et_dateOfBirth.getText().toString().length() > 0)) {

                    // If this is updating a user. Call update job.
                    if (activityMode.equals("updateUser")) {
                        dbConnection.updateServiceUser(et_firstName.getText().toString(),
                                et_lastName.getText().toString(),
                                et_knownAs.getText().toString(),
                                newUserSex,
                                et_dateOfBirth.getText().toString(),
                                et_dateAdmitted.getText().toString(),
                                newUserDNACPR,
                                newUserEthnicityID,
                                currentServiceUserID);

                        // If Checkbox value has changed. Insert or delete default GP.
                        if (cb_addToCompanyGPs.isChecked()) {
                            dbConnection.insertDefaultServiceToUser(Long.valueOf(currentServiceUserID), 5);
                        } else {
                            dbConnection.UpdateEndDateForServiceUserDefaultContact(currentServiceUserID, 5);
                        }
                        Toast.makeText(AddUpdateServiceUser.this, "User updated.", Toast.LENGTH_SHORT).show();
                    } else {

                        // Further validation required, make sure room number is populated.
                        if (spinner_roomNumber.getText().toString().length() > 0){

                            //Insert to Database.
                            dbConnection = Database.getInstance();

                            // First Insert New Image. (Needed for FK)
                            Long newImageId = dbConnection.insertImage(newUserImage);
                            Integer newImageIdInt = newImageId.intValue();

                            // If successful insert main user.
                            if (newImageId != null) {
                                Long userKey = dbConnection.insertServiceUser(  et_firstName.getText().toString(),
                                        et_lastName.getText().toString(),
                                        et_knownAs.getText().toString(),
                                        newUserSex,
                                        newImageIdInt,
                                        et_dateOfBirth.getText().toString(),
                                        et_dateAdmitted.getText().toString(),
                                        newUserDNACPR,
                                        newUserEthnicityID);

                                // If successful and key is returned, add to room and GP if selected.
                                if (userKey != null) {
                                    dbConnection.insertRoomOccupancy(newUserRoomNumber, userKey, et_dateAdmitted.getText().toString());
                                    if (cb_addToCompanyGPs.isChecked()) {
                                        dbConnection.insertDefaultServiceToUser(userKey, 5);
                                    }
                                    Toast.makeText(AddUpdateServiceUser.this, "User created.", Toast.LENGTH_SHORT).show();
                                } else {
                                    Log.e("Error Creating User", "Error Inserting new service user");
                                    Toast.makeText(AddUpdateServiceUser.this, "Error Creating user.", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Log.e("Error Creating User", "Error Inserting new image.");
                                Toast.makeText(AddUpdateServiceUser.this, "Error uploading photo.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                } else {
                    Toast.makeText(AddUpdateServiceUser.this, "Please complete all details required.", Toast.LENGTH_SHORT).show();
                }
            }
        });


        b_updateRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbConnection = Database.getInstance();
                // Update Old Room with Date to of today.
                dbConnection.updateOccupiedRoomEndDate(currentRoomNumber, currentServiceUserID);
                // Insert New record for new room with date from of today.
                String currentDate = String.valueOf(LocalDate.now());
                String formattedCurrentDate = Utilities.formatDateStringToString(currentDate);
                dbConnection.insertRoomOccupancy(newUserRoomNumber, Long.valueOf(currentServiceUserID), formattedCurrentDate);
            }
        });

        // Handle Taking new photo.
        b_takeNewPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Make sure phone has permissions to use camera.
                if (ContextCompat.checkSelfPermission(AddUpdateServiceUser.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(AddUpdateServiceUser.this, new String[]{
                            Manifest.permission.CAMERA
                    }, 100);
                }

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 100);
            }
        });

        // Button to upload photo.
        b_uploadPicture.setOnClickListener(new View.OnClickListener() {
            Long result;
            @Override
            public void onClick(View view) {
                if (newUserImage != null) {
                    dbConnection = Database.getInstance();
                    result = dbConnection.insertImage(newUserImage);
                    if (result != null) {
                        Integer imageId = result.intValue();
                        dbConnection.updateUserPhoto(currentServiceUserID, imageId);
                        tv_uploadPhotoWarning.setText("Photo Uploaded");
                        b_uploadPicture.setVisibility(View.GONE);
                    }
                } else {
                    Toast.makeText(AddUpdateServiceUser.this, "Please take a picture before attempting to upload.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void getCurrentUserDetails() {
        Database dbConnection;
        dbConnection = Database.getInstance();

        try {
            List<HashMap<String, Object>> serviceUserDetailsList;
            serviceUserDetailsList = dbConnection.getServiceUserDetails(currentServiceUserID);
            if (serviceUserDetailsList.size() == 0) {
                Toast.makeText(this, "Error getting service user details", Toast.LENGTH_LONG).show();
            } else {
                if (serviceUserDetailsList.get(0).get("known_as") != null) {
                    et_knownAs.setText(serviceUserDetailsList.get(0).get("known_as").toString());
                }
                if (serviceUserDetailsList.get(0).get("sex") != null) {
                    newUserSex = serviceUserDetailsList.get(0).get("sex").toString();
                    switch (serviceUserDetailsList.get(0).get("sex").toString()) {
                        case "F":
                            spinner_sex.setText("Female", false);
                            break;
                        case "M":
                            spinner_sex.setText("Male", false);
                            break;
                    }
                }
                if (serviceUserDetailsList.get(0).get("dnacpr_status") != null) {
                    newUserDNACPR = Integer.parseInt(serviceUserDetailsList.get(0).get("dnacpr_status").toString());
                    String[] dnacprValue = getResources().getStringArray(R.array.DNACPRChoices);
                    switch (serviceUserDetailsList.get(0).get("dnacpr_status").toString()) {
                        case "0":
                            spinner_dnacpr.setText(dnacprValue[0], false);
                            break;
                        case "1":
                            spinner_dnacpr.setText(dnacprValue[1], false);
                            break;
                    }
                }
                if (serviceUserDetailsList.get(0).get("first_name") != null) {
                    et_firstName.setText(serviceUserDetailsList.get(0).get("first_name").toString());
                }
                if (serviceUserDetailsList.get(0).get("last_name") != null) {
                    et_lastName.setText(serviceUserDetailsList.get(0).get("last_name").toString());
                }
                if (serviceUserDetailsList.get(0).get("date_of_birth") != null) {
                    //et_dateOfBirth.setText(serviceUserDetailsList.get(0).get("date_of_birth").toString());
                    String formattedDOBDate = Utilities.formatDateStringToString(serviceUserDetailsList.get(0).get("date_of_birth").toString());
                    et_dateOfBirth.setText(formattedDOBDate);
                }
                if (serviceUserDetailsList.get(0).get("date_of_admittance") != null) {
                    String formattedAdmittanceDate = Utilities.formatDateStringToString(serviceUserDetailsList.get(0).get("date_of_admittance").toString());
                    et_dateAdmitted.setText(formattedAdmittanceDate);
                }
                if (serviceUserDetailsList.get(0).get("ethnicity_description") != null) {
                    newUserEthnicityID = Integer.parseInt(serviceUserDetailsList.get(0).get("ethnicity").toString());
                    spinner_ethnicity.setText(serviceUserDetailsList.get(0).get("ethnicity_description").toString(), false);
                }
                if (serviceUserDetailsList.get(0).get("image_blob") != null) {
                    byte[] fileBytes = (byte[]) serviceUserDetailsList.get(0).get("image_blob");
                    Utilities.setImageViewWithByteArray(findViewById(R.id.addNewServiceUserImage), (fileBytes));
                }

                // Set DefaultGP Checkbox value.
                Integer defaultGP = dbConnection.getCurrentlyUsingDefaultService(currentServiceUserID, 5);
                if ((defaultGP != null) && (defaultGP == 1)) {
                    cb_addToCompanyGPs.setChecked(true);
                } else {
                    cb_addToCompanyGPs.setChecked(false);
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    // Handle getting the image from the camera.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                newUserImage = (Bitmap) data.getExtras().get("data");
                iv_serviceUserPhoto.setImageBitmap(newUserImage);

                // If updating user, present upload photo option.
                if (activityMode.equals("updateUser")) {
                    b_uploadPicture.setVisibility(View.VISIBLE);
                    tv_uploadPhotoWarning.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    public void initNavigationDrawer() {
        NavigationView navigationView = findViewById(R.id.addNewServiceUserNavView);
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout_new_service_user);
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
}