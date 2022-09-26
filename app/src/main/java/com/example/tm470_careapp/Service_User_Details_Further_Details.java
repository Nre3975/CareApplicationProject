package com.example.tm470_careapp;

import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.textfield.TextInputLayout;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;


public class Service_User_Details_Further_Details extends Fragment {

    private AlertDialog insertDialog;
    private AlertDialog updateDialog;
    private View updateSUFDDialogView;
    private View newDataDialogView;
    private Button updateHWButton;
    private Button updateBPButton;
    private Database dbConnection;
    private TextInputLayout ti_sufdBehaviouralNotes;
    private TextInputLayout ti_sufdBMI;
    private TextInputLayout ti_sufdBPdiastolic;
    private TextInputLayout ti_sufdBPResult;
    private TextInputLayout ti_sufdBPSystolic;
    private TextInputLayout ti_sufdDietaryRequirements;
    private TextInputLayout ti_sufdHeight;
    private TextInputLayout ti_sufdMovingAndHandlinReqs;
    private TextInputLayout ti_sufdPersonalPreferences;
    private TextInputLayout ti_sufdWeight;
    private TextView tv_pageHeaderText;
    private EditText et_sufdBehaviouralNotes;
    private EditText et_sufdBMI;
    private EditText et_sufdBPdiastolic;
    private EditText et_sufdBPResult;
    private EditText et_sufdBPSystolic;
    private EditText et_sufdDietaryRequirements;
    private EditText et_sufdHeight;
    private EditText et_sufdMovingAndHandlinReqs;
    private EditText et_sufdPersonalPreferences;
    private EditText et_sufdWeight;
    private Integer serviceUserId;
    private Integer staffLevel;

