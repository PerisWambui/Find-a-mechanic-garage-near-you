package com.wambui.dice.fastservice;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CustomeExpandablelistAdapter extends RecyclerView.Adapter<CustomeExpandablelistAdapter.CustomeExpandableListViewHolder> {

    private List<ModelClass>userList;
    public CustomeExpandablelistAdapter(List<ModelClass>userList){
        this.userList=userList;

    }


    @NonNull
    @Override
    public CustomeExpandableListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.list_group,parent,false);
        return new CustomeExpandableListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomeExpandableListViewHolder holder, int position) {
        int resource=userList.get(position).getImageView();
        String text1=userList.get(position).getTextView1();
        String text2=userList.get(position).getTextView2();

        holder.setData(resource,text1,text2);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class CustomeExpandableListViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView textView1;
        private TextView textView2;
        public CustomeExpandableListViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.happpy);
            textView1=itemView.findViewById(R.id.txtGroupName);
            textView2=itemView.findViewById(R.id.txtGroupLogo);




        }

        public void setData(int resource, String text1, String text2) {
            imageView.setImageResource(resource);
            textView1.setText(text1);
            textView2.setText(text2);

        }
    }
}




