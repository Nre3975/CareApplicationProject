package com.example.tm470_careapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class RecyclerViewAdapter_RoomList extends RecyclerView.Adapter<RecyclerViewAdapter_RoomList.MyViewHolder> {

    Context context;
    ArrayList<RoomCardModel> roomCardModels;

    public RecyclerViewAdapter_RoomList(Context context, ArrayList<RoomCardModel> roomCardModels) {
        this.context = context;
        this.roomCardModels = roomCardModels;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter_RoomList.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create appearance of each row.
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycler_room_card, parent, false);
        return new RecyclerViewAdapter_RoomList.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter_RoomList.MyViewHolder holder, int position) {
        // Assign values to view
        String RoomNumberText = "Room: " + roomCardModels.get(position).getRoomNumber();
        String serviceUserID = roomCardModels.get(position).getRoomServiceUserId();
        holder.roomCardText1.setText(roomCardModels.get(position).getRoomOccupantFullName());
        holder.roomCardText2.setText(RoomNumberText);
        holder.roomCardText3.setText(roomCardModels.get(position).getRoomOccupantSex());
        holder.roomCardText4.setText(roomCardModels.get(position).getRoomType());
        holder.roomCardImageView.setImageBitmap(roomCardModels.get(position).getImageBitmap());
        holder.roomCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ServiceUserDetails.class);
                intent.putExtra("SERVICE_USER_ID", roomCardModels.get(position).getRoomServiceUserId());
                intent.putExtra("SERVICE_USER_KNOWN_AS", roomCardModels.get(position).getRoomOccupantKnownAs());
                intent.putExtra("ROOM_NUMBER", roomCardModels.get(position).getRoomNumber());
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return roomCardModels.size();
    }

    // Assign values to the variables on the card.
    public static class MyViewHolder extends RecyclerView.ViewHolder{

        CardView roomCardView;
        ImageView roomCardImageView;
        TextView roomCardText1, roomCardText2, roomCardText3, roomCardText4;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            roomCardImageView = itemView.findViewById(R.id.iv_roomCardImageView);
            roomCardText1 = itemView.findViewById(R.id.tv_roomCardOccupantName);
            roomCardText2 = itemView.findViewById(R.id.tv_roomCardRoomNumber);
            roomCardText3 = itemView.findViewById(R.id.tv_roomCardOccupantSex);
            roomCardText4 = itemView.findViewById(R.id.tv_suEndedOnText);
            roomCardView = itemView.findViewById(R.id.cv_roomCardView);
        }
    }
}
