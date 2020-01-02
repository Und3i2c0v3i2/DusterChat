package practice.und3i2c0v3i2.dusterchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import practice.und3i2c0v3i2.dusterchat.databinding.ActivityPhoneLoginBinding;

public class PhoneLoginActivity extends AppCompatActivity {

    private ActivityPhoneLoginBinding binding;
    private String verificationId;
    private PhoneAuthProvider.ForceResendingToken resendToken;
    private FirebaseAuth auth;

    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_phone_login);
        binding.setLoginHandler(this);

        auth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);

        binding.btnVerifyCode.setEnabled(false);
        binding.btnSendVerificationCode.setEnabled(false);


        binding.phoneNumberInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.btnSendVerificationCode.setEnabled(true);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void sendUserToLoginPage() {
        Intent loginIntent = new Intent(this, LoginActivity.class);
        loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }

    public void sendUserToHomePage() {
        Intent homeIntent = new Intent(this, HomeActivity.class);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(homeIntent);
        finish();
    }

    public void sendVerificationCode() {

        String phoneNumber = binding.phoneNumberInput.getText().toString();
        binding.phoneNumberInput.setText("");
        binding.btnSendVerificationCode.setEnabled(false);

        if (!TextUtils.isEmpty(phoneNumber)) {

            setupProgressDialog("Phone Verification");

            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    phoneNumber,        // Phone number to verify
                    60,                 // Timeout duration
                    TimeUnit.SECONDS,   // Unit of timeout
                    PhoneLoginActivity.this,               // Activity (for callback binding)
                    verificationCallback);        // OnVerificationStateChangedCallbacks
        }
    }

    private void setupProgressDialog(String title) {
        progressDialog.setTitle(title);
        progressDialog.setMessage("Please wait while verification is in progress");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public void verifyCode() {
        String verificationCode = binding.phoneVerifiationCodeInput.getText().toString();
        binding.phoneVerifiationCodeInput.setText("");
        binding.btnVerifyCode.setEnabled(false);

        if(!TextUtils.isEmpty(verificationCode)) {

            setupProgressDialog("Verification Code");

            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, verificationCode);
            signInWithPhoneAuthCredential(credential);
        }
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks verificationCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            progressDialog.dismiss();
            signInWithPhoneAuthCredential(phoneAuthCredential);
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            progressDialog.dismiss();
            Toast.makeText(PhoneLoginActivity.this, "Invalid phone number. Please enter correct number with your country code", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCodeSent(@NonNull String verificationId,
                               @NonNull PhoneAuthProvider.ForceResendingToken token) {
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.

            // Save verification ID and resending token so we can use them later
            progressDialog.dismiss();
            PhoneLoginActivity.this.verificationId = verificationId;
            resendToken = token;
            binding.btnVerifyCode.setEnabled(true);

            Toast.makeText(PhoneLoginActivity.this, "Verification code has been sent to provided phone number.", Toast.LENGTH_LONG).show();
        }
    };

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            sendUserToHomePage();
                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(PhoneLoginActivity.this, "Invalid verification code", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }
}
