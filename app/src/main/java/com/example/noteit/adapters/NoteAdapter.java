package com.example.noteit.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.noteit.DAO.NoteDAO;
import com.example.noteit.activites.HomeActivity;
import com.example.noteit.util.SaveSharedPreference;
import com.example.noteit.R;
import com.example.noteit.activites.EditActivity;
import com.example.noteit.activites.FormatFontActivity;
import com.example.noteit.models.Note;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;


public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteHolder> {
    private List<Note> noteList;
    public static final int LAUCH_EDIT = 90998;
    int[] colors = new int[]{Color.parseColor("#28AFEA"), Color.parseColor("#00EBCA"), Color.parseColor("#1ED760"), Color.parseColor("#51AEFF"), Color.parseColor("#51AEFF"), Color.parseColor("#7FAD39"),Color.parseColor("#FFE68A"),Color.parseColor("#8C6CA8")};

    class NoteHolder extends RecyclerView.ViewHolder{
        TextView title,content,lastModified;
        CardView card;
        ImageView pinned, popupmenu,lock;
        NoteAdapter noteAdapter;
        NoteHolder(View view){
            super(view);
            title = view.findViewById(R.id.tv_title);
            content = view.findViewById(R.id.tv_content);
            lastModified = view.findViewById(R.id.last_modified);
            card = view.findViewById(R.id.note_card);
            pinned = view.findViewById(R.id.pin);
            popupmenu = view.findViewById(R.id.popupmenu);
            lock = view.findViewById(R.id.lock);
        }

        public NoteHolder linkAdapter(NoteAdapter adapter){
            this.noteAdapter =adapter;
            return this;
        }
        public void notifyDelete(){
            noteAdapter.notifyItemRemoved(getAdapterPosition());
            noteAdapter.notifyItemRangeChanged(getAdapterPosition(),noteList.size());
        }
    }
    public void filterList(ArrayList<Note> filterllist) {
        noteList = filterllist;
        notifyDataSetChanged();
    }

    public NoteAdapter(List<Note> otherNoteList){
        Collections.sort(otherNoteList, (object2, object1) -> {
            if(object2.isPinned() != object1.isPinned())
                return !object2.isPinned() ? 1 : -1 ;
            if(object2.isPinned() != object1.isPinned())
                return !object2.isPinned() ? 1 : -1;
            int result = object1.getLastModified().compareTo(object2.getLastModified());
            if (result != 0) return result;
            return 0;
        });
        this.noteList = otherNoteList;
    }
    @NonNull
    @Override
    public NoteAdapter.NoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_grid_note_item,parent,false);
        return new NoteHolder(itemView).linkAdapter(this);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteAdapter.NoteHolder holder, int position) {
        Note note = noteList.get(position);

        SaveSharedPreference.setFontsize(holder.itemView.getContext(), note.getFontSize(), note.getID());
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy - HH:mm");
        if(note.getTitle().trim().length() == 0){
            holder.title.setText("Ghi chú không có tiêu đề");
        }else{
            holder.title.setText(note.getTitle());
        }
        if(note.getContent().trim().length() == 0){
            holder.content.setHint("Chưa có nội dung");
        }else{
            holder.content.setText(note.getContent().replace("<br>","\n").replace("<i>","").replace("</i>","").replace("<b>","").replace("</b>","").replace("<u>","").replace("</u>",""));
        }
        Random rdm = new Random();
        holder.card.setCardBackgroundColor(colors[rdm.nextInt(colors.length)]);
        if(!note.isPinned()){
            holder.pinned.setVisibility(View.GONE);
        }else{
            holder.pinned.setVisibility(View.VISIBLE);
        }
        if(!note.isSecured()){
            holder.lock.setVisibility(View.GONE);
            
        }else{
            holder.content.setText("Bảo mật");
            holder.lock.setVisibility(View.VISIBLE);
        }
        holder.lastModified.setText(formatter.format(note.getLastModified()).toString());

        holder.card.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(note.isSecured()){
                    AlertDialog.Builder passwordDialog = new AlertDialog.Builder(v.getContext());
                    passwordDialog.setTitle("Ghi chú này hiện đang được bảo mật, vui lòng nhập mật khẩu!");
                    final EditText passwordInput = new EditText(v.getContext());
                    passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    passwordInput.setHint("Mật khẩu ghi chú");
                    passwordDialog.setView(passwordInput);
                    passwordDialog.setPositiveButton("Xác nhận", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    passwordDialog.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    final AlertDialog dialog = passwordDialog.create();
                    dialog.show();
                    dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.cancel();
                        }
                    });
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View view)
                        {
                            Boolean wantToCloseDialog = false;

                            String pwd = passwordInput.getText().toString();
                            if(!pwd.equals(note.getPassword())){
                                Toast.makeText(v.getContext(), "Mật khẩu không chính xác", Toast.LENGTH_SHORT).show();
                                passwordInput.setText("");

                            }else{
                                wantToCloseDialog = true;
                                Intent intent = new Intent(v.getContext(), EditActivity.class);
                                intent.putExtra("noteID",note.getID());
                                ((Activity) v.getContext()).startActivityForResult(intent, LAUCH_EDIT);
                            }
                            if(wantToCloseDialog)
                                dialog.dismiss();
                        }
                    });
                }else {
                    Intent intent = new Intent(v.getContext(), EditActivity.class);
                    intent.putExtra("noteID",note.getID());
                    intent.putExtra("labels", (Serializable) note.getLabel());
                    ((Activity) v.getContext()).startActivityForResult(intent, LAUCH_EDIT);
                }

            }
        });
        holder.popupmenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopupMenu popupMenu = new PopupMenu(v.getContext(),v);
                popupMenu.setGravity(Gravity.END);
                popupMenu.getMenu().add("Chia sẻ").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Intent send = new Intent(Intent.ACTION_SEND);
                        send.setType("text/html");
                        send.putExtra(Intent.EXTRA_SUBJECT,note.getTitle());
                        send.putExtra(Intent.EXTRA_TEXT, note.getContent());
                        v.getContext().startActivity(Intent.createChooser(send, "Chia sẻ qua"));
                        return false;
                    }
                });
                popupMenu.getMenu().add("Xóa ghi chú").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(v.getContext());
                        dialog.setTitle("Xác nhận xóa ghi chú?");
                        dialog.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                noteList.remove(note);
                                note.setDelete(true);
                                note.setLastModified(new Date());
                                NoteDAO.getInstance().getDb().collection("notes").document(note.getID()).set(note);
                                holder.notifyDelete();
                            }
                        });
                        dialog.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        dialog.show();
                        return false;
                    }
                });
                popupMenu.getMenu().add("Tùy chỉnh").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Intent intent = new Intent(v.getContext(), FormatFontActivity.class);
                        intent.putExtra("id",note.getID());
                        v.getContext().startActivity(intent);
                        return false;
                    }
                });
                popupMenu.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

}
