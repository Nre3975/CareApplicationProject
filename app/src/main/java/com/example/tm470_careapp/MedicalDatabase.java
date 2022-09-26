package com.example.tm470_careapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.drawerlayout.widget.DrawerLayout;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputLayout;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MedicalDatabase extends AppCompatActivity {

    private NavDrawerHelper navDrawerHelper;
    private AlertDialog dAttachToUser;
    private AutoCompleteTextView SpinnerChildCategory;
    private AutoCompleteTextView SpinnerChildCondition;
    private Button bAddToUser;
    private Button bAddToDatabase;
    private Database dbConnection;
    private DatePickerDialog datePickerDialog;
    private EditText newValueEditText;
    private RadioGroup rgChooseCategoryType;
    private SwitchMaterial showNewRecordSwitch;
    private SwitchMaterial showAddToUserSwitch;
    private TextInputLayout tilCategoryHolder;
    private TextInputLayout tilConditionHolder;
    private TextView tvChosenCondition;
    private TextView tvChosenCategory;
    private View dAttachToUserView;
    private Boolean valueChosen;
    private int chosenCategoryId;
    private int chosenConditionId;
    private int updateValueSpinner;
    private Integer staffAccessLevel;
    private int serviceUserId;
    private String updateType = "Medical";
    private Map<String, String> spinnerMapCategoryNames;
    private Map<String, String> spinnerMapCategoryIDs;
    private Map<String, String> spinnerMapConditionsNames;
    private Map<String, String> spinnerMapConditionsIDs;
    private Map<String, String> spinnerMapServiceUsers;
    private String[] categoriesSpinnerStrings;
    private String[] conditionsSpinnerStrings;
    private String[] spinnerServiceUserStrings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_database);
        initNavigationDrawer();
        Objects.requireNonNull(getSupportActionBar()).setTitle("Medical Database");
        createLayout();
        maintainSliders();
        getDatabaseDetails();
        buttonListeners();
    }

    // Create layout items.
    private void createLayout() {
        staffAccessLevel = Utilities.getUserAccessLevel();
        showNewRecordSwitch = findViewById(R.id.s_medicalDatabaseNewItemSwitch);
        showAddToUserSwitch = findViewById(R.id.s_medicalDatabaseAddUserSwitch);
        tilCategoryHolder = findViewById(R.id.til_medicalDatabaseSpinnerOne);
        tilConditionHolder = findViewById(R.id.til_MedicalDatabaseSpinnerTwo);
        SpinnerChildCategory = findViewById(R.id.actv_medicalDatabaseSpinnerOne);
        SpinnerChildCondition = findViewById(R.id.actv_MedicalDatabaseSpinnerTwo);
        rgChooseCategoryType = findViewById(R.id.rg_medicalDatabaseRG);
        bAddToUser = findViewById(R.id.b_medicalDatabaseAddToUser);
        bAddToDatabase = findViewById(R.id.b_MedicalDatabaseNewValueSave);
        tvChosenCondition = findViewById(R.id.tv_MedicalDatabaseCurrentBody);
        tvChosenCategory = findViewById(R.id.tv_medicalDatabaseTextNewBody);
        newValueEditText = findViewById(R.id.et_medicalDatabaseNewValue);

        if (staffAccessLevel > 2) {
            showNewRecordSwitch.setVisibility(View.GONE);
            showAddToUserSwitch.setVisibility(View.GONE);
        }
    }

    private void buttonListeners() {
        // Radio Button Listener for Telephone Updates.
        rgChooseCategoryType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedID) {
                switch (checkedID) {
                    case R.id.rb_medicalDatabaseMedicalRB:
                        tilCategoryHolder.setHint("Medical Category");
                        tilConditionHolder.setHint("Medical Condition");
                        updateType = "Medical";
                        break;
                    case R.id.rb_medicalDatabaseAllergyRB:
                        tilCategoryHolder.setHint("Allergy Category");
                        tilConditionHolder.setHint("Allergy Condition");
                        updateType = "Allergy";
                        break;
                    default:
                        break;
                }
                SpinnerChildCategory.getText().clear();
                SpinnerChildCondition.getText().clear();
                tvChosenCondition.setText(getResources().getString(R.string.Please_Select_Category_Condition));
                tvChosenCategory.setText(getResources().getString(R.string.not_selected));
                chosenCategoryId = 0;
                chosenConditionId = 0;
                getDatabaseDetails();
            }
        });

        // Add to Database Button.
        bAddToDatabase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (updateType.equals("Medical")) {
                    // Make sure there is a category chosen, and a value in the textbox.
                    if ((chosenCategoryId > 0) && (newValueEditText.getText().toString().length() > 0)) {
                        Long key = dbConnection.insertNewMedicalCondition(newValueEditText.getText().toString(), chosenCategoryId);
                        if (key != null) {
                            Toast.makeText(MedicalDatabase.this, "New Medical Condition Added", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(MedicalDatabase.this, "Unable to add record. Please complete the details and try again", Toast.LENGTH_SHORT).show();
                    }
                } else if (updateType.equals("Allergy")) {
                    // Make sure there is a category chosen, and a value in the textbox.
                    if ((chosenCategoryId > 0) && (newValueEditText.getText().toString().length() > 0)) {
                        Long key = dbConnection.insertNewAllergyCondition(newValueEditText.getText().toString(), chosenCategoryId);
                        if (key != null) {
                            Toast.makeText(MedicalDatabase.this, "New Allergy Added", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(MedicalDatabase.this, "Unable to add record. Please complete the details and try again", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // Add to Database Button.
        bAddToUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((chosenCategoryId > 0) && (chosenConditionId > 0)) {
                    AlertDialog.Builder dAttachToUserBuilder = new AlertDialog.Builder(MedicalDatabase.this);
                    dAttachToUserView = getLayoutInflater().inflate(R.layout.dialog_box_add_it_to_user, null);
                    EditText dDateFromTextBox = dAttachToUserView.findViewById((R.id.et_dialogAddDetailsToUserDateFrom));
                    EditText dDateToTextBox = dAttachToUserView.findViewById((R.id.et_dialogAddDetailsToUserDateTo));
                    TextView dTextViewToShowDetails = dAttachToUserView.findViewById(R.id.tv_dialogAddDetailsToUserValueToAddText);
                    Button dAttachToUserSave = dAttachToUserView.findViewById(R.id.b_dialogAddDetailsToUserSave);
                    Button dAttachToUserCancel = dAttachToUserView.findViewById(R.id.b_dialogAddDetailsToUserCancel);
                    dTextViewToShowDetails.setText("Category: " + spinnerMapCategoryIDs.get(String.valueOf(chosenCategoryId)) +
                                                    " \nCondition / Allergy: " + spinnerMapConditionsIDs.get(String.valueOf(chosenConditionId)));
                    serviceUserId = 0;

                    // Get Data from DB, and populate string list and map.
                    List<HashMap<String, Object>> serviceUserList;
                    serviceUserList = dbConnection.getServiceUserList();
                    spinnerMapServiceUsers = new HashMap<>();
                    spinnerServiceUserStrings = new String[serviceUserList.size()];
                    for (int i = 0; i < serviceUserList.size(); i++) {
                        spinnerMapServiceUsers.put(serviceUserList.get(i).get("su_name").toString(), serviceUserList.get(i).get("service_user_id").toString());
                        spinnerServiceUserStrings[i] = serviceUserList.get(i).get("su_name").toString();
                    }

                    //Set visible if not Contact Number.
                    AutoCompleteTextView spinnerChild = dAttachToUserView.findViewById(R.id.actv_dialogAddDetailsToUserSpinnerOne);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(MedicalDatabase.this, R.layout.spinner_item, spinnerServiceUserStrings);
                    spinnerChild.setAdapter(adapter);
                    spinnerChild.setThreshold(1);
                    spinnerChild.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            // Get value for the update statement.
                            if (spinnerMapServiceUsers.containsKey(spinnerChild.getText().toString())) {
                                serviceUserId = Integer.parseInt(spinnerMapServiceUsers.get(spinnerChild.getText().toString()));
                            } else {
                                spinnerChild.getText().clear();
                            }
                        }
                    });

                    dAttachToUserBuilder.setView(dAttachToUserView);
                    dAttachToUser = dAttachToUserBuilder.create();
                    dAttachToUser.show();


                    // Cancel Button.
                    dAttachToUserCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dAttachToUser.dismiss();
                        }
                    });

                    dAttachToUserSave.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if ((serviceUserId > 0) && (Utilities.isValidDate(dDateFromTextBox.getText().toString()))) {
                                Boolean insertResult = false;
                                switch (updateType) {
                                    case "Medical":
                                        insertResult = dbConnection.insertNewMedicalConditionForUser(serviceUserId, chosenConditionId,
                                                dDateFromTextBox.getText().toString(), dDateToTextBox.getText().toString());
                                        break;
                                    case "Allergy":
                                        insertResult = dbConnection.insertNewAllergyForUser(serviceUserId, chosenConditionId,
                                                dDateFromTextBox.getText().toString(), dDateToTextBox.getText().toString());
                                        break;
                                }
                                if (insertResult = true) {
                                    Toast.makeText(MedicalDatabase.this, "Successfully added to user.", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(MedicalDatabase.this, "Unable to add record. Please complete the details and try again", Toast.LENGTH_SHORT).show();
                            }
                            dAttachToUser.dismiss();

                        }
                    });


                    // Use Date pickers for the date.
                    Calendar calendar = Calendar.getInstance();
                    int year = calendar.get(Calendar.YEAR);
                    int month = calendar.get(Calendar.MONTH);
                    int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                    dDateFromTextBox.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            datePickerDialog = new DatePickerDialog(MedicalDatabase.this, new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                    dDateFromTextBox.setText(String.format("%02d/%02d/%04d", day, month, year));
                                }
                            }, year, month, dayOfMonth);
                            datePickerDialog.show();
                        }
                    });
                    dDateToTextBox.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            datePickerDialog = new DatePickerDialog(MedicalDatabase.this, new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                    dDateToTextBox.setText(String.format("%02d/%02d/%04d", day, month, year));
                                }
                            }, year, month, dayOfMonth);
                            datePickerDialog.show();
                        }
                    });
                } else {
                    Toast.makeText(MedicalDatabase.this, "Unable to determine which condition to add. Please complete the details and try again",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void maintainSliders() {
        showNewRecordSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                ConstraintLayout newMedicalDatabaseItemView = findViewById(R.id.cl_medicalDatabaseNewRecordContainer);
                if (isChecked == true) {
                    newMedicalDatabaseItemView.setVisibility(View.VISIBLE);
                } else {
                    newMedicalDatabaseItemView.setVisibility(View.GONE);
                }
            }
        });
        showAddToUserSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                ConstraintLayout addConditionToUser = findViewById(R.id.cl_MedicalDatabaseResultDataContainer);
                if (isChecked == true) {
                    addConditionToUser.setVisibility(View.VISIBLE);
                } else {
                    addConditionToUser.setVisibility(View.GONE);
                }
            }
        });
    }

    private void getDatabaseDetails() {
        // Get DB Instance, or open one if possible.
        dbConnection = Database.getInstance();
        //Local Vars.
        spinnerMapCategoryNames = new HashMap<>();
        spinnerMapCategoryIDs = new HashMap<>();
        List<HashMap<String, Object>> categoriesList = null;
        Integer radioId = rgChooseCategoryType.getCheckedRadioButtonId();
        RadioButton dialogRadioButton = findViewById(radioId);
        String categoryColumnName = null;
        String categoryIDColumn = null;

        // Populate Parent spinner.
        try {
            // Populate the list
            switch (dialogRadioButton.getText().toString()) {
                case "Medical Conditions":
                    categoriesList = dbConnection.getMedicalCategories();
                    categoryColumnName = "med_con_category_name";
                    categoryIDColumn = "med_con_category_id";

                    break;
                case "Allergies":
                    categoriesList = dbConnection.getAllergyCategories();
                    categoryColumnName = "allergy_type_desc";
                    categoryIDColumn = "allergy_type_id";
                    break;
            }
            // Populate the Dropdown Menu List, and store it's CategoryID for referring to.
            categoriesSpinnerStrings = new String[categoriesList.size()];
            for (int i = 0; i < categoriesList.size(); i++) {
                // Add to map to keep record of ID and it's Category ID.
                spinnerMapCategoryNames.put(categoriesList.get(i).get(categoryColumnName).toString(), categoriesList.get(i).get(categoryIDColumn).toString());
                spinnerMapCategoryIDs.put(categoriesList.get(i).get(categoryIDColumn).toString(), categoriesList.get(i).get(categoryColumnName).toString());
                categoriesSpinnerStrings[i] = categoriesList.get(i).get(categoryColumnName).toString();
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(MedicalDatabase.this, R.layout.spinner_item, categoriesSpinnerStrings);
            SpinnerChildCategory.setAdapter(adapter);
            SpinnerChildCategory.setThreshold(1);
        } catch (Exception e) {
            Log.e("Get Staff Details Error", "exception", e);
        }

        // When top spinner has value chosen.
        SpinnerChildCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // Get the string locally, and save ID of the chosen category to class var.
                chosenCategoryId = Integer.parseInt(spinnerMapCategoryNames.get(SpinnerChildCategory.getText().toString()));
                String chosenCategoryString = SpinnerChildCategory.getText().toString();
                tvChosenCategory.setText(chosenCategoryString);

                // Local Vars for Second Spinner.
                String conditionColumnName = null;
                String conditionIDColumn = null;
                List<HashMap<String, Object>> conditionsList = null;
                try {
                    // Populate the list
                    switch (dialogRadioButton.getText().toString()) {
                        case "Medical Conditions":
                            conditionsList = dbConnection.getMedicalConditions(chosenCategoryId);
                            conditionColumnName = "med_condition_name";
                            conditionIDColumn = "med_condition_id";
                            break;
                        case "Allergies":
                            conditionsList = dbConnection.getAllergies(chosenCategoryId);
                            conditionColumnName = "allergy_description";
                            conditionIDColumn = "allergy_id";
                            break;
                    }
                    // Populate the Dropdown Menu List, and store it's CategoryID for referring to.
                    spinnerMapConditionsNames = new HashMap<>();
                    spinnerMapConditionsIDs = new HashMap<>();
                    conditionsSpinnerStrings = new String[conditionsList.size()];
                    for (int j = 0; j < conditionsList.size(); j++) {
                        // Add to map to keep record of ID and it's Category ID.
                        spinnerMapConditionsNames.put(conditionsList.get(j).get(conditionColumnName).toString(), conditionsList.get(j).get(conditionIDColumn).toString());
                        spinnerMapConditionsIDs.put(conditionsList.get(j).get(conditionIDColumn).toString(), conditionsList.get(j).get(conditionColumnName).toString());
                        conditionsSpinnerStrings[j] = conditionsList.get(j).get(conditionColumnName).toString();
                    }
                    ArrayAdapter<String> conditionsAdapter = new ArrayAdapter<String>(MedicalDatabase.this, R.layout.spinner_item, conditionsSpinnerStrings);
                    SpinnerChildCondition.setAdapter(conditionsAdapter);
                    SpinnerChildCondition.setThreshold(1);
                } catch (Exception e) {
                    Log.e("Get Staff Details Error", "exception", e);
                }

                // When second spinner has value chosen.
                SpinnerChildCondition.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        chosenConditionId = Integer.parseInt(spinnerMapConditionsNames.get(SpinnerChildCondition.getText().toString()));
                        tvChosenCondition = findViewById(R.id.tv_MedicalDatabaseCurrentBody);
                        tvChosenCondition.setText(chosenCategoryString + ": " + SpinnerChildCondition.getText().toString());
                        valueChosen = true;
                    }
                });
            }
        });
    }

    public void initNavigationDrawer() {
        NavigationView navigationView = findViewById(R.id.medicalDatabaseNavView);
        DrawerLayout drawerLayout = findViewById(R.id.drawerLayoutMedicalDatabase);
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