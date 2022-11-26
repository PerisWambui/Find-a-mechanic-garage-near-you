package com.wambui.dice.fastservice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.data.DataBufferSafeParcelable;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FeebackActivity extends AppCompatActivity {

    private Button SendFeedbtn;
    DatabaseReference databaseReference;

    FeedbackInfo feedbackInfo;
    FirebaseDatabase firebaseDatabase;


    private EditText feeeed;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feeback);

        feeeed=findViewById(R.id.feedback);
        firebaseDatabase = FirebaseDatabase.getInstance();

        databaseReference = firebaseDatabase.getReference("FeedbackInfo");

        feedbackInfo=new FeedbackInfo();

        SendFeedbtn=findViewById(R.id.feedback_button);

        SendFeedbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String details=feeeed.getText().toString();

                if(TextUtils.isEmpty((details))) {

                    Toast.makeText(FeebackActivity.this, "Please Give Somefeedback", Toast.LENGTH_SHORT).show();
                }
                  else  {
                      addDatatoFirebase(details);
                }
                }

            private void addDatatoFirebase(String details) {
                feedbackInfo.setFeedbackDetails(details);

                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        databaseReference.setValue(feedbackInfo);

                        Toast.makeText(FeebackActivity.this, "Feedback Sent", Toast.LENGTH_SHORT).show();
                    }


                    @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // if the data is not added or it is cancelled then
                            // we are displaying a failure toast message.
                            Toast.makeText(FeebackActivity.this, "Opps there is nothing " + error, Toast.LENGTH_SHORT).show();
                        }

        });








    }
});}}