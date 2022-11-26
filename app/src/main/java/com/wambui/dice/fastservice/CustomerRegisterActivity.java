package com.wambui.dice.fastservice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class CustomerRegisterActivity extends AppCompatActivity {

    private EditText mEmail, mPassword;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private Button mLogin, msignup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_register);



        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        mLogin = findViewById(R.id.login);
        msignup = findViewById(R.id.verify);


        mAuth = FirebaseAuth.getInstance();

        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference().child("Customers");

        msignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = mEmail.getText().toString().trim();
                String pass = mPassword.getText().toString().trim();

                if (user.isEmpty()){
                    mEmail.setError("Email cannot be empty");
                }
                if (pass.isEmpty()){
                    mPassword.setError("Password cannot be empty");
                } else{
                    mAuth.createUserWithEmailAndPassword(user, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {


                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (!task.isSuccessful()) {
                                Toast.makeText(CustomerRegisterActivity.this, "Sign Up Error", Toast.LENGTH_SHORT).show();

                            } else {
                                Map<String, Object> map=new HashMap<>();
                                map.put("email",user);
                                if(mAuth.getCurrentUser()!=null) {
                                    map.put("userId", mAuth.getCurrentUser().getUid());
                                }

                                databaseReference.child(mAuth.getCurrentUser().getUid()).setValue(map).addOnCompleteListener(new OnCompleteListener() {

                                    @Override
                                    public void onComplete(@NonNull Task task) {
                                        if(task.isSuccessful()){

                                        }
                                    }
                                });
                                mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {


                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(CustomerRegisterActivity.this, "Registered Successfully ! Please, check your Email for verification.", Toast.LENGTH_SHORT).show();

                                        }
                                        else{
                                            Toast.makeText(CustomerRegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                });


                            }
                        }
                    });                }

            }
        });

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomerRegisterActivity.this, CustomerLoginActivity.class);
                startActivity(intent);
                return;

            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
//        mAuth.addAuthStateListener(firebaseAuthListener);
    }


    @Override
    protected void onStop() {
        super.onStop();
//        mAuth.removeAuthStateListener(firebaseAuthListener);
    }



}
