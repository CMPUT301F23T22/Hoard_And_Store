package com.example.hoard;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
/**
 * The main entry point of the application.
 *
 */
public class MainActivity extends AppCompatActivity {

    private Button button;
    private EditText username;
    private ItemDBController dbController;
    private GoogleSignInOptions gso;
    private GoogleSignInClient gsc;
    private Button googleSignInButton;
    private FirebaseAuth mAuth;
    private SignInClient oneTapClient;
    private BeginSignInRequest signInRequest;
    private Button signInButton;
    private Button createAccountButton;
    private EditText passwordText;
    private EditText usernameText;
    private EditText emailText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        passwordText = findViewById(R.id.passwordInput);
        usernameText = findViewById(R.id.usernameInput);
        emailText = findViewById(R.id.emailInput);
        mAuth = FirebaseAuth.getInstance();
        signInButton = findViewById(R.id.signInButton);
        createAccountButton = findViewById(R.id.createAccountButton);
        dbController = ItemDBController.getInstance();
//        username = findViewById(R.id.user_name_login);
//        button = findViewById(R.id.login_button);
//        googleSignInButton = findViewById(R.id.google_signon_button);
//        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
//        gsc = GoogleSignIn.getClient(this, gso);
//        mAuth = FirebaseAuth.getInstance();
//
//        oneTapClient = Identity.getSignInClient(this);
//        signInRequest = BeginSignInRequest.builder()
//                .setPasswordRequestOptions(BeginSignInRequest.PasswordRequestOptions.builder()
//                        .setSupported(true)
//                        .build())
//                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
//                        .setSupported(true)
//                        // Your server's client ID, not your Android client ID.
//                        // Only show accounts previously used to sign in.
//                        .setFilterByAuthorizedAccounts(true)
//                        .build())
//                // Automatically sign in when exactly one credential is retrieved.
//                .setAutoSelectEnabled(true)
//                .build();

//        googleSignInButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                signIn();
//            }
//        });

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = passwordText.getText().toString();
                String email = emailText.getText().toString();
                String username = usernameText.getText().toString();
                signIn(email, password, username);

//                //if the username is empty raise highlight edit text as red
//
//                //else check if username exists
//                User usr = new User(username.getText().toString().toLowerCase());
//
//                dbController = ItemDBController.getInstance();
//                dbController.login(
//                        usr,
//                        aVoid -> {
//                            // Success logic: Launch the activity
//                            Intent intent = new Intent(MainActivity.this, ListScreen.class);
//                            startActivity(intent);
//                        }
//                );

            }
        });

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = passwordText.getText().toString();
                String email = emailText.getText().toString();
                String username = usernameText.getText().toString();
                createAccount(email, password, username);
            }
        });


    }
//    @Override
//    public void onStart() {
//        super.onStart();
//        // Check if user is signed in (non-null) and update UI accordingly.
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if(currentUser != null){
//            reload();
//        }
//    }

    public void reload(){
        homePage();
    }

    private void homePage() {
        Intent intent = new Intent(MainActivity.this, ListScreen.class);
        startActivity(intent);
    }

    private void signInFailed() {

    }

    private void updateUI(FirebaseUser user){

    }
    private void createAccount(String email, String password, String userName){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            dbController = ItemDBController.getInstance();
                            dbController.addUser(user,email, userName);
                            Toast.makeText(MainActivity.this, "Account Created Please Sign In",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }
    private void signIn(String email, String password, String username){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            dbController.login(new ItemDBController.OnAccountActionComplete() {
                                @Override
                                public void OnAccountActionComplete() {
                                    // This will be executed only after dbController.login() completes
                                    // and calls the OnAccountActionComplete() callback.
                                    dbController.getUsername().addOnCompleteListener(new OnCompleteListener<String>() {
                                        @Override
                                        public void onComplete(@NonNull Task<String> task) {
                                            if (task.isSuccessful()) {
                                                String username = task.getResult();
                                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                        .setDisplayName(username)
                                                        .build();

                                                user.updateProfile(profileUpdates)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    homePage();
                                                                    Log.d(TAG, "User profile updated.");
                                                                }
                                                            }
                                                        });
                                            } else {
                                                // Handle the exception
                                                Exception exception = task.getException();
                                                if (exception != null) {
                                                    exception.printStackTrace();
                                                }
                                            }
                                        }
                                    });
                                }
                            });

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed. Create new Account?",
                                    Toast.LENGTH_SHORT).show();
                            signInFailed();
                        }
                    }
                });
    }
}