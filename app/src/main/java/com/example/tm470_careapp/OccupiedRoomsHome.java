package com.example.tm470_careapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MenuItem;
import com.google.android.material.navigation.NavigationView;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OccupiedRoomsHome extends AppCompatActivity {

    ActionBarDrawerToggle actionBarDrawerToggle;
    NavDrawerHelper navDrawerHelper;
    ArrayList<RoomCardModel> roomCardModels = new ArrayList<>();

    //Make Single, Twin, Double Vector Images.
    int[] roomCardImages = {R.drawable.drawable_bed_single, R.drawable.drawable_bed_double, R.drawable.drawable_bed_twin};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rooms_home);
        initNavigationDrawer();

        // Create Recycler view of rooms.
        RecyclerView recyclerView = findViewById(R.id.rv_occupiedRoomsRecyclerView);
        setupRoomModels();
        RecyclerViewAdapter_RoomList adapter = new RecyclerViewAdapter_RoomList(this, roomCardModels);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        getSupportActionBar().setTitle("Room Details");
    }

    /* Get Occupied room details from the database, then build up an array of rooms.
    ** Use model class to add a new item to the array for each room using the model class properties. */
    private void setupRoomModels() {
        Database dbConnection;
        dbConnection = Database.getInstance();

        try {
            List<HashMap<String, Object>> roomList;
            roomList = dbConnection.getOccupiedRoomDetails();
            Bitmap imageBitmap = null;
            byte[] fileBytes;

            for (int i = 0; i < roomList.size(); i++) {
                // Convert ImageBlob to Bitmap before passing it into model class.
                if (roomList.get(i).get("image_blob") != null) {
                    fileBytes = (byte[]) roomList.get(i).get("image_blob");
                    imageBitmap = Utilities.getImageBitmap(fileBytes);
                } else {
                    imageBitmap = null;
                }

                // Use model class to create object for array.
                roomCardModels.add(new RoomCardModel(
                        roomList.get(i).get("known_as").toString(),
                        roomList.get(i).get("last_name").toString(),
                        roomList.get(i).get("room_number").toString(),
                        roomList.get(i).get("sex").toString(),
                        roomList.get(i).get("room_type_description").toString(),
                        roomList.get(i).get("service_user_id").toString(),
                        imageBitmap)
                );
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    // Nav Bar.
    // Navigation Bar Initialization.
    public void initNavigationDrawer() {
        NavigationView navigationView = findViewById(R.id.roomlist_navigation_view);
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout_RoomsHome);

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