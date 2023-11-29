package com.example.hoard;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;

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
    private View dialogView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        ListView listViewProfileOptions = findViewById(R.id.listViewProfileOptions);

        dbController = ItemDBController.getInstance();
        String[] profileOptions = {"Change Username", "Change Email", "Change Password"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, profileOptions);

        listViewProfileOptions.setAdapter(adapter);

        listViewProfileOptions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Handle item click
                switch (position) {
                    case 0:
                        // Open ChangeUsernameFragment
//                        confirmPasswordInputLayout.setVisibility(View.INVISIBLE);
//                        newPasswordInputLayout.setVisibility(View.INVISIBLE);
                        showChangeUsernameDialog();
                        break;
                    case 1:
                        // Open ChangeEmailFragment
                        showChangeEmailDialog();
                        break;
                    case 2:
                        // Open ChangePasswordFragment
                        showChangePasswordDialog();
                        break;
                    // Add cases for other options
                }
            }
        });
    }

    private void showChangeUsernameDialog() {
        // Create a custom AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Inflate the custom layout for the dialog
        View dialogView = getLayoutInflater().inflate(R.layout.change_username, null);
        builder.setView(dialogView);

        // Set custom title if needed
        builder.setTitle("Change Username");

        // Set positive button and its click listener
        builder.setPositiveButton("Change", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle the positive button click, e.g., retrieve data from the EditText
                EditText newUsernameEditText = dialogView.findViewById(R.id.usernameEditText);
                EditText currentEmailEditText = dialogView.findViewById(R.id.emailEditText);
                EditText currentPasswordEditText = dialogView.findViewById(R.id.currentPasswordEditText);

                String newUsername = newUsernameEditText.getText().toString();
                String currentEmail = currentEmailEditText.getText().toString();
                String currentPassword = currentPasswordEditText.getText().toString();

                dbController.updateUserName(newUsername).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Intent intent = new Intent(EditProfileActivity.this, ListScreen.class);
                            startActivity(intent);
                        } else {
                            Exception exception = task.getException();
                        }
                    }
                });



            }
        });

        // Set negative button and its click listener
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle the negative button click or do nothing to dismiss the dialog
            }
        });

        // Create and show the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showChangeEmailDialog() {
        // Create a custom AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Inflate the custom layout for the dialog
        View dialogView = getLayoutInflater().inflate(R.layout.change_email, null);
        builder.setView(dialogView);

        // Set custom title if needed
        builder.setTitle("Change Email");

        // Set positive button and its click listener
        builder.setPositiveButton("Change", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText currentEmailEditText = dialogView.findViewById(R.id.emailEditText);
                EditText currentPasswordEditText = dialogView.findViewById(R.id.currentPasswordEditText);
                EditText newEmailEditText = dialogView.findViewById(R.id.newemailEditText);

                String currentEmail = currentEmailEditText.getText().toString();
                String currentPassword = currentPasswordEditText.getText().toString();
                String newEmail = newEmailEditText.getText().toString();

//                dbController.updateUser(currentPassword, null, newUsername,currentEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if (task.isSuccessful()){
//                            Intent intent = new Intent(EditProfileActivity.this, ListScreen.class);
//                            startActivity(intent);
//                        } else {
//                            Exception exception = task.getException();
//                        }
//                    }
//                });
            }
        });

        // Set negative button and its click listener
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle the negative button click or do nothing to dismiss the dialog
            }
        });

        // Create and show the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showChangePasswordDialog() {
        // Create a custom AlertDialog
        View dialogView = getLayoutInflater().inflate(R.layout.change_password, null);
        final AlertDialog builder = new AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("Change Password")
            .setPositiveButton("Change", null) //Set to null. We override the onclick
            .setNegativeButton("Cancel", null)
            .create();


        // Set positive button and its click listener

        builder.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {

                Button button = ((AlertDialog) builder).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        TextInputLayout emailInputLayout = dialogView.findViewById(R.id.emailInputLayout);
                        TextInputLayout currentPasswordInputLayout = dialogView.findViewById(R.id.currentPasswordInputLayout);
                        TextInputLayout newPasswordInputLayout = dialogView.findViewById(R.id.newPasswordInputLayout);
                        TextInputLayout confirmPasswordInputLayout = dialogView.findViewById(R.id.currentPasswordInputLayout);

                        EditText currentEmailEditText = dialogView.findViewById(R.id.emailEditText);
                        EditText currentPasswordEditText = dialogView.findViewById(R.id.currentPasswordEditText);
                        EditText newPasswordEditText = dialogView.findViewById(R.id.newPasswordEditText);
                        EditText confirmPasswordEditText = dialogView.findViewById(R.id.confirmPasswordEditText);

                        String currentEmail = currentEmailEditText.getText().toString();
                        String currentPassword = currentPasswordEditText.getText().toString();
                        String newPassword = newPasswordEditText.getText().toString();
                        String confirmPassword = confirmPasswordEditText.getText().toString();

                        //check if passwords match
                        if (!(newPassword.equals(confirmPassword)) | newPassword.length() <= 6) {
                            try {
                                passwordUpdated = false;
                                throw new Exception("Password does not match");
                            } catch (Exception e) {
                                newPasswordInputLayout.setError("Make sure password matches and is greater than 6 characters");
                                newPasswordInputLayout.setErrorEnabled(true);
                                newPasswordInputLayout.setBoxStrokeColor(Color.RED);

                                confirmPasswordInputLayout.setError("Make sure password matches and is greater than 6 characters");
                                confirmPasswordInputLayout.setErrorEnabled(true);
                                confirmPasswordInputLayout.setBoxStrokeColor(Color.RED);

                            }
                        } else {
                            dbController.updateUserPassword(currentPassword, newPassword, currentEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        builder.dismiss();
                                        Intent intent = new Intent(EditProfileActivity.this, ListScreen.class);
                                        startActivity(intent);
                                    } else {
                                        Exception exception = task.getException();
                                        Toast.makeText(EditProfileActivity.this, "Current Password is Incorrect",
                                                Toast.LENGTH_SHORT).show();

                                        emailInputLayout.setError("Email or Password Incorrect");
                                        emailInputLayout.setErrorEnabled(true);
                                        emailInputLayout.setBoxStrokeColor(Color.RED);

                                        currentPasswordInputLayout.setError("Email or Password Incorrect");
                                        currentPasswordInputLayout.setErrorEnabled(true);
                                        currentPasswordInputLayout.setBoxStrokeColor(Color.RED);
                                    }
                                }
                            });
                        }

                    }
                });
            }
        });

        builder.show();
    }





