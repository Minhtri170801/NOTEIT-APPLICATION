package com.example.noteit.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.noteit.R;
import com.example.noteit.models.Label;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class LabelAdapterForDetail extends RecyclerView.Adapter<LabelAdapterForDetail.LabelHolder> {
    private List<Label> labelList;
    public List<Label> getSelected() {
        return selected;
    }

    private List<Label> selected = new ArrayList<>();
    class LabelHolder extends RecyclerView.ViewHolder{
        TextView name;
        private LabelAdapterForDetail labelAdapterForAdd;
        CardView card;
        LabelHolder(View view){
            super(view);
            name = view.findViewById(R.id.tv_label_name);
            card = view.findViewById(R.id.card_label);
        }
        public LabelHolder linkAdapter(LabelAdapterForDetail adapter){
            this.labelAdapterForAdd = adapter;
            return this;
        }
    }

    public LabelAdapterForDetail(List<Label> labelList){
        this.labelList = labelList;
    }
    @NonNull
    @Override
    public LabelAdapterForDetail.LabelHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_label,parent,false);
        return new LabelHolder(itemView).linkAdapter(this);
    }

    @Override
    public void onBindViewHolder(@NonNull LabelAdapterForDetail.LabelHolder holder, int position) {
        Label label = labelList.get(position);
        holder.name.setText(label.getName());
        Random rdm = new Random();
        holder.card.setCardBackgroundColor(Color.parseColor("#FBFCFC"));

        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!selected.contains(label)){
                    selected.add(label);
                    holder.name.setTextColor(Color.parseColor("#FBFCFC"));
                    holder.card.setCardBackgroundColor(Color.parseColor("#00EBCA"));
                }else{
                    selected.remove(label);
                    holder.name.setTextColor(Color.parseColor("#001E4D"));
                    holder.card.setCardBackgroundColor(Color.parseColor("#FBFCFC"));
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return labelList.size();
    }

}
