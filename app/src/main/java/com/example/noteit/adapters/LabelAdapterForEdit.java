package com.example.noteit.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.noteit.R;
import com.example.noteit.interfaces.LabelOnClickListener;
import com.example.noteit.models.Label;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class LabelAdapterForEdit extends RecyclerView.Adapter<LabelAdapterForEdit.LabelHolder> {
    private List<Label> labelList;

    public List<Label> getSelected() {
        return selected;
    }

    public LabelOnClickListener getListener() {
        return listener;
    }

    public void setListener(LabelOnClickListener listener) {
        this.listener = listener;
    }

    private LabelOnClickListener listener;
    private List<Label> selected = new ArrayList<>();
    class LabelHolder extends RecyclerView.ViewHolder{
        TextView name;
        private LabelAdapterForEdit labelAdapter;
        CardView card;
        LabelHolder(View itemView){
            super(itemView);
            name = itemView.findViewById(R.id.tv_label_name);
            card = itemView.findViewById(R.id.card_label);
        }
        public LabelHolder linkAdapter(LabelAdapterForEdit adapter){
            this.labelAdapter = adapter;
            return this;
        }
        public void bind(Label label, LabelOnClickListener listener){
           name.setText(label.getName());
            Random rdm = new Random();
            card.setCardBackgroundColor(Color.parseColor("#FBFCFC"));
            itemView.setOnClickListener(view->{
                if(listener != null){
                    listener.onLabelClicked(label,getAdapterPosition());
                }
            });
        }
    }

    public LabelAdapterForEdit(List<Label> labelList){
        this.labelList = labelList;
    }
    @NonNull
    @Override
    public LabelAdapterForEdit.LabelHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_label,parent,false);
        return new LabelHolder(itemView).linkAdapter(this);
    }

    @Override
    public void onBindViewHolder(@NonNull LabelAdapterForEdit.LabelHolder holder, int position) {
        Label label = labelList.get(position);

        holder.bind(label,listener);

    }

    public static Label selectLabel(Label label) {

        return label;
    }


    @Override
    public int getItemCount() {
        return labelList.size();
    }

}
