package com.example.noteit.activites;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.noteit.DAO.UserDAO;
import com.example.noteit.R;
import com.example.noteit.util.Mailing;
import com.example.noteit.util.Randomize;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;


public class ForgotActivity extends Activity {
    ImageButton imgbtn_back;
    final static int LAUCH_CHANGEPASSWORD = 99504434;
    Button btn_confirm;
    boolean isValidEmail = true;
    EditText et_email;
    TextView tv_validation_message;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_pass1);
        imgbtn_back = findViewById(R.id.imgbtn_back);
        tv_validation_message = findViewById(R.id.tv_validation_message);
        tv_validation_message.setText("");
        et_email = findViewById(R.id.et_email);
        et_email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!Patterns.EMAIL_ADDRESS.matcher(et_email.getText().toString().trim()).matches()){
                    tv_validation_message.setText("Email không đúng định dạng");
                    isValidEmail = false;
                }else{
                    tv_validation_message.setText("");
                    isValidEmail = true;
                }
            }
        });
        btn_confirm = findViewById(R.id.btn_confirm);

        imgbtn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_confirm.setOnClickListener(v-> {
            String email = et_email.getText().toString().trim();
            if(email.matches("")){
                tv_validation_message.setText("Vui lòng nhập email");
            }else{
                if(isValidEmail){
                    UserDAO.getInstance().getDb().collection("users").document(et_email.getText().toString().trim()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()){
                                if(task.getResult().exists()){
                                    String otp = Randomize.getInstance().randomOTP();
                                    Thread thread = new Thread(()->{
                                        Mailing.getInstance().sendEmailRecovery(email,otp);
                                    });
                                    thread.start();
                                    Intent intent = new Intent(ForgotActivity.this, RecoveryPassword.class);
                                    intent.putExtra("email",et_email.getText().toString().trim());
                                    intent.putExtra("otp",otp);
                                    Toast.makeText(ForgotActivity.this, "Mã xác nhận mới đã được gửi đến email " + et_email.getText().toString().trim() , Toast.LENGTH_SHORT).show();
                                    startActivityForResult(intent,LAUCH_CHANGEPASSWORD);
                                }else{
                                    tv_validation_message.setText("Email không tồn tại");
                                    isValidEmail = false;
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == LAUCH_CHANGEPASSWORD){
            if (resultCode == RESULT_OK){
                finish();
            }
        }
    }
}
