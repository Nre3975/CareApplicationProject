package com.example.tm470_careapp;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import java.sql.SQLException;

public class NavDrawerHelper extends ContextWrapper {

    ActionBarDrawerToggle actionBarDrawerToggle;
    private Database dbConnection;
    private Integer staffAccessLevel;
    private Integer seniorRequiredAccessLevel = 3;
    private Integer adminRequiredAccessLevel = 1;

    public ActionBarDrawerToggle getActionBarDrawerToggle() {
        return actionBarDrawerToggle;
    }

    public NavDrawerHelper(Context context) {
        super(context);
    }

    public void initNav(final DrawerLayout drawerLayout, NavigationView navigationView, Activity activity, final boolean isFinish) {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                staffAccessLevel = Utilities.getUserAccessLevel();
                int id = menuItem.getItemId();
                switch (id) {
                    case R.id.nav_staff_home:
                        if (!activity.getClass().getSimpleName().equals("StaffHome")) {
                            startActivity(new Intent(getBaseContext(), StaffDetails.class));
                            activity.overridePendingTransition(0, 0);
                            if (isFinish) ((Activity) getBaseContext()).finish();
                            drawerLayout.closeDrawers();
                        }
                        break;
                    case R.id.nav_occupied_rooms:
                        if (!activity.getClass().getSimpleName().equals("RoomsHome")) {
                            startActivity(new Intent(getBaseContext(), OccupiedRoomsHome.class));
                            activity.overridePendingTransition(0, 0);
                            if (isFinish) ((Activity) getBaseContext()).finish();
                            drawerLayout.closeDrawers();
                        }
                        break;
                    case R.id.nav_maint_medical:
                        if (!activity.getClass().getSimpleName().equals("MedicalDatabase")) {
                            startActivity(new Intent(getBaseContext(), MedicalDatabase.class));
                            activity.overridePendingTransition(0, 0);
                            if (isFinish) ((Activity) getBaseContext()).finish();
                            drawerLayout.closeDrawers();
                        }
                        break;
                    case R.id.nav_maint_newServiceUser:
                        if (!activity.getClass().getSimpleName().equals("AddNewServiceUser")) {
                            if (staffAccessLevel < seniorRequiredAccessLevel) {
                                startActivity(new Intent(getBaseContext(), AddUpdateServiceUser.class));
                                activity.overridePendingTransition(0, 0);
                                if (isFinish) ((Activity) getBaseContext()).finish();
                                drawerLayout.closeDrawers();
                            } else {
                                Toast.makeText(getApplicationContext(), "Access Denied!", Toast.LENGTH_SHORT).show();
                            }
                        }
                        break;
                    case R.id.nav_maint_newStaffMember:
                        if (!activity.getClass().getSimpleName().equals("AddNewStaffMember")) {

                            if (staffAccessLevel < adminRequiredAccessLevel) {
                                startActivity(new Intent(getBaseContext(), UserMaintenance.class));
                                activity.overridePendingTransition(0, 0);
                                if (isFinish) ((Activity) getBaseContext()).finish();
                                drawerLayout.closeDrawers();
                            } else {
                                Toast.makeText(getApplicationContext(), "Access Denied!", Toast.LENGTH_SHORT).show();
                            }
                        }
                        break;
                    case R.id.nav_maint_contacts:
                        if (!activity.getClass().getSimpleName().equals("nav_maint_contacts")) {
                            Toast.makeText(getApplicationContext(), "Coming Soon!", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.nav_Logout:
                        try {
                            Database dbConnection = Database.getInstance();
                            startActivity(new Intent(getBaseContext(), MainActivity.class));
                            dbConnection.closeInstance();
                            drawerLayout.closeDrawers();
                            Toast.makeText(getApplicationContext(), "Logged Out successfully", Toast.LENGTH_SHORT).show();
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }
                    break;
                }
                return true;
            }
        });

        actionBarDrawerToggle = new ActionBarDrawerToggle(activity, drawerLayout, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    public Boolean handleOnItemSelected(MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        } else {
            return false;
        }
    }
}