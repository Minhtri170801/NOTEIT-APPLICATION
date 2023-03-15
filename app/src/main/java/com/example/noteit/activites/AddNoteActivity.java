package com.example.noteit.activites;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.noteit.DAO.NoteDAO;
import com.example.noteit.adapters.LabelAdapterForDetail;
import com.example.noteit.util.SaveSharedPreference;
import com.example.noteit.adapters.LabelAdapterForAdd;
import com.example.noteit.R;
import com.example.noteit.models.Label;
import com.example.noteit.models.Note;
import com.example.noteit.util.Randomize;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.richeditor.RichEditor;

public class AddNoteActivity extends Activity {
    RichEditor richEditor;
    EditText et_title;
    ImageView imgv_close,imgv_save,fm_italic,fm_bold,fm_underline;
    RecyclerView rv_labels;
    List<Label> labelList = new ArrayList<>();
    LabelAdapterForDetail labelAdapterForAdd;
    String noteContent;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_note);
        et_title = findViewById(R.id.et_title);
        imgv_close = findViewById(R.id.imgv_close);
        imgv_save = findViewById(R.id.imgv_save);
        rv_labels = findViewById(R.id.rv_labels);
        fm_italic = findViewById(R.id.fm_italic);
        fm_bold = findViewById(R.id.fm_bold);
        fm_underline = findViewById(R.id.fm_underline);

        for(String str: SaveSharedPreference.getLabels(AddNoteActivity.this)){
            labelList.add(new Label(str));
        }
        labelAdapterForAdd = new LabelAdapterForDetail(labelList);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rv_labels.setLayoutManager(linearLayoutManager);
        rv_labels.setItemAnimator(new DefaultItemAnimator());
        rv_labels.setAdapter(labelAdapterForAdd);
        imgv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnFail = new Intent();
                setResult(RESULT_CANCELED,returnFail);
                finish();
            }
        });
        Note note = new Note();
        richEditor = (RichEditor) findViewById(R.id.richEditor);
        richEditor.setEditorHeight(200);
        richEditor.setEditorFontSize(15);
        richEditor.setPadding(10, 10, 10, 10);
        richEditor.setPlaceholder("Nhập nội dung ghi chú ở đây...");
        richEditor.setOnTextChangeListener(new RichEditor.OnTextChangeListener() {
            @Override
            public void onTextChange(String text) {
                noteContent = text;
                note.setContent(noteContent);
            }
        });
        fm_italic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                richEditor.setItalic();
            }
        });
        fm_bold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                richEditor.setBold();
            }
        });
        fm_underline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                richEditor.setUnderline();
            }
        });
        imgv_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                note.setID(Randomize.getInstance().randomCode());
                note.setTitle(et_title.getText().toString().trim());
                note.setLabel(labelAdapterForAdd.getSelected());
                note.setOwner(SaveSharedPreference.getUser(AddNoteActivity.this));
                NoteDAO.getInstance().addNote(note);
                Intent intent = new Intent();
                intent.putExtra("temp","temp");
                setResult(RESULT_OK,intent);
                finish();
            }
        });
    }
}
