package com.example.tm470_careapp;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import com.google.android.material.switchmaterial.SwitchMaterial;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class service_user_details_medical_details extends Fragment {

    ArrayList<SUMedicalConditionCardModel> SUCurrentMedicalConditionCardModels = new ArrayList<>();
    ArrayList<SUMedicalConditionCardModel> SUHistoricMedicalConditionCardModels = new ArrayList<>();
    RecyclerView sumhCurrentConditionsRecyclerView;
    RecyclerView sumhHistoricConditionsRecyclerView;
    SwitchMaterial showHistoricSwitch;
    TextView tv_headerText;

    public service_user_details_medical_details() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        System.out.println("service_user_details_medical_details Create");
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_service_user_details_medical_details, container, false);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //Create Layout
        createLayoutItems(view);

        // Create Recycler view of rooms.
        sumhCurrentConditionsRecyclerView = view.findViewById(R.id.rv_sumhCurrentConditionsRecyclerView);
        sumhHistoricConditionsRecyclerView = view.findViewById(R.id.rv_sumhHistoricConditionsRecyclerView);

        //Create and set adapters.
        RecyclerViewAdapter_SUCMC cmcAdapter = new RecyclerViewAdapter_SUCMC(getActivity(), SUCurrentMedicalConditionCardModels);
        RecyclerViewAdapter_SUHMC hmcAdapter = new RecyclerViewAdapter_SUHMC(getActivity(), SUHistoricMedicalConditionCardModels);
        sumhCurrentConditionsRecyclerView.setAdapter(cmcAdapter);
        sumhHistoricConditionsRecyclerView.setAdapter(hmcAdapter);

        //Attach Layouts Managers.
        sumhCurrentConditionsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        sumhHistoricConditionsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // CLear out old view.
        SUCurrentMedicalConditionCardModels.clear();
        SUHistoricMedicalConditionCardModels.clear();
        sumhCurrentConditionsRecyclerView.getAdapter().notifyDataSetChanged();
        sumhHistoricConditionsRecyclerView.getAdapter().notifyDataSetChanged();

        // Get Details.
        getServiceUserMedicalDetails();
        showHideMedicalHistory(view);
        tv_headerText.setText(ServiceUserDetails.getHeaderString());
        ((AppCompatActivity)getActivity()).getSupportActionBar().setSubtitle("Medical History");
        super.onViewCreated(view, savedInstanceState);
    }

    private void createLayoutItems(@NonNull View view) {
        System.out.println("createLayoutItems service_user_details_medical_details ");
        showHistoricSwitch = (SwitchMaterial)view.findViewById(R.id.sw_sumhShowHideHistoric);
        tv_headerText = view.findViewById(R.id.tv_sumhHeaderText);
    }

    private void showHideMedicalHistory(@NonNull View view) {
        showHistoricSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                System.out.println("Changed");
                ConstraintLayout medicalHistory = view.findViewById(R.id.cl_sumhConstraintViewTwo);
                if (isChecked == true) {
                    System.out.println("True");
                    medicalHistory.setVisibility(View.VISIBLE);
                } else {
                    System.out.println("False");
                    medicalHistory.setVisibility(View.GONE);
                }
            }
        });
    }

    private void getServiceUserMedicalDetails() {
        System.out.println("Get Service User Medical Details");
        Database dbConnection;
        dbConnection = Database.getInstance();
        Integer serviceUserId = ServiceUserDetails.getServiceUserIDInt();
        try {
            List<HashMap<String, Object>> suCurrentMedConList = new ArrayList<>();
            suCurrentMedConList = dbConnection.getServiceUserCurrMedCons(serviceUserId);

            if (suCurrentMedConList.size() == 0) {
                System.out.println("No Medical Conditions Found");
            } else {
                for (int i = 0; i < suCurrentMedConList.size(); i++) {
                    // Use model class to create object for array.

                    if (suCurrentMedConList.get(i).get("date_to") == null) {

                        SUCurrentMedicalConditionCardModels.add(new SUMedicalConditionCardModel(Objects.toString(suCurrentMedConList.get(i).get("med_condition_name"), null),
                                Objects.toString(suCurrentMedConList.get(i).get("med_con_category_name"), null),
                                Objects.toString(suCurrentMedConList.get(i).get("date_from"), null),
                                Objects.toString(suCurrentMedConList.get(i).get("date_to"), null)));

                    } else {
                    // Use model class to create object for array.
                    SUHistoricMedicalConditionCardModels.add(new SUMedicalConditionCardModel(Objects.toString(suCurrentMedConList.get(i).get("med_condition_name"), null),
                            Objects.toString(suCurrentMedConList.get(i).get("med_con_category_name"), null),
                            Objects.toString(suCurrentMedConList.get(i).get("date_from"), null),
                            Objects.toString(suCurrentMedConList.get(i).get("date_to"), null)));
                }}
            }
        } catch (SQLException throwables) {
            System.out.println(throwables.toString());
            System.out.println(throwables.getMessage());
        }
    }
}