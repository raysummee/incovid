package com.ventricles.incovid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.widget.TextViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class AdapterRecyclerQuickViews extends RecyclerView.Adapter<AdapterRecyclerQuickViews.QuickViewHolder> {
    private Context context;
    private LayoutInflater inflater;
    private List<Model_red_zone> list;

    public void refresh_list(List<Model_red_zone>  ___red_zone_list){
        list = new ArrayList<>();
        list.addAll(___red_zone_list);
        notifyDataSetChanged();
    }
    public AdapterRecyclerQuickViews(Context context, List<Model_red_zone> list) {
        this.context = context;
        this.list = list;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public QuickViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_quick_views,parent,false);
        return new QuickViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuickViewHolder holder, int position) {
        final Model_red_zone __red_zone = list.get(position);
            if (position==getItemCount()-1){
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(0,0,0,0);
                holder.cardView.setLayoutParams(layoutParams);
            }else{
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                float dpi = context.getResources().getDisplayMetrics().density;
                layoutParams.setMargins(0,0, (int) (10*dpi),0);
                holder.cardView.setLayoutParams(layoutParams);
            }
            holder.textView.setText(__red_zone.getPlaceName());
            TextViewCompat.setAutoSizeTextTypeWithDefaults(holder.textView, TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM);
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((MainActivity)context).__update_camera(new LatLng(__red_zone.getLat(),__red_zone.getLon()));
                }
            });
    }

    @Override
    public int getItemCount() {
        if (list!=null)
        return list.size();
        else
            return 0;
    }

    public class QuickViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        TextView textView;

        public QuickViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_quick_view_items);
            textView = itemView.findViewById(R.id.place_name_quick_view_item);
        }
    }
}
