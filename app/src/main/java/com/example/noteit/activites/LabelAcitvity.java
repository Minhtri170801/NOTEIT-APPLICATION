package com.example.noteit.activites;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.noteit.DAO.LabelDAO;
import com.example.noteit.R;
import com.example.noteit.adapters.LabelAdapterForAdd;
import com.example.noteit.adapters.LabelAdapterForHome;
import com.example.noteit.models.Label;
import com.example.noteit.util.SaveSharedPreference;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class LabelAcitvity extends AppCompatActivity {
    EditText et_label_name;
    Button bt_add_label;
    List<Label> labelList = new ArrayList<>();
    RecyclerView rv_labels;
    TextView tv_validation_message;
    LabelAdapterForAdd labelAdapterForAdd;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.labels);
        et_label_name = findViewById(R.id.et_label_name);
        bt_add_label = findViewById(R.id.bt_add_label);
        tv_validation_message = findViewById(R.id.tv_validation_message);
        rv_labels = findViewById(R.id.rv_labels);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
                        labelAdapterForAdd = new LabelAdapterForAdd(labelList);
                        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(),4);
                        gridLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                        rv_labels.setLayoutManager(gridLayoutManager);
                        rv_labels.setItemAnimator(new DefaultItemAnimator());
                        rv_labels.setAdapter(labelAdapterForAdd);
                    });
                }
            }
        });
        bt_add_label.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = et_label_name.getText().toString().trim();
                if(name.length() == 0){
                    tv_validation_message.setText("Vui lòng nhập tên nhãn");
                }else{
                    LabelDAO.getInstance().getDb().collection("labels").document(name).set(new Label(name));
                    labelList.add(new Label(name));
                    et_label_name.setText("");
                    Thread thread = new Thread(()->{
                        SaveSharedPreference.putLabels(LabelAcitvity.this);
                    });
                    thread.start();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.return_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case (16908332):
                Intent returnIntent = new Intent();
                setResult(RESULT_OK, returnIntent);
                finish();
                break;
        }
        return false;
    }
}
