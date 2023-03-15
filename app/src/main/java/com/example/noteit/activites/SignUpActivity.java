package com.example.noteit.activites;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.noteit.DAO.UserDAO;
import com.example.noteit.R;
import com.example.noteit.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

public class SignUpActivity extends Activity {
    Button btn_signup;
    EditText et_name,et_email,et_pass, et_confirm_pass;
    TextView tv_validation_message,tv_open_signin;
    boolean isValidEmail = true;
    Animation animShake;
    User registerUser = new User();
    public static final int TO_OTP = 4234345;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);
        initView();
        tv_validation_message.setText("");
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
        tv_open_signin.setOnClickListener(view ->{
            finish();
        });

        btn_signup.setOnClickListener(view->{
            String signUpName =  et_name.getText().toString().trim();
            String signUpEmail = et_email.getText().toString().trim();
            String signUpPassword = et_pass.getText().toString().trim();
            String signUpCFPassword = et_confirm_pass.getText().toString().trim();
            if(tv_validation_message.getText().toString().trim() != ""){
                et_confirm_pass.setText("");
                et_pass.setText("");
                tv_validation_message.startAnimation(animShake);
            }
            Intent otpIntent = new Intent(this, ConfirmOTPActivity.class);
            if(validateInput(signUpName,signUpEmail,signUpPassword,signUpCFPassword)) {
                Thread thread = new Thread(()->{
                    UserDAO.getInstance().getDb().collection("users").document(et_email.getText().toString().trim()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>(){
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()){
                                if(task.getResult().exists()){
                                    runOnUiThread(()->{
                                        et_confirm_pass.setText("");
                                        et_pass.setText("");
                                        tv_validation_message.setText("Email đã tồn tại");
                                        isValidEmail = false;
                                    });
                                }else{
                                    if(isValidEmail){
                                        registerUser = new User(et_name.getText().toString(), et_email.getText().toString(), et_pass.getText().toString());
                                        try {
                                            if(UserDAO.getInstance().register(registerUser)){
                                                runOnUiThread(()->{
                                                    otpIntent.putExtra("OTP",UserDAO.getInstance().getOTP());
                                                    otpIntent.putExtra("email",registerUser.getEmail());
                                                    otpIntent.putExtra("name",registerUser.getName());
                                                    startActivityForResult(otpIntent,TO_OTP);
                                                });
                                            }
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        }
                    });
                });
                thread.start();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == TO_OTP){
            if(resultCode == RESULT_OK){
                Thread thread = new Thread(()->{
                    if(registerUser != null){
                        UserDAO.getInstance().confirmed(registerUser);
                    }
                });
                thread.start();
                Intent returnIntent1 = new Intent();
                setResult(RESULT_OK, returnIntent1);
                finish();
            }else if(resultCode == RESULT_CANCELED){
                et_name.setText("");
                et_email.setText("");
                et_confirm_pass.setText("");
                et_pass.setText("");
                Intent returnIntent1 = new Intent();
                setResult(RESULT_CANCELED, returnIntent1);
                finish();
            }
        }
    }

    private void initView() {
        et_name = findViewById(R.id.et_name);
        et_email = findViewById(R.id.et_email);
        et_pass = findViewById(R.id.et_pass);
        et_confirm_pass = findViewById(R.id.et_confirm_pass);
        tv_validation_message = findViewById(R.id.tv_validation_message);
        btn_signup = findViewById(R.id.btn_signup);
        animShake = AnimationUtils.loadAnimation(this,R.anim.shake);
        tv_open_signin = findViewById(R.id.tv_open_signin);
    }

    private boolean validateInput(String name, String email, String password, String cfPassword) {
        if(name.matches("")){
            et_confirm_pass.setText("");
            et_pass.setText("");
            tv_validation_message.setText("Vui lòng nhập tên người dùng");
            return false;
        }
        if(email.matches("")){
            et_confirm_pass.setText("");
            et_pass.setText("");
            tv_validation_message.setText("Vui lòng nhập email");
            return false;
        }
        if(password.matches("")){
            et_confirm_pass.setText("");
            et_pass.setText("");
            tv_validation_message.setText("Vui lòng nhập mật khẩu");
            return false;
        }
        if(password.length() < 6){
            et_confirm_pass.setText("");
            et_pass.setText("");
            tv_validation_message.setText("Mật khẩu phải có ít nhất 6 kí tự");
            return false;
        }
        if(cfPassword.matches("")){
            et_confirm_pass.setText("");
            et_pass.setText("");
            tv_validation_message.setText("Vui lòng nhập mật khẩu xác nhận");
            return false;
        }
        if(cfPassword.length() < 6){
            et_confirm_pass.setText("");
            et_pass.setText("");
            tv_validation_message.setText("Mật khẩu phải xác nhận phải có ít nhất 6 kí tự");
            return false;
        }
        if(!cfPassword.equals(password)){
            et_confirm_pass.setText("");
            et_pass.setText("");
            tv_validation_message.setText("Mật khẩu xác nhận không đúng");
            return false;
        }
        tv_validation_message.setText("");
        return true;
    }
}
