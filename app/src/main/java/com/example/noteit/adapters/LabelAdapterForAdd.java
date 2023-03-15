package com.example.noteit.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.noteit.DAO.LabelDAO;
import com.example.noteit.R;
import com.example.noteit.models.Label;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class LabelAdapterForAdd extends RecyclerView.Adapter<LabelAdapterForAdd.LabelHolder> {
    private List<Label> labelList;

    public List<Label> getSelected() {
        return selected;
    }

    private List<Label> selected = new ArrayList<>();
    class LabelHolder extends RecyclerView.ViewHolder{
        TextView name;
        ImageView img_close;
        LabelAdapterForAdd labelAdapterForAdd;
        CardView card;
        LabelHolder(View view){
            super(view);
            name = view.findViewById(R.id.tv_label_name);
            card = view.findViewById(R.id.card_label);
            img_close = view.findViewById(R.id.img_close);
            img_close.setVisibility(View.GONE);
        }
        public LabelHolder linkAdapter(LabelAdapterForAdd adapter){
            this.labelAdapterForAdd = adapter;
            return this;
        }
        private void notifyDelete(){
            labelAdapterForAdd.notifyItemRemoved(getAdapterPosition());
            labelAdapterForAdd.notifyItemRangeChanged(getAdapterPosition(),labelList.size());
        }
    }

    public LabelAdapterForAdd(List<Label> labelList){
        this.labelList = labelList;
    }
    @NonNull
    @Override
    public LabelAdapterForAdd.LabelHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_label_with_delete,parent,false);
        return new LabelHolder(itemView).linkAdapter(this);
    }

    @Override
    public void onBindViewHolder(@NonNull LabelAdapterForAdd.LabelHolder holder, int position) {
        Label label = labelList.get(position);
        holder.name.setText(label.getName());
        holder.card.setCardBackgroundColor(Color.parseColor("#FBFCFC"));
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.img_close.getVisibility() == View.GONE){
                    holder.img_close.setVisibility(View.VISIBLE);
                }else{
                    holder.img_close.setVisibility(View.GONE);
                }
            }
        });
        holder.img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                labelList.remove(label);
                LabelDAO.getInstance().getDb().collection("labels").document(label.getName()).delete();
                holder.notifyDelete();
            }
        });

    }


    @Override
    public int getItemCount() {
        return labelList.size();
    }

}
