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

import java.util.Map;

public class DocumentFragment extends Fragment {
    private final String TAG = "DOCUMENT";

    private FragmentDocumentBinding binding;
    private FirestoreAccess firestoreAccess = new FirestoreAccess();

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentDocumentBinding.inflate(inflater, container, false);
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


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}