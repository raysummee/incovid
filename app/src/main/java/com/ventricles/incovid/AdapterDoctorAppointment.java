package com.ventricles.incovid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AdapterDoctorAppointment extends RecyclerView.Adapter<AdapterDoctorAppointment.ViewHolderDoctorAppointment> {
    Context context;
    private LayoutInflater inflater;
    private List<modelDoctorAppointment> list;

    public AdapterDoctorAppointment(Context context, List<modelDoctorAppointment> list){
        this.context = context;
        this.list = list;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolderDoctorAppointment onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_rounded_card_doctor_counselling, parent,false);
        return new AdapterDoctorAppointment.ViewHolderDoctorAppointment(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderDoctorAppointment holder, int position) {
        modelDoctorAppointment modelDoctorAppointment = list.get(position);
        holder.name.setText(modelDoctorAppointment.name);
        holder.contact.setText(modelDoctorAppointment.contact);
        String specialisation = "Specialisation"+modelDoctorAppointment.specialisation;
        holder.specialisation.setText(specialisation);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolderDoctorAppointment extends RecyclerView.ViewHolder{
        TextView name;
        TextView specialisation;
        TextView contact;
        public ViewHolderDoctorAppointment(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.txtTitleDoctorCounselling);
            specialisation = itemView.findViewById(R.id.txtSubTitleDoctorCounselling);
            contact = itemView.findViewById(R.id.txtMoreDoctorCounselling);
        }
    }
}
