package com.adrian_pol.firebase;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.adrian_pol.firebase.databinding.FragmentDocumentBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;

public class Document extends Fragment {

    private FragmentDocumentBinding binding;
    private TextView title;
    private TextView body;
    private String tokenActual;
    private FirebaseUser user;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentDocumentBinding.inflate(inflater, container, false);
        title = binding.titleText;
        body = binding.bodyText;
        user = FirebaseAuth.getInstance().getCurrentUser();
        getToken();

        return binding.getRoot();

    }
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

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(Document.this)
                        .navigate(R.id.action_SecondFragment_to_FirstFragment);
            }
        });
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ArrayList<String> registro = new ArrayList<>();


        registro.add(user.getEmail());
        registro.add(tokenActual);
        Log.d("ComprobarArray",registro.get(0)+" "+registro.get(1));

        binding = null;
    }

}