package com.example.noteit.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.example.noteit.DAO.LabelDAO;
import com.example.noteit.models.Label;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SaveSharedPreference {

    private static SaveSharedPreference instance;
    private SaveSharedPreference(){}
    public static SaveSharedPreference getInstance(){
        if(instance == null){
            instance = new SaveSharedPreference();
        }
        return instance;
    }

    static final String PREF_EMAIL ="email";
    static final String PREF_VIEW_TYPE ="view type";
    static final String CURRENT_NOTE = "current note";
    static final String DELETE_DATE = "delete day";
    static final String ALARM_SOUND = "alarm sound";
    static final String HAS_CHANGE = "changes";
    static SharedPreferences getSharedPreferences(Context ctx){
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public static void setViewType(Context ctx, String type){
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_VIEW_TYPE,type);
        editor.commit();
    }

    public static void setUser(Context ctx, String email){
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_EMAIL,email);
        editor.commit();
    }
    public static String getUser(Context ctx){
        return getSharedPreferences(ctx).getString(PREF_EMAIL,"");
    }

    public static String getViewType(Context ctx){
        return getSharedPreferences(ctx).getString(PREF_VIEW_TYPE,"grid");
    }
    public static void setCurrentNoteID(Context ctx, String id){
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(CURRENT_NOTE,id);
        editor.commit();
    }
    public static String getCurrentNote(Context ctx){
        return getSharedPreferences(ctx).getString(CURRENT_NOTE,"");
    }

    public static void setDeleteDate(Context ctx, int day){
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putInt(DELETE_DATE,day);
        editor.commit();
    }
    public static int getDeleteDate(Context ctx){
        return getSharedPreferences(ctx).getInt(DELETE_DATE,1);
    }

    public static void setAlarmSound(Context ctx, String name){
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(ALARM_SOUND,name);
        editor.commit();
    }
    public static String getAlarmSound(Context ctx){
        return getSharedPreferences(ctx).getString(ALARM_SOUND,"");
    }
    public static void setFontsize(Context ctx, int size, String id){
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putInt(id, size);
        editor.commit();
    }
    public static int getFontSize(Context ctx, String id){
        return getSharedPreferences(ctx).getInt(id,15);
    }
    public static void setHasChange(Context ctx, String change){
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(HAS_CHANGE,change);
        editor.commit();
    }

    public static String getHasChange(Context ctx){
        return getSharedPreferences(ctx).getString(HAS_CHANGE,"");
    }

    static final String CREATE_ALARM = "alarm";
    public static void setCreateAlarm(Context ctx,String id){
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(CREATE_ALARM,id);
        editor.commit();
    }
    public static String getCreateAlarm(Context ctx){
        return getSharedPreferences(ctx).getString(CREATE_ALARM,"");
    }
    public static void setFromNotification(Context context,String id){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString("fromnoti",id);
        editor.commit();
    }
    public static String getFromNotification(Context ctx){
        return getSharedPreferences(ctx).getString("fromnoti","");
    }
    public static int getCurrentAlarm(Context ctx){
        return getSharedPreferences(ctx).getInt("current alarm",0);
    }
    public static void newAlarm(Context ctx){
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putInt("current alarm",getCurrentAlarm(ctx) + 1);
        editor.commit();
    }
    public static void setAcitvated(Context ctx, boolean activated){
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putBoolean("activated", activated);
        editor.commit();
    }
    public static boolean getActivated(Context ctx){
        return getSharedPreferences(ctx).getBoolean("activated",false);
    }

    public static void setNotes(Context ctx, int notes){
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putInt("notes",notes);
        editor.commit();
    }
    public  static int getNotesNumber(Context ctx){
        return getSharedPreferences(ctx).getInt("notes",0);
    }
    public  static void putLabels(Context ctx){
        List<Label> labelList = new ArrayList<>();
        LabelDAO.getInstance().getDb().collection("labels").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> documentSnapshotList = queryDocumentSnapshots.getDocuments();
                for (DocumentSnapshot d : documentSnapshotList) {
                    Label label = d.toObject(Label.class);
                    labelList.add(label);
                }
                if (labelList.size() > 0) {
                    SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
                    HashSet<String> set = new HashSet<String>();
                    for (Label label: labelList){
                        set.add(label.getName());

                    }
//                    for(String str : set){
//                        Toast.makeText(ctx, str, Toast.LENGTH_SHORT).show();
//                    }
                    editor.putStringSet("labels", set);
                    editor.commit();
                }
            }
        });
    }

    public static Set<String> getLabels(Context ctx){
        return getSharedPreferences(ctx).getStringSet("labels",null);
    }
}
