package com.paliy.gymcounter_test_04;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.paliy.gymcounter_test_04.dbUtils.DBManager;


import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    List<String> title;
    List<String> count;
    Button Add;

    public Date currentViewDate;
    LayoutInflater inflater;

    View.OnClickListener listener;

    public Adapter(Context ctx, List<String> title, List<String> count, View.OnClickListener listener){
        this.listener = listener;
        this.count = count;
        this.title = title;

        this.inflater = LayoutInflater.from(ctx);

    }


    public void addItem(String title, String count){
        this.title.add(title);
        this.count.add(count);
    }

    public void addItems(List<String> titles, List<String> counts){
        this.title.addAll(titles);
        this.count.addAll(counts);
    }

    public void setCutterViewDate(Date date){
        currentViewDate = date;
    }

    View mainView;
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.activity_main2, parent, false);
        mainView = view;
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.title.setText(title.get(position));
        holder.count.setText(count.get(position));
        holder.title.setOnClickListener(listener);
    }


    @Override
    public int getItemCount() {
        return title.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        TextView title;
        TextView count;
        Button addBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.tvTitle);
            count = itemView.findViewById(R.id.tvCount);
            addBtn = itemView.findViewById(R.id.btnAdd);
            addBtn.setOnClickListener(this);
        }
//        public void bind(String item, final View.OnClickListener listener) {
//
//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override public void onClick(View v) {
//                    listener.onItemClick(item);
//                }
//            });
//        }

        @Override
        public void onClick(View v) {
            Log.println(Log.DEBUG,"TEST", String.valueOf(getAdapterPosition()));
            int currentVal = Integer.parseInt(count.getText().toString());
            count.setText(String.valueOf(currentVal+5));

         //   Date date;

            DBManager dbManager = new DBManager(addBtn.getContext());
            try {
                dbManager.open();
                //dbManager.updateCounter((String) title.getText(),5, new Date(), " " );

                dbManager.updateCounterRaw((String) title.getText(),5, currentViewDate, " " );

            } catch (SQLException throwable) {
                throwable.printStackTrace();
            }



        }
    }
}
