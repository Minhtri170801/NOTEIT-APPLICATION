package com.example.noteit.activites;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.noteit.DAO.LabelDAO;
import com.example.noteit.DAO.NoteDAO;
import com.example.noteit.adapters.LabelAdapterForHome;
import com.example.noteit.util.MyReceiver;
import com.example.noteit.util.SaveSharedPreference;
import com.example.noteit.R;
import com.example.noteit.adapters.LabelAdapterForEdit;
import com.example.noteit.interfaces.LabelOnClickListener;
import com.example.noteit.models.Label;
import com.example.noteit.models.Note;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import jp.wasabeef.richeditor.RichEditor;

public class EditActivity extends Activity implements LabelOnClickListener {
    RichEditor richEditor;
    Animation slideUp,slideDown;
    Switch sw_password,sw_alarm;
    TextView tv_empty,tv_selected,tv_newLabel,tv_validation_message,tv_validation_message_alarm;
    RecyclerView rv_labels,rv_label_selected;
    List<Label> selectedlabelList = new ArrayList<>();
    List<Label> labelList = new ArrayList<>();
    LinearLayout input_password,input_alarm;
    ImageView imgv_close,imgv_save,fm_italic,fm_bold,fm_underline,pin;
    LabelAdapterForEdit labelAdapter1, labelAdapter2 ;
    EditText et_title,et_password,et_date,et_time;
    String noteID;
    NestedScrollView scrollView;
    Boolean isValidPassword = true;
    Note edited = new Note();
    CheckBox cb_1min;
    Note note = new Note();
    Boolean isAlarmed = false;
    public static int broadcastCode=0;
    int curDay = -1,curMonth = -1,curYear = -1,curHour = -1,curMin = -1;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_note);
        noteID = getIntent().getStringExtra("noteID");
        if(noteID == null){
            if(SaveSharedPreference.getFromNotification(EditActivity.this).length() != 0){
                noteID = SaveSharedPreference.getFromNotification(EditActivity.this);

            }
        }

        NoteDAO.getInstance().getDb().collection("notes").document(noteID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshots) {
                    note = (documentSnapshots.toObject(Note.class));
//                    selectedlabelList = note.getLabel();
                        initViews(note);
                        setEvents(note);
                        SaveSharedPreference.setCurrentNoteID(EditActivity.this,noteID);

                    if(note.isSecured()){
                            sw_password.setChecked(true);
                            et_password.setText(note.getPassword());
                    }else{
                            sw_password.setChecked(false);
                            input_password.setVisibility(View.GONE);
                    }
                    if(note.isAlarmed()){
                        sw_alarm.setChecked(true);
                        SimpleDateFormat localDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                        String date = localDateFormat.format(note.getTime());
                        String[] dates = date.split("/");
                        curDay = Integer.parseInt(dates[0]);
                        curMonth = Integer.parseInt(dates[1]);
                        curYear = Integer.parseInt(dates[2]);
                        et_date.setText(date);
                        SimpleDateFormat localDateFormat1 = new SimpleDateFormat("HH:mm");
                        String time = localDateFormat1.format(note.getTime());
                        String[] times = time.split(":");
                        curHour = Integer.parseInt(times[0]);
                        curMin = Integer.parseInt(times[1]);
                        et_time.setText(time);
                        if(note.getTime().compareTo(new Date()) <= 0){
                            sw_alarm.setChecked(false);
                            note.setAlarmed(false);
                            note.setTime(new Date());
                            et_time.setText("");
                            et_date.setText("");
                            input_alarm.setVisibility(View.GONE);
                        }
                    }else{
                        sw_alarm.setChecked(false);
                    }
                }
            });
    }

    private final void focusOnView(View view){
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.scrollTo(0, view.getBottom());
            }
        });
    }

    int year = -1 ;
    int month = -1;
    int day = -1;
    Date selectedDate = null;
    Date added1min = null;
    public void showDatePicker(){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());

        if(curYear == -1 || curMonth == -1 || curDay == -1){
            year = cal.get(Calendar.YEAR);
            month = cal.get(Calendar.MONTH);
            day = cal.get(Calendar.DAY_OF_MONTH);
        }else {
            year = curYear;
            month = curMonth;
            day = curDay;
        }

        DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int syear, int smonth, int sday) {
                String date = sday + "/"+ (smonth + 1)+"/" + syear;
                year = syear;
                month = smonth;
                day = sday;
                et_date.setText(date);
            }
        }, year, month, day);
        dialog.show();

    }


    int hour = -1;
    int minute = -1;
    public  void showTimePicker(){
        Calendar cal = Calendar.getInstance();

        cal.setTimeInMillis(System.currentTimeMillis());
        if(curHour == -1 || curMin == -1 ){
            hour = cal.get(Calendar.HOUR_OF_DAY);
            minute = cal.get(Calendar.MINUTE);
        }else{
            hour = curHour;
            minute = curMin;
        }
        if (hour>=0&&hour<12){
            cal.set(Calendar.AM_PM,Calendar.AM);
        }else{
            cal.set(Calendar.AM_PM,Calendar.PM);
        }

        TimePickerDialog dialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMin) {
                String time = selectedHour + ":" + selectedMin;
                hour = selectedHour;
                minute = selectedMin;
                et_time.setText(time);

            }
        }, hour, minute, true);
        dialog.show();
    }
    private boolean validateTime(){

        if(et_time.getText().toString().matches("") || et_date.getText().toString().matches("")){
            tv_validation_message_alarm.setVisibility(View.VISIBLE);
            tv_validation_message_alarm.setText("Vui lòng chọn đầy đủ ngày và tháng");
            focusOnView(input_alarm);
            return false;
        }
        if(selectedDate.before(new Date())){
            focusOnView(input_alarm);
            tv_validation_message_alarm.setVisibility(View.VISIBLE);
            tv_validation_message_alarm.setText("Vui lòng chọn thời gian hợp lệ");
            return false;
        }
        tv_validation_message_alarm.setVisibility(View.GONE);
        tv_validation_message_alarm.setText("");
        tv_validation_message_alarm.setVisibility(View.GONE);
        return true;
    }
    private void setEvents(Note note) {
        selectedlabelList = note.getLabel();
        sw_password.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    edited.setSecured(true);
                    input_password.setVisibility(View.VISIBLE);
                }else{
                    tv_validation_message.setText("");
                    edited.setSecured(false);
                    edited.setPassword("");
                    input_password.startAnimation(slideUp);
                    input_password.setVisibility(View.GONE);
                }
            }
        });

        sw_alarm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    edited.setAlarmed(true);
                    isAlarmed = true;
                    input_alarm.setVisibility(View.VISIBLE);
                }else{
                    tv_validation_message.setText("");
                    edited.setAlarmed(false);
                    isAlarmed = false;
                    input_alarm.startAnimation(slideUp);
                    input_alarm.setVisibility(View.GONE);
                }
            }
        });
        cb_1min.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                et_date.setEnabled(!isChecked);
                et_time.setEnabled(!isChecked);
                added1min = new Date();
            }
        });
        et_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });
        et_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker();
            }
        });
        et_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(et_password.getText().toString().length() == 0) {
                    tv_validation_message.setVisibility(View.VISIBLE);
                    tv_validation_message.setText("Vui lòng nhập mật khẩu");
                    isValidPassword = false;
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(et_password.getText().toString().trim().matches("") ) {
                    tv_validation_message.setVisibility(View.VISIBLE);
                    tv_validation_message.setText("Vui lòng nhập mật khẩu");
                    isValidPassword = false;
                }else if(et_password.getText().toString().length() < 6){
                    tv_validation_message.setVisibility(View.VISIBLE);
                    tv_validation_message.setText("Mật khẩu phải có tối thiểu 6 kí tự");
                    isValidPassword = false;
                }else{
                    tv_validation_message.setVisibility(View.GONE);
                    tv_validation_message.setText("");
                    isValidPassword = true;
                }

            }
        });
        et_date.setInputType(InputType.TYPE_NULL);

        et_time.setInputType(InputType.TYPE_NULL);
        imgv_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edited.setLabel(selectedlabelList);
                edited.setTitle(et_title.getText().toString().trim());
                edited.setContent(richEditor.getHtml());
                if(edited.isSecured()){
                    if(et_password.getText().toString().trim().length()<6){
                        edited.setSecured(false);
                        edited.setPassword("");
                    }else if(isValidPassword == true){
                            edited.setPassword(et_password.getText().toString().trim());
                    }
                }else{
                    edited.setSecured(false);
                    edited.setPassword("");
                }
                edited.setID(noteID);
                if(edited.isAlarmed()){
                    if(added1min == null){
                        if(selectedDate == null){
                            try {
                                selectedDate = new SimpleDateFormat("dd/MM/yyyy hh:mm").parse(et_date.getText().toString()+ " " + et_time.getText().toString());
                                if(validateTime() == true){
                                    edited.setTime(selectedDate);
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }else{
                        Calendar date = Calendar.getInstance();
                        long timeInSecs = date.getTimeInMillis();
                        added1min = new Date(timeInSecs + (1 * 60 * 1000));
                        edited.setTime(added1min);
                    }
                    SaveSharedPreference.setCreateAlarm(EditActivity.this,noteID);
                    AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(edited.getTime());
                    calendar.set(Calendar.SECOND,0);
                    calendar.set(Calendar.MILLISECOND,0);
                    Intent intent = new Intent(EditActivity.this, MyReceiver.class);
                    intent.putExtra("id", edited.getID());
                    intent.putExtra("title",edited.getTitle());

                    SaveSharedPreference.newAlarm(EditActivity.this);
                    Random rdm = new Random();
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(EditActivity.this,rdm.nextInt(300),intent,PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmManager.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pendingIntent);
                }else{
                    SaveSharedPreference.setCreateAlarm(EditActivity.this,"");
                }

                edited.setFontSize(SaveSharedPreference.getFontSize(EditActivity.this,noteID));
                edited.setOwner(SaveSharedPreference.getUser(EditActivity.this));
                edited.setLastModified(new Date());


                Thread thread = new Thread(()->{
                    NoteDAO.getInstance().getDb().collection("notes").document(noteID).set(edited);
                });

                thread.start();
                Intent returnIntent = new Intent();
                setResult(RESULT_OK,returnIntent);
                SaveSharedPreference.setFromNotification(EditActivity.this,"");
                finish();
            }
        });
        imgv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveSharedPreference.setFromNotification(EditActivity.this,"");
                finish();
            }
        });
        applyPinned(note);
        pin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(note.isPinned()){
                    note.setPinned(false);
                    edited.setPinned(false);
                }else{
                    note.setPinned(true);
                    edited.setPinned(true);
                }
                applyPinned(note);
            }
        });
        tv_empty.setVisibility(View.GONE);
        et_title.setText(note.getTitle());
        richEditor.setEditorHeight(200);
        richEditor.setPadding(10, 10, 10, 10);
        richEditor.setEditorFontSize(SaveSharedPreference.getFontSize(EditActivity.this,noteID));
        richEditor.setHtml(note.getContent());
        rv_label_selected = findViewById(R.id.rv_label_selected);

        if(selectedlabelList.size() == 0){
            tv_empty.setVisibility(View.VISIBLE);
            tv_selected.setVisibility(View.GONE);

        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        labelAdapter1 = new LabelAdapterForEdit(selectedlabelList);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rv_label_selected.setLayoutManager(linearLayoutManager);
        rv_label_selected.setItemAnimator(new DefaultItemAnimator());
        rv_label_selected.setAdapter(labelAdapter1);


        rv_labels = findViewById(R.id.rv_labels);
        labelList.clear();
        for(String str: SaveSharedPreference.getLabels(EditActivity.this)){
            labelList.add(new Label(str));
        }

        List<String> selectedLabelNames = new ArrayList<>();
        for (Label selectedLabel : selectedlabelList){
            selectedLabelNames.add(selectedLabel.getName());
        }

        for (Iterator<Label> iterator = labelList.iterator(); iterator.hasNext(); ){
            if(selectedLabelNames.contains(iterator.next().getName())){
                iterator.remove();
            }
        }

        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(getApplicationContext());
        labelAdapter2 = new LabelAdapterForEdit(labelList);
        if(labelAdapter2 != null){
            labelAdapter2.setListener(this);
        }
        if(labelAdapter1 != null){
            labelAdapter1.setListener(this);
        }

        linearLayoutManager1.setOrientation(LinearLayoutManager.HORIZONTAL);
        rv_labels.setLayoutManager(linearLayoutManager1);
        rv_labels.setItemAnimator(new DefaultItemAnimator());
        rv_labels.setAdapter(labelAdapter2);

        fm_italic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                richEditor.setItalic();
            }
        });
        fm_bold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                richEditor.setBold();
            }
        });
        fm_underline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                richEditor.setUnderline();
            }
        });
    }

    private void applyPinned(Note note) {
        if(note.isPinned()){
            pin.setImageResource(R.drawable.ic_baseline_push_pin_24);

        }else{
            pin.setImageResource(R.drawable.ic_baseline_push_unpin_24);
        }
        edited.setPinned(note.isPinned());
    }

    private void initViews(Note note) {
        et_title = findViewById(R.id.et_title);
        tv_validation_message = findViewById(R.id.tv_validation_message);
        tv_validation_message.setText("");
        tv_validation_message_alarm = findViewById(R.id.tv_validation_message_alarm);
        tv_validation_message_alarm.setText("");
        et_password = findViewById(R.id.et_password);
        imgv_close = findViewById(R.id.imgv_close);
        imgv_save = findViewById(R.id.imgv_save);
        tv_empty = findViewById(R.id.tv_empty);
        tv_selected = findViewById(R.id.tv_selected);
        tv_selected.setVisibility(View.VISIBLE);
        fm_italic = findViewById(R.id.fm_italic);
        fm_bold = findViewById(R.id.fm_bold);
        cb_1min = findViewById(R.id.cb_1min);
        fm_underline = findViewById(R.id.fm_underline);
        richEditor = (RichEditor) findViewById(R.id.richEditor);
        et_date = findViewById(R.id.et_date);
        et_time = findViewById(R.id.et_time);
        tv_newLabel = findViewById(R.id.tv_newLabel);
        pin = findViewById(R.id.pin);
        slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        slideDown = AnimationUtils.loadAnimation(this, R.anim.slide_down);
        sw_password = findViewById(R.id.sw_password);
        sw_alarm = findViewById(R.id.sw_alarm);
        input_password = findViewById(R.id.input_password);
        input_alarm = findViewById(R.id.input_alarm);
        tv_validation_message_alarm.setVisibility(View.GONE);
        tv_validation_message.setVisibility(View.GONE);
        scrollView = findViewById(R.id.scrollView);
        if(note.isSecured())
            input_password.setVisibility(View.VISIBLE);
        else
            input_password.setVisibility(View.GONE);
        if(note.isAlarmed())
            input_alarm.setVisibility(View.VISIBLE);
        else
            input_alarm.setVisibility(View.GONE);
    }

    @Override
    public Label onLabelClicked(Label label, int position) {
        if(labelList.contains(label)){
            labelList.remove(label);
            labelAdapter2.notifyItemRemoved(position);
            labelAdapter2.notifyItemRangeChanged(position, labelList.size());
            tv_empty.setVisibility(View.GONE);
            tv_selected.setVisibility(View.VISIBLE);
            selectedlabelList.add(label);
            labelAdapter1.notifyDataSetChanged();
        }else{
            selectedlabelList.remove(label);
            if(selectedlabelList.isEmpty()){
                tv_empty.setVisibility(View.VISIBLE);
                tv_selected.setVisibility(View.GONE);
            }
            labelAdapter1.notifyItemRemoved(position);
            labelAdapter1.notifyItemRangeChanged(position, labelList.size());
            labelList.add(label);
            tv_newLabel.setVisibility(View.VISIBLE);
            labelAdapter2.notifyDataSetChanged();
        }
        return label;
    }
}