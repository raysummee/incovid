package com.ventricles.incovid;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {
    ImageView img_logo;
    TextView ___appName;
    TextView __logging_you_in;
    TextView ___just_a_moment;
    Button ___btnTryAgain;

    ProgressBar pbLogin;
    FirebaseAuth mAuth;
    GoogleSignInClient mGoogleSignInClient;
    RelativeLayout layout;
    static final int RC_SIGN_IN_GOOGLE = 9901;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        pbLogin = findViewById(R.id.pbar_login);
        img_logo = findViewById(R.id.logo_login);
        ___appName = findViewById(R.id.txtAppName);
        __logging_you_in = findViewById(R.id.Logging_you_in);
        ___just_a_moment = findViewById(R.id.just_a_moment);
        ___btnTryAgain = findViewById(R.id.btn_try_again_login);
        ___btnTryAgain.setAlpha(0f);
        ___btnTryAgain.setVisibility(View.VISIBLE);
        layout = findViewById(R.id.rel_lay_login);

        ___btnTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ___btnTryAgain.animate().setDuration(500).alpha(0f).start();
                signIn();
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);

        mAuth = FirebaseAuth.getInstance();
       animation();
    }

    public void animation(){
        ___appName.setAlpha(0f);
        ___appName.setVisibility(View.VISIBLE);
        __logging_you_in.setAlpha(0f);
        __logging_you_in.setVisibility(View.VISIBLE);
        __logging_you_in.animate().setDuration(0).translationYBy(500f).start();
        ___just_a_moment.setAlpha(0f);
        ___just_a_moment.setVisibility(View.VISIBLE);
        img_logo.setAlpha(0f);
        img_logo.setVisibility(View.VISIBLE);

        img_logo.animate().setDuration(500).scaleX(1.5f).scaleY(1.5f).translationYBy(500f).alpha(1f).withEndAction(new Runnable() {
            @Override
            public void run() {
                img_logo.animate().setStartDelay(2500).setDuration(500).scaleY(1f).scaleX(1f).translationYBy(-500f).start();
            }
        }).start();

        ___appName.animate().setStartDelay(500).setDuration(500).scaleX(1.2f).scaleY(1.2f).translationYBy(500f).alpha(1f).withEndAction(new Runnable() {
            @Override
            public void run() {
                ___appName.animate().setStartDelay(500).setDuration(500).alpha(0f).start();
                __logging_you_in.animate().setStartDelay(1000).setDuration(500).alpha(1f).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        __logging_you_in.animate().setStartDelay(500).setDuration(500).translationYBy(-500f).withStartAction(new Runnable() {
                            @Override
                            public void run() {
                                ___just_a_moment.animate().setStartDelay(500).setDuration(500).alpha(1f).start();
                                signIn();
                            }
                        }).start();
                    }
                }).start();
            }
        }).start();
    }


    private void signIn(){
        pbLogin.setVisibility(View.VISIBLE);
        Intent signinIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signinIntent, RC_SIGN_IN_GOOGLE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN_GOOGLE){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            if (task.isComplete())
                pbLogin.setVisibility(View.GONE);
            if (task.isCanceled()) {
                Toast.makeText(this, R.string.canceled_login, Toast.LENGTH_SHORT).show();
                pbLogin.setVisibility(View.GONE);
                ___btnTryAgain.animate().setDuration(500).alpha(1f).start();
            }
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                assert account != null;
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                e.printStackTrace();
                Toast.makeText(this, R.string.failed_to_login, Toast.LENGTH_SHORT).show();
                pbLogin.setVisibility(View.GONE);
                ___btnTryAgain.animate().setDuration(500).alpha(1f).start();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct){
        pbLogin.setVisibility(View.VISIBLE);
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(),null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Log.v("signin","completed");
                            if (mAuth.getCurrentUser()!=null) {
                                Toast.makeText(LoginActivity.this, getString(R.string.welcome)+mAuth.getCurrentUser().getDisplayName(), Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish();
                            }

                        }else{
                            Snackbar.make(layout, R.string.failed_to_login, Snackbar.LENGTH_LONG).show();
                            pbLogin.setVisibility(View.GONE);
                            ___btnTryAgain.animate().setDuration(500).alpha(1f).start();
                        }
                    }
                });
    }




}
