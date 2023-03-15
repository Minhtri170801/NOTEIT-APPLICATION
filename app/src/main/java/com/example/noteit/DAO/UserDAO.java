package com.example.noteit.DAO;



import android.util.Log;
import android.widget.TextView;

import com.example.noteit.models.User;
import com.example.noteit.util.Mailing;
import com.example.noteit.util.Randomize;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.List;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class UserDAO {

    private static UserDAO instance;

    public static FirebaseFirestore getDb() {
        return db;
    }


    private static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private UserDAO(){}

    private static boolean isExisted = false;

    public  static  UserDAO getInstance(){
        if(instance == null){
            instance = new UserDAO();
        }
        return instance;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser.clone(currentUser);
    }

    private static User currentUser = new User();

    public User getCurrentUser() {
        return currentUser;
    }


    public static String getOTP() {
        return OTP;
    }

    private static void setOTP(String OTP) {
        UserDAO.OTP = OTP;
    }

    public void confirmed(User user){
        user.setActivated(true);
        db.collection("users").document(user.getEmail()).set(user);
    }

    private static String OTP;
    public boolean register(User user) throws InterruptedException {
        if(!user.isActivated()){
            String registerOTP = Randomize.getInstance().randomOTP();
            this.OTP = registerOTP;
            String registerName= user.getName();
            String registerEmail = user.getEmail();

            Thread mailingThread = new Thread(()->Mailing.getInstance().sendEmailRegister(registerEmail,registerOTP,registerName));
            mailingThread.start();

        }
        String password = user.getPassword();
        String hashed = BCrypt.withDefaults().hashToString(12, password.toCharArray());
        user.setPassword(hashed);
        try{
            db.collection("users").document(user.getEmail()).set(user);
            return true;
        }catch (Exception ex){
            return false;
        }
    }

    public void changePassword(String email, String newPassword){
        String hashed = BCrypt.withDefaults().hashToString(12, newPassword.toCharArray());
        db.collection("users").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                for (DocumentSnapshot d : list) {

                    User user = d.toObject(User.class);
                    if(user.getEmail().equals(email)){
                        user.setPassword(hashed);
                        db.collection("users").document(user.getEmail()).set(user);
                        break;
                    }
                }
            }
        });
    }
}


