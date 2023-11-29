package com.example.hoard;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
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
                validateUpdate();
            }
        });
    }

    private void validateUpdate(){
        String username = usernameEditText.getText().toString();
        String currentPassword = currentPasswordEditText.getText().toString();
        String newPassword = newPasswordEditText.getText().toString();
        String confirmPassword = confirmPasswordEditText.getText().toString();
        String email = emailEditText.getText().toString();

        //check if passwords match
        if (!(newPassword.equals(confirmPassword)) | newPassword.length() <= 6) {
            try {
                passwordUpdated = false;
                throw new Exception("Password does not match");
            } catch (Exception e) {
                handlePasswordError(e);
            }
        } else {
            dbController.updateUser(currentPassword, newPassword, username,email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Intent intent = new Intent(EditProfileActivity.this, ListScreen.class);
                        startActivity(intent);
                    } else {
                        Exception exception = task.getException();
                        handlePasswordError(exception);
                    }
                }
            });
        }

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
            currentPasswordInputLayout.setError("Password is Incorrect");
            currentPasswordInputLayout.setErrorEnabled(true);
            currentPasswordInputLayout.setBoxStrokeColor(Color.RED);

        } else {
            newPasswordInputLayout.setError("Make sure password matches and is greater than 6 characters");
            newPasswordInputLayout.setErrorEnabled(true);
            newPasswordInputLayout.setBoxStrokeColor(Color.RED);

            confirmPasswordInputLayout.setError("Make sure password matches and is greater than 6 characters");
            confirmPasswordInputLayout.setErrorEnabled(true);
            confirmPasswordInputLayout.setBoxStrokeColor(Color.RED);

        }
    }
}
