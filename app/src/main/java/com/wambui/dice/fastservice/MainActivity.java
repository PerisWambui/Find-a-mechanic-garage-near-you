package com.wambui.dice.fastservice;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button Bmechanic,Bcustomer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bmechanic=(Button) findViewById(R.id.Amechanic);
        Bcustomer=(Button) findViewById(R.id.Acustomer);

        Bmechanic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,MechanicLoginActivity.class);
                startActivity(intent);
                finish();

            }

        });
        Bcustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,CustomerLoginActivity.class);
                startActivity(intent);
                finish();


            }
        });


    }
}