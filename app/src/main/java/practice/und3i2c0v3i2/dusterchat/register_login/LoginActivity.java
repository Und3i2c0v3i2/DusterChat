package practice.und3i2c0v3i2.dusterchat.register_login;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import practice.und3i2c0v3i2.dusterchat.R;
import practice.und3i2c0v3i2.dusterchat.databinding.ActivityLoginBinding;
import practice.und3i2c0v3i2.dusterchat.home.HomeActivity;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private ActivityLoginBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        binding.setLifecycleOwner(this);
        binding.setLoginHandler(this);

        auth = FirebaseAuth.getInstance();

    }


    public void loginWithEmail() {

        String email = binding.loginUsername.getText().toString();
        String password = binding.loginPassword.getText().toString();



        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "email and password cannot be empty", Toast.LENGTH_SHORT).show();
        } else {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Login in progress");
            progressDialog.setTitle("Verifying e-mail and password. Please wait");
            progressDialog.show();

            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressDialog.dismiss();
                           if (task.isSuccessful()) {
                               Toast.makeText(LoginActivity.this, "Welcome", Toast.LENGTH_SHORT).show();
                                sendUserToHomePage();
                            } else {
                                String message = task.getException().getMessage();
                                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }


    }

    public void loginWithPhone() {
        Intent phoneLoginIntent = new Intent(this, PhoneLoginActivity.class);
        phoneLoginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(phoneLoginIntent);
        finish();
    }

    public void restorePassword() {

    }

    public void sendUserToHomePage() {

        Intent homeIntent = new Intent(this, HomeActivity.class);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(homeIntent);
        finish();

    }


    public void sendUserToRegisterPage() {
        Intent registerIntent = new Intent(this, RegisterActivity.class);
        registerIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(registerIntent);
        finish();
    }

}
