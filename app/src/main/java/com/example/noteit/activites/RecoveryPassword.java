package com.example.noteit.activites;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.noteit.DAO.UserDAO;
import com.example.noteit.R;
import com.example.noteit.util.Mailing;
import com.example.noteit.util.Randomize;

public class RecoveryPassword extends Activity {
    ImageButton imgbtn_back;
    EditText et_code, et_newpass, et_confirm_newpass;
    TextView tv_validation_message,tv_againCode,tv_timer;
    Button btn_confirm;
    String OTP = "", finalEmail ="";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_pass2);
        initViews();
        tv_timer.setText("");
        Thread thread = new Thread(()->{
            for(int i = 60;i > 0;i--){
                try {
                    Thread.sleep(1000);
                    int finalI = i;
                    runOnUiThread(()->{tv_timer.setText("Mã OTP sẽ hết hạn trong vòng: " + finalI +"s");});
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            runOnUiThread(()->{
                AlertDialog alertDialog = new AlertDialog.Builder(RecoveryPassword.this).create();
                alertDialog.setTitle("Thất bại");
                alertDialog.setMessage("Xác thực mã OTP thất bại!");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent returnIntent = new Intent();
                                setResult(RESULT_CANCELED, returnIntent);
                                finish();
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            });
        });
        thread.start();
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            OTP = bundle.getString("otp");
            finalEmail = bundle.getString("email");
        }
        imgbtn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_againCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread thread2 = new Thread(()->{
                    OTP = Randomize.getInstance().randomOTP();
                    Mailing.getInstance().sendEmailRecovery(finalEmail,OTP);
                });
                thread2.start();
                Toast.makeText(RecoveryPassword.this, "Mã xác nhận mới đã được gửi đến email " + finalEmail , Toast.LENGTH_SHORT).show();
            }
        });
        btn_confirm.setOnClickListener(v-> {
            String code = et_code.getText().toString().trim();
            String password = et_newpass.getText().toString().trim();
            String cfPassword = et_confirm_newpass.getText().toString().trim();
                if(validateInput(code,password,cfPassword)){
                    Thread thread1 = new Thread(()->{
                        UserDAO.getInstance().changePassword(finalEmail,et_newpass.getText().toString().trim());
                        runOnUiThread(()->{
                            AlertDialog alertDialog = new AlertDialog.Builder(RecoveryPassword.this).create();
                            alertDialog.setTitle("Thành công");
                            alertDialog.setMessage("Đổi mật khẩu thành công. Vui lòng đăng nhập lại!");
                            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent returnIntent = new Intent();
                                            setResult(RESULT_OK, returnIntent);
                                            finish();
                                            dialog.dismiss();
                                        }
                                    });
                            alertDialog.show();
                        });
                    });
                    thread1.start();
            }
        });
    }

    private boolean validateInput(String code, String password, String cfPassword) {
        if(code.matches("")){
            tv_validation_message.setText("Vui lòng nhập mã xác nhận");
            return false;
        }
        if(!code.equals(OTP)){
            tv_validation_message.setText("Mã xác nhận không khớp");
            return false;
        }
        if(code.length() != 6){
            tv_validation_message.setText("Mã xác nhận phải có 6 ký tự");
            return false;
        }
        if(password.matches("")){
            tv_validation_message.setText("Vui lòng nhập mật khẩu mới");
            return false;
        }
        if(cfPassword.matches("")){
            tv_validation_message.setText("Vui lòng nhập mật khẩu xác nhận");
            return false;
        }
        if(password.length() < 6){
            tv_validation_message.setText("Mật khẩu phải có ít nhất 6 ký tự");
            return false;
        }
        if(cfPassword.length() < 6){
            tv_validation_message.setText("Mật khẩu phải có ít nhất 6 ký tự");
            return false;
        }
        if(!password.equals(cfPassword)){
            tv_validation_message.setText("Mật khẩu xác nhận không khớp");
            return false;
        }

        tv_validation_message.setText("");
        return true;

    }

    private void initViews() {
        tv_timer = findViewById(R.id.tv_timer);
        imgbtn_back = findViewById(R.id.imgbtn_back);
        tv_againCode = findViewById(R.id.tv_againCode);
        btn_confirm = findViewById(R.id.btn_confirm);
        et_code = findViewById(R.id.et_code);
        et_newpass = findViewById(R.id.et_newpass);
        et_confirm_newpass = findViewById(R.id.et_confirm_newpass);
        tv_validation_message = findViewById(R.id.tv_validation_message);
        tv_validation_message.setText("");

    }
}
