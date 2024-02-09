package com.adrian_pol.firebase.firebase;

import android.util.Log;

import com.adrian_pol.firebase.datos.Datos;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class FirestoreAccess {

    private static final String DOCUMENT_ID = Datos.getDocumentId();

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void getDocument(DocumentCallback callback) {
        db.collection("documents")
                .document(DOCUMENT_ID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            callback.onDocumentReceived(document.getData());
                        } else {
                            Log.d("FIRESTORE", "No such document");
                        }
                    } else {
                        Log.d("FIRESTORE", "get failed with ", task.getException());
                    }
                });
    }

    public void saveDocument(String title, String body) {
        Map<String, Object> doc = new HashMap<>();
        doc.put("title", title);
        doc.put("body", body);

        db.collection("documents")
                .document(DOCUMENT_ID)
                .update(doc);
    }

    public interface DocumentCallback {
        void onDocumentReceived(Map<String, Object> document);
        void onError(Exception e);
    }
}
