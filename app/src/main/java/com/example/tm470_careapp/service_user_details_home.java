package com.example.tm470_careapp;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;


public class service_user_details_home extends Fragment {

    private RecyclerView suAllergiesRecyclerView;
    private AlertDialog d_imageDialog;
    private ArrayList<SUAllergyCardModel> suAllergyCardModels = new ArrayList<>();
    private TextView tv_suDetailsAllergyHeader;
    private TextView tv_suDetailsHeaderText;
    private TextInputLayout ti_suDetailsKnownAs;
    private TextInputLayout ti_suDetailsSex;
    private TextInputLayout ti_suDetailsDNACRP;
    private TextInputLayout ti_suDetailsFullName;
    private TextInputLayout ti_suDateOfBirth;
    private TextInputLayout ti_suDateAdmitted;
    private TextInputLayout ti_suEthnicity;
    private TextInputLayout ti_suDetailsGPChoice;
    private ImageView iv_serviceUserImage;
    private Database dbConnection;
    private EditText et_suDetailsKnownAs;
    private EditText et_suDetailsSex;
    private EditText et_suDetailsDNACRP;
    private EditText et_suDetailsFullName;
    private EditText et_suDateOfBirth;
    private EditText et_suDateAdmitted;
    private EditText et_suEthnicity;
    private EditText et_suDetailsGPChoice;
    private FloatingActionButton fab_editUser;
    private Integer serviceUserId;
    private Integer staffLevel = 99;
    private String roomNumber;
    private Boolean imageSet = false;
    private View v_newLargeImageView;

