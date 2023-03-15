package com.example.noteit.DAO;

import com.example.noteit.models.Note;
import com.example.noteit.util.Randomize;
import com.google.firebase.firestore.FirebaseFirestore;

public class NoteDAO {
    private static NoteDAO instance;

    public  FirebaseFirestore getDb() {
        return db;
    }


    private static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private NoteDAO(){}

    private static boolean isExisted = false;

    public  static  NoteDAO getInstance(){
        if(instance == null){
            instance = new NoteDAO();
        }
        return instance;
    }

    public void addNote(Note note){
        db.collection("notes").document(note.getID()).set(note);
    }
}
