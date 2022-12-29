package com.paliy.gymcounter_test_04;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.paliy.gymcounter_test_04.dbUtils.DBManager;


import java.sql.SQLException;
import java.util.Date;
import java.util.List;

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
        ImageButton menuBtn;
        ConstraintLayout cardConstrLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.tvTitle);
            count = itemView.findViewById(R.id.tvCount);
            addBtn = itemView.findViewById(R.id.btnAdd);
            menuBtn = itemView.findViewById(R.id.menuBtn);
            cardConstrLayout = itemView.findViewById(R.id.cardConstrLayout);

            cardConstrLayout.setOnLongClickListener(v -> {
                // TODO Auto-generated method stub
                onShowItemMenu(v);
                return true;
            });

            menuBtn.setOnClickListener(this::onShowItemMenu);
            addBtn.setOnClickListener(view -> {
                Log.println(Log.DEBUG,"TEST", String.valueOf(getAdapterPosition()));
                int currentVal = Integer.parseInt(count.getText().toString());
                count.setText(String.valueOf(currentVal+5));


                DBManager dbManager = new DBManager(addBtn.getContext());
                try {
                    dbManager.open();
                    //dbManager.updateCounter((String) title.getText(),5, new Date(), " " );

                    dbManager.updateCounterRaw((String) title.getText(),5, currentViewDate, " " );

                } catch (SQLException throwable) {
                    throwable.printStackTrace();
                }
                dbManager.close();
            });
        }


        @Override
        public void onClick(View v) {

        }

        public void onShowItemMenu(View view){
            Dialog mDialog = new Dialog(view.getContext());
            // Get the layout inflater
            LayoutInflater inflater =  LayoutInflater.from(view.getContext());//getLayoutInflater();
            // Inflate and set the layout for the dialog
            final View dialogView = inflater.inflate(R.layout.card_item_menu,null);
            Button deleteBtn = dialogView.findViewById(R.id.deleteBtn);
            Button editBtn = dialogView.findViewById(R.id.editTitleBtn);
            Button editApplyBtn = dialogView.findViewById(R.id.editApplyBtn);
            Button cancelBtn = dialogView.findViewById(R.id.cancelBtn);
            EditText editTitleText = dialogView.findViewById(R.id.titleEditText);

            mDialog.setContentView(dialogView);

            editBtn.setOnClickListener(view1 -> {
                //#ToDo edit title
                editBtn.setVisibility(View.INVISIBLE);
                editTitleText.setVisibility(View.VISIBLE);
                editApplyBtn.setVisibility(View.VISIBLE);

                editApplyBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view1) {

                    }
                });
            });

            deleteBtn.setOnClickListener(view12 -> {
                DBManager dbManager = new DBManager(addBtn.getContext());
                try {
                    dbManager.open();
                   //#ToDo add toaster deleted and refresh list
                   int res = dbManager.delete((String) title.getText(), currentViewDate);
                   if(res == 1){
                       Toast.makeText(view12.getContext(), title.getText() + " Deleted!", Toast.LENGTH_SHORT).show();
                       notifyDataSetChanged();
                   }


                } catch (SQLException throwable) {
                    throwable.printStackTrace();
                }
                dbManager.close();
            });

            cancelBtn.setOnClickListener(view13 -> {
                mDialog.cancel();
            });
            mDialog.show();
        }
    }
}
