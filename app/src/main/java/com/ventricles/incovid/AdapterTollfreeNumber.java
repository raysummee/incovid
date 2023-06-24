package com.ventricles.incovid;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AdapterTollfreeNumber extends RecyclerView.Adapter<AdapterTollfreeNumber.ViewholderTollfreeNumber> {

    private Context context;
    private LayoutInflater inflater;
    private List<Model_toll_free_no> list;

    public AdapterTollfreeNumber(Context context, List<Model_toll_free_no> list) {
        this.context = context;
        this.list = list;
        this.inflater = LayoutInflater.from(context);
    }

    public void refresh_list(List<Model_toll_free_no> model_toll_free_nos){
        this.list = model_toll_free_nos;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewholderTollfreeNumber onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_number, parent,false);
        return new ViewholderTollfreeNumber(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewholderTollfreeNumber holder, int position) {
        final Model_toll_free_no model_toll_free_no = list.get(position);
        holder.txt_header.setText(model_toll_free_no.getCall_name());
        holder.txt_sub_header.setText(String.valueOf(model_toll_free_no.getCall_no()));
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+model_toll_free_no.getCall_no()));
                context.startActivity(intent);
            }
        });
        holder.layout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipboardManager clipboardManager  = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("call_number", String.valueOf(model_toll_free_no.getCall_no()));
                assert clipboardManager != null;
                clipboardManager.setPrimaryClip(clipData);
                Toast.makeText(context, R.string.phone_number_copied, Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    protected class ViewholderTollfreeNumber extends RecyclerView.ViewHolder{
        TextView txt_header, txt_sub_header;
        RelativeLayout layout;


        public ViewholderTollfreeNumber(@NonNull View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.lay_rel_list_view_items);
            txt_header = itemView.findViewById(R.id.txt_header_list_view_details);
            txt_sub_header = itemView.findViewById(R.id.txt_sub_header_list_view_details);
        }
    }
}
