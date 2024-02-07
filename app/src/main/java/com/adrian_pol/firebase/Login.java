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

import com.adrian_pol.firebase.Datos.Datos;
import com.adrian_pol.firebase.databinding.FragmentLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.Executor;

public class Login extends Fragment {

    private Datos datos;
    private FragmentLoginBinding binding;
    private FirebaseAuth mAuth;
    private DatabaseReference database;
    boolean registerUser1;
    boolean registerUser2;
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentLoginBinding.inflate(inflater, container, false);
        getBooleanBBDDRegister();
        updateBooleanRegister();

        return binding.getRoot();

    }

    public void updateBooleanRegister(){
        if(!registerUser1){
            database.child("registro_1").setValue(true);
        }
        if (registerUser1 && !registerUser2) {
            database.child("registro_2").setValue(true);
        }
    }
    public void getBooleanBBDDRegister(){
        database.child("registro_1").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(!task.isSuccessful()){
                    Log.e("Error Task","Error ",task.getException());
                }else {
                    registerUser1 = Boolean.parseBoolean(task.getResult().getValue().toString());

                }
            }
        });
        database.child("registro_2").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(!task.isSuccessful()){
                    Log.e("Error Task","Error ",task.getException());
                }else {
                    registerUser2 = Boolean.parseBoolean(task.getResult().getValue().toString());
                }


                if (registerUser1 && registerUser2) {
                    binding.registerButtom.setVisibility(View.INVISIBLE);
                }else {
                    binding.registerButtom.setVisibility(View.VISIBLE);
                }
            }
        });
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
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateBooleanRegister();
                            getBooleanBBDDRegister();
                            loginUser(gmail,password);

                            updateUI(user);

                        } else {
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

    public void getToken(){
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("TAG", "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        // Log and toast
                        String msg = token;
                        Log.d("TAG", msg);
                        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
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
        datos = new Datos();
        mAuth = FirebaseAuth.getInstance();

        database = FirebaseDatabase.getInstance(datos.getURL_FIREBASE_BBDD()).getReference("usuarios");
    }
}