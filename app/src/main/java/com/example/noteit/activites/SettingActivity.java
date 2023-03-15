package com.example.noteit.activites;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.noteit.DAO.UserDAO;
import com.example.noteit.R;
import com.example.noteit.models.User;
import com.example.noteit.util.Mailing;
import com.example.noteit.util.Randomize;
import com.example.noteit.util.SaveSharedPreference;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class SettingActivity extends AppCompatActivity {
    Spinner time;
    public static final int TO_OTP_RE = 4224345;
    Button saveDay, savePassword,sendActivated;
    int days;
    TextView tv_validation_message;
    EditText et_oldpass,et_newpass,et_confirm_newpass;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_system);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        time = findViewById(R.id.time);
        saveDay = findViewById(R.id.bt_save_time);
        savePassword = findViewById(R.id.btn_change_pass);
        et_oldpass = findViewById(R.id.et_oldpass);
        sendActivated = findViewById(R.id.sendActivated);

        if(!SaveSharedPreference.getActivated(SettingActivity.this)  ){
            sendActivated.setEnabled(true);
        }else{
            sendActivated.setEnabled(false);
        }
        sendActivated.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserDAO.getDb().collection("users").document(SaveSharedPreference.getUser(SettingActivity.this)).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String registerOTP = Randomize.getInstance().randomOTP();
                        User user = documentSnapshot.toObject(User.class);
                        Intent otpIntent = new Intent(SettingActivity.this,ConfirmOTPActivity.class);
                        otpIntent.putExtra("OTP",registerOTP);
                        otpIntent.putExtra("email",user.getEmail());
                        otpIntent.putExtra("name",user.getName());
                        Thread mailingThread = new Thread(()->Mailing.getInstance().sendEmailRegister(user.getEmail(),registerOTP,user.getName()));
                        mailingThread.start();
                        startActivityForResult(otpIntent,TO_OTP_RE);
                    }
                });

            }
        });
        et_newpass = findViewById(R.id.et_newpass);
        et_confirm_newpass = findViewById(R.id.et_confirm_newpass);
        tv_validation_message = findViewById(R.id.tv_validation_message);
        List<String> day_list = new ArrayList<>();
        day_list.add("1");
        day_list.add("2");
        day_list.add("3");
        day_list.add("4");
        day_list.add("5");
        day_list.add("6");
        day_list.add("7");

        ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item,day_list);

        adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        time.setAdapter(adapter);
        time.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                days = Integer.parseInt(day_list.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        saveDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveSharedPreference.setDeleteDate(SettingActivity.this,days);
                Toast.makeText(SettingActivity.this, "" + days, Toast.LENGTH_SHORT).show();
            }
        });


        savePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserDAO.getDb().collection("users").document(SaveSharedPreference.getUser(SettingActivity.this)).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        User user = documentSnapshot.toObject(User.class);
                        BCrypt.Result result = BCrypt.verifyer().verify(et_oldpass.getText().toString().trim().toCharArray(), user.getPassword());
                        if(!result.verified){
                            tv_validation_message.setText("Mật khẩu hiện tại không chính xác");
                            et_oldpass.setText("");
                            et_newpass.setText("");
                            et_confirm_newpass.setText("");
                        }else if(et_newpass.getText().toString().trim().length() == 0 || et_confirm_newpass.getText().toString().trim().length() == 0){
                            tv_validation_message.setText("Mật khẩu phải có ít nhất 6 kí tự");
                            et_oldpass.setText("");
                            et_newpass.setText("");
                            et_confirm_newpass.setText("");
                        }else if(!et_newpass.getText().toString().trim().equals(et_confirm_newpass.getText().toString().trim())) {
                            tv_validation_message.setText("Mật khẩu  xác nhận không chính xác");
                            et_oldpass.setText("");
                            et_newpass.setText("");
                            et_confirm_newpass.setText("");
                        }else{
                            String hashed = BCrypt.withDefaults().hashToString(12, et_newpass.getText().toString().trim().toCharArray());
                            user.setPassword(hashed);
                            Thread thread = new Thread(()->{
                               UserDAO.getDb().collection("users").document(user.getEmail()).set(user);
                               runOnUiThread(()->{
                                   Toast.makeText(SettingActivity.this, "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                               });
                            });
                            thread.start();
                            et_oldpass.setText("");
                            et_newpass.setText("");
                            et_confirm_newpass.setText("");
                        }
                    }
                });
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.setting_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void checkSession() {
        if(SaveSharedPreference.getUser(SettingActivity.this).length() == 0)
        {
            finish();
        }
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case(16908332):
                finish();
                break;
            case (R.id.menu_garbage):
                Intent intent = new Intent(this,GarbageActivity.class);
                startActivity(intent);
                break;
            case (R.id.menu_logout):
                SaveSharedPreference.setUser(SettingActivity.this,"");
                SaveSharedPreference.setNotes(SettingActivity.this,0);
                SaveSharedPreference.setAcitvated(SettingActivity.this, true);
                checkSession();
                UserDAO.getInstance().setCurrentUser(new User());
                Intent intent2 = new Intent();
                setResult(RESULT_OK);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == TO_OTP_RE){
            if(resultCode == RESULT_OK)
            {

                if(data != null){
                    UserDAO.getDb().collection("users").document(SaveSharedPreference.getUser(SettingActivity.this)).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            User user = documentSnapshot.toObject(User.class);
                            sendActivated.setEnabled(false);
                            SaveSharedPreference.setAcitvated(SettingActivity.this,true);
                            user.setActivated(true);
                            UserDAO.getDb().collection("users").document(SaveSharedPreference.getUser(SettingActivity.this)).set(user);
                        }
                    });
                }
            }
        }
    }
}
