package com.example.noteit.activites;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.noteit.DAO.NoteDAO;
import com.example.noteit.R;
import com.example.noteit.adapters.LabelAdapterForEdit;
import com.example.noteit.interfaces.LabelOnClickListener;
import com.example.noteit.models.Label;
import com.example.noteit.models.Note;
import com.example.noteit.util.MyReceiver;
import com.example.noteit.util.SaveSharedPreference;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import jp.wasabeef.richeditor.RichEditor;

public class DetailActivity extends Activity {
    RichEditor richEditor;

    Switch sw_password,sw_alarm;
    TextView tv_empty,tv_selected;
    RecyclerView rv_label_selected;
    List<Label> selectedlabelList = new ArrayList<>();

    ImageView imgv_close;
    LabelAdapterForEdit labelAdapter1 ;
    EditText et_title;
    String noteID;
    Note note = new Note();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_note);
        noteID = getIntent().getStringExtra("noteID");
        if(noteID == null){
            if(SaveSharedPreference.getFromNotification(DetailActivity.this).length() != 0){
                noteID = SaveSharedPreference.getFromNotification(DetailActivity.this);
            }
        }
        findViewById(R.id.imgv_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        NoteDAO.getInstance().getDb().collection("notes").document(noteID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshots) {
                    note = (documentSnapshots.toObject(Note.class));
                        initViews(note);
                        setEvents(note);
                        SaveSharedPreference.setCurrentNoteID(DetailActivity.this,noteID);
                }
            });

    }


    private void setEvents(Note note) {
        selectedlabelList = note.getLabel();
        tv_empty.setVisibility(View.GONE);
        et_title.setText(note.getTitle());
        richEditor.setEditorHeight(200);
        richEditor.setPadding(10, 10, 10, 10);
        richEditor.setEditorFontSize(SaveSharedPreference.getFontSize(DetailActivity.this, noteID));
        richEditor.setHtml(note.getContent());
        rv_label_selected = findViewById(R.id.rv_label_selected);

        if (selectedlabelList.size() == 0) {
            tv_empty.setVisibility(View.VISIBLE);
            tv_selected.setVisibility(View.GONE);

        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        labelAdapter1 = new LabelAdapterForEdit(selectedlabelList);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rv_label_selected.setLayoutManager(linearLayoutManager);
        rv_label_selected.setItemAnimator(new DefaultItemAnimator());
        rv_label_selected.setAdapter(labelAdapter1);


    }

    private void initViews(Note note) {
        et_title = findViewById(R.id.et_title);
        imgv_close = findViewById(R.id.imgv_close);
        tv_empty = findViewById(R.id.tv_empty);
        tv_selected = findViewById(R.id.tv_selected);
        tv_selected.setVisibility(View.VISIBLE);
        richEditor = (RichEditor) findViewById(R.id.richEditor);
        richEditor.setEnabled(false);


    }


}