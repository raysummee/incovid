package com.ventricles.incovid;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class ActivityTollFreeNo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actvity_toll_free_no);
        List<Model_toll_free_no> list = new ArrayList<>();
        RecyclerView recyclerView = findViewById(R.id.Recycler_tollfree_no);
        final AdapterTollfreeNumber adapterTollfreeNumber = new AdapterTollfreeNumber(this, list);
        recyclerView.setAdapter(adapterTollfreeNumber);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManagerVertical = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManagerVertical);

        __repository repository = new __repository(this);
        repository.gettollfreeno().observe(this, new Observer<List<Model_toll_free_no>>() {
            @Override
            public void onChanged(List<Model_toll_free_no> model_toll_free_nos) {
                adapterTollfreeNumber.refresh_list(model_toll_free_nos);
                Log.v("testing","read_toll");
            }
        });

    }
}
