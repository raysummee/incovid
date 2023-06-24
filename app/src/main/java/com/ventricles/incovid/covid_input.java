package com.ventricles.incovid;

import android.animation.LayoutTransition;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * <p>A fragment that shows a list of items as a modal bottom sheet.</p>
 * <p>You can show this modal bottom sheet from your activity like this:</p>
 * <pre>
 *     covid_input.newInstance(30).show(getSupportFragmentManager(), "dialog");
 * </pre>
 */
public class covid_input extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_ITEM_COUNT = "item_count";
    boolean expanded = false;
    CardView cardView;
    float card_height;

    private FirebaseFirestore db;
    private SymptomsApiService symptomsApiService;

    LinearLayout linearLayout;

    Spinner spinner;
    EditText editRecentVisit;
    EditText editName;

    // TODO: Customize parameters
    public static covid_input newInstance(int itemCount) {
        final covid_input fragment = new covid_input();
        final Bundle args = new Bundle();
        args.putInt(ARG_ITEM_COUNT, itemCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private void sendPost() {
        try {

            Call<ResponseBody> call = symptomsApiService.sendPosts(
                    "\""+ editName.getText().toString()+"\"",
                    "\""+editRecentVisit.getText().toString()+"\"",
                    "\""+spinner.getItemAtPosition(spinner.getSelectedItemPosition()).toString()+"\""
            );
            assert call != null;
            Log.e("test", String.valueOf(call.request().toString()));
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        Log.e("testomg", response.body().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if(response.isSuccessful()){
                        Log.e("testomg", String.valueOf(response.code()));

                        Log.e("testinh", "success");
                        if (response.code() == 201) {
                            Toast.makeText(getContext(), "submitted and being reviewed", Toast.LENGTH_SHORT).show();
                            getActivity().onBackPressed();
                        } else {
                            Toast.makeText(getContext(), "failed! Please try again", Toast.LENGTH_SHORT).show();
                        }
                    }



                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.e("test", t.getLocalizedMessage());
                    Toast.makeText(getContext(), t.toString(), Toast.LENGTH_LONG).show();
                }

            });
        }catch (Exception e){
            Log.e("error",e.getMessage());
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_covid_input_list_dialog, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        db = FirebaseFirestore.getInstance();
        List<String> list_symptoms = Arrays.asList(getResources().getStringArray(R.array.symptoms));
        ImageButton btn_close = view.findViewById(R.id.close_btn_covid_input);
        linearLayout = view.findViewById(R.id.lay_covid_input);
        linearLayout.setTranslationX(-getResources().getDisplayMetrics().heightPixels);
        linearLayout.setAlpha(0f);
        linearLayout.setVisibility(View.VISIBLE);
        linearLayout.animate().setDuration(200).alpha(1f).translationX(0).start();
        linearLayout.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        editName = view.findViewById(R.id.name_covid_input);
        editRecentVisit = view.findViewById(R.id.recent_visit_covid_input);
        spinner = view.findViewById(R.id.spinner_symptom);
        if (FirebaseAuth.getInstance().getCurrentUser()!=null)
        editName.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());

        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        Button btn_submit = view.findViewById(R.id.btn_submit_symptoms);
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(editName.getText()) && !TextUtils.isEmpty(editRecentVisit.getText()) && spinner.getSelectedItemPosition()!=0) {
//                    Map<String, Object> userInput = new HashMap<>();
//                    Toast.makeText(getContext(), R.string.submiting_wait, Toast.LENGTH_SHORT).show();
//                    userInput.put("name",editName.getText().toString());
//                    userInput.put("last_place", editRecentVisit.getText().toString());
//                    userInput.put("symptoms", spinner.getItemAtPosition(spinner.getSelectedItemPosition()).toString());
//                    db.collection("userInput").document().set(userInput).addOnSuccessListener(new OnSuccessListener<Void>() {
//                        @Override
//                        public void onSuccess(Void aVoid) {
//                            Toast.makeText(getContext(), R.string.submitted_and_being_reviewed, Toast.LENGTH_SHORT).show();
//                            getActivity().onBackPressed();
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Toast.makeText(getContext(), R.string.submission_failed, Toast.LENGTH_SHORT).show();
//                        }
//                    });
                    Toast.makeText(getContext(), R.string.submiting_wait, Toast.LENGTH_SHORT).show();
                    Gson gson = new GsonBuilder()
                            .setLenient()
                            .create();
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl("https://hibernian-bill.000webhostapp.com")
                            .addConverterFactory(GsonConverterFactory.create(gson))
                            .build();
                    symptomsApiService = retrofit.create(SymptomsApiService.class);

                    sendPost();

                }else{
                    if (TextUtils.isEmpty(editName.getText()))
                        editName.setError(getString(R.string.enter_a_name));
                    if (TextUtils.isEmpty(editRecentVisit.getText()))
                        editRecentVisit.setError(getString(R.string.enter_recent_visit));
                    if (spinner.getSelectedItemPosition()==0) {
                        TextView error = (TextView) spinner.getSelectedView();
                        error.setError(getString(R.string.enter_symptoms));
                    }
                }
            }
        });
        ArrayAdapter<String> adapterSymptoms = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, list_symptoms){
            @Override
            public boolean isEnabled(int position) {
                if (position!=0){
                    return true;
                }else{
                    return false;
                }
            }
        };
        spinner.setAdapter(adapterSymptoms);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position!=0){
                    String selectedItem = parent.getItemAtPosition(position).toString();
                   // Toast.makeText(getContext(), selectedItem, Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }




}
