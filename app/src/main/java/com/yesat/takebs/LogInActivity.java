package com.yesat.takebs;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.onesignal.OSNotification;
import com.onesignal.OneSignal;

public class LogInActivity extends AppCompatActivity {
    
    private static final String TAG = "yernar";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Button login;
    private EditText etEmail;
    private EditText etPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.None)
                .setNotificationReceivedHandler(new OneSignal.NotificationReceivedHandler() {
                    @Override
                    public void notificationReceived(OSNotification notification) {

                    }
                })
                .autoPromptLocation(true)
                .init();

        final ProgressBar pb = (ProgressBar) findViewById(R.id.progressBar2);
        pb.getIndeterminateDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);
        etEmail = (EditText) findViewById(R.id.et_email_login);
        etPass = (EditText) findViewById(R.id.et_pass_login);
        setCheck(LogInActivity.this,etEmail,etPass);

        mAuth = FirebaseAuth.getInstance();
        try {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        }
        catch (DatabaseException ex){

        }
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {

                    OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
                        @Override
                        public void idsAvailable(String userId, String registrationId) {
                            Log.d(TAG, "User:" + userId);
                            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                            mDatabase.child("users").child(user.getUid()).child("oneSignalId").setValue(userId);
                            if (registrationId != null)
                                Log.d(TAG, "registrationId:" + registrationId);

                            Intent intent = new Intent(LogInActivity.this, MainActivity.class);
                            startActivityForResult(intent, 2);
                            Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                        }
                    });
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }

            }
        };
        findViewById(R.id.tv_sign_in)
            .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LogInActivity.this, SignUpActivity.class);
                startActivityForResult(intent, 1);
            }
        });
        login = (Button)findViewById(R.id.bt_login);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(check(LogInActivity.this,etEmail,etPass))return;
                String email = etEmail.getText().toString();
                String password = etPass.getText().toString();
                try {
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(LogInActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    login.setText("Login");
                                    pb.setVisibility(View.INVISIBLE);
                                    Log.d(TAG,"dismissed");
                                    Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                                    if (!task.isSuccessful()) {
                                        Log.w(TAG, "signInWithEmail", task.getException());
                                        Toast.makeText(LogInActivity.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                    login.setText("");
                    pb.setVisibility(View.VISIBLE);
                }catch (IllegalArgumentException ex){
                    Toast.makeText(LogInActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                    login.setText("Login");
                    pb.setVisibility(View.INVISIBLE);
                }

            }
        });
        findViewById(R.id.tv_for_pass).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(LogInActivity.this, R.style.myDialog));
                final EditText input = new EditText(LogInActivity.this);
                input.setHint("Email...");
                input.layout(20,0,20,0);
                builder.setView(input,50,0,50,0);
                builder.setTitle("Reset Password");
                builder.setNegativeButton("Cancel", null);
                builder.setPositiveButton("Reset", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String text = input.getText().toString();
                        try {
                            mAuth.sendPasswordResetEmail(text);
                        } catch (IllegalArgumentException e) {
                            Toast.makeText(LogInActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                builder.show();
            }
        });

    }
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }
    @Override
    public void onStop() {
        super.onStop();
        etEmail.setText("");
        etPass.setText("");
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            finish();
            return true;
        }
        return false;
    }
    public static boolean check(Context context,TextView... tvs){
        boolean tmp = false;
        for (TextView tv:tvs) {
            if (tv.length() == 0) {
                tv.getBackground().mutate().setColorFilter(context.getResources().getColor(R.color.your_color), PorterDuff.Mode.SRC_ATOP);
                tmp = true;
            } else {
                tv.getBackground().mutate().setColorFilter(context.getResources().getColor(R.color.your_color2), PorterDuff.Mode.SRC_ATOP);
            }
        }
        return tmp;
    }
    public static void setCheck(final Context context, TextView... tvs){
        for (final TextView tv:tvs) {
            tv.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if(hasFocus)return;
                    if(tv.length() == 0){
                        tv.getBackground().mutate().setColorFilter(context.getResources().getColor(R.color.your_color), PorterDuff.Mode.SRC_ATOP);
                    }else{
                        tv.getBackground().mutate().setColorFilter(context.getResources().getColor(R.color.your_color2), PorterDuff.Mode.SRC_ATOP);
                    }
                }
            });
        }
    }
}
