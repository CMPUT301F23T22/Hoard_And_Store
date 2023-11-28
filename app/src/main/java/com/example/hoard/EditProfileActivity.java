package com.example.hoard;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class EditProfileActivity extends AppCompatActivity {

    private TextInputEditText usernameEditText;
    private TextInputLayout usernameInputLayout;
    private ItemDBController dbController;
    private TextInputEditText currentPasswordEditText;
    private TextInputLayout currentPasswordInputLayout;
    private TextInputEditText newPasswordEditText;
    private TextInputLayout newPasswordInputLayout;
    private TextInputEditText confirmPasswordEditText;
    private TextInputLayout confirmPasswordInputLayout;
    private TextInputEditText emailEditText;
    private Button saveButton;
    private FirebaseUser user;
    private Boolean validatedCurrPass = false;
    private Boolean validatedNewPassMatch = false;
    private Button closeButton;
    private boolean passwordUpdated = false;
    private boolean usernameUpdated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Initialize UI elements
        usernameEditText = findViewById(R.id.usernameEditText);
        usernameInputLayout = findViewById(R.id.usernameInputLayout);

        currentPasswordEditText = findViewById(R.id.currentPasswordEditText);
        currentPasswordInputLayout = findViewById(R.id.currentPasswordInputLayout);

        newPasswordEditText = findViewById(R.id.newPasswordEditText);
        newPasswordInputLayout = findViewById(R.id.newPasswordInputLayout);

        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        confirmPasswordInputLayout = findViewById(R.id.confirmPasswordInputLayout);

        emailEditText = findViewById(R.id.emailEditText);
        saveButton = findViewById(R.id.saveButton);
        dbController = ItemDBController.getInstance();
        closeButton = findViewById(R.id.closeButton);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditProfileActivity.this, ListScreen.class);
                startActivity(intent);
            }
        });

        // Set click listener for the Save button
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateUsernameUpdate();
                validateCurrentPassword();
            }
        });
    }


//    private void saveProfileChanges() {
//        // Get user input
//        String newUsername = usernameEditText.getText().toString();
//        String newPassword = passwordEditText.getText().toString();
//        String newEmail = emailEditText.getText().toString();
//
//        // Implement your logic to save the changes to the user profile
//        // For simplicity, let's just print the changes for now
//        System.out.println("New Username: " + newUsername);
//        System.out.println("New Password: " + newPassword);
//        System.out.println("New Email: " + newEmail);
//
//        // You can also save the changes to your backend server or local storage
//        // Add your logic here based on your application's requirements
//    }

    private void validateUsernameUpdate(){
        String username = usernameEditText.getText().toString();
        if (!username.isEmpty()){
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(username)
                    .build();

            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "User profile updated.");
                            } else {
                                Toast.makeText(EditProfileActivity.this, "User Name Updated Failed",
                                        Toast.LENGTH_SHORT).show();
                                Exception exception = task.getException();
                                handleUserNameError(exception);
                            }
                        }
                    });

        }

    }

    private void validateCurrentPassword() {
        String username = usernameEditText.getText().toString();
        String currentPassword = currentPasswordEditText.getText().toString();
        String newPassword = newPasswordEditText.getText().toString();
        String confirmPassword = confirmPasswordEditText.getText().toString();

        user = FirebaseAuth.getInstance().getCurrentUser();
        final String email = user.getEmail();
        AuthCredential credential = EmailAuthProvider.getCredential(email, currentPassword);


        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    if (!(newPassword.equals(confirmPassword))) {
                        try {
                            passwordUpdated = false;
                            throw new Exception("Password does not match");
                        } catch (Exception e) {
                            handlePasswordError(e);
                        }
                    } else {
                        passwordUpdated = true;
                    }

                        if (passwordUpdated) {
                            dbController.updateUser(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        passwordUpdated = true;
                                        Intent intent = new Intent(EditProfileActivity.this, ListScreen.class);
                                        startActivity(intent);
                                    } else {
                                        // Handle the exception
                                        Exception exception = task.getException();
                                        handlePasswordError(exception);
                                    }
                                }
                            });
                        }

                    } else {
                        Toast.makeText(EditProfileActivity.this, "Reauthentication failed",
                            Toast.LENGTH_SHORT).show();
                        Exception exception = task.getException();
                        handlePasswordError(exception);

                    }

                }
        });
    }
    private void handleUserNameError(Exception exception){
        Toast.makeText(EditProfileActivity.this, "Username change unsuccesfull",
                Toast.LENGTH_SHORT).show();
        usernameInputLayout.setError("Username error");
        usernameInputLayout.setErrorEnabled(true);
        usernameInputLayout.setBoxStrokeColor(Color.RED);
    }

    private void handlePasswordError(Exception exception) {
        if (exception instanceof FirebaseAuthInvalidCredentialsException) {
            // The user with the provided email does not exist
            Toast.makeText(EditProfileActivity.this, "Current Password is Incorrect",
                    Toast.LENGTH_SHORT).show();
            currentPasswordInputLayout.setError("Password does not match current");
            currentPasswordInputLayout.setErrorEnabled(true);
            currentPasswordInputLayout.setBoxStrokeColor(Color.RED);

        } else {
            newPasswordInputLayout.setError("Passwords do not match");
            newPasswordInputLayout.setErrorEnabled(true);
            newPasswordInputLayout.setBoxStrokeColor(Color.RED);

            confirmPasswordInputLayout.setError("Passwords do not match");
            confirmPasswordInputLayout.setErrorEnabled(true);
            confirmPasswordInputLayout.setBoxStrokeColor(Color.RED);

        }
    }
}
