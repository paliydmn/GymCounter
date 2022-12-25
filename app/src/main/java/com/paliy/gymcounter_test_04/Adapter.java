package com.paliy.gymcounter_test_04;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import static android.util.Log.println;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    List<String> abc;
    List<Integer> abcImage;
    LayoutInflater inflater;

    View.OnClickListener listener;

    public Adapter(Context ctx, List<String> abc, List<Integer> abcImage, View.OnClickListener listener){
        this.listener = listener;

        this.abc = abc;
        this.abcImage = abcImage;

        this.inflater = LayoutInflater.from(ctx);

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.activity_main2, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.abc.setText(abc.get(position));
        holder.abc.setOnClickListener(listener);

//        holder.abcImage.setImageResource(abcImage.get(position));
    }


    @Override
    public int getItemCount() {
        return abc.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView abc;
        ImageView abcImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            abc = itemView.findViewById(R.id.textView);
            abcImage = itemView.findViewById(R.id.imageView);

            abcImage.setOnClickListener(this);
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

        }
    }
}
