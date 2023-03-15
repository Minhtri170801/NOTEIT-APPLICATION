package com.example.noteit.activites;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.noteit.DAO.NoteDAO;
import com.example.noteit.DAO.UserDAO;
import com.example.noteit.R;
import com.example.noteit.adapters.NoteAdapter;
import com.example.noteit.adapters.NoteAdapterGarbage;
import com.example.noteit.models.Label;
import com.example.noteit.models.Note;
import com.example.noteit.models.User;
import com.example.noteit.util.SaveSharedPreference;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class GarbageActivity extends AppCompatActivity {
    RecyclerView glv_note;
    TextView empty;
    public NoteAdapterGarbage noteAdapter;
    List<Note> noteList = new ArrayList<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trash);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        glv_note = findViewById(R.id.glv_note);
        empty = findViewById(R.id.empty);
        glv_note.setHasFixedSize(true);
        glv_note.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        noteList.clear();
        NoteDAO.getInstance().getDb().collection("notes").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> documentSnapshotList = queryDocumentSnapshots.getDocuments();
                for (DocumentSnapshot d : documentSnapshotList) {
                    Note note = d.toObject(Note.class);
                    if (note.getOwner().equals(SaveSharedPreference.getUser(GarbageActivity.this)) && note.isDelete()) {
                        noteList.add(note);
                    }
                }
                if(noteList.size() > 0) {
                    noteAdapter = new NoteAdapterGarbage(noteList);
                    glv_note.setAdapter(noteAdapter);
                    empty.setVisibility(View.GONE);
                }else{
                    empty.setVisibility(View.VISIBLE);
                }
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.garbage_menu,menu);
//        menu.getItem(R.id.menu_type).setVisible(false);
//        menu.getItem(R.id.menu_garbage).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    private void checkSession() {
        if(SaveSharedPreference.getUser(GarbageActivity.this).length() == 0)
        {
            finish();
        }
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case (R.id.menu_logout):
                SaveSharedPreference.setUser(GarbageActivity.this,"");
                SaveSharedPreference.setNotes(GarbageActivity.this,0);
                SaveSharedPreference.setAcitvated(GarbageActivity.this, true);
                checkSession();
                UserDAO.getInstance().setCurrentUser(new User());
                break;
            case(16908332):
                Intent returnIntent = new Intent();
                setResult(RESULT_OK,returnIntent);
                finish();
                break;
            case (R.id.menu_labels):
                Intent intent = new Intent(this, LabelAcitvity.class);
                startActivity(intent);
                break;

        }
        return super.onOptionsItemSelected(item);
    }
}
