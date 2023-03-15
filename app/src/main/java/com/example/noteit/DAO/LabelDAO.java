package com.example.noteit.DAO;

import com.google.firebase.firestore.FirebaseFirestore;

public class LabelDAO {
    private static LabelDAO instance;

    public  FirebaseFirestore getDb() {
        return db;
    }

    private static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private LabelDAO(){}

    public  static  LabelDAO getInstance(){
        if(instance == null){
            instance = new LabelDAO();
        }
        return instance;
    }

}
