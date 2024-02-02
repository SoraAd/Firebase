package com.adrian_pol.firebase;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import com.adrian_pol.firebase.databinding.FragmentLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

public class Login extends Fragment {

    private FragmentLoginBinding binding;
    private FirebaseAuth mAuth;
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentLoginBinding.inflate(inflater, container, false);

        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.registerButtom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String gmail = String.valueOf(binding.emailText.getText());
                String password = String.valueOf(binding.passwordText.getText());
                if (!gmail.isEmpty() && !password.isEmpty()) {
                    if(password.length() >=6) {
                        createUser(gmail, password);
                    }else {
                        Toast.makeText(getContext(),"The password is to short, minim 6 digits",Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(getContext(),"Complete the information",Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.loginButtom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String gmail = String.valueOf(binding.emailText.getText());
                String password = String.valueOf(binding.passwordText.getText());
                if (!gmail.isEmpty() && !password.isEmpty()) {
                    loginUser(gmail, password);
                }else {
                    Toast.makeText(getContext(),"Complete the information",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void createUser(String gmail, String password){
        String TAG = "Registro";
        mAuth.createUserWithEmailAndPassword(gmail,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(getContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    public void loginUser(String gmail, String password){
        String TAG = "Inicio sesion";
        mAuth.signInWithEmailAndPassword(gmail, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            NavHostFragment.findNavController(Login.this)
                                    .navigate(R.id.action_FirstFragment_to_SecondFragment);
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(getContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser o) {
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onStart() {
        //Registro E Inicio de sesion
        //https://firebase.google.com/docs/auth/android/password-auth?authuser=0&hl=es
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            //Detecta usuario iniciado
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
    }
}