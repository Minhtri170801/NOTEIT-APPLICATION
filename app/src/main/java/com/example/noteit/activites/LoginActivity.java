package com.example.noteit.activites;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.example.noteit.util.SaveSharedPreference;
import com.example.noteit.DAO.UserDAO;
import com.example.noteit.R;
import com.example.noteit.models.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import at.favre.lib.crypto.bcrypt.BCrypt;


public class LoginActivity extends Activity {
    static final int LAUCH_SIGN_UP = 100292;
    static final int LAUCH_FORGOT = 879192;
    TextView tv_open_signup, tv_forgot_pass, tv_validation_message;
    EditText et_email,et_pass;
    Button btn_signin;
    public static final int USER_NOT_EXIST = 5645;
    public static final int WRONG_PASSWORD = 22342;
    public static final int LOGGED_IN = 3927;
    Boolean isValidEmail = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in);
        Animation animShake;
        btn_signin = findViewById(R.id.btn_signin);
        btn_signin.setEnabled(true);
        et_email = findViewById(R.id.et_email);
        et_pass = findViewById(R.id.et_pass);
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
        animShake = AnimationUtils.loadAnimation(this,R.anim.shake);
        tv_validation_message = findViewById(R.id.tv_validation_message);
        tv_validation_message.setText("");
        tv_forgot_pass = findViewById(R.id.tv_forgot_pass);
        tv_forgot_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgotActivity.class);
                startActivityForResult(intent,LAUCH_FORGOT);
            }
        });

        tv_open_signup = findViewById(R.id.tv_open_signup);

        tv_open_signup.setOnClickListener(view ->  {
                Intent signUpIntent = new Intent(this, SignUpActivity.class);
                startActivityForResult(signUpIntent,LAUCH_SIGN_UP);
        });
        btn_signin.setOnClickListener(v -> {
            String email = et_email.getText().toString().trim();
            String password = et_pass.getText().toString().trim();
            if(tv_validation_message.getText().toString().trim() != ""){
                tv_validation_message.startAnimation(animShake);
            }
                if(validateInput(email,password)){
                    Thread thread = new Thread(()->{
                        runOnUiThread(()->{
                            btn_signin.setEnabled(false);
                        });
                    UserDAO.getInstance().getDb().collection("users").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            User u = new User();
                            u.setEmail("@@");
                            for (DocumentSnapshot d : list) {
                                User user = d.toObject(User.class);
                                if(user.getEmail().equals(email)){
                                    u.clone(user);
                                    break;
                                }
                            }
                            if(u.getEmail().equals("@@")){
                                runOnUiThread(()->{
                                    btn_signin.setEnabled(true);
                                    tv_validation_message.setText("Tài khoản không tồn tại");
                                });
                            }else{
                                BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), u.getPassword());
                                if(!result.verified){
                                    runOnUiThread(()->{
                                        btn_signin.setEnabled(true);
                                        tv_validation_message.setText("Mật khẩu không chính xác");
                                        et_pass.getText().clear();
                                    });
                                }else{
                                    runOnUiThread(()->{
                                        btn_signin.setEnabled(false);
                                    });
                                    Intent returnIntent = new Intent();
                                    setResult(RESULT_OK, returnIntent);
                                    returnIntent.putExtra("email",u.getName());
                                    SaveSharedPreference.setUser(LoginActivity.this, u.getEmail());
                                    SaveSharedPreference.setAcitvated(LoginActivity.this,u.isActivated());
                                    SaveSharedPreference.setNotes(LoginActivity.this,u.getNotes());
                                    UserDAO.getInstance().setCurrentUser(u);
                                    finish();
                                }
                            }
                        }
                    });
                });
                thread.start();
            }
        });
    }

    private boolean validateInput(String email,String password) {

        if(email.matches("")){
            et_pass.getText().clear();
            tv_validation_message.setText("Vui lòng nhập email");
            return false;
        }
        if(password.matches("")){
            tv_validation_message.setText("Vui lòng nhập mật khẩu");
            return false;
        }
        if(password.length() < 6){
            et_pass.getText().clear();
            tv_validation_message.setText("Mật khẩu phải có ít nhất 6 kí tự");
            return false;
        }
        if(isValidEmail == false){
            return false;
        }
        tv_validation_message.setText("");
        return true;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == LAUCH_SIGN_UP){
            if(resultCode == RESULT_OK){
                Toast.makeText(this, "Tạo tài khoản thành công. Vui lòng đăng nhập!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}