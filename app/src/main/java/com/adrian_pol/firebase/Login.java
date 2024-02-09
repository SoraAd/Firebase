package com.adrian_pol.firebase;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.adrian_pol.firebase.datos.Datos;
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

    private FragmentLoginBinding binding;
    private FirebaseAuth mAuth;
    private DatabaseReference database;
    boolean registerUser1;
    boolean registerUser2;
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // FCM SDK (and your app) can post notifications.
                } else {
                    // TODO: Inform user that that your app will not show notifications.
                }
            });

    private void askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentLoginBinding.inflate(inflater, container, false);

        return binding.getRoot();

    }

    public void updateBooleanRegister(){
        //Cambiar Boolean
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
                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(getContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();
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
                        }
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onStart() {
        super.onStart();
        getBooleanBBDDRegister();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            System.out.println("Fetching FCM registration token failed");
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        // Log and toast
                        System.out.println(token);
                        Toast.makeText(getContext(),"Your device registration token is "+ token
                                , Toast.LENGTH_SHORT).show();
                    }
                });

        askNotificationPermission();

        database = FirebaseDatabase.getInstance(Datos.getUrlFirebaseBbdd()).getReference("usuarios");
    }
}