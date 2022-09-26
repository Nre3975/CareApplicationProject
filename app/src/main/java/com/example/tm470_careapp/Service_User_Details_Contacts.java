package com.example.tm470_careapp;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class Service_User_Details_Contacts extends Fragment {

    RecyclerView suContactsRecyclerView;
    ArrayList<SUContactsCardModel> suContactsCardModels = new ArrayList<>();
    TextView tv_headerText;

    public Service_User_Details_Contacts() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_service_user_details_contacts, container, false);
        return v;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //Create Layout
        createLayoutItems(view);
        // Create Recycler view of rooms.
        suContactsRecyclerView = view.findViewById(R.id.rv_suContactsRecyclerView);
        //Create and set adapters.
        RecyclerViewAdapter_SUContact contactAdapter = new RecyclerViewAdapter_SUContact(getActivity(), suContactsCardModels);
        suContactsRecyclerView.setAdapter(contactAdapter);
        //Attach Layouts Managers.
        suContactsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        // CLear out old view.
        suContactsCardModels.clear();
        suContactsRecyclerView.getAdapter().notifyDataSetChanged();
        // Get Details.
        getServiceUserContactDetails();
        tv_headerText.setText(ServiceUserDetails.getHeaderString());
        ((AppCompatActivity)getActivity()).getSupportActionBar().setSubtitle("Contacts");
        super.onViewCreated(view, savedInstanceState);
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void getServiceUserContactDetails() {
        Database dbConnection;
        dbConnection = Database.getInstance();
        Integer serviceUserId = ServiceUserDetails.getServiceUserIDInt();
        try {
            List<HashMap<String, Object>> suContactList = new ArrayList<>();
            suContactList = dbConnection.getServiceUserContacts(serviceUserId);

            if (suContactList.size() == 0) {
                System.out.println("No Contacts Found");
            } else {
                for (int i = 0; i < suContactList.size(); i++) {
                    // Use model class to create object for array.
                    String address;
                    address = Stream.of(Objects.toString(suContactList.get(i).get("address_line1"), ""),
                            Objects.toString(suContactList.get(i).get("address_line2"), ""),
                            Objects.toString(suContactList.get(i).get("address_line3"), ""),
                            Objects.toString(suContactList.get(i).get("city"), ""),
                            Objects.toString(suContactList.get(i).get("county_or_province"), ""),
                            Objects.toString(suContactList.get(i).get("post_code"), "")).filter(Objects::nonNull).collect(Collectors.joining("\n"));

                    address = address.replaceAll("\n+", "\n");

                    suContactsCardModels.add(new SUContactsCardModel(
                            Objects.toString(suContactList.get(i).get("contact_name"), Objects.toString(suContactList.get(i).get("org_name"), null)),
                            Objects.toString(suContactList.get(i).get("ct_description"), null),
                            Objects.toString(suContactList.get(i).get("parent_organisations"), null),
                            Objects.toString(suContactList.get(i).get("main_telno"), null),
                            Objects.toString(suContactList.get(i).get("work_telno"), null),
                            Objects.toString(suContactList.get(i).get("home_telno"), null),
                            Objects.toString(suContactList.get(i).get("primary_email"), null),
                            Objects.toString(suContactList.get(i).get("secondary_email"), null),
                            address.toString())
                    );
                }
            }
        } catch (SQLException throwables) {
            Log.e("Get SUContacts Error", throwables.getMessage());
        }
    }

    private void createLayoutItems(@NonNull View view) {
        tv_headerText = view.findViewById(R.id.tv_ContactsHeaderText );
    }
}