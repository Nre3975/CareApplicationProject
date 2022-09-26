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

public class RecyclerViewAdapter_SUHMC extends RecyclerView.Adapter<RecyclerViewAdapter_SUHMC.MyViewHolder> {

    Context context;
    ArrayList<SUMedicalConditionCardModel> suCHistoricMedicalCondCardModels = new ArrayList<>();

    public RecyclerViewAdapter_SUHMC(Context context, ArrayList<SUMedicalConditionCardModel> suCHistoricMedicalCondCardModels) {
        this.context = context;
        this.suCHistoricMedicalCondCardModels = suCHistoricMedicalCondCardModels;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter_SUHMC.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create appearance of each row.
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycler_su_medical_card, parent, false);
        return new RecyclerViewAdapter_SUHMC.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter_SUHMC.MyViewHolder holder, int position) {
        // Assign values to view
        holder.suMedicalTypeName.setText(suCHistoricMedicalCondCardModels.get(position).getSUMedicalCondition());
        holder.suMedicalInformation.setText(suCHistoricMedicalCondCardModels.get(position).getSUMedicalConditionCategory());
        holder.suMedDiagnosedOnDate.setText(suCHistoricMedicalCondCardModels.get(position).getSUMedicalConditionDateDiagnosed());
        holder.suMedicalEndedOnDate.setText(suCHistoricMedicalCondCardModels.get(position).getSUMedicalConditionDateEnded());
        holder.suMedicalCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (suCHistoricMedicalCondCardModels.get(position).getIsVisible()) {
                    TransitionManager.beginDelayedTransition(holder.suMedicalCardView, new AutoTransition());
                    holder.suMedicalExpandableView.setVisibility(View.GONE);
                    suCHistoricMedicalCondCardModels.get(position).setIsVisible(false);
                } else {
                    TransitionManager.beginDelayedTransition(holder.suMedicalCardView, new AutoTransition());
                    holder.suMedicalExpandableView.setVisibility(View.VISIBLE);
                    suCHistoricMedicalCondCardModels.get(position).setIsVisible(true);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return suCHistoricMedicalCondCardModels.size();
    }

    // Assign values to the variables on the card.
    public static class MyViewHolder extends RecyclerView.ViewHolder{

        CardView suMedicalCardView;
        TextView suMedicalTypeName, suMedicalInformation, suMedDiagnosedOnDate, suMedicalEndedOnDate;
        ConstraintLayout suMedicalExpandableView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            suMedicalTypeName = itemView.findViewById(R.id.tv_suMedicalTypeName);
            suMedicalInformation = itemView.findViewById(R.id.tv_suMedicalInformation);
            suMedDiagnosedOnDate = itemView.findViewById(R.id.tv_suMedDiagnosedOnDate);
            suMedicalEndedOnDate = itemView.findViewById(R.id.tv_suMedicalEndedOnDate);
            suMedicalCardView = itemView.findViewById(R.id.cv_suMedicalCardView);
            suMedicalExpandableView = itemView.findViewById(R.id.cl_suMedicalExpandableView);
        }
    }
}
