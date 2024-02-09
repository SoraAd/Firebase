package com.adrian_pol.firebase.firebase;

import android.util.Log;

import com.adrian_pol.firebase.datos.Datos;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class FirestoreAccessToken {

    private static final String DOCUMENT_ID = Datos.getDocumentIdToken();

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void getDocument(DocumentCallback callback) {
        db.collection("claves")
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
        doc.put("token1", title);
        doc.put("token2", body);

        db.collection("claves")
                .document(DOCUMENT_ID)
                .update(doc);
    }

    public interface DocumentCallback {
        void onDocumentReceived(Map<String, Object> document);
        void onError(Exception e);
    }
}
