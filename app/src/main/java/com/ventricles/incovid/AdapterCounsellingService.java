package com.ventricles.incovid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AdapterCounsellingService extends RecyclerView.Adapter<AdapterCounsellingService.ViewHolderCounsellingService> {
    Context context;
    private LayoutInflater inflater;
    private List<modelCounsellingService> list;

    public AdapterCounsellingService(Context context, List<modelCounsellingService> list){
        this.context = context;
        this.list = list;
        this.inflater = LayoutInflater.from(context);
    }
    @NonNull
    @Override
    public ViewHolderCounsellingService onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_rounded_card_doctor_counselling, parent,false);
        return new AdapterCounsellingService.ViewHolderCounsellingService(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderCounsellingService holder, int position) {
        modelCounsellingService modelCounsellingService = list.get(position);
        holder.org.setText(modelCounsellingService.org);
        holder.natureSupport.setText(modelCounsellingService.natureOfSupport);
        holder.contact.setText(modelCounsellingService.contact);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class  ViewHolderCounsellingService extends RecyclerView.ViewHolder{
        TextView org;
        TextView natureSupport;
        TextView contact;
        public ViewHolderCounsellingService(@NonNull View itemView) {
            super(itemView);
            org = itemView.findViewById(R.id.txtTitleDoctorCounselling);
            natureSupport = itemView.findViewById(R.id.txtSubTitleDoctorCounselling);
            contact = itemView.findViewById(R.id.txtMoreDoctorCounselling);
        }
    }
}
