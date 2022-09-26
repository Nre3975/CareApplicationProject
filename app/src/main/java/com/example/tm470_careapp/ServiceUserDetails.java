package com.example.tm470_careapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import android.os.Bundle;
import android.view.MenuItem;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class ServiceUserDetails extends AppCompatActivity {

    private static String serviceUserID;
    private static String roomNumber;
    private static String suKnownAs;
    ActionBarDrawerToggle actionBarDrawerToggle;
    NavDrawerHelper navDrawerHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get user ID from intent.
         Bundle intentDetails = getIntent().getExtras();
        if (intentDetails != null) {
            serviceUserID = intentDetails.getString("SERVICE_USER_ID");
            suKnownAs= intentDetails.getString("SERVICE_USER_KNOWN_AS");
            roomNumber = intentDetails.getString("ROOM_NUMBER");
        }

        //Build UI. This should be done after variable setup etc. has been done.
        setContentView(R.layout.activity_service_user_details);
        BottomNavigationView bottomNavigationView = findViewById(R.id.service_user_details_bottom_nav);
        NavController navController = Navigation.findNavController(this,  R.id.fragment_serviceUserDetailsFragmentContainer);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        //AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.serviceUserDetailsHome, R.id.service_user_details_further_details, R.id.service_user_details_medical_details, R.id.service_user_details_contacts).build();
        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        initNavigationDrawer();
        getSupportActionBar().setTitle("Service User Details");
    }

    public static Integer getServiceUserIDInt() {
        return Integer.parseInt(serviceUserID);
    }

    public static Integer getRoomNumber() {
        return Integer.parseInt(roomNumber);
    }

    public static String getHeaderString() {
        return suKnownAs + " (Room "+roomNumber+")";
    }

    // Nav Bar.
    // Navigation Bar Initialization.
    public void initNavigationDrawer() {
        NavigationView navigationView = findViewById(R.id.service_user_details_navigation_view);
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout_ServiceUserDetailsHome);
        navDrawerHelper = new NavDrawerHelper(this);
        navDrawerHelper.initNav(drawerLayout, navigationView, this, false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    // Required to make hamburger button open the Nav Drawer.
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        navDrawerHelper.handleOnItemSelected(item);
        return super.onOptionsItemSelected(item);
    }
}