//        // Initialize UI elements
//        usernameEditText = findViewById(R.id.usernameEditText);
//        usernameInputLayout = findViewById(R.id.usernameInputLayout);
//
//        currentPasswordEditText = findViewById(R.id.currentPasswordEditText);
//        currentPasswordInputLayout = findViewById(R.id.currentPasswordInputLayout);
//
//        newPasswordEditText = findViewById(R.id.newPasswordEditText);
//        newPasswordInputLayout = findViewById(R.id.newPasswordInputLayout);
//
//        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
//        confirmPasswordInputLayout = findViewById(R.id.confirmPasswordInputLayout);
//
//        emailEditText = findViewById(R.id.emailEditText);
//        saveButton = findViewById(R.id.saveButton);
//        dbController = ItemDBController.getInstance();
//        closeButton = findViewById(R.id.closeButton);

//        closeButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(EditProfileActivity.this, ListScreen.class);
//                startActivity(intent);
//            }
//        });
//
//        // Set click listener for the Save button
//        saveButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                validateUpdate();
//            }
//        });
//    }
//
//    private void validateUpdate(){
//        String username = usernameEditText.getText().toString();
//        String currentPassword = currentPasswordEditText.getText().toString();
//        String newPassword = newPasswordEditText.getText().toString();
//        String confirmPassword = confirmPasswordEditText.getText().toString();
//        String email = emailEditText.getText().toString();
//
//        //check if passwords match
//        if (!(newPassword.equals(confirmPassword)) | newPassword.length() <= 6) {
//            try {
//                passwordUpdated = false;
//                throw new Exception("Password does not match");
//            } catch (Exception e) {
//                handlePasswordError(e);
//            }
//        } else {
//            dbController.updateUser(currentPassword, newPassword, username,email).addOnCompleteListener(new OnCompleteListener<Void>() {
//                @Override
//                public void onComplete(@NonNull Task<Void> task) {
//                    if (task.isSuccessful()){
//                        Intent intent = new Intent(EditProfileActivity.this, ListScreen.class);
//                        startActivity(intent);
//                    } else {
//                        Exception exception = task.getException();
//                        handlePasswordError(exception);
//                    }
//                }
//            });
//        }
//
//    }
//
//
    private void handleUserNameError(Exception exception){
        Toast.makeText(EditProfileActivity.this, "Username change unsuccesfull",
                Toast.LENGTH_SHORT).show();
        usernameInputLayout.setError("Username error");
        usernameInputLayout.setErrorEnabled(true);
        usernameInputLayout.setBoxStrokeColor(Color.RED);
    }
//
    private void handlePasswordError(Exception exception) {
        TextInputLayout currentPasswordInputLayout = dialogView.findViewById(R.id.currentPasswordInputLayout);
        TextInputLayout newPasswordInputLayout = dialogView.findViewById(R.id.newPasswordInputLayout);
        TextInputLayout confirmPasswordInputLayout = dialogView.findViewById(R.id.currentPasswordInputLayout);

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
