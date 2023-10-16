package com.paliy.gymcounter_test_04;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.paliy.gymcounter_test_04.dbUtils.DBManager;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    public static final String DESCRIPTION_FIELD = "description";
    List<String> titleList;
    List<String> countList;
    Date currentViewDate;
    LayoutInflater inflater;
    View.OnClickListener listener;
    View mainView;
    FragmentManager fragmentManager;

    public Adapter(Context ctx, List<String> title, List<String> count, View.OnClickListener listener, FragmentManager fragmentManager) {
        this.listener = listener;
        this.countList = count;
        this.titleList = title;
        this.inflater = LayoutInflater.from(ctx);
        this.fragmentManager = fragmentManager;
    }

    public void setCurrentViewDate(Date date) {
        currentViewDate = date;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.card_item_activity, parent, false);
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
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        if (sdf.format(new Date()).equals(sdf.format(currentViewDate))) {
            holder.addBtn.setVisibility(View.VISIBLE);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, NumberPicker.OnValueChangeListener {
        final PopupScreen popupScreen = new PopupScreen();
        TextView titleTV;
        TextView countTV;
        Button addBtn;
        ImageButton menuBtn;
        ConstraintLayout cardConstrLayout;
        TextView infoTV;
        ImageButton countBtn;
        View.OnClickListener onAutoCountClick = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("count click = " + titleTV.getText());
                popupScreen.show(fragmentManager, String.valueOf(getAdapterPosition()));
                popupScreen.setOnClickHandler((action, countValue) -> {
                    int currentVal = Integer.parseInt(countTV.getText().toString());
                    DBManager dbManager = new DBManager(view.getContext());
                    try {
                        dbManager.open();
                        dbManager.updateAddCounterRaw((String) titleTV.getText(), countValue, currentViewDate);
                    } catch (SQLException throwable) {
                        throwable.printStackTrace();
                    }
                    dbManager.close();
                    countTV.setText(String.valueOf(currentVal + countValue));
                    System.out.println("Handler");
                });
            }
        };
        View.OnClickListener addBtnOnClick = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.println(Log.DEBUG, "TEST", String.valueOf(getAdapterPosition()));
                int currentVal = Integer.parseInt(countTV.getText().toString());
                DBManager dbManager = new DBManager(addBtn.getContext());
                try {
                    dbManager.open();
                    dbManager.updateAddCounterRaw((String) titleTV.getText(), StaticData.PLUS_VALUE, currentViewDate);
                } catch (SQLException throwable) {
                    throwable.printStackTrace();
                }
                dbManager.close();
                countTV.setText(String.valueOf(currentVal + StaticData.PLUS_VALUE));
            }
        };

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTV = itemView.findViewById(R.id.tvTitle);
            countTV = itemView.findViewById(R.id.tvCount);
            addBtn = itemView.findViewById(R.id.btnAdd);
            menuBtn = itemView.findViewById(R.id.menuBtn);
            cardConstrLayout = itemView.findViewById(R.id.cardConstrLayout);
            infoTV = itemView.findViewById(R.id.infoTV);
            countBtn = itemView.findViewById(R.id.countImBtn);

            //#Todo better to rewrite onAutoCountClick counter. current code snipped were written just for tests.
            countBtn.setOnClickListener(onAutoCountClick);
            cardConstrLayout.setOnLongClickListener(v -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    showNumberPicker();
                }
                return true;
            });
            menuBtn.setOnClickListener(this::onShowCardMenu);
            addBtn.setOnClickListener(addBtnOnClick);
        }

        @RequiresApi(api = Build.VERSION_CODES.Q)
        public void showNumberPicker() {
            final Dialog d = new Dialog(this.countTV.getContext());
            d.setTitle("NumberPicker");
            d.setContentView(R.layout.num_picker_dialog);
            Button b1 = d.findViewById(R.id.button1);
            Button b2 = d.findViewById(R.id.button2);
            NumberPicker np = d.findViewById(R.id.numberPicker1);
            np.setTextColor(Color.WHITE);
            np.setTextSize(80);
            np.setMaxValue(1000);
            np.setMinValue(0);
            np.setValue(Integer.parseInt(countTV.getText().toString()));
            np.setWrapSelectorWheel(false);
            np.setOnValueChangedListener(this);
            b1.setOnClickListener(v -> {
                int addToCounter = np.getValue();
                countTV.setText(String.valueOf(addToCounter));
                DBManager dbManager = new DBManager(addBtn.getContext());
                try {
                    dbManager.open();
                    dbManager.updateSetCounterRaw((String) titleTV.getText(), addToCounter, currentViewDate, " ");
                } catch (SQLException throwable) {
                    throwable.printStackTrace();
                }
                dbManager.close();
                d.dismiss();
            });
            b2.setOnClickListener(v -> d.dismiss());
            d.show();
        }

        @Override
        public void onValueChange(NumberPicker numberPicker, int i, int i1) {

        }

        @Override
        public void onClick(View v) {

        }

        public void onShowCardMenu(View view) {
            Context ctx = view.getContext();
            Dialog mDialog = new Dialog(view.getContext());
            LayoutInflater inflater = LayoutInflater.from(view.getContext());
            final View dialogView = inflater.inflate(R.layout.card_item_menu, null);

            Button deleteBtn = dialogView.findViewById(R.id.deleteBtn);
            Button editBtn = dialogView.findViewById(R.id.editTitleBtn);
            Button editApplyBtn = dialogView.findViewById(R.id.editApplyBtn);
            Button cancelBtn = dialogView.findViewById(R.id.cancelBtn);
            EditText editTitleText = dialogView.findViewById(R.id.titleEditText);
            EditText descriptionEditText = dialogView.findViewById(R.id.desciptionEditText);
            TextView infoTV = dialogView.findViewById(R.id.infoTV);
            ImageView expandInfoImgV = dialogView.findViewById(R.id.expandInfoImgV);
            infoTV.setMovementMethod(new ScrollingMovementMethod());

            String mTitleTV = (String) titleTV.getText();
            mDialog.setContentView(dialogView);
            DBManager dbManager = new DBManager(ctx);
            try {
                dbManager.open();
                Cursor cursor = dbManager.selectByTitleAndDate(currentViewDate, titleTV.getText().toString());
                if (cursor.moveToFirst()) {
                    do {
                        String description = cursor.getString(cursor.getColumnIndex(DESCRIPTION_FIELD));
                        infoTV.setText(description);
                    } while (cursor.moveToNext());
                }
                cursor.close();
                dbManager.close();
                infoTV.post(() -> {
                    if (infoTV.getLineCount() > 2) {
                        expandInfoImgV.setVisibility(View.VISIBLE);
                    }
                });

            } catch (SQLException throwable) {
                throwable.printStackTrace();
            }

            editBtn.setOnClickListener(view1 -> {
                //#ToDo edit title
                editBtn.setVisibility(View.INVISIBLE);
                editTitleText.setVisibility(View.VISIBLE);
                descriptionEditText.setVisibility(View.VISIBLE);
                deleteBtn.setVisibility(View.INVISIBLE);
                editApplyBtn.setVisibility(View.VISIBLE);

                editTitleText.setText(mTitleTV);
                descriptionEditText.setText(infoTV.getText());

                editApplyBtn.setOnClickListener(view11 -> {
                    DBManager dbManager1 = new DBManager(ctx);
                    try {
                        dbManager1.open();
                        String newTitle = editTitleText.getText().toString();
                        String newDescr = descriptionEditText.getText().toString();
                        if (!titleTV.getText().equals(newTitle) || !infoTV.getText().toString().equals(newDescr)) {
                            boolean res = dbManager1.updateTitleAndDescrRaw((String) titleTV.getText(), newTitle, newDescr, currentViewDate);
                            if (res) {
                                Toast.makeText(view11.getContext(), newTitle + " Edited!", Toast.LENGTH_SHORT).show();
                                titleTV.setText(newTitle);
                            }
                        }
                    } catch (SQLException throwable) {
                        throwable.printStackTrace();
                    }
                    dbManager1.close();
                    mDialog.cancel();
                });
            });

            deleteBtn.setOnClickListener(view12 -> {
                DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            try {
                                dbManager.open();
                                int res = dbManager.deleteExFromMainByTitleDate((String) titleTV.getText(), currentViewDate);
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
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            //No button clicked
                            break;
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(view12.getContext());
                builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            });

            expandInfoImgV.setOnClickListener(v -> {
                if (infoTV.getMaxLines() == 2) {
                    infoTV.setMaxLines(10);
                    expandInfoImgV.setRotationX(0);
                } else {
                    infoTV.setMaxLines(2);
                    expandInfoImgV.setRotationX(180);
                }
            });

            cancelBtn.setOnClickListener(view13 -> mDialog.cancel());
            mDialog.show();
        }
    }
}
