package com.example.tm470_careapp;

import android.content.Context;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerViewAdapter_SUContact extends RecyclerView.Adapter<RecyclerViewAdapter_SUContact.MyViewHolder> {

    Context context;
    ArrayList<SUContactsCardModel> suContactsCardModels = new ArrayList<>();

    public RecyclerViewAdapter_SUContact(Context context, ArrayList<SUContactsCardModel> suContactsCardModels) {
        this.context = context;
        this.suContactsCardModels = suContactsCardModels;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter_SUContact.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create appearance of each row.
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycler_su_contact_card, parent, false);
        return new RecyclerViewAdapter_SUContact.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter_SUContact.MyViewHolder holder, int position) {
        // Assign values to view
        holder.suContactName.setText(suContactsCardModels.get(position).getSuContactName());
        holder.suContactType.setText(suContactsCardModels.get(position).getSuContactType());
        holder.suParentOrg.setText(suContactsCardModels.get(position).getSuContactParentOrg());
        holder.suContactNumberMain.setText(suContactsCardModels.get(position).getSuContactNumberMain());
        holder.suContactNumberWork.setText(suContactsCardModels.get(position).getSuContactNumberWork());
        holder.suContactNumberHome.setText(suContactsCardModels.get(position).getSuContactNumberHome());
        holder.suContactEmailMain.setText(suContactsCardModels.get(position).getSuContactEmailMain());
        holder.suContactEmailSecondary.setText(suContactsCardModels.get(position).getSuContactEmailSecondary());
        holder.suContactAddress.setText(suContactsCardModels.get(position).getSuContactAddress());
        holder.suContactExpandAddressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (suContactsCardModels.get(position).getAddressIsVisible()) {
                    TransitionManager.beginDelayedTransition(holder.suContactCardView, new AutoTransition());
                    holder.suContactExpandableAddressView.setVisibility(View.GONE);
                    suContactsCardModels.get(position).setAddressIsVisible(false);
                } else {
                    TransitionManager.beginDelayedTransition(holder.suContactCardView, new AutoTransition());
                    holder.suContactExpandableAddressView.setVisibility(View.VISIBLE);
                    suContactsCardModels.get(position).setAddressIsVisible(true);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return suContactsCardModels.size();
    }

    // Assign values to the variables on the card.
    public static class MyViewHolder extends RecyclerView.ViewHolder{

        Button suContactExpandAddressButton;
        CardView suContactCardView;
        TextView suContactName, suContactType, suParentOrg, suContactNumberMain, suContactNumberWork, suContactNumberHome, suContactEmailMain, suContactEmailSecondary, suContactAddress;
        ConstraintLayout suContactExpandableAddressView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            suContactName = itemView.findViewById(R.id.tv_suContactName);
            suContactType = itemView.findViewById(R.id.tv_suContactType);
            suParentOrg = itemView.findViewById(R.id.tv_suContactParentOrg);
            suContactNumberMain = itemView.findViewById(R.id.tv_suContactNumberMain);
            suContactNumberWork = itemView.findViewById(R.id.tv_suContactNumberWork);
            suContactNumberHome = itemView.findViewById(R.id.tv_suContactNumberHome);
            suContactEmailMain = itemView.findViewById(R.id.tv_suContactEmailMain);
            suContactEmailSecondary = itemView.findViewById(R.id.tv_suContactEmailSecondary);
            suContactAddress = itemView.findViewById(R.id.tv_suContacAddress);

            suContactCardView = itemView.findViewById(R.id.cv_suContactCardView);
            suContactExpandableAddressView = itemView.findViewById(R.id.cl_suContactExpandableAddressView);
            suContactExpandAddressButton = itemView.findViewById(R.id.b_suContactExpandAddressButton);
        }
    }
}
