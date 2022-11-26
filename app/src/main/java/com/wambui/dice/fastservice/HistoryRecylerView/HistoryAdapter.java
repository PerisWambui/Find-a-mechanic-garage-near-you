package com.wambui.dice.fastservice.HistoryRecylerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.recyclerview.widget.RecyclerView;

import com.wambui.dice.fastservice.R;

import java.util.ArrayList;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {
    private List<HistoryObject> itemList;
    private Context context;


    public HistoryAdapter(List<HistoryObject> itemList, Context context) {
        this.itemList = itemList;
        this.context = context;
    }

    @Override
    public HistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
        return new HistoryViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        holder.serviceId.setText(itemList.get(position).getServiceId());
        if(itemList.get(position).getTime()!=null){
            holder.time.setText(itemList.get(position).getTime());
        }
    }

    //getting it to reload Items






    @Override
    public int getItemCount() {
        return this.itemList.size();
    }

    public class HistoryViewHolder extends RecyclerView.ViewHolder{
        public TextView serviceId;
        public TextView time;

        public HistoryViewHolder(View layoutView) {
            super(layoutView);
            time=layoutView.findViewById(R.id.time);
            serviceId=layoutView.findViewById(R.id.serviceId);
        }
    }
}