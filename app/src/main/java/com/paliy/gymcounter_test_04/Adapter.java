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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    List<String> titleList;
    List<String> countList;

    Date currentViewDate;
    LayoutInflater inflater;
    View.OnClickListener listener;

    public Adapter(Context ctx, List<String> title, List<String> count, View.OnClickListener listener) {
        this.listener = listener;
        this.countList = count;
        this.titleList = title;
        this.inflater = LayoutInflater.from(ctx);
    }

    public void setCurrentViewDate(Date date) {
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
        holder.titleTV.setText(titleList.get(position));
        holder.countTV.setText(countList.get(position));
        holder.titleTV.setOnClickListener(listener);
        changeAddBtnVisibility(holder);
    }

    @Override
    public int getItemCount() {
        return titleList.size();
    }

    public void changeAddBtnVisibility(ViewHolder holder) {
        if (!new Date().before(currentViewDate)) {
            holder.addBtn.setVisibility(View.GONE);
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        if (sdf.format(new Date()).equals(sdf.format(currentViewDate))) {
            holder.addBtn.setVisibility(View.VISIBLE);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView titleTV;
        TextView countTV;
        Button addBtn;
        ImageButton menuBtn;
        ConstraintLayout cardConstrLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTV = itemView.findViewById(R.id.tvTitle);
            countTV = itemView.findViewById(R.id.tvCount);
            addBtn = itemView.findViewById(R.id.btnAdd);
            menuBtn = itemView.findViewById(R.id.menuBtn);
            cardConstrLayout = itemView.findViewById(R.id.cardConstrLayout);

            cardConstrLayout.setOnLongClickListener(v -> {
                onShowItemMenu(v);
                return true;
            });

            menuBtn.setOnClickListener(this::onShowItemMenu);
            addBtn.setOnClickListener(view -> {
                Log.println(Log.DEBUG, "TEST", String.valueOf(getAdapterPosition()));
                int currentVal = Integer.parseInt(countTV.getText().toString());
                countTV.setText(String.valueOf(currentVal + 5));

                DBManager dbManager = new DBManager(addBtn.getContext());
                try {
                    dbManager.open();
                    dbManager.updateCounterRaw((String) titleTV.getText(), 5, currentViewDate, " ");
                } catch (SQLException throwable) {
                    throwable.printStackTrace();
                }
                dbManager.close();
            });
        }

        @Override
        public void onClick(View v) {

        }

        public void onShowItemMenu(View view) {
            Context ctx = view.getContext();
            Dialog mDialog = new Dialog(view.getContext());
            // Get the layout inflater
            LayoutInflater inflater = LayoutInflater.from(view.getContext());
            // Inflate and set the layout for the dialog
            final View dialogView = inflater.inflate(R.layout.card_item_menu, null);
            Button deleteBtn = dialogView.findViewById(R.id.deleteBtn);
            Button editBtn = dialogView.findViewById(R.id.editTitleBtn);
            Button editApplyBtn = dialogView.findViewById(R.id.editApplyBtn);
            Button cancelBtn = dialogView.findViewById(R.id.cancelBtn);
            EditText editTitleText = dialogView.findViewById(R.id.titleEditText);

            String mTitleTV = (String) titleTV.getText();

            mDialog.setContentView(dialogView);

            editBtn.setOnClickListener(view1 -> {
                //#ToDo edit title
                editBtn.setVisibility(View.INVISIBLE);
                editTitleText.setVisibility(View.VISIBLE);
                editApplyBtn.setVisibility(View.VISIBLE);

                editTitleText.setText(mTitleTV);

                editApplyBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view1) {
                        DBManager dbManager = new DBManager(ctx);
                        try {
                            dbManager.open();
                            String newString = editTitleText.getText().toString();
                            boolean res = dbManager.updateTitleRaw((String) titleTV.getText(), newString, currentViewDate);
                            if (res) {
                                Toast.makeText(view1.getContext(), newString + " Edited!", Toast.LENGTH_SHORT).show();
                                titleList.set(titleList.indexOf(mTitleTV), newString);
                                notifyDataSetChanged();
                                mDialog.cancel();
                            }
                        } catch (SQLException throwable) {
                            throwable.printStackTrace();
                        }
                        dbManager.close();
                    }
                });
            });

            deleteBtn.setOnClickListener(view12 -> {
                DBManager dbManager = new DBManager(ctx);
                try {
                    dbManager.open();
                    int res = dbManager.delete((String) titleTV.getText(), currentViewDate);
                    if (res == 1) {
                        Toast.makeText(view12.getContext(), mTitleTV + " Deleted!", Toast.LENGTH_SHORT).show();
                        titleList.remove(mTitleTV);
                        notifyDataSetChanged();
                        mDialog.cancel();
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
