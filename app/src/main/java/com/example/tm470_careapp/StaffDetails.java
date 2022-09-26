package com.example.tm470_careapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputLayout;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class StaffDetails extends AppCompatActivity {

    // Layout Variables.
    private NavDrawerHelper navDrawerHelper;
    private AlertDialog updateDialog;
    private AutoCompleteTextView spinner_staffNames;
    private TextInputLayout til_knownAs;
    private TextInputLayout til_fullName;
    private TextInputLayout til_telephoneNumber;
    private TextInputLayout til_email;
    private TextInputLayout til_empPosition;
    private TextInputLayout til_hours;
    private TextInputLayout til_reportsTo;
    private TextInputLayout til_staffSearch;
    private EditText et_known_as;
    private EditText et_staffName;
    private EditText et_telephoneNumber;
    private EditText et_email;
    private EditText et_empPosition;
    private EditText et_contractedHours;
    private EditText et_reportsTo;
    private ImageView iv_staffImage;
    private View dialogView;
    private Button b_takeNewPhoto;
    private Button b_uploadPhoto;
    // Class wide Variables.
    private Database dbConnection;
    private Bitmap newUserImage;
    private Boolean SpinnerListOnDialog;
    private Boolean textOneRequired;
    private Boolean textTwoRequired;
    private Integer staffAccessLevel = 99;
    private Integer managerRequiredAccessLevel = 3;
    private Integer staffProfileRepresentsIDNumber;
    private Integer staffUserIDNumber;
    private Map<String, String> spinnerMap;
    private Map<String, String> spinnerStaffMembers;
    private String[] spinnerStrings;
    private String staffTelephoneId;
    private String columnToUpdate;
    private String updateValue;
    private String updateValueSpinner;
    private String updateQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get User Details.
        getCurrentUser();
        // Layout + Clickers.
        setContentView(R.layout.activity_staff_home);
        getSupportActionBar().setTitle("Staff Details");
        initNavigationDrawer();
        createLayoutItems();
        cameraButtonsFunctionality();
        getUsersResponsibleFor();
        // Populate Details.
        getStaffDetails();
    }

    // Get access level and ID of user logged in.
    private void getCurrentUser() {
        staffAccessLevel = Utilities.getUserAccessLevel();
        dbConnection = Database.getInstance();
        staffUserIDNumber = dbConnection.getStaffID();
        // Copy ID to variable used to determine who's profile this is.
        staffProfileRepresentsIDNumber = staffUserIDNumber;
    }

    private void createLayoutItems() {
        //Buttons.
        b_takeNewPhoto = findViewById(R.id.b_staffHomeNewPhoto);
        b_uploadPhoto = findViewById(R.id.b_staffHomeUploadPhoto);
        //ImageView
        iv_staffImage = findViewById(R.id.iv_staffHomeUserImage);
        //Textboxes + EditTexts.
        et_known_as = (EditText) findViewById(R.id.et_staffhomeKnownAs);
        et_staffName = (EditText) findViewById(R.id.et_staffhomeFullName);
        et_telephoneNumber = (EditText) findViewById(R.id.et_staffhomeTelNo);
        et_email = (EditText) findViewById(R.id.et_staffhomeEmail);
        et_empPosition = (EditText) findViewById(R.id.et_staffhomeEmpPos);
        et_contractedHours = (EditText) findViewById(R.id.et_staffhomeHours);
        et_reportsTo = (EditText) findViewById(R.id.et_staffhomeReportsTo);
        til_knownAs = findViewById(R.id.til_staffHomeKnownAs);
        til_fullName = findViewById(R.id.til_staffhomeFullName);
        til_telephoneNumber = findViewById(R.id.til_staffhomeTelNo);
        til_email = findViewById(R.id.til_staffhomeEmail);
        til_empPosition = findViewById(R.id.til_staffhomeEmpPos);
        til_hours = findViewById(R.id.til_staffhomeHours);
        til_reportsTo = findViewById(R.id.til_staffhomeReportsTo);
        til_staffSearch = findViewById(R.id.til_staffHomeSelectUser);
        spinner_staffNames = findViewById(R.id.actv_staffHomeSelectUser);

        // End Icon Clickers.
        UpdateOnClicks(til_knownAs);
        UpdateOnClicks(til_fullName);
        UpdateOnClicks(til_telephoneNumber);
        UpdateOnClicks(til_email);

        // Control access to some areas.
        if (staffAccessLevel > managerRequiredAccessLevel) {
            til_staffSearch.setVisibility(View.GONE);
            b_takeNewPhoto.setVisibility(View.GONE);
        }

        // If not their own profile, allow updating extra fields, else don't.
        if (staffUserIDNumber == staffProfileRepresentsIDNumber) {
            til_reportsTo.setEndIconMode(TextInputLayout.END_ICON_NONE);
            til_hours.setEndIconMode(TextInputLayout.END_ICON_NONE);
            til_empPosition.setEndIconMode(TextInputLayout.END_ICON_NONE);
        } else {
            if (staffAccessLevel > managerRequiredAccessLevel) {
                til_reportsTo.setEndIconMode(TextInputLayout.END_ICON_CUSTOM);
                til_reportsTo.setEndIconContentDescription(R.string.staff_update_reports_to);
                UpdateOnClicks(til_reportsTo);
                til_hours.setEndIconMode(TextInputLayout.END_ICON_CUSTOM);
                til_hours.setEndIconContentDescription(R.string.staff_update_contracted_hours);
                UpdateOnClicks(til_hours);
                til_empPosition.setEndIconMode(TextInputLayout.END_ICON_CUSTOM);
                til_empPosition.setEndIconContentDescription(R.string.staff_update_emp_position);
                UpdateOnClicks(til_empPosition);
            }
        }
    }

    private void getUsersResponsibleFor() {

        // Populate Spinner.
        spinnerStaffMembers = new HashMap<>();
        List<HashMap<String, Object>> staffMembersList;
        staffMembersList = dbConnection.getUsersResponsibleFor();
        String[] spinnerStringsStaff = new String[staffMembersList.size()];
        for (int i = 0; i < staffMembersList.size(); i++) {
            spinnerStaffMembers.put(staffMembersList.get(i).get("reportee").toString(), staffMembersList.get(i).get("staff_id").toString());
            spinnerStringsStaff[i] = staffMembersList.get(i).get("reportee").toString();
        }
        //Available Rooms Spinner
        ArrayAdapter<String> adapterStaffSpinner = new ArrayAdapter<String>(StaffDetails.this, R.layout.spinner_item, spinnerStringsStaff);
        spinner_staffNames.setAdapter(adapterStaffSpinner);
        spinner_staffNames.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                staffProfileRepresentsIDNumber = Integer.parseInt(Objects.requireNonNull(spinnerStaffMembers.get(spinner_staffNames.getText().toString())));
                getStaffDetails();
                createLayoutItems();
            }
        });
    }

    private void cameraButtonsFunctionality() {
        // Handle Taking new photo.
        b_takeNewPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Make sure phone has permissions to use camera.
                if (ContextCompat.checkSelfPermission(StaffDetails.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(StaffDetails.this, new String[]{
                            Manifest.permission.CAMERA
                    }, 100);
                }
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 100);
            }
        });

        // Handle Uploading and saving to user.
        b_uploadPhoto.setOnClickListener(new View.OnClickListener() {
            Long result;
            @Override
            public void onClick(View view) {
                if (newUserImage != null) {
                    dbConnection = Database.getInstance();
                    result = dbConnection.insertImage(newUserImage);
                    if (result != null) {
                        Integer imageId = result.intValue();
                        dbConnection.updateStaffPhoto(staffProfileRepresentsIDNumber, imageId);
                        b_uploadPhoto.setVisibility(View.GONE);
                        Toast.makeText(StaffDetails.this, "Picture Uploaded.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(StaffDetails.this, "Please take a picture before attempting to upload.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void UpdateOnClicks(@NonNull TextInputLayout til_button) {
        til_button.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dialog Variables Setup.
                AlertDialog.Builder editDialogBuilder = new AlertDialog.Builder(StaffDetails.this);
                dialogView = getLayoutInflater().inflate(R.layout.dialog_box_staff_update, null);
                TextInputLayout DialogTI1 = dialogView.findViewById(R.id.til_dialogUpdateSUReqsTextBox1);
                TextInputLayout DialogTI2 = dialogView.findViewById(R.id.til_updateFieldFreeText2);
                TextInputLayout SpinnerParent = dialogView.findViewById(R.id.til_updateFieldSpinner1);
                EditText DialogEditText1 = (EditText) dialogView.findViewById(R.id.et_dialogUpdateSUReqsTextBox1);
                EditText DialogEditText2 = (EditText) dialogView.findViewById(R.id.et_updateFieldTil2);
                Button dialogCancel = dialogView.findViewById(R.id.b_dialogUpdateSUReqsButtonCancel);
                Button dialogSubmit = dialogView.findViewById(R.id.b_dialogUpdateSUReqsButtonSave);
                RadioGroup dialogRadioGroup = dialogView.findViewById(R.id.rg_staffUpdateRadioGroup);
                dbConnection = Database.getInstance();

                // Variables to control.
                String fieldButtonClicked = (String) v.getContentDescription();
                String staffQueryReturnColumnName = null;
                String staffQueryReturnColumnNameTwo = null;
                String firstFieldToUpdate;
                String secondFieldToUpdate = null;
                SpinnerListOnDialog = false;
                textOneRequired = false;
                textTwoRequired = false;
                spinnerMap = new HashMap<>();

                /* Depending on which field was clicked, setup Dialog appearance and columns required.
                 ** staffQueryReturnColumnName - Primary field from query which represents this column.
                 ** staffQueryReturnColumnNameTwo - Secondary field from query which represents this column.
                 ** updateColumn - Field which will be used for the update statement.
                 */
                switch (fieldButtonClicked) {
                    case "known_as":
                        editDialogBuilder.setTitle("Known As");
                        DialogTI1.setHint("Known As");
                        staffQueryReturnColumnName = "known_as";
                        break;
                    case "full_name":
                        editDialogBuilder.setTitle("Full Name");
                        DialogTI1.setHint("First Name");
                        DialogTI2.setVisibility(View.VISIBLE);
                        staffQueryReturnColumnName = "first_name";
                        staffQueryReturnColumnNameTwo = "last_name";
                        break;
                    // More complex case, Needs the text box if new or edit, and a spinner if existing.
                    // Load both and hide whichever is not required and update okay button.
                    case "telephone_number":
                        editDialogBuilder.setTitle("Telephone Number");
                        DialogTI1.setHint("Telephone Number");
                        staffQueryReturnColumnName = "contact_tel_no";
                        DialogEditText1.setInputType(InputType.TYPE_CLASS_PHONE);
                        DialogEditText1.setKeyListener(DigitsKeyListener.getInstance("0123456789.+"));
                        dialogRadioGroup.setVisibility(View.VISIBLE);
                        //Set Spinner to required, and get it's values.
                        SpinnerListOnDialog = true;
                        List<HashMap<String, Object>> telephoneNumberList;
                        telephoneNumberList = dbConnection.getTelephoneNumbers();
                        spinnerStrings = new String[telephoneNumberList.size()];
                        for (int i = 0; i < telephoneNumberList.size(); i++) {
                            spinnerMap.put(telephoneNumberList.get(i).get("contact_tel_no").toString(), telephoneNumberList.get(i).get("contact_tel_id").toString());
                            spinnerStrings[i] = telephoneNumberList.get(i).get("contact_tel_no").toString();
                        }
                        break;
                    case "email_address":
                        editDialogBuilder.setTitle("Email Address");
                        DialogTI1.setHint("Email Address");
                        staffQueryReturnColumnName = "contact_email";
                        columnToUpdate = "contact_email";
                        break;
                    case "emp_position":
                        editDialogBuilder.setTitle("Employment Position");
                        DialogTI1.setHint("Employment Position");
                        staffQueryReturnColumnName = "role_title";
                        columnToUpdate = "job_role_ri";
                        //Set Spinner to required, and get it's values.
                        SpinnerListOnDialog = true;
                        List<HashMap<String, Object>> positionList;
                        positionList = dbConnection.getPositions();
                        spinnerStrings = new String[positionList.size()];
                        for (int i = 0; i < positionList.size(); i++) {
                            spinnerMap.put(positionList.get(i).get("role_title").toString(), positionList.get(i).get("job_role_id").toString());
                            spinnerStrings[i] = positionList.get(i).get("role_title").toString();
                        }
                        break;
                    case "contracted_hours":
                        editDialogBuilder.setTitle("Contracted Hours");
                        DialogTI1.setHint("Contracted Hours");
                        staffQueryReturnColumnName = "contract_type_desc";
                        columnToUpdate = "contract_type_ri";
                        //Set Spinner to required, and get it's values.
                        SpinnerListOnDialog = true;
                        List<HashMap<String, Object>> contractList;
                        contractList = dbConnection.getHours();
                        spinnerStrings = new String[contractList.size()];
                        for (int i = 0; i < contractList.size(); i++) {
                            spinnerMap.put(contractList.get(i).get("contract_type_desc").toString(), contractList.get(i).get("contract_type_id").toString());
                            spinnerStrings[i] = contractList.get(i).get("contract_type_desc").toString();
                        }
                        break;
                    case "reports_to":
                        editDialogBuilder.setTitle("Reports To");
                        DialogTI1.setHint("Reports To");
                        staffQueryReturnColumnName = "reports_to";
                        columnToUpdate = "reports_to";
                        //Set Spinner to required, and get it's values.
                        SpinnerListOnDialog = true;
                        List<HashMap<String, Object>> reportsToList;
                        reportsToList = dbConnection.getReportsTo();
                        spinnerStrings = new String[reportsToList.size()];
                        for (int i = 0; i < reportsToList.size(); i++) {
                            spinnerMap.put(reportsToList.get(i).get("reports_to").toString(), reportsToList.get(i).get("staff_id").toString());
                            spinnerStrings[i] = reportsToList.get(i).get("reports_to").toString();
                        }
                        break;
                }

                // ReQuery the data to make sure we're using the latest version as the "Current Value";
                List<HashMap<String, Object>> getCurrentValues;
                getCurrentValues = dbConnection.getUserStaffDetails(staffProfileRepresentsIDNumber);
                firstFieldToUpdate = getCurrentValues.get(0).get(staffQueryReturnColumnName).toString();

                // Pass in required fields text, and make sure visibility is accurate.
                if (staffQueryReturnColumnName != null) {
                    DialogEditText1.setText(firstFieldToUpdate);
                }
                if (staffQueryReturnColumnNameTwo != null) {
                    secondFieldToUpdate = getCurrentValues.get(0).get(staffQueryReturnColumnNameTwo).toString();
                    DialogEditText2.setText(secondFieldToUpdate);
                }
                if (SpinnerListOnDialog) {
                    //Set visible if not Contact Number.
                    AutoCompleteTextView spinnerChild = dialogView.findViewById(R.id.updateFieldSpinner1ACT);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(StaffDetails.this, R.layout.spinner_item, spinnerStrings);
                    spinnerChild.setAdapter(adapter);
                    spinnerChild.setThreshold(1);
                    spinnerChild.setText(firstFieldToUpdate, false);
                    if (!fieldButtonClicked.equals("telephone_number")) {
                        DialogTI1.setVisibility(View.GONE);
                        SpinnerParent.setVisibility(View.VISIBLE);
                        spinnerChild.setFocusable(false);
                    }
                    spinnerChild.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            // Get value for the update statement.
                            updateValue = spinnerMap.get(spinnerChild.getText().toString());
                            updateValueSpinner = spinnerChild.getText().toString();
                        }
                    });
                };
                // Radio Button Listener for Telephone Updates.
                dialogRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup radioGroup, int checkedID) {
                        switch (checkedID) {
                            case R.id.rb_staffUpdateRadioUpdate:
                                if (DialogTI1.getVisibility() != View.VISIBLE) {
                                    DialogTI1.setVisibility(View.VISIBLE);
                                }
                                if (SpinnerParent.getVisibility() != View.GONE) {
                                    SpinnerParent.setVisibility(View.GONE);
                                }
                                dialogSubmit.setText("Update");
                                DialogEditText1.setText(firstFieldToUpdate);
                                break;
                            case R.id.rb_staffUpdateRadioNew:
                                if (DialogTI1.getVisibility() != View.VISIBLE) {
                                    DialogTI1.setVisibility(View.VISIBLE);
                                }
                                if (SpinnerParent.getVisibility() != View.GONE) {
                                    SpinnerParent.setVisibility(View.GONE);
                                }
                                dialogSubmit.setText("Create New");
                                DialogEditText1.getText().clear();
                                break;
                            case R.id.rb_staffUpdateRadioExisting:
                                if (DialogTI1.getVisibility() != View.GONE) {
                                    DialogTI1.setVisibility(View.GONE);
                                }
                                if (SpinnerParent.getVisibility() != View.VISIBLE) {
                                    SpinnerParent.setVisibility(View.VISIBLE);
                                }
                                dialogSubmit.setText("Link Existing");
                                break;
                            default:
                                break;
                        }
                    }
                });

                // Cancel Button.
                dialogCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        updateDialog.dismiss();
                    }
                });
                String finalSecondFieldToUpdate = secondFieldToUpdate;
                //Submit Button.
                dialogSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Validate and save changes to the update.
                        if ((fieldButtonClicked.equals("emp_position")) || (fieldButtonClicked.equals("contracted_hours")) || (fieldButtonClicked.equals("reports_to"))) {
                            if ((updateValueSpinner != null) || (!updateValueSpinner.equals(firstFieldToUpdate))) {
                                dbConnection.updateSingleStaffDetails(fieldButtonClicked, updateValue, staffProfileRepresentsIDNumber);
                            }
                        }
                        if ((fieldButtonClicked.equals("known_as")) || (fieldButtonClicked.equals("email_address"))) {
                            if ((DialogEditText1.getText() != null) && (!DialogEditText1.getText().toString().equals(firstFieldToUpdate))) {
                                dbConnection.updateSingleStaffDetails(fieldButtonClicked, DialogEditText1.getText().toString(), staffProfileRepresentsIDNumber);
                            }
                        }
                        if (fieldButtonClicked.equals("full_name")) {
                            // Both need to be entered, but only one needs to be different.
                            if (((DialogEditText1.getText().toString().length() > 0) && (DialogEditText2.getText().toString().length() > 0)) &&
                                    (!DialogEditText1.getText().toString().equals(firstFieldToUpdate)) || (!DialogEditText2.getText().toString().equals(finalSecondFieldToUpdate))) {

                                dbConnection.updateStaffName(DialogEditText1.getText().toString(), DialogEditText2.getText().toString(), staffProfileRepresentsIDNumber);
                            }
                        }
                        if (fieldButtonClicked.equals("telephone_number")) {
                            Integer radioId = dialogRadioGroup.getCheckedRadioButtonId();
                            RadioButton dialogRadioButton = dialogView.findViewById(radioId);
                            switch (dialogRadioButton.getText().toString()) {
                                case "Update Existing":
                                    if ((DialogEditText1.getText() != null) && (!DialogEditText1.getText().toString().equals(firstFieldToUpdate))) {
                                        dbConnection.updateTelephoneNUmber(Integer.parseInt(staffTelephoneId), DialogEditText1.getText().toString());
                                    }
                                    break;
                                case "Create New":
                                    if ((DialogEditText1.getText() != null) && (!DialogEditText1.getText().toString().equals(firstFieldToUpdate))) {
                                        Long newTelephoneId = dbConnection.insertTelephone(DialogEditText1.getText().toString());
                                        String newTelephoneIdString = String.valueOf(newTelephoneId);
                                        dbConnection.updateSingleStaffDetails(fieldButtonClicked, newTelephoneIdString, staffProfileRepresentsIDNumber);
                                    }
                                    break;
                                case "From List":
                                    if ((updateValueSpinner != null) && (!updateValueSpinner.equals(firstFieldToUpdate))) {
                                        dbConnection.updateSingleStaffDetails(fieldButtonClicked, updateValue, staffProfileRepresentsIDNumber);
                                    }
                                    break;
                                default:
                                    Log.d("ErrorUpdatingStaff", "onClick: Unknonw Update Telephone Operation.");
                            }
                        }
                        updateDialog.dismiss();
                        getStaffDetails();
                    }
                });
                // Raise Dialog.
                editDialogBuilder.setView(dialogView);
                updateDialog = editDialogBuilder.create();
                updateDialog.show();
            }
        });
    }

    // Handle getting the image from the camera.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                newUserImage = (Bitmap) data.getExtras().get("data");
                iv_staffImage.setImageBitmap(newUserImage);
                // If updating user, present upload photo option.
                b_uploadPhoto.setVisibility(View.VISIBLE);
                //tv_uploadPhotoWarning.setVisibility(View.VISIBLE);
            }
        }
    }

    private void getStaffDetails() {
        // Get DB Instance, or open one if possible.
        dbConnection = Database.getInstance();
        if (dbConnection != null) {
            try {
                // Initialise list of maps with database query.
                List<HashMap<String, Object>> staffList;
                staffList = dbConnection.getUserStaffDetails(staffProfileRepresentsIDNumber);
                // Populate Page, and keep a record in the map to compare update values to. .
                if (staffList.get(0).get("known_as") != null) {
                    et_known_as.setText(staffList.get(0).get("known_as").toString());
                }
                if ((staffList.get(0).get("first_name") != null) || (staffList.get(0).get("last_name") != null)) {
                    String fullName = (staffList.get(0).get("first_name").toString()) + " " + (staffList.get(0).get("last_name").toString());
                    fullName = fullName.trim();
                    et_staffName.setText(fullName);
                }
                if (staffList.get(0).get("telephone_no_id") != null) {
                    staffTelephoneId = (staffList.get(0).get("telephone_no_id").toString());
                }
                if (staffList.get(0).get("contact_tel_no") != null) {
                    et_telephoneNumber.setText(staffList.get(0).get("contact_tel_no").toString());
                }
                if (staffList.get(0).get("contact_email") != null) {
                    et_email.setText(staffList.get(0).get("contact_email").toString());
                }
                if (staffList.get(0).get("role_title") != null) {
                    et_empPosition.setText(staffList.get(0).get("role_title").toString());
                }
                if (staffList.get(0).get("contract_type_desc") != null) {
                    et_contractedHours.setText(staffList.get(0).get("contract_type_desc").toString());
                }
                if (staffList.get(0).get("reports_to") != null) {
                    et_reportsTo.setText(staffList.get(0).get("reports_to").toString());
                }
                if (staffList.get(0).get("image_blob") != null) {
                    byte[] fileBytes = (byte[]) staffList.get(0).get("image_blob");
                    Utilities.setImageViewWithByteArray(findViewById(R.id.iv_staffHomeUserImage), (fileBytes));
                }
            } catch (Exception e) {
                Log.e("Get Staff Details Error", "exception", e);
            }
        }
    }

    public void initNavigationDrawer() {
        NavigationView navigationView = findViewById(R.id.design_navigation_view);
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout_staffhome);
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