    public Service_User_Details_Further_Details() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_service_user_details_further_details, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // Get Service User ID.
        serviceUserId = ServiceUserDetails.getServiceUserIDInt();
        getUserPermissionLevel();
        //Create Layout
        createLayoutItems(view);
        // Get Details.
        getServiceUserFurtherDetails();
        setButtonListeners();
        tv_pageHeaderText.setText(ServiceUserDetails.getHeaderString());
        ((AppCompatActivity)getActivity()).getSupportActionBar().setSubtitle("Further Details");
        super.onViewCreated(view, savedInstanceState);
    }

    private void createLayoutItems(@NonNull View view) {
        ti_sufdBehaviouralNotes = view.findViewById(R.id.til_sufdBehaviouralNotes);
        ti_sufdBMI = view.findViewById(R.id.til_sufdBMI);
        ti_sufdBPdiastolic = view.findViewById(R.id.til_sufdBPdiastolic);
        ti_sufdBPResult = view.findViewById(R.id.til_sufdBPResult);
        ti_sufdBPSystolic = view.findViewById(R.id.til_sufdBPSystolic);
        ti_sufdDietaryRequirements = view.findViewById(R.id.til_sufdDietaryRequirements);
        ti_sufdHeight = view.findViewById(R.id.til_sufdHeight);
        ti_sufdMovingAndHandlinReqs = view.findViewById(R.id.til_sufdMovingAndHandlinReqs);
        ti_sufdPersonalPreferences = view.findViewById(R.id.til_sufdPersonalPreferences);
        ti_sufdWeight = view.findViewById(R.id.til_sufdWeight);
        tv_pageHeaderText = view.findViewById(R.id.tv_sufdHeaderText);
        et_sufdBehaviouralNotes = (EditText) view.findViewById(R.id.et_sufdBehaviouralNotes);
        et_sufdBMI = (EditText) view.findViewById(R.id.et_sufdBMI);
        et_sufdBPdiastolic = (EditText) view.findViewById(R.id.et_sufdBPdiastolic);
        et_sufdBPResult = (EditText) view.findViewById(R.id.et_sufdBPResult);
        et_sufdBPSystolic = (EditText) view.findViewById(R.id.et_sufdBPSystolic);
        et_sufdDietaryRequirements = (EditText) view.findViewById(R.id.et_sufdDietaryRequirements);
        et_sufdHeight = (EditText) view.findViewById(R.id.et_sufdHeight);
        et_sufdMovingAndHandlinReqs = (EditText) view.findViewById(R.id.et_sufdMovingAndHandlinReqs);
        et_sufdPersonalPreferences = (EditText) view.findViewById(R.id.et_sufdPersonalPreferences);
        et_sufdWeight = (EditText) view.findViewById(R.id.et_sufdWeight);
        updateHWButton = getView().findViewById(R.id.b_sufdUpdateHWButton);
        updateBPButton = getView().findViewById(R.id.b_sufdUpdateBPButton);

        // Control access to some areas.
        if (staffLevel > 2) {
            ti_sufdDietaryRequirements.setEndIconMode(TextInputLayout.END_ICON_NONE);
            ti_sufdBehaviouralNotes.setEndIconMode(TextInputLayout.END_ICON_NONE);
            ti_sufdMovingAndHandlinReqs.setEndIconMode(TextInputLayout.END_ICON_NONE);
            ti_sufdPersonalPreferences.setEndIconMode(TextInputLayout.END_ICON_NONE);
        }

        UpdateOnClicks(ti_sufdDietaryRequirements);
        UpdateOnClicks(ti_sufdBehaviouralNotes);
        UpdateOnClicks(ti_sufdMovingAndHandlinReqs);
        UpdateOnClicks(ti_sufdPersonalPreferences);
    }

    private void setButtonListeners() {
        updateHWButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recordNewBPHWData(1);
            }
        });
        updateBPButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recordNewBPHWData(2);
            }
        });
    }

    private void getUserPermissionLevel() {
        dbConnection = Database.getInstance();
        staffLevel = dbConnection.getStaffAccessLevel();
        // Default to level 3 for minimal perms.
        if (staffLevel == 0) {
            staffLevel = 3;
        }
    }

    @Override
    public void onResume() {
        getServiceUserFurtherDetails();
        super.onResume();
    }

    private void UpdateOnClicks(@NonNull TextInputLayout tilButtonPressed) {
        tilButtonPressed.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dialog Variables Setup.
                AlertDialog.Builder updateSUFDDialogBuilder = new AlertDialog.Builder(getActivity());
                updateSUFDDialogView = getLayoutInflater().inflate(R.layout.dialog_box_update_service_user_reqs, null, false);
                TextInputLayout updateSUFDDialogTextInput = updateSUFDDialogView.findViewById((R.id.til_dialogUpdateSUReqsTextBox1));
                EditText updateSUFDDialogTextEditBox = (EditText) updateSUFDDialogView.findViewById(R.id.et_dialogUpdateSUReqsTextBox1);
                Button dialogCancel = updateSUFDDialogView.findViewById(R.id.b_dialogUpdateSUReqsButtonCancel);
                Button dialogSubmit = updateSUFDDialogView.findViewById(R.id.b_dialogUpdateSUReqsButtonSave);
                String fieldBeingUpdated = (String) v.getContentDescription().toString();
                String staffQueryReturnColumnName = null;

                switch (fieldBeingUpdated) {
                    case "dietary_requirements":
                        updateSUFDDialogBuilder.setTitle("Dietary Requirements");
                        updateSUFDDialogTextInput.setHint("Dietary Requirements");
                        staffQueryReturnColumnName = "dietary_requirements";
                        break;
                    case "moving_and_handling":
                        updateSUFDDialogBuilder.setTitle("Moving And Handling");
                        updateSUFDDialogTextInput.setHint("Moving And Handling");
                        staffQueryReturnColumnName = "moving_and_handling";
                        break;
                    case "behaviour_history":
                        updateSUFDDialogBuilder.setTitle("Behaviour History");
                        updateSUFDDialogTextInput.setHint("Behaviour History");
                        staffQueryReturnColumnName = "behaviour_history";
                        break;
                    case "personal_preferences":
                        updateSUFDDialogBuilder.setTitle("Personal Preferences");
                        updateSUFDDialogTextInput.setHint("Personal Preferences");
                        staffQueryReturnColumnName = "personal_preferences";
                        break;
                }

                // ReQuery the data to make sure we're using the latest version as the "Current Value";
                List<HashMap<String, Object>> getCurrentValues;
                getCurrentValues = dbConnection.getServiceUserFurtherDetails(serviceUserId);
                String latestValue = Objects.toString(getCurrentValues.get(0).get(staffQueryReturnColumnName), null);
                // Set the Dialog text box to current value.
                updateSUFDDialogTextEditBox.setText(latestValue);

                // Cancel Button.
                dialogCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        updateDialog.dismiss();
                    }
                });
                dialogSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Validate and save changes to the update.
                        if ((updateSUFDDialogTextEditBox.getText() != null) && (!updateSUFDDialogTextEditBox.getText().toString().equals(latestValue))) {
                            dbConnection.updateServiceUserFurtherInfoRec(fieldBeingUpdated, updateSUFDDialogTextEditBox.getText().toString(), serviceUserId);
                            getServiceUserFurtherDetails();
                        }
                        updateDialog.dismiss();
                    }
                });
                // Raise Dialog.
                updateSUFDDialogBuilder.setView(updateSUFDDialogView);
                updateDialog = updateSUFDDialogBuilder.create();
                updateDialog.show();
            }
        });
    }


    private void recordNewBPHWData(Integer bpOrHW) {
        AlertDialog.Builder newBPHWDialogBuilder = new AlertDialog.Builder(getActivity());
        newDataDialogView = getLayoutInflater().inflate(R.layout.dialog_box_new_vitals, null, false);
        TextInputLayout newBPHWReadingDialogTextLayoutOne = newDataDialogView.findViewById((R.id.til_suBPHWDialogTextLayoutOne));
        TextInputLayout newBPHWReadingDialogTextLayoutTwo = newDataDialogView.findViewById(R.id.til_suBPHWDialogTextLayoutTwo);
        EditText newBPHWReadingDialogEditTextOne = (EditText) newDataDialogView.findViewById(R.id.et_suBPHWDialogTextLayoutOne);
        EditText newBPHWReadingDialogEditTextTwo = (EditText) newDataDialogView.findViewById(R.id.et_suBPHWDialogTextLayoutTwo);
        TextView newBPHWReadingDialogWarning = newDataDialogView.findViewById(R.id.tv_suBPHWDialogWarning);
        Button dialogCancel = newDataDialogView.findViewById(R.id.b_suBPHWDialogCancel);
        Button dialogSubmit = newDataDialogView.findViewById(R.id.b_suBPHWDialogSave);

        if (bpOrHW == 2) {
            newBPHWDialogBuilder.setTitle("Blood Pressure");
            newBPHWReadingDialogTextLayoutOne.setHint("Systolic");
            newBPHWReadingDialogTextLayoutTwo.setHint("Diastolic");
        } else {
            newBPHWDialogBuilder.setTitle("Height & Weight");
            newBPHWReadingDialogTextLayoutOne.setHint("Height");
            newBPHWReadingDialogTextLayoutTwo.setHint("Weight");
        }
        newBPHWDialogBuilder.setView(newDataDialogView);
        insertDialog = newBPHWDialogBuilder.create();
        insertDialog.show();

        dialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertDialog.dismiss();
            }
        });
        dialogSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((newBPHWReadingDialogEditTextOne == null) || (Integer.parseInt(newBPHWReadingDialogEditTextOne.getText().toString()) == 0) || (newBPHWReadingDialogEditTextTwo == null) || (Integer.parseInt(newBPHWReadingDialogEditTextTwo.getText().toString()) == 0)) {
                    newBPHWReadingDialogWarning.setVisibility(View.VISIBLE);
                } else {
                    if (bpOrHW == 2) {
                        Integer userSystolic = Integer.parseInt(newBPHWReadingDialogEditTextOne.getText().toString());
                        Integer userDiastolic = Integer.parseInt(newBPHWReadingDialogEditTextTwo.getText().toString());
                        dbConnection = Database.getInstance();
                        dbConnection.insertNewServiceUserBPVitals(1, userSystolic, userDiastolic);
                    } else {
                        newBPHWReadingDialogEditTextOne.setText(180);
                        newBPHWReadingDialogEditTextTwo.setText(et_sufdWeight.getText());
                        Double userHeight = Double.parseDouble(newBPHWReadingDialogEditTextOne.getText().toString());
                        Double userWeight = Double.parseDouble(newBPHWReadingDialogEditTextTwo.getText().toString());
                        dbConnection = Database.getInstance();
                        dbConnection.insertNewServiceUserHWVitals(1, userHeight, userWeight);
                    }
                    insertDialog.dismiss();
                    getServiceUserFurtherDetails();
                }
            }
        });
    }

    private void getServiceUserFurtherDetails() {
        dbConnection = Database.getInstance();
        try {
            List<HashMap<String, Object>> serviceUserFurtherDetailsList = null;
            serviceUserFurtherDetailsList = dbConnection.getServiceUserFurtherDetails(this.serviceUserId);
            if (serviceUserFurtherDetailsList.size() == 0) {
                Toast.makeText(getActivity(), "Error getting service user details", Toast.LENGTH_LONG).show();
            } else {
                if (serviceUserFurtherDetailsList.get(0).get("su_height_cms") != null) {
                    et_sufdHeight.setText(Objects.toString(serviceUserFurtherDetailsList.get(0).get("su_height_cms"), null));
                }
                if (serviceUserFurtherDetailsList.get(0).get("su_weight_kgs") != null) {
                    et_sufdWeight.setText(Objects.toString(serviceUserFurtherDetailsList.get(0).get("su_weight_kgs"), null));
                }
                if (serviceUserFurtherDetailsList.get(0).get("su_bmi") != null) {
                    et_sufdBMI.setText(Objects.toString(serviceUserFurtherDetailsList.get(0).get("su_bmi"), null));
                    Double intBMI = (Double.parseDouble(Objects.toString(serviceUserFurtherDetailsList.get(0).get("su_bmi"))));
                    if (intBMI > 0 && intBMI < 18.5) {
                        // Underweight
                        et_sufdBMI.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.customBlue)));
                    } else if (intBMI > 18.5 && intBMI < 24.9) {
                        //Normal
                        et_sufdBMI.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.customGreen)));
                    } else if (intBMI > 25.0 && intBMI < 29.9) {
                        // Overweight
                        et_sufdBMI.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.customOrange)));
                    } else if (intBMI > 30) {
                        // Obese
                        et_sufdBMI.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.customRed)));
                    }
                }
                if (serviceUserFurtherDetailsList.get(0).get("su_bp_systolic") != null) {
                    et_sufdBPSystolic.setText(Objects.toString(serviceUserFurtherDetailsList.get(0).get("su_bp_systolic"), null));
                }
                if (serviceUserFurtherDetailsList.get(0).get("su_bp_diastolic") != null) {
                    et_sufdBPdiastolic.setText(Objects.toString(serviceUserFurtherDetailsList.get(0).get("su_bp_diastolic"), null));
                }
                if (serviceUserFurtherDetailsList.get(0).get("dietary_requirements") != null) {
                    et_sufdDietaryRequirements.setText(Objects.toString(serviceUserFurtherDetailsList.get(0).get("dietary_requirements"), null));
                }
                if (serviceUserFurtherDetailsList.get(0).get("moving_and_handling") != null) {
                    et_sufdMovingAndHandlinReqs.setText(Objects.toString(serviceUserFurtherDetailsList.get(0).get("moving_and_handling"), null));
                }
                if (serviceUserFurtherDetailsList.get(0).get("behaviour_history") != null) {
                    et_sufdBehaviouralNotes.setText(Objects.toString(serviceUserFurtherDetailsList.get(0).get("behaviour_history"), null));
                }
                if (serviceUserFurtherDetailsList.get(0).get("personal_preferences") != null) {
                    et_sufdPersonalPreferences.setText(Objects.toString(serviceUserFurtherDetailsList.get(0).get("personal_preferences"), null));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}