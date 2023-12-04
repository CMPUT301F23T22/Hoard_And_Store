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
import com.google.android.material.textfield.TextInputLayout;

public class EditProfileActivity extends AppCompatActivity {
    private ItemDBController dbController;
    private final boolean passwordUpdated = false;
    private MenuItem sort;
    private MenuItem home;
    private BottomNavigationView bottomNav;
    private MenuItem sortMenuItem;
    private MenuItem homeMenuItem;
    private MenuItem profileMenuItem;
    private MenuItem chartMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        bottomNav = findViewById(R.id.bottomNavigationView);
        Menu bottomMenu = bottomNav.getMenu();
        sort = bottomMenu.findItem(R.id.nav_sort);
        home = bottomMenu.findItem(R.id.nav_home);

        ListView listViewProfileOptions = findViewById(R.id.listViewProfileOptions);
        ListView listViewProfileAuth = findViewById(R.id.profileAuthOptions);

        dbController = ItemDBController.getInstance();
        String[] profileOptions = {"Change Username", "Change Email", "Change Password"};
        String[] profileAuth = {"Sign Out", "Delete Account"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, profileOptions);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, profileAuth);


        bottomNav = findViewById(R.id.bottomNavigationView);
        bottomMenu = bottomNav.getMenu();

        sortMenuItem = bottomMenu.findItem(R.id.nav_sort);
        homeMenuItem = bottomMenu.findItem(R.id.nav_home);
        chartMenuItem = bottomMenu.findItem(R.id.nav_chart);
        profileMenuItem = bottomMenu.findItem(R.id.nav_profile);

        profileMenuItem.setChecked(true);

        listViewProfileOptions.setAdapter(adapter);
        listViewProfileAuth.setAdapter(adapter1);

        listViewProfileAuth.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Handle item click
                switch (position) {
                    case 0:
                        showConfirmDialog("Confirm Sign out", "Please Come back Soon!",
                                new EditProfileActivity.DialogInterfaceCallback() {
                                    @Override
                                    public void onPositiveButtonClick(DialogInterface dialog) {
                                        // Delete the selected items from Firestore and update UI
                                        handleAccountSignOut();
                                    }

                                });
                        break;
                    case 1:
                        showConfirmDialog("Confirm Account Deletion", "Are you sure you want to leave this great app?",
                                new EditProfileActivity.DialogInterfaceCallback() {
                                    @Override
                                    public void onPositiveButtonClick(DialogInterface dialog) {
                                        // Delete the selected items from Firestore and update UI
                                        handleAccountDeletion();
                                    }

                                });
                        break;
                    // Add cases for other options
                }
            }
        });

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


        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_home) {
                    Intent homeIntent = new Intent(getApplicationContext(), ListScreen.class);
                    homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(homeIntent);
                } else if (id == R.id.nav_sort) {
                    Intent sortIntent = new Intent(getApplicationContext(), SortActivity.class);
                    sortIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(sortIntent);
                    return true;
                } else if (id == R.id.nav_chart) {
                    Intent chartIntent = new Intent(getApplicationContext(), ItemBreakdown.class);
                    chartIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(chartIntent);

                } else if (id == R.id.nav_profile){
//                    Intent profileIntent = new Intent(getApplicationContext(), EditProfileActivity.class);
//                    profileIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                    startActivity(profileIntent);
                }

                return true;
            }
        });
    }



    /**
     * Shows an alert dialog for the user to change their username
     * Will need to verify password/email
     */
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
                Button button = builder.getButton(AlertDialog.BUTTON_POSITIVE);
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


    /**
     * Shows an alert dialog for the user to change their email
     * Will need to verify password/email
     */
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
                Button button = builder.getButton(AlertDialog.BUTTON_POSITIVE);
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

    /**
     * Shows an alert dialog for the user to change their password
     * Will need to verify password/email ans that new password matches confirm password
     */
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

                Button button = builder.getButton(AlertDialog.BUTTON_POSITIVE);
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

    /**
     * Handles the delete account process for the user account.
     */
    private void handleAccountDeletion(){
        dbController.deleteAccount();
        Intent accountDeletionIntent = new Intent(getApplicationContext(), MainActivity.class);
        accountDeletionIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(accountDeletionIntent);
    }

    /**
     * Handles the sign-out process for the user account.
     */
    private void handleAccountSignOut(){
        dbController.signOut();
        Intent signOutIntent = new Intent(getApplicationContext(), MainActivity.class);
        signOutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(signOutIntent);

    }

    private interface DialogInterfaceCallback {
        void onPositiveButtonClick(DialogInterface dialog);
    }

    /**
     * basic alert dialog that is updated and changed depending on what we are doing
     *
     * @param  title title for the alert dialog
     * @param  message message indicating a some confirmation or other message to show user
     */
    private void showConfirmDialog(String title, String message, EditProfileActivity.DialogInterfaceCallback callback) {
        new android.app.AlertDialog.Builder(EditProfileActivity.this, R.style.PurpleAlertDialog)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Yes", (dialog, which) -> {
                    if (callback != null) {
                        callback.onPositiveButtonClick(dialog);
                    }
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
