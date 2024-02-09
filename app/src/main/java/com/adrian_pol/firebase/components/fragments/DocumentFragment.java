package com.adrian_pol.firebase.components.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.adrian_pol.firebase.R;
import com.adrian_pol.firebase.databinding.FragmentDocumentBinding;
import com.adrian_pol.firebase.firebase.FirestoreAccess;
import com.adrian_pol.firebase.firebase.FirestoreAccessToken;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

public class DocumentFragment extends Fragment {
    private final String TAG = "DOCUMENT";

    private FragmentDocumentBinding binding;
    private FirestoreAccess firestoreAccess = new FirestoreAccess();
    private FirestoreAccessToken firestoreAccessToken = new FirestoreAccessToken();
    private FirebaseUser user;
    private String tokenActual;
    private String token1;
    private String token2;
    private String usuarioActual;
    public void getToken(){
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("TAG", "Fetching FCM registration token failed", task.getException());
                            return;
                        }
                        tokenActual = task.getResult();
                        Log.d("TAG-Token", tokenActual);
                    }
                });
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentDocumentBinding.inflate(inflater, container, false);
        user = FirebaseAuth.getInstance().getCurrentUser();

        loadToken();
        getToken();
        ArrayList<String> registro = new ArrayList<>();

        usuarioActual = user.getEmail();

        registro.add(user.getEmail());
        registro.add(tokenActual);
        Log.d("ComprobarArray",user.getEmail());
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadDocument();

        binding.buttonSave.setOnClickListener(v -> saveDocument());
    }

    public void loadDocument() {
        Log.d(TAG, "Loading document...");
        firestoreAccess.getDocument(new FirestoreAccess.DocumentCallback() {
            @Override
            public void onDocumentReceived(Map<String, Object> document) {
                if (getActivity() == null) return;
                getActivity().runOnUiThread(() -> {
                    binding.titleEditText.setText((String) document.get("title"));
                    binding.bodyEditText.setText((String) document.get("body"));
                });
            }

            @Override
            public void onError(Exception e) {
                if (getActivity() == null) return;
                getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "Error loading document", Toast.LENGTH_SHORT).show());
            }
        });
    }

    public void saveDocument() {
        String title = binding.titleEditText.getText().toString();
        String body = binding.bodyEditText.getText().toString();

        firestoreAccess.saveDocument(title, body);

        if (getActivity() != null) {
            Toast.makeText(getActivity(), "Document saved", Toast.LENGTH_SHORT).show();
        }
    }


    public void saveDataToken(){


        if(usuarioActual.equals(token1.substring(0,token1.indexOf("+")))){
            if(!tokenActual.equals(token1.substring(token1.indexOf("+")+1))){
                String save = usuarioActual +"+"+tokenActual;
                firestoreAccessToken.saveDocument("token",save);
            }
        }
        if(usuarioActual.equals(token2.substring(0,token2.indexOf("+")))){
            if(!tokenActual.equals(token2.substring(token2.indexOf("+")+1))){
                String save = usuarioActual +"+"+tokenActual;
                firestoreAccessToken.saveDocument(token1,save);
            }
        }
    }

    public void loadToken() {
        Log.d(TAG, "Loading token...");
        firestoreAccessToken.getDocument(new FirestoreAccessToken.DocumentCallback() {
            @Override
            public void onDocumentReceived(Map<String, Object> document) {
                Log.d(TAG,"Entra");
                if (getActivity() == null) return;
                token1 = (String) document.get("token");
                Log.d(TAG,token1.substring(0,token1.indexOf("+")));
                Log.d(TAG,token1.substring(token1.indexOf("+")+1));
                token2 = (String) document.get("token2");
                Log.d(TAG,token2.toString());
            }

            @Override
            public void onError(Exception e) {
                getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "Error loading document", Toast.LENGTH_SHORT).show());
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        saveDataToken();

        binding = null;
    }

}