package com.example.hoard;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.snackbar.Snackbar;
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
    private MenuItem sort;
    private MenuItem home;
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        bottomNav = findViewById(R.id.bottomNavigationView);
        Menu bottomMenu = bottomNav.getMenu();
        sort = bottomMenu.findItem(R.id.nav_sort);
        home = bottomMenu.findItem(R.id.nav_home);

        ListView listViewProfileOptions = findViewById(R.id.listViewProfileOptions);
        closeButton = findViewById(R.id.closeButton);

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

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditProfileActivity.this, ListScreen.class);
                startActivity(intent);
            }
        });

        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_home) {

                } else if (id == R.id.nav_sort) {
                    Intent sortIntent = new Intent(getApplicationContext(), SortActivity.class);
                    startActivity(sortIntent);
                    return true;
                }

                return true;
            }
        });
    }




    private void showChangeUsernameDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.change_username, null);
        final AlertDialog builder = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setTitle("Change Username")
                .setPositiveButton("Change", null)
                .setNegativeButton("Cancel", null)
                .create();

        builder.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button button = ((AlertDialog) builder).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        TextInputLayout usernameInputLayout = dialogView.findViewById(R.id.usernameInputLayout);
                        TextInputLayout emailInputLayout = dialogView.findViewById(R.id.emailInputLayout);
                        TextInputLayout currentPasswordInputLayout = dialogView.findViewById(R.id.currentPasswordInputLayout);


                        EditText newUsernameEditText = dialogView.findViewById(R.id.usernameEditText);
                        EditText currentEmailEditText = dialogView.findViewById(R.id.emailEditText);
                        EditText currentPasswordEditText = dialogView.findViewById(R.id.currentPasswordEditText);

                        String newUsername = newUsernameEditText.getText().toString();
                        String currentEmail = currentEmailEditText.getText().toString();
                        String currentPassword = currentPasswordEditText.getText().toString();

                        if (newUsername.length() > 15){
                            usernameInputLayout.setError(getString(R.string.username_length_error));
                            usernameInputLayout.setErrorEnabled(true);
                            usernameInputLayout.setBoxStrokeColor(Color.RED);
                        } else {
                            dbController.updateUserName(newUsername).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        showSnackbar("Username updated successfully");
                                        builder.dismiss();
                                    } else {
                                        Exception exception = task.getException();
                                        emailInputLayout.setError(getString(R.string.email_and_or_password_incorrect));
                                        emailInputLayout.setErrorEnabled(true);
                                        emailInputLayout.setBoxStrokeColor(Color.RED);

                                        currentPasswordInputLayout.setError(getString(R.string.email_and_or_password_incorrect));
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

        // Create and show the AlertDialog
        builder.show();
    }

    private void showChangeEmailDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.change_email, null);
        final AlertDialog builder = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setTitle("Change Email")
                .setPositiveButton("Change", null)
                .setNegativeButton("Cancel", null)
                .create();


        builder.setOnShowListener(new DialogInterface.OnShowListener() {


            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button button = ((AlertDialog) builder).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        EditText currentEmailEditText = dialogView.findViewById(R.id.emailEditText);
                        EditText currentPasswordEditText = dialogView.findViewById(R.id.currentPasswordEditText);
                        EditText newEmailEditText = dialogView.findViewById(R.id.newemailEditText);

                        String currentEmail = currentEmailEditText.getText().toString();
                        String currentPassword = currentPasswordEditText.getText().toString();
                        String newEmail = newEmailEditText.getText().toString();

                        dbController.updateUserEmail(currentEmail, newEmail, currentPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    showSnackbar("Email updated successfully");
                                } else {
                                    builder.dismiss();
                                    Exception exception = task.getException();
                                }
                            }
                        });
                    }

                });
            }
        });

        // Create and show the AlertDialog
        builder.show();
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
                        TextInputLayout confirmPasswordInputLayout = dialogView.findViewById(R.id.confirmPasswordInputLayout);

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
                                newPasswordInputLayout.setError(getString(R.string.password_match_and_length_error));
                                newPasswordInputLayout.setErrorEnabled(true);
                                newPasswordInputLayout.setBoxStrokeColor(Color.RED);

                                confirmPasswordInputLayout.setError(getString(R.string.password_match_and_length_error));
                                confirmPasswordInputLayout.setErrorEnabled(true);
                                confirmPasswordInputLayout.setBoxStrokeColor(Color.RED);

                            }
                        } else {
                            dbController.updateUserPassword(currentPassword, newPassword, currentEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        builder.dismiss();
                                        showSnackbar("Password updated successfully");
                                    } else {
                                        Exception exception = task.getException();
                                        Toast.makeText(EditProfileActivity.this, "Current Password is Incorrect",
                                                Toast.LENGTH_SHORT).show();

                                        emailInputLayout.setError(getString(R.string.email_and_or_password_incorrect));
                                        emailInputLayout.setErrorEnabled(true);
                                        emailInputLayout.setBoxStrokeColor(Color.RED);

                                        currentPasswordInputLayout.setError(getString(R.string.email_and_or_password_incorrect));
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

    private void showSnackbar(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
    }
}
