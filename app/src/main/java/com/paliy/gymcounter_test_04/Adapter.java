package com.paliy.gymcounter_test_04;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    List<String> title;
    List<String> count;
    Button Add;

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

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.activity_main2, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.title.setText(title.get(position));
        holder.count.setText(count.get(position));
        holder.title.setOnClickListener(listener);

//        holder.abcImage.setImageResource(abcImage.get(position));
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

        }
    }
}
