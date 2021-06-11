package com.kota.simpletimetable;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {

    Context context;
    ArrayList<Slot> list;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
        void onDeleteClick(int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public RecyclerViewAdapter(Context context, ArrayList<Slot> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.layout_slotcard, parent, false);
        MyViewHolder holder = new MyViewHolder(v, mListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.Title.setText(list.get(position).getTitle());
        holder.Code.setText(list.get(position).getCode());
        holder.Room.setText(list.get(position).getRoom());

        holder.Time.setText(list.get(position).getTime());


        if(list.get(position).isEdit_mode()){
            holder.closeCard.setVisibility(View.VISIBLE);
        }else{
            holder.closeCard.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView Title,Code,Room,Time;
        private ImageView closeCard;

        public MyViewHolder(@NonNull View itemView,final OnItemClickListener listener) {
            super(itemView);
            Title = itemView.findViewById(R.id.card_title);
            Code = itemView.findViewById(R.id.card_code);
            Room = itemView.findViewById(R.id.card_Room);
            closeCard = itemView.findViewById(R.id.card_close);
            Time = itemView.findViewById(R.id.card_time);

            closeCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onDeleteClick(position);
                        }
                    }
                }
            });
        }
    }

}
