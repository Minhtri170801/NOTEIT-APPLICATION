package com.example.noteit.activites;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.noteit.DAO.NoteDAO;
import com.example.noteit.util.SaveSharedPreference;
import com.example.noteit.R;
import com.example.noteit.models.Note;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.slider.Slider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class FormatFontActivity extends AppCompatActivity {
    String id,fontStyle;
    int fontSize;
    Slider slider_fontSize;
    RadioGroup radgroup_fontStyle;
    RadioButton rbtn_font_default, rbtn_font_bold, rbtn_font_italic;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_fontsize);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        slider_fontSize = findViewById(R.id.slider_fontSize);
        Intent returnIntent = new Intent();
        id = getIntent().getStringExtra("id");
        fontStyle = getIntent().getStringExtra("fontStyle");
        fontSize = SaveSharedPreference.getFontSize(FormatFontActivity.this, id);

        slider_fontSize.setValue((float) fontSize);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sub_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case(16908332):
                //TODO save
                fontSize = (int) slider_fontSize.getValue();
                Intent intent = new Intent();
                Thread thread = new Thread(()->{
                    NoteDAO.getInstance().getDb().collection("notes").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            List<DocumentSnapshot> documentSnapshotList = queryDocumentSnapshots.getDocuments();
                            for(DocumentSnapshot d: documentSnapshotList){
                                Note note = d.toObject(Note.class);
                                if(note.getID().equals(id)){

                                    note.setFontSize(fontSize);
                                    runOnUiThread(()->{
                                        intent.putExtra("id",note.getID());
                                        SaveSharedPreference.setFontsize(FormatFontActivity.this,fontSize,note.getID());
                                    });
                                    NoteDAO.getInstance().getDb().collection("notes").document(id).set(note);
                                    break;
                                }
                            }
                        }
                    });
                });
                thread.start();
                finish();
                break;
            case(R.id.menu_garbage):
                Intent intent1 = new Intent(this, GarbageActivity.class);
                startActivity(intent1);
                break;
            case (R.id.menu_settings):
                Intent intent2 = new Intent(this, SettingActivity.class);
                startActivity(intent2);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
        public boolean onKeyDown(int keyCode, KeyEvent event) {
            if ((keyCode == KeyEvent.KEYCODE_BACK)) {
                fontSize = (int) slider_fontSize.getValue();
                Intent intent = new Intent();
                Thread thread = new Thread(()->{
                    NoteDAO.getInstance().getDb().collection("notes").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            List<DocumentSnapshot> documentSnapshotList = queryDocumentSnapshots.getDocuments();
                            for(DocumentSnapshot d: documentSnapshotList){
                                Note note = d.toObject(Note.class);
                                if(note.getID().equals(id)){

                                    note.setFontSize(fontSize);
                                    runOnUiThread(()->{
                                        intent.putExtra("id",note.getID());
                                        SaveSharedPreference.setFontsize(FormatFontActivity.this,fontSize,note.getID());
                                    });

                                    NoteDAO.getInstance().getDb().collection("notes").document(id).set(note);
                                    break;
                                }
                            }
                        }
                    });
                });
                thread.start();
                finish();
            }
            return super.onKeyDown(keyCode, event);
        }

}

