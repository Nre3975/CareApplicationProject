package com.example.tm470_careapp;

import android.content.Context;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class RecyclerViewAdapter_SUA extends RecyclerView.Adapter<RecyclerViewAdapter_SUA.MyViewHolder> {

    Context context;
    ArrayList<SUAllergyCardModel> suAllergyCardModels = new ArrayList<>();

    public RecyclerViewAdapter_SUA(Context context, ArrayList<SUAllergyCardModel> suAllergyCardModels) {
        this.context = context;
        this.suAllergyCardModels = suAllergyCardModels;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter_SUA.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create appearance of each row.
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycler_su_allergy_card, parent, false);
        return new RecyclerViewAdapter_SUA.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter_SUA.MyViewHolder holder, int position) {
        // Assign values to view
        holder.suAllergyTypeName.setText(suAllergyCardModels.get(position).getSuAllergyType());
        holder.suAllergyInformation.setText(suAllergyCardModels.get(position).getSuAllergyCategory());
        holder.suDiagnosedOnDate.setText(suAllergyCardModels.get(position).getSuAllergyDateDiagnosed());
        holder.suAllergyEndedOnDate.setText(suAllergyCardModels.get(position).getSuAllergyDateEnded());
        holder.suAllergyCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (suAllergyCardModels.get(position).getIsVisible()) {
                    TransitionManager.beginDelayedTransition(holder.suAllergyCardView, new AutoTransition());
                    holder.suAllergyExpandableView.setVisibility(View.GONE);
                    suAllergyCardModels.get(position).setIsVisible(false);
                } else {
                    TransitionManager.beginDelayedTransition(holder.suAllergyCardView, new AutoTransition());
                    holder.suAllergyExpandableView.setVisibility(View.VISIBLE);
                    suAllergyCardModels.get(position).setIsVisible(true);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return suAllergyCardModels.size();
    }

    // Assign values to the variables on the card.
    public static class MyViewHolder extends RecyclerView.ViewHolder{

        CardView suAllergyCardView;
        TextView suAllergyTypeName, suAllergyInformation, suDiagnosedOnDate, suAllergyEndedOnDate;
        ConstraintLayout suAllergyExpandableView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            suAllergyTypeName = itemView.findViewById(R.id.tv_suAllergyTypeName);
            suAllergyInformation = itemView.findViewById(R.id.tv_suAllergyInformation);
            suDiagnosedOnDate = itemView.findViewById(R.id.tv_suDiagnosedOnDate);
            suAllergyEndedOnDate = itemView.findViewById(R.id.tv_suAllergyEndedOnDate);
            suAllergyCardView = itemView.findViewById(R.id.cv_suAllergyCardView);
            suAllergyExpandableView = itemView.findViewById(R.id.cl_suAllergyExpandableView);
        }
    }
}
