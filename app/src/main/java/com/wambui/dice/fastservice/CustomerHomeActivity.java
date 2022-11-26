package com.wambui.dice.fastservice;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class CustomerHomeActivity extends AppCompatActivity {

    private ImageView callBtn,ssearch,ppayment,exlist ,feed;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_home);


        feed=findViewById(R.id.ffeedback);
        callBtn=findViewById(R.id.imageView3);
        exlist=findViewById(R.id.garage);
        ssearch=findViewById(R.id.searchmech);
        ppayment=findViewById(R.id.payment);
        callBtn.setOnClickListener(view -> {
            Intent callingIntent=new Intent(Intent.ACTION_DIAL);
            callingIntent.setData(Uri.parse("tel:+2541234567"));
            startActivity(callingIntent);
        });
        ssearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CustomerHomeActivity.this, CustomerMapActivity.class);
                startActivity(intent);
                finish();
            }
        });

             ppayment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(CustomerHomeActivity.this, PaymentActivity.class);
                    startActivity(intent);
                    finish();
                }
        });

        exlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CustomerHomeActivity.this, Garage2Activity.class);
                startActivity(intent);
                finish();
            }
        });

        feed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CustomerHomeActivity.this, FeebackActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}