    public service_user_details_home() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_service_user_details_home, container, false);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // Get user level.
        getUserPermissionLevel();
        //Create Layout
        serviceUserId = ServiceUserDetails.getServiceUserIDInt();
        createLayoutItems(view);
        buttonListeners(view);
        // Create Recycler view of rooms.
        suAllergiesRecyclerView = view.findViewById(R.id.rv_suAllergiesRecycleView);
        RecyclerViewAdapter_SUA adapter = new RecyclerViewAdapter_SUA(getActivity(), suAllergyCardModels);
        suAllergiesRecyclerView.setAdapter(adapter);
        suAllergiesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        // CLear out old view.
        suAllergyCardModels.clear();
        suAllergiesRecyclerView.getAdapter().notifyDataSetChanged();
        // Get Details.
        getServiceUserDetails();
        ((AppCompatActivity)getActivity()).getSupportActionBar().setSubtitle("Basic Details");
        super.onViewCreated(view, savedInstanceState);
    }

    private void createLayoutItems(@NonNull View view) {
        iv_serviceUserImage = view.findViewById(R.id.iv_suDetailsImage);
        ti_suDetailsKnownAs = view.findViewById(R.id.til_suDetailsKnownAs);
        ti_suDetailsSex = view.findViewById(R.id.til_suDetailsSex);
        ti_suDetailsDNACRP = view.findViewById(R.id.til_suDetailsDNACRP);
        ti_suDetailsFullName = view.findViewById(R.id.til_suDetailsFullName);
        ti_suDateOfBirth = view.findViewById(R.id.til_suDetailsDateOfBirth);
        ti_suDateAdmitted = view.findViewById(R.id.til_suDetailsDateAdmitted);
        ti_suEthnicity = view.findViewById(R.id.til_suDetailsEthnicity);
        ti_suDetailsGPChoice = view.findViewById(R.id.til_suDetailsGPChoice);
        et_suDetailsKnownAs = (EditText)view.findViewById(R.id.et_suDetailsKnownAs);
        et_suDetailsSex = (EditText)view.findViewById(R.id.et_suDetailsSex);
        et_suDetailsDNACRP = (EditText)view.findViewById(R.id.et_suDetailsDNACRP);
        et_suDetailsFullName = (EditText)view.findViewById(R.id.et_suDetailsFullName);
        et_suDateOfBirth = (EditText)view.findViewById(R.id.et_suDetailsDateOfBirth);
        et_suDateAdmitted = (EditText)view.findViewById(R.id.et_suDetailsDateAdmitted);
        et_suEthnicity = (EditText)view.findViewById(R.id.et_suDetailsEthnicity);
        et_suDetailsGPChoice = (EditText)view.findViewById(R.id.et_suDetailsGPChoice);
        tv_suDetailsAllergyHeader = view.findViewById(R.id.tv_suDetailsAllergyHeader);
        tv_suDetailsHeaderText = view.findViewById(R.id.et_suDetailsHeaderText);
        fab_editUser = view.findViewById(R.id.fab_suDetailsHome);

        if (staffLevel < 3) {
            fab_editUser.setVisibility(View.VISIBLE);
        }
        // Set Page Headers.
        tv_suDetailsHeaderText.setText(ServiceUserDetails.getHeaderString());
    }

    private void buttonListeners(@NonNull View view) {
        fab_editUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), AddUpdateServiceUser.class);
                intent.putExtra("SERVICE_USER_ID", serviceUserId);
                intent.putExtra("SERVICE_USER_ROOM_ID", ServiceUserDetails.getRoomNumber());
                view.getContext().startActivity(intent);
            }
        });
        iv_serviceUserImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBiggerPicture();
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

    private void showBiggerPicture() {
        AlertDialog.Builder newImageIncreaseDialogBuilder = new AlertDialog.Builder(getActivity());
        v_newLargeImageView = getLayoutInflater().inflate(R.layout.dialog_image, null, false);
        ImageView d_largeImageView = v_newLargeImageView.findViewById(R.id.iv_dialogSingleImageBox);
        Button dialogCancel = v_newLargeImageView.findViewById(R.id.b_dialogSingleImageBoxCancel);

        if (imageSet) {
            Bitmap bmSrc1 = ((BitmapDrawable)iv_serviceUserImage.getDrawable()).getBitmap();
            Bitmap bmSrc2 = bmSrc1.copy(bmSrc1.getConfig(), true);
            d_largeImageView.setImageDrawable(getResources().getDrawable(R.drawable.drawable_bed_single));
            Bitmap bitmap1=((BitmapDrawable)iv_serviceUserImage.getDrawable()).getBitmap();
            d_largeImageView.setImageBitmap(bitmap1);
            newImageIncreaseDialogBuilder.setView(v_newLargeImageView);
            d_imageDialog = newImageIncreaseDialogBuilder.create();
            d_imageDialog.show();
        }

        dialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                d_imageDialog.dismiss();
            }
        });
    }


    private void getServiceUserDetails() {
        Database dbConnection;
        dbConnection = Database.getInstance();
        Integer serviceUserId = ServiceUserDetails.getServiceUserIDInt();
        try {
            List<HashMap<String,Object>> serviceUserDetailsList;
            serviceUserDetailsList = dbConnection.getServiceUserDetails(serviceUserId);
            if (serviceUserDetailsList.size() == 0) {
                Toast.makeText(getActivity(), "Error getting service user details", Toast.LENGTH_LONG).show();
            } else {
                if (serviceUserDetailsList.get(0).get("known_as") != null) {
                    et_suDetailsKnownAs.setText(serviceUserDetailsList.get(0).get("known_as").toString());
                }
                if (serviceUserDetailsList.get(0).get("sex") != null) {
                    et_suDetailsSex.setText(serviceUserDetailsList.get(0).get("sex").toString());
                }
                if (serviceUserDetailsList.get(0).get("dnacpr_status") != null) {
                    et_suDetailsDNACRP.setText(serviceUserDetailsList.get(0).get("dnacpr_status").toString());

                    if (serviceUserDetailsList.get(0).get("dnacpr_status").equals("0")) {
                        // No  DNA
                        et_suDetailsDNACRP.setText(R.string.NODNACPR);
                    } else if (serviceUserDetailsList.get(0).get("dnacpr_status").equals("1")) {
                        // DNACPR
                        et_suDetailsDNACRP.setText("DNACPR");
                        et_suDetailsDNACRP.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.customRed)));
                    }
                }
                if ((serviceUserDetailsList.get(0).get("first_name") != null) || (serviceUserDetailsList.get(0).get("last_name") != null)) {
                    String fullName = (serviceUserDetailsList.get(0).get("first_name").toString()) + " " + (serviceUserDetailsList.get(0).get("last_name").toString());
                    fullName = fullName.trim();
                    et_suDetailsFullName.setText(fullName);
                }
                if (serviceUserDetailsList.get(0).get("date_of_birth") != null) {
                    String formattedDOBDate = Utilities.formatDateStringToString(serviceUserDetailsList.get(0).get("date_of_birth").toString());
                    et_suDateOfBirth.setText(formattedDOBDate);
                }
                if (serviceUserDetailsList.get(0).get("date_of_admittance") != null) {
                    String formattedAdmittedDate =  Utilities.formatDateStringToString(serviceUserDetailsList.get(0).get("date_of_admittance").toString());
                    et_suDateAdmitted.setText(formattedAdmittedDate);
                }
                if (serviceUserDetailsList.get(0).get("ethnicity_description") != null) {
                    et_suEthnicity.setText(serviceUserDetailsList.get(0).get("ethnicity_description").toString());
                }
                if (serviceUserDetailsList.get(0).get("org_name") != null) {
                    et_suDetailsGPChoice.setText(serviceUserDetailsList.get(0).get("org_name").toString());
                }
                if (serviceUserDetailsList.get(0).get("image_blob") != null) {
                    byte[] fileBytes = (byte[]) serviceUserDetailsList.get(0).get("image_blob");
                    Utilities.setImageViewWithByteArray(getView().findViewById(R.id.iv_suDetailsImage), (fileBytes));
                    imageSet = true;
                }
            }




            // Get Allergies for the Recycler View.
            List<HashMap<String,Object>> suAllergyList = new ArrayList<>();
            suAllergyList = dbConnection.getServiceUserAllergies(serviceUserId);

                for (int i = 0; i < suAllergyList.size(); i++) {
                    // Use model class to create object for array.
                    suAllergyCardModels.add(new SUAllergyCardModel(Objects.toString(suAllergyList.get(i).get("allergy_description"), null),
                            Objects.toString(suAllergyList.get(i).get("allergy_type_desc"), null),
                            Objects.toString(suAllergyList.get(i).get("date_from"), null),
                            Objects.toString(suAllergyList.get(i).get("date_to"), null)));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }




}