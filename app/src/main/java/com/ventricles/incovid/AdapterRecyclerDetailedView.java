package com.ventricles.incovid;

import android.animation.LayoutTransition;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class AdapterRecyclerDetailedView extends RecyclerView.Adapter<AdapterRecyclerDetailedView.viewHolderRecyclerDetailed> {
    private Context context;
    private LayoutInflater inflater;
    private List<ModelDetailedPlace> list;

    public void refreshList(List<ModelDetailedPlace> list){
        this.list = list;
        notifyDataSetChanged();
    }

    public AdapterRecyclerDetailedView(Context context, List<ModelDetailedPlace> list) {
        this.context = context;
        this.list = list;
        this.inflater = LayoutInflater.from(context);
    }


    @NonNull
    @Override
    public viewHolderRecyclerDetailed onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_view_items, parent,false);
        return new viewHolderRecyclerDetailed(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final viewHolderRecyclerDetailed holder, int position) {
       final ModelDetailedPlace modelDetailedPlace = list.get(position);
        holder.txt_header.setText(modelDetailedPlace.placename);
        holder.txt_sub_header.setText(modelDetailedPlace.placeAddress);
        holder.layout.setVisibility(View.VISIBLE);
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
                assert inputMethodManager != null;
                inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(),0);
                ((MapsActivity)context).__add_search_marker(new LatLng(modelDetailedPlace.lat,modelDetailedPlace.lon));
                ((MapsActivity)context).close_expanded_card();


            }
        });

    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolderRecyclerDetailed extends RecyclerView.ViewHolder{
        RelativeLayout layout;
        TextView txt_header;
        TextView txt_sub_header;
        public viewHolderRecyclerDetailed(@NonNull View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.lay_rel_list_view_items);
            txt_header = itemView.findViewById(R.id.txt_header_list_view_details);
            txt_sub_header = itemView.findViewById(R.id.txt_sub_header_list_view_details);
        }
    }
}
