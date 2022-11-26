package com.wambui.dice.fastservice;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.ColorSpace;
import android.os.Bundle;
import android.widget.Adapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Garage2Activity extends AppCompatActivity {
        RecyclerView mrecyclerView;
        LinearLayoutManager layoutManager;
        List<ModelClass>userList;
        Adapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_garage2);
        
        intData();
        initRecyclerView();
        
    }

    private void initRecyclerView() {
        mrecyclerView=findViewById(R.id.recylerView);
        layoutManager=new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        mrecyclerView.setLayoutManager(layoutManager);
        CustomeExpandablelistAdapter CustomeExpandablelistAdapteradapter = new CustomeExpandablelistAdapter(userList);
        mrecyclerView.setAdapter(CustomeExpandablelistAdapteradapter);
        //adapter.notify();
        CustomeExpandablelistAdapteradapter.notifyDataSetChanged();
    }

    private void intData() {
        userList =new ArrayList<>();

        userList.add(new ModelClass(R.drawable.logo1,"Happy Garage","Contact:011111111,located in Nairobi"));
        userList.add(new ModelClass(R.drawable.logo5,"Grace Garage","Contact:011111111,located in Nakuru"));
        userList.add(new ModelClass(R.drawable.logo3,"Happy Garage","Contact:022222222,located in Mombasa"));
        userList.add(new ModelClass(R.drawable.logo4,"Happy Garage","Contact:033333333,located in Kakamega"));
        userList.add(new ModelClass(R.drawable.logo2,"Happy Garage","Contact:044444444,located in Kisii"));
        userList.add(new ModelClass(R.drawable.logo5,"Happy Garage","Contact:044444444,located in Maua"));
        userList.add(new ModelClass(R.drawable.logo2,"Happy Garage","Contact:0666666666,located in Nakuru"));
        userList.add(new ModelClass(R.drawable.logo3,"Happy Garage","Contact:07777777777,located in Maua"));


    }


}
