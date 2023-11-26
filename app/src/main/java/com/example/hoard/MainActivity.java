package com.example.hoard;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.ActionMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
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
    private TextInputLayout passwordText;
    private TextInputLayout usernameText;
    private TextInputLayout emailText;
    private boolean isSignInMode = true;
    private MaterialCardView loginCardView;
    private TextInputLayout usernameInputLayout;
    private TextInputLayout emailInputLayout;
    private TextInputLayout passwordInputLayout;
    private MaterialButton signInButton;
    private MaterialButton createAccountButton;
    private SpannableString spannableString;
    private TextView clickableAccountOption;
    private ClickableSpan signUpClickable;
    private ClickableSpan signInClickable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        passwordText = findViewById(R.id.passwordInput);
//        usernameText = findViewById(R.id.usernameInput);
//        emailText = findViewById(R.id.emailInput);
        mAuth = FirebaseAuth.getInstance();

//        createAccountButton = findViewById(R.id.createAccountButton);
        dbController = ItemDBController.getInstance();
        loginCardView = findViewById(R.id.loginCardView);

        usernameInputLayout = findViewById(R.id.usernameInputLayout);
        emailInputLayout = findViewById(R.id.emailInputLayout);
        passwordInputLayout = findViewById(R.id.passwordInputLayout);
        signInButton = findViewById(R.id.signInButton);
        clickableAccountOption = findViewById(R.id.clickableSignIn);
        spannableString = new SpannableString(clickableAccountOption.getText());
//        createAccountButton = findViewById(R.id.createAccountButton);

        signUpClickable = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                setupSignUpMode();
                Toast.makeText(MainActivity.this, "Sign Up", Toast.LENGTH_SHORT).show();
            }
        };

        signInClickable = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                setupSignInMode();
                Toast.makeText(MainActivity.this, "Sign in", Toast.LENGTH_SHORT).show();
            }
        };

        setupSignInMode();

//        spannableString.setSpan(signUpClickable, 7,11, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        spannableString.setSpan(signInClickable, 16,20, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
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
                if (isSignInMode) {
                    String password = passwordInputLayout.getEditText().getText().toString();
                    String email = emailInputLayout.getEditText().getText().toString();
                    signIn(email, password);
                } else {
                    String password = passwordInputLayout.getEditText().getText().toString();
                    String email = emailInputLayout.getEditText().getText().toString();
                    String username = usernameInputLayout.getEditText().getText().toString();
                    createAccount(email,password,username);
                    setupSignInMode();
                }



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

//        createAccountButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (isSignInMode) {
//                    // Switch to sign-up mode
//                    setupSignUpMode();
//                } else {
//                    // Switch to sign-in mode
//                    setupSignInMode();
//                }
//                String password = passwordInputLayout.toString();
//                String email = emailInputLayout.toString();
//                String username = usernameInputLayout.toString();
//                createAccount(email, password, username);
//            }
//        });


    }

    private void setupSignInMode() {
        // Set up views for sign-in mode
        clickableAccountOption.setText("Don't have an account? Sign up");
        SpannableString singupSpannableString = new SpannableString(clickableAccountOption.getText());
        isSignInMode = true;
        loginCardView.setCardBackgroundColor(getResources().getColor(R.color.signInCardColor));
//        usernameInputLayout.setVisibility(View.VISIBLE);

        usernameInputLayout.setVisibility(View.GONE);
        passwordInputLayout.setHint("Password");
        signInButton.setText("Sign In");
//        createAccountButton.setText("Create Account");
        singupSpannableString.setSpan(signUpClickable, 23 , 29, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        clickableAccountOption.setText(singupSpannableString);
        clickableAccountOption.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void setupSignUpMode() {
        // Set up views for sign-up mode
        clickableAccountOption.setText("Already have an account? Sign in");
        SpannableString singupSpannableString = new SpannableString(clickableAccountOption.getText());

        isSignInMode = false;
        loginCardView.setCardBackgroundColor(getResources().getColor(R.color.signInCardColor));
        usernameInputLayout.setVisibility(View.VISIBLE);
        emailInputLayout.setVisibility(View.VISIBLE);
        passwordInputLayout.setHint("Create Password");
        signInButton.setText("Create Account");

        singupSpannableString.setSpan(signInClickable, 25 , 32, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        clickableAccountOption.setText(singupSpannableString);
        clickableAccountOption.setMovementMethod(LinkMovementMethod.getInstance());

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
    private void signIn(String email, String password){
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
                                                handleError(exception);
                                            }
                                        }
                                    });
                                }
                            });

                        } else {
                            // If sign in fails, display a message to the user.
                            handleError(task.getException());
                            signInFailed();
                        }
                    }
                });
    }

    private void handleError(Exception exception){
        if (exception instanceof FirebaseAuthInvalidUserException) {
            // The user with the provided email does not exist
            Toast.makeText(MainActivity.this, "Email address not registered. Create a new account.",
                    Toast.LENGTH_SHORT).show();
            // You may want to redirect the user to the registration page
            setupSignUpMode();
        } else if (exception instanceof FirebaseAuthInvalidCredentialsException) {
            // The provided email and password combination is invalid
            Toast.makeText(MainActivity.this, "Invalid email or password. Please try again.",
                    Toast.LENGTH_SHORT).show();

            emailInputLayout.setError("Incorrect Email");
            emailInputLayout.setErrorEnabled(true);
            emailInputLayout.setBoxStrokeColor(Color.RED);

            passwordInputLayout.setError("Incorrect Password");
            passwordInputLayout.setErrorEnabled(true);
            passwordInputLayout.setBoxStrokeColor(Color.RED);


        } else {
            // Handle other exceptions
            Toast.makeText(MainActivity.this, "Authentication failed. Please try again.",
                    Toast.LENGTH_SHORT).show();
            exception.printStackTrace();
        }

    }

}