package com.example.noteit.activites;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.noteit.R;
import com.example.noteit.util.Mailing;
import com.example.noteit.util.Randomize;
import com.example.noteit.util.SaveSharedPreference;

public class ConfirmOTPActivity extends Activity {
    Button btn_confirm;
    EditText et_getcode1, et_getcode2, et_getcode3, et_getcode4, et_getcode5, et_getcode6;
    TextView tv_skip,tv_email,tv_timer,tv_validation_message,tv_againCode;
    String finalOTP ="";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirm_account);
        initViews();
        tv_timer.setText("");
        tv_validation_message.setText("");
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
                AlertDialog alertDialog = new AlertDialog.Builder(ConfirmOTPActivity.this).create();
                alertDialog.setTitle("Thất bại");
                alertDialog.setMessage("Xác thực mã OTP thất bại. Bạn sẽ bị hạn chế một vài tính năng. Vui lòng đăng nhập và xác thực lại!");
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

        tv_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                setResult(RESULT_CANCELED,returnIntent);
                finish();
            }
        });

        Bundle bundle = getIntent().getExtras();
        String OTP ="", email ="",name ="";

        if(bundle!=null){
            OTP = bundle.getString("OTP");
            email = bundle.getString("email");
            name = bundle.getString("name");
            finalOTP = OTP;
        }

        String finalEmail = email;
        String finalName = name;
        tv_againCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread thread2 = new Thread(()->{
                    finalOTP = Randomize.getInstance().randomOTP();
                    Mailing.getInstance().sendEmailRegister(finalEmail,finalOTP, finalName);
                });
                thread2.start();
                Toast.makeText(ConfirmOTPActivity.this, "Mã xác nhận mới đã được gửi đến email " + finalEmail , Toast.LENGTH_SHORT).show();
            }
        });

        tv_email.setText("Vui lòng nhập mã xác nhận đã gửi đến email: " + email);

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newOTP = et_getcode1.getText().toString() + et_getcode2.getText().toString() + et_getcode3.getText().toString() + et_getcode4.getText().toString() + et_getcode5.getText().toString() + et_getcode6.getText().toString();
                if(!newOTP.equals(finalOTP)){
                    tv_validation_message.setText("Mã xác thực không đúng");
                    et_getcode1.setText("");
                    et_getcode2.setText("");
                    et_getcode3.setText("");
                    et_getcode4.setText("");
                    et_getcode5.setText("");
                    et_getcode6.setText("");
                }else{
                    SaveSharedPreference.setAcitvated(ConfirmOTPActivity.this,true);
                    AlertDialog alertDialog1 = new AlertDialog.Builder(ConfirmOTPActivity.this).create();
                    alertDialog1.setTitle("Thành công");
                    alertDialog1.setMessage("Xác thực thành công. Vui lòng đăng nhập để sử dụng ứng dụng");
                    alertDialog1.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    Intent returnIntent1 = new Intent();
                                    setResult(RESULT_OK, returnIntent1);
                                    finish();
                                    dialog.dismiss();
                                }
                            });
                    alertDialog1.show();
                }
            }
        });
    }

    private void initViews() {
        tv_timer = findViewById(R.id.tv_timer);
        btn_confirm = findViewById(R.id.btn_confirm);
        et_getcode1 = findViewById(R.id.et_getcode1);
        et_getcode2 = findViewById(R.id.et_getcode2);
        et_getcode3 = findViewById(R.id.et_getcode3);
        et_getcode4 = findViewById(R.id.et_getcode4);
        et_getcode5 = findViewById(R.id.et_getcode5);
        et_getcode6 = findViewById(R.id.et_getcode6);
        tv_againCode = findViewById(R.id.tv_againCode);
        tv_validation_message = findViewById(R.id.tv_validation_message);
        tv_skip = findViewById(R.id.tv_skip);
        tv_email = findViewById(R.id.tv_email);
    }
}
