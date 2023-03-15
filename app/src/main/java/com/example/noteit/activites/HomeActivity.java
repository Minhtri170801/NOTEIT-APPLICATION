package com.example.noteit.activites;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.noteit.DAO.LabelDAO;
import com.example.noteit.DAO.NoteDAO;
import com.example.noteit.util.SaveSharedPreference;
import com.example.noteit.DAO.UserDAO;
import com.example.noteit.R;

import com.example.noteit.adapters.LabelAdapterForHome;
import com.example.noteit.adapters.NoteAdapter;
import com.example.noteit.models.Label;
import com.example.noteit.models.Note;
import com.example.noteit.models.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {
    public static final int LAUNCH_LOG_IN = 9778;
    public static final int LAUCH_EDIT = 90998;
    RecyclerView glv_note,rv_labels;

    public  NoteAdapter noteAdapter;
    List<Note> noteList = new ArrayList<>();
    List<Label> labelList = new ArrayList<>();
    TextView empty;
    public LabelAdapterForHome labelAdapterForHome;

    FloatingActionButton floatingActionButton;
    final  static int LAUNCH_ADD = 32342;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_list);
        initViews();
        SaveSharedPreference.putLabels(HomeActivity.this);
            labelList.clear();
            LabelDAO.getInstance().getDb().collection("labels").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    List<DocumentSnapshot> documentSnapshotList = queryDocumentSnapshots.getDocuments();
                    for (DocumentSnapshot d : documentSnapshotList) {
                        Label label = d.toObject(Label.class);
                        labelList.add(label);
                    }
                    if (labelList.size() > 0) {
                        runOnUiThread(() -> {
                            labelAdapterForHome = new LabelAdapterForHome(labelList);
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
                            linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                            rv_labels.setLayoutManager(linearLayoutManager);
                            rv_labels.setItemAnimator(new DefaultItemAnimator());
                            rv_labels.setAdapter(labelAdapterForHome);
                        });
                    }
                }
            });

        checkSession();
        applyMenuType();
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(SaveSharedPreference.getActivated(HomeActivity.this) == false){
                    if(SaveSharedPreference.getNotesNumber(HomeActivity.this) < 5){
                        Intent intent = new Intent(HomeActivity.this, AddNoteActivity.class);
                        startActivityForResult(intent, LAUNCH_ADD);
                    }else{
                        AlertDialog alertDialog = new AlertDialog.Builder(HomeActivity.this).create();
                        alertDialog.setTitle("Thất bại");
                        alertDialog.setMessage("Số lần tạo ghi chú của tài khoản của bạn hiện đã đạt quá giá hạn. Vui lòng xác minh tài khoản để có thể tạo thêm ghi chú");
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                        alertDialog.show();
                    }
                }else{
                    Intent intent = new Intent(HomeActivity.this, AddNoteActivity.class);
                    startActivityForResult(intent, LAUNCH_ADD);
                }
            }
        });

        Thread thread0 = new Thread(() -> {
            runOnUiThread(() -> {
                glv_note.setHasFixedSize(true);
                if (SaveSharedPreference.getViewType(this).equals("grid")) {
                    glv_note.setLayoutManager(new GridLayoutManager(this, 2));
                } else {
                    glv_note.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
                }

            });
            noteList.clear();
            NoteDAO.getInstance().getDb().collection("notes").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    List<DocumentSnapshot> documentSnapshotList = queryDocumentSnapshots.getDocuments();
                    for (DocumentSnapshot d : documentSnapshotList) {
                        Note note = d.toObject(Note.class);
                        if (note.getOwner().equals(SaveSharedPreference.getUser(HomeActivity.this)) && !note.isDelete()) {
                            noteList.add(note);
                        }
                    }
                    if (noteList.size() > 0) {
                        runOnUiThread(() -> {
                            noteAdapter = new NoteAdapter(noteList);
                            glv_note.setAdapter(noteAdapter);
                            empty.setVisibility(View.GONE);
                        });
                    } else {
                        runOnUiThread(() -> {
                            empty.setVisibility(View.VISIBLE);
                        });
                    }
                }
            });
        });
        thread0.start();
        if (SaveSharedPreference.getFromNotification(HomeActivity.this).length() != 0) {
            String id = SaveSharedPreference.getFromNotification(HomeActivity.this);
            NoteDAO.getInstance().getDb().collection("notes").document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Note note = (documentSnapshot.toObject(Note.class));
                    if (note.isSecured()) {
                        AlertDialog.Builder passwordDialog = new AlertDialog.Builder(HomeActivity.this);
                        passwordDialog.setTitle("Ghi chú này hiện đang được bảo mật, vui lòng nhập mật khẩu!");
                        final EditText passwordInput = new EditText(HomeActivity.this);
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
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Boolean wantToCloseDialog = false;

                                String pwd = passwordInput.getText().toString();
                                if (!pwd.equals(note.getPassword())) {
                                    Toast.makeText(HomeActivity.this, "Mật khẩu không chính xác", Toast.LENGTH_SHORT).show();
                                    passwordInput.setText("");

                                } else {
                                    wantToCloseDialog = true;
                                    Intent intent = new Intent(HomeActivity.this, EditActivity.class);
                                    intent.putExtra("noteID", note.getID());
                                    HomeActivity.this.startActivityForResult(intent, LAUCH_EDIT);
                                }
                                if (wantToCloseDialog)
                                    dialog.dismiss();
                            }
                        });
                    } else {
                        Intent intent = new Intent(HomeActivity.this, EditActivity.class);
                        intent.putExtra("noteID", note.getID());
                        HomeActivity.this.startActivityForResult(intent, LAUCH_EDIT);
                    }
                }
            });
        }
    }

    private void checkSession() {
        if(SaveSharedPreference.getUser(HomeActivity.this).length() == 0)
        {
            Intent loginIntent = new Intent(this,LoginActivity.class);
            startActivityForResult(loginIntent,LAUNCH_LOG_IN);
        }else{
            if(SaveSharedPreference.getViewType(HomeActivity.this).length() == 0)
            {
                SaveSharedPreference.setViewType(HomeActivity.this,"grid");
            }
        }
    }

    private void applyMenuType() {
        if(SaveSharedPreference.getViewType(HomeActivity.this).equals("grid")){
            glv_note.setLayoutManager(new GridLayoutManager(this, 2));
        }else{
            glv_note.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));
        }
    }

    private void initViews() {
        rv_labels = findViewById(R.id.rv_labels);
        glv_note = findViewById(R.id.glv_note);
        floatingActionButton = findViewById(R.id.floatingActionButton);
        empty = findViewById(R.id.empty);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        MenuItem searchItem = menu.findItem(R.id.menu_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ArrayList<Note> filterdList = new ArrayList<>();
                if(newText.length() > 0){
                    for(Note note : noteList){
                        if(note.getTitle().toLowerCase(Locale.ROOT).contains(newText.toLowerCase(Locale.ROOT)) || note.getContent().toLowerCase(Locale.ROOT).contains(newText.toLowerCase(Locale.ROOT))){
                            filterdList.add(note);
                        }
                    }
                }else{
                    noteAdapter.filterList((ArrayList<Note>) noteList);
                }
                if(filterdList.size() > 0){
                    noteAdapter.filterList(filterdList);
                }
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case(R.id.menu_type):
                if(SaveSharedPreference.getViewType(HomeActivity.this).equals("grid")){
                    SaveSharedPreference.setViewType(HomeActivity.this,"list");
                    applyMenuType();
                    item.setIcon(ContextCompat.getDrawable(HomeActivity.this,R.drawable.ic_girdview));

                }else{
                    SaveSharedPreference.setViewType(HomeActivity.this,"grid");
                    applyMenuType();
                    item.setIcon(ContextCompat.getDrawable(HomeActivity.this,R.drawable.ic_listview));
                }
                break;
            case (R.id.menu_logout):
                SaveSharedPreference.setUser(HomeActivity.this,"");
                SaveSharedPreference.setNotes(HomeActivity.this,0);
                SaveSharedPreference.setAcitvated(HomeActivity.this, true);
                checkSession();
                UserDAO.getInstance().setCurrentUser(new User());
                break;
            case(R.id.menu_garbage):
                Intent intent = new Intent(this, GarbageActivity.class);
                startActivityForResult(intent,LAUCH_EDIT);
                break;
            case (R.id.menu_settings):
                Intent intent1 = new Intent(this, SettingActivity.class);
                startActivityForResult(intent1,LAUNCH_ADD);
                break;
            case (R.id.menu_labels):
                Intent intent2 = new Intent(this, LabelAcitvity.class);
                startActivityForResult(intent2,ADD_LABEL);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    final static int ADD_LABEL = 3424;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        checkSession();
        if(requestCode == LAUNCH_ADD){
            if(resultCode == RESULT_OK){
                if(data != null){
                    UserDAO.getDb().collection("users").document(SaveSharedPreference.getUser(HomeActivity.this)).get().addOnSuccessListener(
                            new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    User user = documentSnapshot.toObject(User.class);
                                    if(user.isActivated() == false){
                                        int notes = SaveSharedPreference.getNotesNumber(HomeActivity.this);
                                        user.setNotes(notes + 1);
                                        SaveSharedPreference.setNotes(HomeActivity.this,notes + 1);
                                        UserDAO.getDb().collection("users").document(SaveSharedPreference.getUser(HomeActivity.this)).set(user);
                                    }else{
                                        user.setNotes(0);
                                    }
                                }
                            }
                    );
                    Thread thread1 = new Thread(()->{
                        noteList.clear();
                        NoteDAO.getInstance().getDb().collection("notes").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                List<DocumentSnapshot> documentSnapshotList = queryDocumentSnapshots.getDocuments();
                                for(DocumentSnapshot d : documentSnapshotList){
                                    Note note = d.toObject(Note.class);
                                    if(note.getOwner().equals(SaveSharedPreference.getUser(HomeActivity.this)) && !note.isDelete()){
                                        noteList.add(note);
                                    }
                                }
                                if(noteList.size() > 0){
                                    runOnUiThread(()->{
                                        noteAdapter = new NoteAdapter(noteList);
                                        glv_note.setAdapter(noteAdapter);
                                        empty.setVisibility(View.GONE);
                                        noteAdapter.notifyDataSetChanged();
                                    });
                                }else{
                                    runOnUiThread(()->{
                                        empty.setVisibility(View.VISIBLE);
                                    });
                                }
                            }
                        });
                    });
                    thread1.start();
                }
            }
        }else if(requestCode == LAUNCH_LOG_IN || requestCode == LAUCH_EDIT){
            if(resultCode == RESULT_OK){
                if(data != null){
                    Thread thread1 = new Thread(()->{
                        noteList.clear();
                        NoteDAO.getInstance().getDb().collection("notes").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                List<DocumentSnapshot> documentSnapshotList = queryDocumentSnapshots.getDocuments();
                                for(DocumentSnapshot d : documentSnapshotList){
                                    Note note = d.toObject(Note.class);
                                    if(note.getOwner().equals(SaveSharedPreference.getUser(HomeActivity.this)) && !note.isDelete()){
                                        noteList.add(note);
                                    }
                                }
                                if(noteList.size() > 0){
                                    runOnUiThread(()->{
                                        noteAdapter = new NoteAdapter(noteList);
                                        glv_note.setAdapter(noteAdapter);
                                        empty.setVisibility(View.GONE);
                                        noteAdapter.notifyDataSetChanged();
                                    });
                                }else{
                                    runOnUiThread(()->{
                                        empty.setVisibility(View.VISIBLE);
                                    });
                                }
                            }
                        });
                    });
                    thread1.start();
                }
            }
        }else if(requestCode == ADD_LABEL){
            if(resultCode == RESULT_OK){
                if(data != null){

                        labelList.clear();
                        LabelDAO.getInstance().getDb().collection("labels").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                List<DocumentSnapshot> documentSnapshotList = queryDocumentSnapshots.getDocuments();
                                for (DocumentSnapshot d : documentSnapshotList) {
                                    Label label = d.toObject(Label.class);
                                    labelList.add(label);
                                }
                                if (labelList.size() > 0) {
                                    runOnUiThread(() -> {
                                        labelAdapterForHome = new LabelAdapterForHome(labelList);
                                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
                                        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                                        rv_labels.setLayoutManager(linearLayoutManager);
                                        rv_labels.setItemAnimator(new DefaultItemAnimator());
                                        rv_labels.setAdapter(labelAdapterForHome);
                                        labelAdapterForHome.notifyDataSetChanged();
                                    });
                                }
                            }
                        });

                }
            }
        }
    }
}
